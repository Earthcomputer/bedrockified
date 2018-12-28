package net.earthcomputer.bedrockified.seedsearch;

import net.earthcomputer.bedrockified.BedrockRandom;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class TechRockMultiMonumentPrecomputer {

    private static int prevMonument1X;
    private static int prevMonument1Z;
    private static int prevMonument2X;
    private static int prevMonument2Z;

    private static int[] precomputedRange = {
              0,  0,  0,  0,  0,  1,  2,  3,  3,  3,  2,  1,  0,  0,  0,  0,  0,
              0,  0,  0,  0,  1,  3,  5,  7,  8,  7,  6,  4,  1,  0,  0,  0,  0,
              0,  0,  0,  1,  3,  6,  9, 12, 13, 12,  9,  6,  3,  1,  0,  0,  0,
              0,  0,  1,  3,  6, 10, 14, 17, 18, 17, 14, 10,  6,  3,  1,  0,  0,
              0,  1,  3,  6, 10, 15, 19, 22, 23, 22, 19, 15, 10,  6,  3,  1,  0,
              1,  3,  6, 10, 15, 19, 22, 24, 25, 24, 22, 19, 15, 10,  6,  3,  1,
              2,  5,  9, 14, 19, 22, 24, 25, 25, 25, 24, 22, 19, 14,  9,  5,  2,
              3,  7, 12, 17, 22, 24, 25, 25, 25, 25, 25, 24, 22, 17, 12,  7,  3,
              3,  8, 13, 18, 23, 25, 25, 25, 25, 25, 25, 25, 23, 18, 13,  8,  3,
              3,  7, 12, 17, 22, 24, 25, 25, 25, 25, 25, 24, 22, 17, 12,  7,  3,
              2,  5,  9, 14, 19, 22, 24, 25, 25, 25, 24, 22, 19, 14,  9,  5,  2,
              1,  3,  6, 10, 15, 19, 22, 24, 25, 24, 22, 19, 15, 10,  6,  3,  1,
              0,  1,  3,  6, 10, 15, 19, 22, 23, 22, 19, 15, 10,  6,  3,  1,  0,
              0,  0,  1,  3,  6, 10, 14, 17, 18, 17, 14, 10,  6,  3,  1,  0,  0,
              0,  0,  0,  1,  3,  6,  9, 12, 13, 12,  9,  6,  3,  1,  0,  0,  0,
              0,  0,  0,  0,  1,  3,  5,  7,  8,  7,  6,  4,  1,  0,  0,  0,  0,
              0,  0,  0,  0,  0,  1,  2,  3,  3,  3,  2,  1,  0,  0,  0,  0,  0
    };

    private static int[] inRangeArray = new int[75 * 75];

    public static void main(String[] args) throws IOException {
        BedrockRandom rand = new BedrockRandom();

        rand.setSeed(0);
        prevMonument1X = (rand.nextInt(27) + rand.nextInt(27)) / 2;
        prevMonument1Z = (rand.nextInt(27) + rand.nextInt(27)) / 2;
        rand.setSeed(341873128712L);
        prevMonument2X = 32 + (rand.nextInt(27) + rand.nextInt(27)) / 2;
        prevMonument2Z = (rand.nextInt(27) + rand.nextInt(27)) / 2;

        int partialSeed = 0;
        PrintWriter pw = new PrintWriter(new FileWriter(new File("multi_monument_partial_seeds.txt")));

        int i = 0;
        do {
            if (i % 10000 == 0)
                System.out.printf("%.3f%%\n", Integer.toUnsignedLong(i) / (double) (1L << 32) * 100);
            MultiMonumentInfo monumentInfo = getMonumentInfo(rand, partialSeed);
            if (monumentInfo != null)
                pw.println(monumentInfo.getPartialSeed() + " " + monumentInfo.isTriHutRange4() + " " + monumentInfo.getSpawningChunksRange6());
            partialSeed += 132897987541L;
            i++;
        } while (partialSeed != 0);

        System.out.println("Done.");

        pw.flush();
        pw.close();
    }

    private static MultiMonumentInfo getMonumentInfo(BedrockRandom rand, int partialSeed) {
        int m1X, m1Z, m2X, m2Z, m3X, m3Z, m4X, m4Z;

        m1X = prevMonument1X;
        m1Z = prevMonument1Z;

        m2X = prevMonument2X;
        m2Z = prevMonument2Z;

        rand.setSeed(partialSeed + 132897987541L);
        prevMonument1X = (rand.nextInt(27) + rand.nextInt(27)) / 2;
        prevMonument1Z = (rand.nextInt(27) + rand.nextInt(27)) / 2;
        m3X = prevMonument1X;
        m3Z = 32 + prevMonument1Z;

        rand.setSeed(partialSeed + 341873128712L + 132897987541L);
        prevMonument2X = 32 + (rand.nextInt(27) + rand.nextInt(27)) / 2;
        prevMonument2Z = (rand.nextInt(27) + rand.nextInt(27)) / 2;
        m4X = prevMonument2X;
        m4Z = 32 + prevMonument2Z;

        Arrays.fill(inRangeArray, 0);

        for (int y = 0; y < 17; y++)
            System.arraycopy(precomputedRange, y * 17, inRangeArray, (m1Z + y) * 75 + m1X, 17);

        for (int y = 0; y < 17; y++) {
            int srcInd = y * 17;
            int dstInd = (m2Z + y) * 75 + m2X;
            for (int x = 0; x < 17; x++)
                inRangeArray[dstInd + x] += precomputedRange[srcInd + x];
        }

        int maxInRange = 0;

        for (int y = 0; y < 17; y++) {
            int srcInd = y * 17;
            int dstInd = (m3Z + y) * 75 + m3X;
            for (int x = 0; x < 17; x++)
                maxInRange = Math.max(maxInRange, inRangeArray[dstInd + x] += precomputedRange[srcInd + x]);
        }

        for (int y = 0; y < 17; y++) {
            int srcInd = y * 17;
            int dstInd = (m4Z + y) * 75 + m4X;
            for (int x = 0; x < 17; x++)
                maxInRange = Math.max(maxInRange, inRangeArray[dstInd + x] + precomputedRange[srcInd + x]);
        }

        if (maxInRange <= 50)
            return null;

        boolean isTriHutRange4 =
                (m1X == 26 && m2X == 26 ? 1 : 0)
                + (m2X == 32 && m2Z == 26 ? 1 : 0)
                + (m3X == 26 && m3Z == 32 ? 1 : 0)
                + (m4X == 32 && m4Z == 32 ? 1 : 0)
                >= 3;

        return new MultiMonumentInfo(partialSeed, isTriHutRange4, maxInRange);
    }

}
