package net.earthcomputer.bedrockified;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

import static net.earthcomputer.bedrockified.BedrockAddOceanTemperatureLayer.*;

public enum BedrockAddOceanEdgeLayer implements ICastleTransformer {
    INSTANCE;

    @Override
    public int apply(IContext context, int north, int east, int south, int west, int middle) {
        // Make sure no warm ocean is next to frozen ocean
        if (middle == OCEAN_WARM
            && (north == OCEAN_FROZEN
                || east == OCEAN_FROZEN
                || west == OCEAN_FROZEN
                || south == OCEAN_FROZEN)) {
            return OCEAN;
        } else if (middle == OCEAN_FROZEN
            && (north == OCEAN_WARM
                || east == OCEAN_WARM
                || west == OCEAN_WARM
                || south == OCEAN_WARM)) {
            return OCEAN;
        } else {
            return middle;
        }
    }
}
