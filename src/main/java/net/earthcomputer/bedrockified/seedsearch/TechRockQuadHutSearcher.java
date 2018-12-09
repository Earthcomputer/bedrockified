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

public class TechRockQuadHutSearcher {

    private static List<PartialSeed> partialSeeds;

    public static void main(String[] args) throws IOException {
        Bootstrap.register();

        partialSeeds = SeedFindingUtils.readPartialSeeds("witch_hut_partial_seeds.txt");
        for (int radius = 0;; radius++) {
            List<QuadHut> quadHuts = new ArrayList<>();

            for (int x = -radius; x < radius; x++) {
                int x_f = x - 1;
                int z = radius - Math.abs(x) - 1;
                getAllQuadHutsAt(x_f, z).forEach(seed -> quadHuts.add(new QuadHut(seed, x_f, z)));
            }
            for (int x = radius; x > -radius; x--) {
                int x_f = x - 1;
                int z = Math.abs(x) - radius - 1;
                getAllQuadHutsAt(x_f, z).forEach(seed -> quadHuts.add(new QuadHut(seed, x_f, z)));
            }

            if (!quadHuts.isEmpty()) {
                for (QuadHut quadHut : quadHuts) {
                    System.out.println(quadHut.partialSeed.getTickingRange() + " " + quadHut.partialSeed.getSeed() + " " + quadHut.regionX + " " + quadHut.regionZ);
                }
                break;
            }
        }
    }

    private static List<PartialSeed> getAllQuadHutsAt(int x, int z) {
        int subtrahend = (int) (341873128712L * x + 132897987541L * z + 14357617);

        Random rand = new SharedSeedRandom();

        List<PartialSeed> seeds = new ArrayList<>();

        for (PartialSeed partialSeed : partialSeeds) {
            int worldSeed = partialSeed.getSeed() - subtrahend;

            ChunkGeneratorOverworld chunkGen = SeedFindingUtils.createFakeChunkGen(worldSeed);
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

            seeds.add(new PartialSeed(worldSeed, partialSeed.getTickingRange()));
        }

        return seeds;
    }

    private static class QuadHut {
        private PartialSeed partialSeed;
        private int regionX;
        private int regionZ;

        public QuadHut(PartialSeed partialSeed, int regionX, int regionZ) {
            this.partialSeed = partialSeed;
            this.regionX = regionX;
            this.regionZ = regionZ;
        }
    }

}
