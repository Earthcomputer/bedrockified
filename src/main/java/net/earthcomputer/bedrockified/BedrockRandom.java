package net.earthcomputer.bedrockified;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class BedrockRandom extends Random {

    private int seed; // (DWORD*) this + 0
    private int[] arr = new int[624]; // (DWORD*) this + 1
    private int i; // (DWORD*) this + 625
    private boolean haveNextNextGaussian; // (DWORD*) this + 626
    private float nextNextGaussian; // (float*) this + 627
    private int index; // (DWORD*) this + 628
    private boolean valid = false;

    private static int[] randomMTArray = {0, 0x9908b0df};
    private static final double TWO_POW_M32 = 1.0 / (1L << 32);

    @Override
    public double nextDouble() {
        return _genRandReal2();
    }

    @Override
    public boolean nextBoolean() {
        return (_genRandInt32() & 0x8000000) != 0;
    }

    private void _initGenRand(int initialValue) {
        this.arr[0] = initialValue;
        for (this.i = 1; this.i < 624; this.i++) {
            this.arr[i] = 1812433253
                    * ((this.arr[this.i - 1] >>> 30) ^ this.arr[this.i - 1])
                    + this.i;
        }
        this.index = 624;
    }

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

        float multiplier = MathHelper.sqrt(-2 * (float) Math.log(s) / s);
        nextNextGaussian = v2 * multiplier;
        haveNextNextGaussian = true;
        return v1 * multiplier;
    }

    private int _genRandInt32() {
        if (this.i < 625) {
            if (this.i == 624) {
                this.i = 0;
            }
        } else {
            _initGenRand(5489);
            this.i = 0;
        }
        if (this.i >= 227) {
            if (this.i >= 623) {
                this.arr[623] = randomMTArray[this.arr[0] & 1]
                        ^ ((this.arr[0] & 0x7fffffff | this.arr[623] & 0x80000000) >>> 1)
                        ^ this.arr[396];
            } else {
                this.arr[this.i] = randomMTArray[this.arr[this.i + 1] & 1]
                        ^ ((this.arr[this.i + 1] & 0x7fffffff | this.arr[this.i] & 0x80000000) >>> 1)
                        ^ this.arr[this.i - 227];
            }
        } else {
            this.arr[this.i] = randomMTArray[this.arr[this.i + 1] & 1]
                    ^ ((this.arr[this.i + 1] & 0x7fffffff | this.arr[this.i] & 0x80000000) >>> 1)
                    ^ this.arr[this.i + 397];
            if (this.index < 624) {
                this.arr[this.index] = 1812433253
                        * ((this.arr[this.index - 1] >> 30) ^ this.arr[this.index - 1])
                        + this.index;
                this.index++;
            }
        }
        int v1 = i++;
        int v2 = this.arr[v1];
        int v3 = ((v2 ^ (v2 >>> 11)) << 7) & 0x9d2c5680 ^ v2 ^ (v2 >>> 11);
        return (v3 << 15) & 0xefc60000 ^ v3 ^ (((v3 << 15) & 0xefc60000 ^ v3) >>> 18);
    }

    private double _genRandReal2() {
        return Integer.toUnsignedLong(_genRandInt32()) * TWO_POW_M32;
    }

    public int nextGaussianInt(int bound) {
        return nextInt(bound) - nextInt(bound);
    }

    public long nextUnsignedInt() {
        return Integer.toUnsignedLong(_genRandInt32());
    }

    private void _initGenRandFast(int initialValue) {
        this.arr[0] = initialValue;
        int v4 = initialValue;
        for (int i = 1; i < 398; i++) {
            this.arr[i] = 1812433253 * ((v4 >>> 30) ^ v4) + i;
            v4 = 1812433253 * ((v4 >>> 30) ^ v4) + i;
        }
        this.i = 624;
        this.index = 398;
    }

    public Vec3d nextGaussianVec3() {
        float x = (float) nextGaussian();
        float y = (float) nextGaussian();
        float z = (float) nextGaussian();
        return new Vec3d(x, y, z);
    }

    public int nextIntInclusive(int a, int b) {
        return nextInt(a, b + 1);
    }

    public byte nextUnsignedChar() {
        return (byte) _genRandInt32();
    }

    public float nextGaussianFloat() {
        return nextFloat() - nextFloat();
    }

    @Override
    public int nextInt(int bound) {
        if (bound > 0)
            return _genRandInt32() % bound;
        else
            return 0;
    }

    public int nextInt(int a, int b) {
        if (a < b)
            return a + nextInt(b - a);
        else
            return a;
    }

    @Override
    public int nextInt() {
        return _genRandInt32() >>> 1;
    }

    @Override
    public void setSeed(long seed) {
        if (valid) // Hackfix for this being called too early in the superconstructor
            setSeed((int) seed);
    }

    public void setSeed(int seed) {
        _setSeed(seed);
    }

    public Vec3d nextVec3() {
        float x = nextFloat();
        float y = nextFloat();
        float z = nextFloat();
        return new Vec3d(x, y, z);
    }

    private void _setSeed(int seed) {
        this.seed = seed;
        this.i = 625;
        this.haveNextNextGaussian = false;
        this.nextNextGaussian = 0;
        _initGenRandFast(seed);
    }

    public float nextFloat(float bound) {
        return nextFloat() * bound;
    }

    public float nextFloat(float a, float b) {
        return a + (nextFloat() * (b - a));
    }

    @Override
    public float nextFloat() {
        return (float) _genRandReal2();
    }

    public BedrockRandom(int seed) {
        valid = true;
        _setSeed(seed);
    }

    public BedrockRandom() {
        this(new Random().nextInt());
    }

    public int getSeed() {
        return seed;
    }

}
