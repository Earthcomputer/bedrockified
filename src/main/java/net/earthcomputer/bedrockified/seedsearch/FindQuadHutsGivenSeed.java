package net.earthcomputer.bedrockified.seedsearch;

import net.minecraft.init.Bootstrap;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.SwampHutStructure;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class FindQuadHutsGivenSeed {

    private static final int WORLD_SEED = 1125144;

    private static List<PartialSeed> partialSeeds;

    public static void main(String[] args) throws IOException {
        Bootstrap.register();

        partialSeeds = SeedFindingUtils.readPartialSeeds("witch_hut_partial_seeds.txt");

        Random rand = new SharedSeedRandom();

        for (int x = -32768; x <= 32768; x++) {
            for (PartialSeed partialSeed : partialSeeds) {
                int z = (int) (partialSeed.getSeed() - WORLD_SEED - 14357617 - 341873128712L * x);
                z *= 1273103741; // multiplicative inverse of b, mod 2^32
                if (z < -32768 || z > 32768)
                    continue;

                ChunkGeneratorOverworld chunkGen = SeedFindingUtils.createFakeChunkGen(WORLD_SEED);
                SwampHutStructure structure = (SwampHutStructure) Feature.SWAMP_HUT;

                ChunkPos pos = structure.getStartPositionForPosition(chunkGen, rand, x * 32, z * 32, 0, 0);
                if (!structure.hasStartAt(chunkGen, rand, pos.x, pos.z))
                    continue;

                pos = structure.getStartPositionForPosition(chunkGen, rand, (x + 1) * 32, z * 32, 0, 0);
                if (!structure.hasStartAt(chunkGen, rand, pos.x, pos.z))
                    continue;

                pos = structure.getStartPositionForPosition(chunkGen, rand, x * 32, (z + 1) * 32, 0, 0);
                if (!structure.hasStartAt(chunkGen, rand, pos.x, pos.z))
                    continue;

                pos = structure.getStartPositionForPosition(chunkGen, rand, (x + 1) * 32, (z + 1) * 32, 0, 0);
                if (!structure.hasStartAt(chunkGen, rand, pos.x, pos.z))
                    continue;

                System.out.println(x + " " + z);
            }
        }
    }

}
