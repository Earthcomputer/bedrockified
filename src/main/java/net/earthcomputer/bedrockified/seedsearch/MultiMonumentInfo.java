package net.earthcomputer.bedrockified.seedsearch;

public class MultiMonumentInfo {

    private final int partialSeed;
    private final boolean isTriHutRange4;
    private final int spawningChunksRange6;

    public MultiMonumentInfo(int partialSeed, boolean isTriHutRange4, int spawningChunksRange6) {
        this.partialSeed = partialSeed;
        this.isTriHutRange4 = isTriHutRange4;
        this.spawningChunksRange6 = spawningChunksRange6;
    }

    public int getPartialSeed() {
        return partialSeed;
    }

    public boolean isTriHutRange4() {
        return isTriHutRange4;
    }

    public int getSpawningChunksRange6() {
        return spawningChunksRange6;
    }
}
