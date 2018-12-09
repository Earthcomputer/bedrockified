package net.earthcomputer.bedrockified.seedsearch;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.particles.IParticleData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedDataStorage;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Predicate;

public class FakeWorld implements IWorld {

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
