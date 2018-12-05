package net.earthcomputer.bedrockified;

import net.minecraft.init.Biomes;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

public enum BedrockAddOceanTemperatureLayer implements IC0Transformer {
    INSTANCE;

    static final int OCEAN = IRegistry.BIOME.getId(Biomes.OCEAN);
    static final int OCEAN_LUKEWARM = IRegistry.BIOME.getId(Biomes.LUKEWARM_OCEAN);
    static final int OCEAN_WARM = IRegistry.BIOME.getId(Biomes.WARM_OCEAN);
    static final int OCEAN_COLD = IRegistry.BIOME.getId(Biomes.COLD_OCEAN);
    static final int OCEAN_FROZEN = IRegistry.BIOME.getId(Biomes.FROZEN_OCEAN);

    @Override
    public int apply(IContext context, int value) {
        float f = context.random(100) / 100f;
        if (f < 0.075f) {
            return OCEAN_WARM;
        } else if (f < 0.4f) {
            return OCEAN_LUKEWARM;
        } else if (f < 0.675f) {
            return OCEAN;
        } else if (f < 0.95f) {
            return OCEAN_COLD;
        } else {
            return OCEAN_FROZEN;
        }
    }
}
