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

public class FindTriHutGivenSeed {

    private static ChunkPos npos = new ChunkPos(16777217, 16777217);

    private static final int WORLD_SEED = 1139032603;

    private static List<PartialSeed> partialSeeds;

    public static void main(String[] args) throws IOException {
        System.out.println("Initializing...");
        Bootstrap.register();

        partialSeeds = SeedFindingUtils.readPartialSeeds("witch_hut_tri_partial_seeds.txt");

        Random rand = new SharedSeedRandom();
        ChunkGeneratorOverworld chunkGen = SeedFindingUtils.createFakeChunkGen(WORLD_SEED);
        SwampHutStructure structure = (SwampHutStructure) Feature.SWAMP_HUT;

        int triHutX = npos.x;
        int triHutZ = npos.z;

        for (int x = 0; Math.abs(x) <= Math.abs(triHutZ); x = x <= 0 ? 1 - x : -x) {
            System.out.println("Searching at x = " + x);
            for (PartialSeed partialSeed : partialSeeds) {
                int z = (int) (partialSeed.getSeed() - WORLD_SEED - 14357617 - 341873128712L * x);
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
                if (count < 1)
                    continue;

                ChunkPos hut3 = structure.getStartPositionForPosition(chunkGen, rand, x * 32, (z + 1) * 32, 0, 0);
                if (structure.hasStartAt(chunkGen, rand, hut3.x, hut3.z))
                    count++;
                else
                    hut3 = npos;
                if (count < 2)
                    continue;

                ChunkPos hut4 = structure.getStartPositionForPosition(chunkGen, rand, (x + 1) * 32, (z + 1) * 32, 0, 0);
                if (structure.hasStartAt(chunkGen, rand, hut4.x, hut4.z))
                    count++;
                else
                    hut4 = npos;
                if (count < 3)
                    continue;

                int tickingRange = (minUnsigned(
                        minUnsigned(
                                maxUnsigned(maxUnsigned(hut2.x, hut4.x) - hut3.x, maxUnsigned(hut3.z, hut4.z) - hut2.z), // excl. hut 1
                                maxUnsigned(hut4.x - minUnsigned(hut1.x, hut3.x), maxUnsigned(hut3.z, hut4.z) - hut1.z) // excl. hut 2
                        ),
                        minUnsigned(
                                maxUnsigned(maxUnsigned(hut2.x, hut4.x) - hut1.x, hut4.z - minUnsigned(hut1.z, hut2.z)), // excl. hut 3
                                maxUnsigned(hut2.x - minUnsigned(hut1.x, hut3.x), hut3.z - minUnsigned(hut1.z, hut2.z)) // excl. hut 4
                        )
                ) + 1) / 2;
                if (tickingRange > 6)
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

    private static int minUnsigned(int a, int b) {
        return (int) Math.min(Integer.toUnsignedLong(a), Integer.toUnsignedLong(b));
    }

    private static int maxUnsigned(int a, int b) {
        return (int) Math.max(Integer.toUnsignedLong(a), Integer.toUnsignedLong(b));
    }

}
