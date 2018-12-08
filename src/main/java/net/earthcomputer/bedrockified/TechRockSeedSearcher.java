package net.earthcomputer.bedrockified;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TechRockSeedSearcher {

    public static void main(String[] args) throws IOException {
        BedrockRandom rand = new BedrockRandom();

        int partialSeed = 0;
        List<Integer>[] partialSeedsByTickingRange = new List[7];
        for (int i = 0; i < partialSeedsByTickingRange.length; i++)
            partialSeedsByTickingRange[i] = new ArrayList<>();

        do {
            if (partialSeed % 10000 == 0)
                System.out.printf("%.3f%%\n", partialSeed / (double) (1L << 32) * 100);
            int tickingRange = tickingRangeRequired(rand, partialSeed);
            if (tickingRange < partialSeedsByTickingRange.length)
                partialSeedsByTickingRange[tickingRange].add(partialSeed);
            partialSeed++;
        } while (partialSeed != 0);

        PrintWriter pw = new PrintWriter(new FileWriter(new File("witch_hut_partial_seeds.txt")));
        System.out.println("Done.");
        for (int tickingRange = 0; tickingRange < partialSeedsByTickingRange.length; tickingRange++) {
            List<Integer> seeds = partialSeedsByTickingRange[tickingRange];
            if (!seeds.isEmpty()) {
                pw.println("TICKING RANGE " + tickingRange + ":");
                seeds.forEach(seed -> pw.println("  " + seed));
            }
        }

        pw.flush();
        pw.close();
    }

    private static int tickingRangeRequired(BedrockRandom rand, int partialSeed) {
        int minHutX = Integer.MAX_VALUE;
        int minHutZ = Integer.MAX_VALUE;
        int maxHutX = Integer.MIN_VALUE;
        int maxHutZ = Integer.MIN_VALUE;
        int hutX, hutZ;

        rand.setSeed(partialSeed);
        hutX = rand.nextInt(24);
        hutZ = rand.nextInt(24);
        if (hutX < minHutX)
            minHutX = hutX;
        if (hutX > maxHutX)
            maxHutX = hutX;
        if (hutZ < minHutZ)
            minHutZ = hutZ;
        if (hutZ > maxHutZ)
            maxHutZ = hutZ;

        rand.setSeed(partialSeed + 341873128712L);
        hutX = 32 + rand.nextInt(24);
        hutZ = rand.nextInt(24);
        if (hutX < minHutX)
            minHutX = hutX;
        if (hutX > maxHutX)
            maxHutX = hutX;
        if (hutZ < minHutZ)
            minHutZ = hutZ;
        if (hutZ > maxHutZ)
            maxHutZ = hutZ;

        rand.setSeed(partialSeed + 132897987541L);
        hutX = rand.nextInt(24);
        hutZ = 32 + rand.nextInt(24);
        if (hutX < minHutX)
            minHutX = hutX;
        if (hutX > maxHutX)
            maxHutX = hutX;
        if (hutZ < minHutZ)
            minHutZ = hutZ;
        if (hutZ > maxHutZ)
            maxHutZ = hutZ;

        rand.setSeed(partialSeed + 341873128712L + 132897987541L);
        hutX = 32 + rand.nextInt(24);
        hutZ = 32 + rand.nextInt(24);
        if (hutX < minHutX)
            minHutX = hutX;
        if (hutX > maxHutX)
            maxHutX = hutX;
        if (hutZ < minHutZ)
            minHutZ = hutZ;
        if (hutZ > maxHutZ)
            maxHutZ = hutZ;

        return (Math.max(maxHutX - minHutX, maxHutZ - minHutZ) + 1) / 2;
    }

}
