package net.earthcomputer.bedrockified;

import net.minecraft.util.math.Vec3d;

import java.util.Random;

/**
 * An RNG implementation equivalent to that of Minecraft: Bedrock Edition.
 *
 * This is a version of the m19937 mersenne twister algorithm.
 * This version contains the improved initialization from http://www.math.sci.hiroshima-u.ac.jp/~m-mat/MT/MT2002/CODES/mt19937ar.c
 * It also contains an optimization to only generate some of the MT array when the RNG is first initialized.
 *
 * @author Earthcomputer
 */
public class BedrockRandom extends Random {

    private static final int N = 624;
    private static final int M = 327;
    private static final int MATRIX_A = 0x9908b0df;
    private static final int UPPER_MASK = 0x80000000;
    private static final int LOWER_MASK = 0x7fffffff;
    private static final int[] MAG_01 = {0, MATRIX_A};
    private static final double TWO_POW_M32 = 1.0 / (1L << 32);

    private int seed; // (DWORD*) this + 0
    private int[] mt = new int[N]; // (DWORD*) this + 1
    private int mti; // (DWORD*) this + 625
    private boolean haveNextNextGaussian; // (DWORD*) this + 626
    private float nextNextGaussian; // (float*) this + 627
    private int mtiFast; // (DWORD*) this + 628

    private boolean valid = false; // Hackfix for setSeed being called too early in the superconstructor


    public BedrockRandom() {
        this(new Random().nextInt());
    }

    public BedrockRandom(int seed) {
        valid = true;
        _setSeed(seed);
    }


    // ===== PUBLIC INTERFACE METHODS ===== //

    public int getSeed() {
        return seed;
    }

    /**
     * This overload exists to override the method in the base class.
     * Although it accepts a 64-bit long, it will be cast to a 32-bit int.
     */
    @Override
    public void setSeed(long seed) {
        if (valid) // Hackfix for this being called too early in the superconstructor
            setSeed((int) seed);
    }

    public void setSeed(int seed) {
        _setSeed(seed);
    }

    /**
     * Generates a non-negative signed integer
     */
    @Override
    public int nextInt() {
        return _genRandInt32() >>> 1;
    }

    @Override
    public int nextInt(int bound) {
        if (bound > 0)
            return Math.floorMod(_genRandInt32(), bound);
        else
            return 0;
    }

    /**
     * Generates a random integer k such that a <= k < b
     */
    public int nextInt(int a, int b) {
        if (a < b)
            return a + nextInt(b - a);
        else
            return a;
    }

    /**
     * Generates a random integer k such that a <= k <= b
     */
    public int nextIntInclusive(int a, int b) {
        return nextInt(a, b + 1);
    }

    /**
     * Generates a random long k such that 0 <= k < 2^32, mti.e. a random unsigned int
     */
    public long nextUnsignedInt() {
        return Integer.toUnsignedLong(_genRandInt32());
    }

    /**
     * Generates a random short k such that 0 <= k < 256, mti.e. a random unsigned byte
     */
    public short nextUnsignedChar() {
        return (short) (_genRandInt32() & 0xff);
    }

    @Override
    public boolean nextBoolean() {
        return (_genRandInt32() & 0x8000000) != 0;
    }

    /**
     * Generates a uniform random float k such that 0 <= k < 1
     */
    @Override
    public float nextFloat() {
        return (float) _genRandReal2();
    }

    /**
     * Generates a uniform random float k such that 0 <= k < bound
     */
    public float nextFloat(float bound) {
        return nextFloat() * bound;
    }

    /**
     * Generates a uniform random float k such that a <= k < b
     */
    public float nextFloat(float a, float b) {
        return a + (nextFloat() * (b - a));
    }

    /**
     * Generates a uniform random double k such that 0 <= k < 1.
     *
     * Note that unlike the Java RNG, there are only 2^32 possible return values for this function
     */
    @Override
    public double nextDouble() {
        return _genRandReal2();
    }

    /**
     * Generates a Gaussian distributed float with mean 0 and standard deviation 1.
     *
     * This method returns a double to override the method in the base class, but it is a
     * float that's generated, and it can be safely cast back to float without loss of precision.
     */
    @Override
    public double nextGaussian() {
        if (haveNextNextGaussian) {
            haveNextNextGaussian = false;
            return nextNextGaussian;
        }

        float v1, v2, s;
        do {
            v1 = nextFloat() * 2 - 1;
            v2 = nextFloat() * 2 - 1;
            s = v1 * v1 + v2 * v2;
        } while (s == 0 || s > 1);

        float multiplier = (float) Math.sqrt(-2 * (float) Math.log(s) / s);
        nextNextGaussian = v2 * multiplier;
        haveNextNextGaussian = true;
        return v1 * multiplier;
    }

