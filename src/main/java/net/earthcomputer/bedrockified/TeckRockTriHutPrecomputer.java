package net.earthcomputer.bedrockified;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class TeckRockTriHutPrecomputer {

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
        PrintWriter pw = new PrintWriter(new FileWriter(new File("witch_hut_tri_partial_seeds.txt")));

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
        int hut1X, hut1Z, hut2X, hut2Z, hut3X, hut3Z, hut4X, hut4Z;

        hut1X = prevHut1X;
        hut1Z = prevHut1Z;

        hut2X = prevHut2X;
        hut2Z = prevHut2Z;

        rand.setSeed(partialSeed + 132897987541L);
        prevHut1X = rand.nextInt(24);
        prevHut1Z = rand.nextInt(24);
        hut3X = prevHut1X;
        hut3Z = 32 + prevHut1Z;

        rand.setSeed(partialSeed + 341873128712L + 132897987541L);
        prevHut2X = 32 + rand.nextInt(24);
        prevHut2Z = rand.nextInt(24);
        hut4X = prevHut2X;
        hut4Z = 32 + prevHut2Z;

        return (Math.min(
        Math.min(
                Math.max(Math.max(hut2X, hut4X) - hut3X, Math.max(hut3Z, hut4Z) - hut2Z), // excl. hut 1
                Math.max(hut4X - Math.min(hut1X, hut3X), Math.max(hut3Z, hut4Z) - hut1Z) // excl. hut 2
        ),
        Math.min(
                Math.max(Math.max(hut2X, hut4X) - hut1X, hut4Z - Math.min(hut1Z, hut2Z)), // excl. hut 3
                Math.max(hut2X - Math.min(hut1X, hut3X), hut3Z - Math.min(hut1Z, hut2Z)) // excl. hut 4
        )
        ) + 1) / 2;
    }

}
