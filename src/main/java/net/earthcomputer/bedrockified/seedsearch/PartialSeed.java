package net.earthcomputer.bedrockified.seedsearch;

public class PartialSeed {
    private int seed;
    private int tickingRange;

    public PartialSeed(int seed, int tickingRange) {
        this.seed = seed;
        this.tickingRange = tickingRange;
    }

    @Override
    public String toString() {
        return "PartialSeed{" +
                "seed=" + seed +
                ", tickingRange=" + tickingRange +
                '}';
    }

    public int getSeed() {
        return seed;
    }

    public int getTickingRange() {
        return tickingRange;
    }
}