    /**
     * Returns a triangularly distributed int k with mode 0 such that -bound < k < bound.
     *
     * Note that the method name, used in the Bedrock code, incorrectly describes this as a Gaussian distribution.
     */
    public int nextGaussianInt(int bound) {
        return nextInt(bound) - nextInt(bound);
    }

    /**
     * Returns a triangularly distributed float k with mode 0 such that -1 < k < 1.
     *
     * Note that the method name, used in the Bedrock code, incorrectly describes this as a Gaussian distribution.
     */
    public float nextGaussianFloat() {
        return nextFloat() - nextFloat();
    }

    /**
     * Returns uniformly distributed 3d float vector v such that 0 <= v_i < 1 for each mti.
     *
     * Note that although this method returns a double vector, the precision is only float,
     * and can be safely cast back to floats without loss of precision.
     */
    public Vec3d nextVec3() {
        float x = nextFloat();
        float y = nextFloat();
        float z = nextFloat();
        return new Vec3d(x, y, z);
    }

    /**
     * Returns a float vector containing 3 Gaussian distributed floats with mean 0 and standard deviation 1.
     *
     * Note that although this method returns a double vector, the precision is only float,
     * and can be safely cast back to floats without loss of precision.
     */
    public Vec3d nextGaussianVec3() {
        float x = (float) nextGaussian();
        float y = (float) nextGaussian();
        float z = (float) nextGaussian();
        return new Vec3d(x, y, z);
    }


    // ===== m19937 MERSENNE TWISTER IMPLEMENTATION ===== //

    private void _setSeed(int seed) {
        this.seed = seed;
        this.mti = N + 1; // uninitialized
        this.haveNextNextGaussian = false;
        this.nextNextGaussian = 0;
        _initGenRandFast(seed);
    }

    private void _initGenRand(int initialValue) {
        this.mt[0] = initialValue;
        for (this.mti = 1; this.mti < N; this.mti++) {
            this.mt[mti] = 1812433253
                    * ((this.mt[this.mti - 1] >>> 30) ^ this.mt[this.mti - 1])
                    + this.mti;
        }
        this.mtiFast = N;
    }

    private void _initGenRandFast(int initialValue) {
        this.mt[0] = initialValue;
        for (this.mtiFast = 1; this.mtiFast <= M; this.mtiFast++) {
            this.mt[this.mtiFast] = 1812433253
                    * ((this.mt[this.mtiFast - 1] >>> 30) ^ this.mt[this.mtiFast - 1])
                    + this.mtiFast;
        }
        this.mti = N;
    }

    private int _genRandInt32() {
        if (this.mti == N) {
            this.mti = 0;
        } else if (this.mti > N) {
            _initGenRand(5489);
            this.mti = 0;
        }

        if (this.mti >= N - M) {
            if (this.mti >= N - 1) {
                this.mt[N - 1] = MAG_01[this.mt[0] & 1]
                        ^ ((this.mt[0] & LOWER_MASK | this.mt[N - 1] & UPPER_MASK) >>> 1)
                        ^ this.mt[M - 1];
            } else {
                this.mt[this.mti] = MAG_01[this.mt[this.mti + 1] & 1]
                        ^ ((this.mt[this.mti + 1] & LOWER_MASK | this.mt[this.mti] & UPPER_MASK) >>> 1)
                        ^ this.mt[this.mti - (N - M)];
            }
        } else {
            this.mt[this.mti] = MAG_01[this.mt[this.mti + 1] & 1]
                    ^ ((this.mt[this.mti + 1] & LOWER_MASK | this.mt[this.mti] & UPPER_MASK) >>> 1)
                    ^ this.mt[this.mti + M];

            if (this.mtiFast < N) {
                this.mt[this.mtiFast] = 1812433253
                        * ((this.mt[this.mtiFast - 1] >>> 30) ^ this.mt[this.mtiFast - 1])
                        + this.mtiFast;
                this.mtiFast++;
            }
        }

        int ret = this.mt[this.mti++];
        ret = ((ret ^ (ret >>> 11)) << 7) & 0x9d2c5680 ^ ret ^ (ret >>> 11);
        ret = (ret << 15) & 0xefc60000 ^ ret ^ (((ret << 15) & 0xefc60000 ^ ret) >>> 18);
        return ret;
    }

    private double _genRandReal2() {
        return Integer.toUnsignedLong(_genRandInt32()) * TWO_POW_M32;
    }

}
