package net.earthcomputer.bedrockified;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TechRockSeedSearcher {

    private static int prevHut1X;
    private static int prevHut1Z;
    private static int prevHut2X;
    private static int prevHut2Z;

    public static void main(String[] args) throws IOException {
        BedrockRandom rand = new BedrockRandom();

        rand.setSeed(0);
        prevHut1X = rand.nextInt(24);
        prevHut1Z = rand.nextInt(24);
        rand.setSeed(341873128712L);
        prevHut2X = 32 + rand.nextInt(24);
        prevHut2Z = rand.nextInt(24);

        int partialSeed = 0;
        PrintWriter pw = new PrintWriter(new FileWriter(new File("witch_hut_partial_seeds.txt")));

        int i = 0;
        do {
            if (i % 10000 == 0)
                System.out.printf("%.3f%%\n", Integer.toUnsignedLong(i) / (double) (1L << 32) * 100);
            int tickingRange = tickingRangeRequired(rand, partialSeed);
            if (tickingRange <= 6)
                pw.println(tickingRange + " " + partialSeed);
            partialSeed += 132897987541L;
            i++;
        } while (partialSeed != 0);

        System.out.println("Done.");

        pw.flush();
        pw.close();
    }

    private static int tickingRangeRequired(BedrockRandom rand, int partialSeed) {
        int minHutX = Integer.MAX_VALUE;
        int minHutZ = Integer.MAX_VALUE;
        int maxHutX = Integer.MIN_VALUE;
        int maxHutZ = Integer.MIN_VALUE;
        int hutX, hutZ;

        hutX = prevHut1X;
        hutZ = prevHut1Z;
        if (hutX < minHutX)
            minHutX = hutX;
        if (hutX > maxHutX)
            maxHutX = hutX;
        if (hutZ < minHutZ)
            minHutZ = hutZ;
        if (hutZ > maxHutZ)
            maxHutZ = hutZ;

        hutX = prevHut2X;
        hutZ = prevHut2Z;
        if (hutX < minHutX)
            minHutX = hutX;
        if (hutX > maxHutX)
            maxHutX = hutX;
        if (hutZ < minHutZ)
            minHutZ = hutZ;
        if (hutZ > maxHutZ)
            maxHutZ = hutZ;

        rand.setSeed(partialSeed + 132897987541L);
        prevHut1X = rand.nextInt(24);
        prevHut1Z = rand.nextInt(24);
        hutX = prevHut1X;
        hutZ = 32 + prevHut1Z;
        if (hutX < minHutX)
            minHutX = hutX;
        if (hutX > maxHutX)
            maxHutX = hutX;
        if (hutZ < minHutZ)
            minHutZ = hutZ;
        if (hutZ > maxHutZ)
            maxHutZ = hutZ;

        rand.setSeed(partialSeed + 341873128712L + 132897987541L);
        prevHut2X = 32 + rand.nextInt(24);
        prevHut2Z = rand.nextInt(24);
        hutX = prevHut2X;
        hutZ = 32 + prevHut2Z;
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
