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

    private boolean hasGeneratedVillageStrongholds = false; // (DWORD*) this + 48
    private ChunkPos[] villageStrongholds = new ChunkPos[3]; // (DWORD*) this + 50
    private VillageStructure villageGenerator; // (DWORD*) this + 56

    private final Object lock = new Object(); // (DWORD*) this + 58

    private int villageStrongholdCount = 3; // (DWORD*) this + 68
    private int gridSize = 200; // (DWORD*) this + 69
    private int gridSizeMinusMargin = 150; // (DWORD*) this + 70
    private int minimumDistance = 10; // (DWORD*) this + 71
    private float successChance = 0.25f; // (DWORD*) this + 72
    private int maxSearchRadius = 100; // (DWORD*) this + 73

    public BedrockStrongholdStructure(VillageStructure villageGenerator) {
        this.villageGenerator = villageGenerator;
    }

    @Override
    protected boolean hasStartAt(IChunkGenerator<?> chunkGen, Random rand, int chunkPosX, int chunkPosZ) {
        synchronized (lock) {
            if (!hasGeneratedVillageStrongholds)
                generateVillageStrongholds(rand, chunkGen);

            for (ChunkPos villageStronghold : villageStrongholds) {
                if (villageStronghold.x == chunkPosX && villageStronghold.z == chunkPosZ)
                    return true;
            }
        }

        return isScatteredStrongholdAt(chunkGen, rand, chunkPosX, chunkPosZ);
    }

    @Nullable
    @Override
    public BlockPos findNearest(World worldIn, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, BlockPos pos, int radius, boolean ungeneratedOnly) {
        if (!hasGeneratedVillageStrongholds)
            generateVillageStrongholds(new SharedSeedRandom(), chunkGenerator);

        return _getNearestStronghold(worldIn, pos);
    }

    // bedrock name: generatePositions
    public void generateVillageStrongholds(Random rand, IChunkGenerator<?> chunkGen) {
        rand.setSeed(chunkGen.getSeed());
        float angle = rand.nextFloat() * (float) Math.PI * 2.0f;
        int radius = rand.nextInt(16) + 40;

        int count = 0;
        while (count < villageStrongholds.length) {
            int cx = MathHelper.floor(radius * Math.cos(angle));
            int cz = MathHelper.floor(radius * Math.sin(angle));

            boolean placedStronghold = false;
            outerLoop:
            for (int offX = cx - 8; offX < cx + 8; offX++) {
                for (int offZ = cz - 8; offZ < cz + 8; offZ++) {
                    if (villageGenerator.hasStartAt(chunkGen, rand, offX, offZ)) {
                        villageStrongholds[count++] = new ChunkPos(offX, offZ);
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

        hasGeneratedVillageStrongholds = true;
    }

    // bedrock name: _hasAdditionalStronghold
    private boolean isScatteredStrongholdAt(IChunkGenerator<?> chunkGen, Random rand, int chunkX, int chunkZ) {
        if (!_isBeyondMinimumDistance(chunkX, chunkZ))
            return false;

        ChunkPos gridCenter = _getCenterOfGrid(chunkX, chunkZ);
        StrongholdResult result = generateScatteredStronghold(chunkGen, gridCenter);
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

    // bedrock name: _generateStronghold
    private StrongholdResult generateScatteredStronghold(IChunkGenerator<?> chunkGen, ChunkPos gridCenter) {
        // this check always fails because no grid center is within minimumDistance of (0, 0)
        if (!_isBeyondMinimumDistance(gridCenter.x, gridCenter.z))
            return StrongholdResult.UNSUCCESSFUL;

        ChunkPos gridCoords = _getGridCoordinates(gridCenter.x, gridCenter.z);

        BedrockRandom rand = new BedrockRandom();
        rand.setSeed(784295783249L * gridCoords.x + 827828252345L * gridCoords.z + chunkGen.getSeed() + 97858791);

        // This selects the middle 100x100 chunks of the 200x200 grid cell (with a 50 chunk margin)
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
                    if (strongholds.size() < expectedListSize) // I suppose they meant size > expected? :thonk:
                        strongholds.ensureCapacity(expectedListSize);

                    ChunkPos villageStronghold = isVillageStrongholdAt(gridX, gridZ);
                    if (villageStronghold != null) {
                        strongholds.add(new StrongholdResult(true, villageStronghold));
                    } else {
                        ChunkPos gridCenter = new ChunkPos(gridX * gridSize + gridSize / 2, gridZ * gridSize + gridSize / 2);
                        StrongholdResult result = generateScatteredStronghold(world.getChunkProvider().getChunkGenerator(), gridCenter);
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

    // bedrock name: _isPregeneratedStrongholdHere
    @Nullable
    private ChunkPos isVillageStrongholdAt(int gridX, int gridZ) {
        synchronized (lock) {
            for (int i = 0; i < villageStrongholdCount; i++) {
                ChunkPos cp = villageStrongholds[i];
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
