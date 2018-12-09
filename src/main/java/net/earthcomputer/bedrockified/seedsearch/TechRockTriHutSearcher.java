package net.earthcomputer.bedrockified.seedsearch;

import net.minecraft.init.Bootstrap;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.SwampHutStructure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TechRockTriHutSearcher {

    private static ChunkPos npos = new ChunkPos(Integer.MAX_VALUE, Integer.MAX_VALUE);

    private static List<PartialSeed> partialSeeds;

    public static void main(String[] args) throws IOException {
        System.out.println("Initializing...");
        Bootstrap.register();

        partialSeeds = SeedFindingUtils.readPartialSeeds("witch_hut_tri_partial_seeds.txt");

        System.out.println("Searching...");
        for (int radius = 0;; radius++) {
            List<TriHut> triHuts = new ArrayList<>();

            if (radius == 0)
                getAllTriHutsAt(-1, -1).forEach(seed -> triHuts.add(new TriHut(seed, -1, -1)));
            else {
                for (int x = -radius; x < radius; x++) {
                    int x_f = x - 1;
                    int z = radius - Math.abs(x) - 1;
                    getAllTriHutsAt(x_f, z).forEach(seed -> triHuts.add(new TriHut(seed, x_f, z)));
                }
                for (int x = radius; x > -radius; x--) {
                    int x_f = x - 1;
                    int z = Math.abs(x) - radius - 1;
                    getAllTriHutsAt(x_f, z).forEach(seed -> triHuts.add(new TriHut(seed, x_f, z)));
                }
            }

            if (!triHuts.isEmpty()) {
                System.out.println("===== RESULTS =====");
                for (TriHut triHut : triHuts) {
                    System.out.println(triHut.partialSeed.getTickingRange() + " " + triHut.partialSeed.getSeed() + " " + triHut.regionX + " " + triHut.regionZ);
                }
                break;
            }
        }
    }

    private static List<PartialSeed> getAllTriHutsAt(int x, int z) {
        int subtrahend = (int) (341873128712L * x + 132897987541L * z + 14357617);

        Random rand = new SharedSeedRandom();

        List<PartialSeed> seeds = new ArrayList<>();

        System.out.println("Searching for tri huts at " + x + ", " + z);

        int i = 0;

        for (PartialSeed partialSeed : partialSeeds) {
            if (i++ % 100 == 0) {
                System.out.printf("%.2f%%\n", (double) i / partialSeeds.size() * 100);
            }

            int worldSeed = partialSeed.getSeed() - subtrahend;

            ChunkGeneratorOverworld chunkGen = SeedFindingUtils.createFakeChunkGen(worldSeed);
            SwampHutStructure structure = (SwampHutStructure) Feature.SWAMP_HUT;

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

            seeds.add(new PartialSeed(worldSeed, tickingRange));
        }

        return seeds;
    }

    private static int minUnsigned(int a, int b) {
        return (int) Math.min(Integer.toUnsignedLong(a), Integer.toUnsignedLong(b));
    }

    private static int maxUnsigned(int a, int b) {
        return (int) Math.max(Integer.toUnsignedLong(a), Integer.toUnsignedLong(b));
    }

    private static class TriHut {
        private PartialSeed partialSeed;
        private int regionX;
        private int regionZ;

        public TriHut(PartialSeed partialSeed, int regionX, int regionZ) {
            this.partialSeed = partialSeed;
            this.regionX = regionX;
            this.regionZ = regionZ;
        }
    }

}
