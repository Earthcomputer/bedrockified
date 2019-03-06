package net.earthcomputer.bedrockified.seedsearch;

import net.earthcomputer.bedrockified.BedrockRandom;

public class MineshaftDiagonal {

    public static void main(String[] args) {
        BedrockRandom rand = new BedrockRandom();
        int seed = 0;
        do {
            rand.setSeed(seed);
            rand.nextInt();
            float f = rand.nextFloat();
            int minDistance = rand.nextInt(80);
            if (f < 0.004 && minDistance == 0)
                System.out.println(seed);
            seed++;
        } while (seed != 0);
    }

}
