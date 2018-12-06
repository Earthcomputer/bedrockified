package net.earthcomputer.bedrockified;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;

import javax.annotation.Nullable;

public class BedrockSpawnPoint {

    public static BlockPos findSpawnPosition(WorldSettings worldSettings, BiomeProvider biomeProvider) {
        if (worldSettings.getTerrainType() == WorldType.FLAT) {
            return new BlockPos(0, Short.MAX_VALUE, 0);
        }

        int x = 0;

        BlockPos spawnPos;

        do {
            spawnPos = findValidSpawnPosition(biomeProvider, x, 0, 10, 4);
            x += 40;
        } while (spawnPos == null);

        return spawnPos;
    }

    @Nullable
    private static BlockPos findValidSpawnPosition(BiomeProvider biomeProvider, int x, int z, int size, int scale) {
        Biome[] biomes = biomeProvider.getBiomes(x / scale, z / scale, size, size);

        assert size <= 10; // For the following assumption to work.
        /*
         * The following assumption is true for the Linux BDS.
         * TODO: more research is needed for other platforms.
         *
         * Here we ignore the case where dz = 0 and dz = size - 1, which would lead to out of bounds
         * access north and south of the area.
         *
         * For the case where dz = 0, we have to be careful this is an okay assumption since this is
         * the first row the game checks. When we're checking north, we're going size (10) ints left
         * of the current index, which may be up to 40 bytes before the beginning of the array. Since
         * the array is stored on the stack, these out of bounds values are very predictable and are
         * all local variables of this routine. All of them in this range are also pointers, except
         * the variable dz itself (which happens to be in this range). Since we know that dz = 0, when
         * we interpret this as a biome ID we get ocean, which is an invalid spawn biome. For the
         * pointers, the chances are negligible that the lower or upper bits hold a valid biome ID.
         *
         * For the case where dz = size - 1, first notice the fact that the chance of a given tile
         * being a valid biome is not independent of the chance that its neighbors are valid biomes.
         * That is, if there is a valid point on the bottom row, the probability is high that there
         * was also a valid point on the row above which would have been found first. Otherwise, the
         * out of bounds data on this end of the array is tricky to predict, as it depends on the data
         * that was there before, put there by whatever routine took up this part of the stack before.
         * But we can again think about probability, and the fact that it is very unlikely for
         * arbitrary data to be a valid biome ID. The most likely counter example is the value 1,
         * which corresponds to both the boolean true and the biome ID plains, which is a valid spawn
         * biome.
         *
         * This is why I do not claim this to be a perfect emulation of the Bedrock edition algorithm,
         * but it should be very close.
         */
        for (int dz = 1; dz < size - 1; dz++) {
            for (int dx = 0; dx < size; dx++) {
                if (isValidSpawn(biomeProvider, biomes[dz * size + dx])
                    && isValidSpawn(biomeProvider, biomes[dz * size + (dx - 1)])
                    && isValidSpawn(biomeProvider, biomes[dz * size + (dx + 1)])
                    && isValidSpawn(biomeProvider, biomes[(dz - 1) * size + dx])
                    && isValidSpawn(biomeProvider, biomes[(dz + 1) * size + dx])) {
                    return new BlockPos(scale * dx + x, Short.MAX_VALUE, scale * dz + z);
                }
            }
        }

        return null;
    }

    private static boolean isValidSpawn(BiomeProvider biomeProvider, Biome biome) {
        return biomeProvider.getBiomesToSpawnIn().contains(biome);
    }

}
