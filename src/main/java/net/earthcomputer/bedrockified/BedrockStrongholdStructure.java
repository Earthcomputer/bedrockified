package net.earthcomputer.bedrockified;

import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.structure.StrongholdStructure;
import net.minecraft.world.gen.feature.structure.VillageStructure;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Random;

public class BedrockStrongholdStructure extends StrongholdStructure {

    private boolean hasGeneratedPositions = false;
    private ChunkPos[] firstStrongholds = new ChunkPos[3];
    private VillageStructure villageGenerator;
    private final Object lock = new Object();
    private int villageStrongholdCount = 3;
    private int gridSize = 200;
    private int gridSizeMinusMargin = 150;
    private int minimumDistance = 10;
    private float successChance = 0.25f;
    private int maxSearchRadius = 100;

    public BedrockStrongholdStructure(VillageStructure villageGenerator) {
        this.villageGenerator = villageGenerator;
    }

    @Override
    protected boolean hasStartAt(IChunkGenerator<?> chunkGen, Random rand, int chunkPosX, int chunkPosZ) {
        synchronized (lock) {
            if (!hasGeneratedPositions)
                generatePositions(rand, chunkGen);

            for (ChunkPos firstStronghold : firstStrongholds) {
                if (firstStronghold.x == chunkPosX && firstStronghold.z == chunkPosZ)
                    return true;
            }
        }

        return _hasAdditionalStronghold(chunkGen, rand, chunkPosX, chunkPosZ);
    }

    @Nullable
    @Override
    public BlockPos findNearest(World worldIn, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, BlockPos pos, int radius, boolean ungeneratedOnly) {
        if (!hasGeneratedPositions)
            generatePositions(new SharedSeedRandom(), chunkGenerator);

        return _getNearestStronghold(worldIn, pos);
    }

    public void generatePositions(Random rand, IChunkGenerator<?> chunkGen) {
        rand.setSeed(chunkGen.getSeed());
        float angle = rand.nextFloat() * (float) Math.PI * 2.0f;
        int radius = rand.nextInt(16) + 40;

        int innerStrongholdCount = 0;
        while (innerStrongholdCount < firstStrongholds.length) {
            int cx = MathHelper.floor(radius * Math.cos(angle));
            int cz = MathHelper.floor(radius * Math.sin(angle));

            boolean placedStronghold = false;
            outerLoop:
            for (int offX = cx - 8; offX < cx + 8; offX++) {
                for (int offZ = cz - 8; offZ < cz + 8; offZ++) {
                    if (villageGenerator.hasStartAt(chunkGen, rand, offX, offZ)) {
                        firstStrongholds[innerStrongholdCount++] = new ChunkPos(offX, offZ);
                        placedStronghold = true;
                        break outerLoop;
                    }
                }
            }
            if (placedStronghold) {
                angle += 0.6f * (float) Math.PI;
                radius += 8;
            } else {
                angle += 0.25f * (float) Math.PI;
                radius += 4;
            }
        }

        hasGeneratedPositions = true;
    }

    private boolean _hasAdditionalStronghold(IChunkGenerator<?> chunkGen, Random rand, int chunkX, int chunkZ) {
        if (!_isBeyondMinimumDistance(chunkX, chunkZ))
            return false;

        ChunkPos center = _getCenterOfGrid(chunkX, chunkZ);
        StrongholdResult result = _generateStronghold(chunkGen, center);
        return result.successful && result.pos.x == chunkX && result.pos.z == chunkZ;
    }

    private boolean _isBeyondMinimumDistance(int chunkX, int chunkZ) {
        return chunkX * chunkX + chunkZ * chunkZ >= minimumDistance * minimumDistance;
    }

    private ChunkPos _getCenterOfGrid(int chunkX, int chunkZ) {
        ChunkPos gridCoords = _getGridCoordinates(chunkX, chunkZ);
        return new ChunkPos(gridCoords.x * gridSize + gridSize / 2, gridCoords.z * gridSize + gridSize / 2);
    }

    private ChunkPos _getGridCoordinates(int chunkX, int chunkZ) {
        return new ChunkPos(MathHelper.floor((float) chunkX / gridSize), MathHelper.floor((float) chunkZ / gridSize));
    }

