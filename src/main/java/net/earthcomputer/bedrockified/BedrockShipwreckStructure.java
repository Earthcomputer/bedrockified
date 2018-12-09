package net.earthcomputer.bedrockified;

import com.google.common.collect.ImmutableSet;
import net.minecraft.init.Biomes;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.structure.OceanMonumentStructure;
import net.minecraft.world.gen.feature.structure.ShipwreckStructure;

import java.util.Random;

public class BedrockShipwreckStructure extends ShipwreckStructure {

    private OceanMonumentStructure monumentGenerator;

    public BedrockShipwreckStructure(OceanMonumentStructure monumentGenerator) {
        this.monumentGenerator = monumentGenerator;
    }

    @Override
    public ChunkPos getStartPositionForPosition(IChunkGenerator<?> chunkGenerator, Random random, int x, int z, int spacingOffsetsX, int spacingOffsetsZ) {
        int shipwreckSpacing = getBiomeFeatureDistance(chunkGenerator);
        int shipwreckSeparation = func_211745_b(chunkGenerator);

        int gridX = x + shipwreckSpacing * spacingOffsetsX;
        int gridZ = z + shipwreckSpacing * spacingOffsetsZ;
        if (x < 0)
            gridX -= shipwreckSpacing - 1;
        if (z < 0)
            gridZ -= shipwreckSpacing - 1;
        gridX /= shipwreckSpacing;
        gridZ /= shipwreckSpacing;

        ((SharedSeedRandom) random).setLargeFeatureSeedWithSalt(chunkGenerator.getSeed(), gridX, gridZ, getSeedModifier());

        int featureX = gridX * shipwreckSpacing;
        int featureZ = gridZ * shipwreckSpacing;
        featureX += (random.nextInt(shipwreckSpacing - shipwreckSeparation) + random.nextInt(shipwreckSpacing - shipwreckSeparation)) / 2;
        featureZ += (random.nextInt(shipwreckSpacing - shipwreckSeparation) + random.nextInt(shipwreckSpacing - shipwreckSeparation)) / 2;

        return new ChunkPos(featureX, featureZ);
    }

    @Override
    public boolean hasStartAt(IChunkGenerator<?> chunkGen, Random rand, int chunkPosX, int chunkPosZ) {
        ChunkPos chunk = getStartPositionForPosition(chunkGen, rand, chunkPosX, chunkPosZ, 0, 0);
        if (chunk.x != chunkPosX || chunk.z != chunkPosZ)
            return false;

        for (int dx = chunkPosX - 5; dx < chunkPosX + 5; dx++) {
            for (int dz = chunkPosZ - 5; dz < chunkPosZ + 5; dz++) {
                if (monumentGenerator.hasStartAt(chunkGen, rand, chunkPosX, chunkPosZ)) {
                    return false;
                }
            }
        }

        Biome biome = chunkGen.getBiomeProvider().getBiome(new BlockPos((chunkPosX << 4) + 8, 0, (chunkPosZ << 4) + 8), Biomes.DEFAULT);
        if (!chunkGen.hasStructure(biome, this))
            return false;

        int radius = isShipwreckBeached(biome) ? 10 : 20;
        for (Biome b : chunkGen.getBiomeProvider().getBiomesInSquare((chunkPosX << 4) + 8, (chunkPosZ << 4) + 8, radius))
            if (b != biome)
                return false;

        return true;
    }

    private boolean isShipwreckBeached(Biome biome) {
        return biome == Biomes.BEACH || biome == Biomes.SNOWY_BEACH || biome == Biomes.MUSHROOM_FIELD_SHORE;
    }
}
