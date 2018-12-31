package net.earthcomputer.bedrockified.seedsearch;

import net.minecraft.init.Bootstrap;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.OceanMonumentStructure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TechRockMultiMonumentSearcher {

    private static List<MultiMonumentInfo> monumentInfos;

    public static void main(String[] args) throws IOException {
        Bootstrap.register();

        monumentInfos = SeedFindingUtils.readMonumentInfos("multi_monument_partial_seeds.txt");
        monumentInfos = monumentInfos.stream().filter(MultiMonumentInfo::isDualHutRange4).collect(Collectors.toList());

        for (int radius = 0;; radius++) {
            List<MultiMonument> multiMonuments = new ArrayList<>();

            if (radius == 0) {
                getAllMultiMonumentsAt(-1, -1).forEach(seed -> multiMonuments.add(new MultiMonument(seed, -1, -1)));
            } else {
                for (int x = -radius; x < radius; x++) {
                    int x_f = x - 1;
                    int z = radius - Math.abs(x) - 1;
                    getAllMultiMonumentsAt(x_f, z).forEach(seed -> multiMonuments.add(new MultiMonument(seed, x_f, z)));
                }
                for (int x = radius; x > -radius; x--) {
                    int x_f = x - 1;
                    int z = Math.abs(x) - radius - 1;
                    getAllMultiMonumentsAt(x_f, z).forEach(seed -> multiMonuments.add(new MultiMonument(seed, x_f, z)));
                }
            }

            if (!multiMonuments.isEmpty()) {
                for (MultiMonument multiMonument : multiMonuments) {
                    System.out.println(multiMonument.monumentInfo.getPartialSeed() + " " + multiMonument.regionX + " " + multiMonument.regionZ);
                }
                break;
            }
        }
    }

    private static List<MultiMonumentInfo> getAllMultiMonumentsAt(int x, int z) {
        int subtrahend = (int) (341873128712L * x + 132897987541L * z + 10387313);

        Random rand = new SharedSeedRandom();

        List<MultiMonumentInfo> infos = new ArrayList<>();
        List<ChunkPos> monuments = new ArrayList<>(4);

        System.out.println("Getting all multi monuments at (" + x + ", " + z + ")");
        for (MultiMonumentInfo info : monumentInfos) {
            int worldSeed = info.getPartialSeed() - subtrahend;

            ChunkGeneratorOverworld chunkGen = SeedFindingUtils.createFakeChunkGen(worldSeed);
            OceanMonumentStructure structure = (OceanMonumentStructure) Feature.OCEAN_MONUMENT;

            monuments.clear();

            ChunkPos pos = structure.getStartPositionForPosition(chunkGen, rand, x * 32, z * 32, 0, 0);
            if (structure.hasStartAt(chunkGen, rand, pos.x, pos.z))
                monuments.add(pos);

            pos = structure.getStartPositionForPosition(chunkGen, rand, (x + 1) * 32, z * 32, 0, 0);
            if (structure.hasStartAt(chunkGen, rand, pos.x, pos.z))
                monuments.add(pos);

            pos = structure.getStartPositionForPosition(chunkGen, rand, x * 32, (z + 1) * 32, 0, 0);
            if (structure.hasStartAt(chunkGen, rand, pos.x, pos.z))
                monuments.add(pos);

            if (monuments.size() < 1)
                continue;

            pos = structure.getStartPositionForPosition(chunkGen, rand, (x + 1) * 32, (z + 1) * 32, 0, 0);
            if (structure.hasStartAt(chunkGen, rand, pos.x, pos.z))
                monuments.add(pos);

            if (monuments.size() < 2)
                continue;

            boolean isDualMonument = false;
            for (int index1 = 0; index1 < monuments.size() - 1; index1++) {
                for (int index2 = index1 + 1; index2 < monuments.size(); index2++) {
                    ChunkPos monument1 = monuments.get(index1);
                    ChunkPos monument2 = monuments.get(index2);
                    if (Math.abs(monument2.x - monument1.x) + Math.abs(monument2.z - monument1.z) <= 6) {
                        isDualMonument = true;
                    }
                }
            }

            if (isDualMonument)
                infos.add(new MultiMonumentInfo(worldSeed, info.isDualHutRange4(), info.getSpawningChunksRange6()));
        }

        return infos;
    }

    private static class MultiMonument {
        private MultiMonumentInfo monumentInfo;
        private int regionX;
        private int regionZ;

        public MultiMonument(MultiMonumentInfo monumentInfo, int regionX, int regionZ) {
            this.monumentInfo = monumentInfo;
            this.regionX = regionX;
            this.regionZ = regionZ;
        }
    }

}