    private StrongholdResult _generateStronghold(IChunkGenerator<?> chunkGen, ChunkPos gridCenter) {
        if (!_isBeyondMinimumDistance(gridCenter.x, gridCenter.z))
            return StrongholdResult.UNSUCCESSFUL;

        ChunkPos gridCoords = _getGridCoordinates(gridCenter.x, gridCenter.z);

        BedrockRandom rand = new BedrockRandom();
        rand.setSeed(784295783249L * gridCoords.x + 827828252345L * gridCoords.z + chunkGen.getSeed() + 97858791);

        int minX = gridSize * gridCoords.x + gridSize - gridSizeMinusMargin;
        int maxX = gridSize * gridCoords.x + gridSizeMinusMargin;
        int minZ = gridSize * gridCoords.z + gridSize - gridSizeMinusMargin;
        int maxZ = gridSize * gridCoords.z + gridSizeMinusMargin;

        ChunkPos strongholdPos = new ChunkPos(rand.nextInt(minX, maxX), rand.nextInt(minZ, maxZ));
        boolean successful = rand.nextFloat() < successChance;

        return new StrongholdResult(successful, strongholdPos);
    }

    @Nullable
    private BlockPos _getNearestStronghold(IWorld world, BlockPos from) {
        ChunkPos cp = new ChunkPos(from);
        ChunkPos gridCoords = _getGridCoordinates(cp.x, cp.z);

        ArrayList<StrongholdResult> strongholds = new ArrayList<>(9);

        int expectedListSize = 1;

        for (int rad = 1; rad < maxSearchRadius; rad++) {
            for (int gridX = gridCoords.x - rad; gridX <= gridCoords.x + rad; gridX++) {
                for (int gridZ = gridCoords.z - rad; gridZ <= gridCoords.z + rad; gridZ++) {
                    // only search the outer ring
                    if (rad > 1 && Math.abs(gridCoords.x - gridX) < rad && Math.abs(gridCoords.z - gridZ) < rad)
                        continue;

                    expectedListSize += 8 * rad;
                    if (strongholds.size() < expectedListSize) // :thonk:
                        strongholds.ensureCapacity(expectedListSize);

                    ChunkPos pregeneratedStronghold = _isPregeneratedStrongholdHere(gridX, gridZ);
                    if (pregeneratedStronghold != null) {
                        strongholds.add(new StrongholdResult(true, pregeneratedStronghold));
                    } else {
                        ChunkPos gridCenter = new ChunkPos(gridX * gridSize + gridSize / 2, gridZ * gridSize + gridSize / 2);
                        StrongholdResult result = _generateStronghold(world.getChunkProvider().getChunkGenerator(), gridCenter);
                        strongholds.add(result);
                    }
                }
            }
        }

        if (_hasStrongholds(strongholds)) {
            ChunkPos strongholdChunk = _getClosestChunkPos(cp, strongholds);
            return new BlockPos((strongholdChunk.x << 4) + 8, 32, (strongholdChunk.z << 4) + 8);
        }

        return null;
    }

    @Nullable
    private ChunkPos _isPregeneratedStrongholdHere(int gridX, int gridZ) {
        synchronized (lock) {
            for (int i = 0; i < villageStrongholdCount; i++) {
                ChunkPos cp = firstStrongholds[i];
                ChunkPos gridPos = _getGridCoordinates(cp.x, cp.z);
                if (gridPos.x == gridX && gridPos.z == gridZ)
                    return cp;
            }
            return null;
        }
    }

    private boolean _hasStrongholds(ArrayList<StrongholdResult> strongholds) {
        for (StrongholdResult stronghold : strongholds)
            if (stronghold.successful)
                return true;
        return false;
    }

    private ChunkPos _getClosestChunkPos(ChunkPos to, ArrayList<StrongholdResult> strongholds) {
        int closestDistanceSq = Integer.MAX_VALUE;
        ChunkPos closestStronghold = null;

        for (StrongholdResult stronghold : strongholds) {
            if (stronghold.successful) {
                int dx = stronghold.pos.x - to.x;
                int dz = stronghold.pos.z - to.z;
                int distSq = dx * dx + dz * dz;
                if (distSq < closestDistanceSq) {
                    closestDistanceSq = distSq;
                    closestStronghold = stronghold.pos;
                }
            }
        }

        assert closestStronghold != null;
        return closestStronghold;
    }

    private static class StrongholdResult {
        boolean successful;
        ChunkPos pos;

        static StrongholdResult UNSUCCESSFUL = new StrongholdResult(false, new ChunkPos(0, 0));

        StrongholdResult(boolean successful, ChunkPos pos) {
            this.successful = successful;
            this.pos = pos;
        }
    }
}
