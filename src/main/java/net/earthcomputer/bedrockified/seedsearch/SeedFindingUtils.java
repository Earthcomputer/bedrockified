package net.earthcomputer.bedrockified.seedsearch;

import net.minecraft.world.IWorld;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProviderSettings;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.storage.WorldInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SeedFindingUtils {

    public static List<PartialSeed> readPartialSeeds(String file) throws IOException {
        List<PartialSeed> partialSeeds = new ArrayList<>();
        for (String line : Files.readAllLines(Paths.get(file))) {
            if (line.isEmpty())
                continue;
            String[] parts = line.split(" ");
            partialSeeds.add(new PartialSeed(Integer.parseInt(parts[1]), Integer.parseInt(parts[0])));
        }
        return partialSeeds;
    }

    public static ChunkGeneratorOverworld createFakeChunkGen(int worldSeed) {
        WorldInfo worldInfo = new WorldInfo();
        worldInfo.randomSeed = Integer.toUnsignedLong(worldSeed);
        IWorld world = new FakeWorld(worldInfo);
        OverworldGenSettings genSettings = new OverworldGenSettings();
        OverworldBiomeProviderSettings biomeProviderSettings = new OverworldBiomeProviderSettings().setWorldInfo(worldInfo).setGeneratorSettings(genSettings);
        BiomeProvider biomeProvider = new OverworldBiomeProvider(biomeProviderSettings);
        return new ChunkGeneratorOverworld(world, biomeProvider, genSettings);
    }

}
