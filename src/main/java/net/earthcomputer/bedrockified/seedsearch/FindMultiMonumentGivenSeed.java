package net.earthcomputer.bedrockified.seedsearch;

import net.minecraft.init.Bootstrap;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.OceanMonumentStructure;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class FindMultiMonumentGivenSeed {

    private static ChunkPos npos = new ChunkPos(16777217, 16777217);

    private static final int WORLD_SEED = 1139032603;

    private static List<MultiMonumentInfo> multiMonuments;

    public static void main(String[] args) throws IOException {
        System.out.println("Initializing...");
        Bootstrap.register();

        multiMonuments = SeedFindingUtils.readMonumentInfos("multi_monument_partial_seeds.txt");

        Random rand = new SharedSeedRandom();
        ChunkGeneratorOverworld chunkGen = SeedFindingUtils.createFakeChunkGen(WORLD_SEED);
        OceanMonumentStructure structure = (OceanMonumentStructure) Feature.OCEAN_MONUMENT;

        int triHutX = npos.x;
        int triHutZ = npos.z;

        for (int x = 0; Math.abs(x) <= Math.abs(triHutX) + Math.abs(triHutZ); x = x <= 0 ? 1 - x : -x) {
            System.out.println("Searching at x = " + x);
            for (MultiMonumentInfo monumentInfo : multiMonuments) {
                int z = (int) (monumentInfo.getPartialSeed() - WORLD_SEED - 10387313 - 341873128712L * x);
                z *= 1273103741; // multiplicative inverse of b, mod 2^32
                if (z < -32768 || z > 32768)
                    continue;

                int count = 0;

                ChunkPos hut1 = structure.getStartPositionForPosition(chunkGen, rand, x * 32, z * 32, 0, 0);
                if (structure.hasStartAt(chunkGen, rand, hut1.x, hut1.z))
                    count++;
                else
                    hut1 = npos;

                ChunkPos hut2 = structure.getStartPositionForPosition(chunkGen, rand, (x + 1) * 32, z * 32, 0, 0);
                if (structure.hasStartAt(chunkGen, rand, hut2.x, hut2.z))
                    count++;
                else
                    hut2 = npos;

                ChunkPos hut3 = structure.getStartPositionForPosition(chunkGen, rand, x * 32, (z + 1) * 32, 0, 0);
                if (structure.hasStartAt(chunkGen, rand, hut3.x, hut3.z))
                    count++;
                else
                    hut3 = npos;
                if (count < 1)
                    continue;

                ChunkPos hut4 = structure.getStartPositionForPosition(chunkGen, rand, (x + 1) * 32, (z + 1) * 32, 0, 0);
                if (structure.hasStartAt(chunkGen, rand, hut4.x, hut4.z))
                    count++;
                else
                    hut4 = npos;
                if (count < 2)
                    continue;

                boolean isDualMonument = false;
                ChunkPos[] monuments = {hut1, hut2, hut3, hut4};
                for (int i = 0; i < 3; i++) {
                    for (int j = i + 1; j < 4; j++) {
                        if (Math.abs(monuments[j].x - monuments[i].x) + Math.abs(monuments[j].z - monuments[i].z) == 6) {
                            isDualMonument = true;
                        }
                    }
                }

                if (!isDualMonument)
                    continue;

                if (Math.abs(x) + Math.abs(z) < Math.abs(triHutX) + Math.abs(triHutZ)) {
                    triHutX = x;
                    triHutZ = z;
                }
            }

            System.out.println("Best so far: " + triHutX + ", " + triHutZ);
        }

        System.out.println("Closest tri hut: " + triHutX + ", " + triHutZ);
    }

}
