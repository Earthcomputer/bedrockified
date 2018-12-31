package net.earthcomputer.bedrockified.seedsearch;

public class MultiMonumentInfo {

    private final int partialSeed;
    private final boolean isDualHutRange4;
    private final int spawningChunksRange6;

    public MultiMonumentInfo(int partialSeed, boolean isDualHutRange4, int spawningChunksRange6) {
        this.partialSeed = partialSeed;
        this.isDualHutRange4 = isDualHutRange4;
        this.spawningChunksRange6 = spawningChunksRange6;
    }

    public int getPartialSeed() {
        return partialSeed;
    }

    public boolean isDualHutRange4() {
        return isDualHutRange4;
    }

    public int getSpawningChunksRange6() {
        return spawningChunksRange6;
    }
}
