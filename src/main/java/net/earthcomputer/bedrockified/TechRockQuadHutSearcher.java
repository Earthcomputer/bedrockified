package net.earthcomputer.bedrockified;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Bootstrap;
import net.minecraft.particles.IParticleData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProviderSettings;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.SwampHutStructure;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedDataStorage;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class TechRockQuadHutSearcher {

    private static List<PartialSeed> partialSeeds = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        Bootstrap.register();

        readPartialSeeds();
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
                    System.out.println(quadHut.partialSeed.tickingRange + " " + quadHut.partialSeed.seed + " " + quadHut.regionX + " " + quadHut.regionZ);
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
            int worldSeed = partialSeed.seed - subtrahend;

            WorldInfo worldInfo = new WorldInfo();
            worldInfo.randomSeed = Integer.toUnsignedLong(worldSeed);
            IWorld world = new FakeWorld(worldInfo);
            OverworldGenSettings genSettings = new OverworldGenSettings();
            OverworldBiomeProviderSettings biomeProviderSettings = new OverworldBiomeProviderSettings().setWorldInfo(worldInfo).setGeneratorSettings(genSettings);
            BiomeProvider biomeProvider = new OverworldBiomeProvider(biomeProviderSettings);
            ChunkGeneratorOverworld chunkGen = new ChunkGeneratorOverworld(world, biomeProvider, genSettings);
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

            seeds.add(new PartialSeed(worldSeed, partialSeed.tickingRange));
        }

        return seeds;
    }

    private static void readPartialSeeds() throws IOException {
        for (String line : Files.readAllLines(Paths.get("witch_hut_partial_seeds.txt"))) {
            if (line.isEmpty())
                continue;
            String[] parts = line.split(" ");
            partialSeeds.add(new PartialSeed(Integer.parseInt(parts[1]), Integer.parseInt(parts[0])));
        }
    }

    private static class PartialSeed {
        private int seed;
        private int tickingRange;

        public PartialSeed(int seed, int tickingRange) {
            this.seed = seed;
            this.tickingRange = tickingRange;
        }

        @Override
        public String toString() {
            return "PartialSeed{" +
                    "seed=" + seed +
                    ", tickingRange=" + tickingRange +
                    '}';
        }
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

    private static class FakeWorld implements IWorld {

        private WorldInfo worldInfo;

        public FakeWorld(WorldInfo worldInfo) {
            this.worldInfo = worldInfo;
        }

        @Override
        public long getSeed() {
            return worldInfo.getSeed();
        }

        @Override
        public ITickList<Block> getPendingBlockTicks() {
            return null;
        }

        @Override
        public ITickList<Fluid> getPendingFluidTicks() {
            return null;
        }

        @Override
        public IChunk getChunk(int chunkX, int chunkZ) {
            return null;
        }

        @Override
        public World getWorld() {
            return null;
        }

        @Override
        public WorldInfo getWorldInfo() {
            return worldInfo;
        }

        @Override
        public DifficultyInstance getDifficultyForLocation(BlockPos pos) {
            return null;
        }

        @Override
        public IChunkProvider getChunkProvider() {
            return null;
        }

        @Override
        public ISaveHandler getSaveHandler() {
            return null;
        }

        @Override
        public Random getRandom() {
            return null;
        }

        @Override
        public void notifyNeighbors(BlockPos pos, Block blockIn) {

        }

        @Override
        public BlockPos getSpawnPoint() {
            return null;
        }

        @Override
        public void playSound(@Nullable EntityPlayer player, BlockPos pos, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {

        }

        @Override
        public void addParticle(IParticleData particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {

        }

        @Nullable
        @Override
        public WorldSavedDataStorage getSavedDataStorage() {
            return null;
        }

        @Override
        public boolean isAirBlock(BlockPos pos) {
            return false;
        }

        @Override
        public Biome getBiome(BlockPos pos) {
            return null;
        }

        @Override
        public int getLightFor(EnumLightType type, BlockPos pos) {
            return 0;
        }

        @Override
        public int getLightSubtracted(BlockPos pos, int amount) {
            return 0;
        }

        @Override
        public boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return false;
        }

        @Override
        public boolean canSeeSky(BlockPos pos) {
            return false;
        }

        @Override
        public int getHeight(Heightmap.Type heightmapType, int x, int z) {
            return 0;
        }

        @Nullable
        @Override
        public EntityPlayer getClosestPlayer(double x, double y, double z, double distance, Predicate<Entity> predicate) {
            return null;
        }

        @Override
        public int getSkylightSubtracted() {
            return 0;
        }

        @Override
        public WorldBorder getWorldBorder() {
            return null;
        }

        @Override
        public boolean checkNoEntityCollision(@Nullable Entity entityIn, VoxelShape shape) {
            return false;
        }

        @Override
        public int getStrongPower(BlockPos pos, EnumFacing direction) {
            return 0;
        }

        @Override
        public boolean isRemote() {
            return false;
        }

        @Override
        public int getSeaLevel() {
            return 0;
        }

        @Override
        public Dimension getDimension() {
            return null;
        }

        @Nullable
        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return null;
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            return null;
        }

        @Override
        public IFluidState getFluidState(BlockPos pos) {
            return null;
        }

        @Override
        public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
            return false;
        }

        @Override
        public boolean spawnEntity(Entity entityIn) {
            return false;
        }

        @Override
        public boolean removeBlock(BlockPos pos) {
            return false;
        }

        @Override
        public void setLightFor(EnumLightType type, BlockPos pos, int lightValue) {

        }

        @Override
        public boolean destroyBlock(BlockPos pos, boolean dropBlock) {
            return false;
        }
    }

}
