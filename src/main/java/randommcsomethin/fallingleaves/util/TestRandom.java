package randommcsomethin.fallingleaves.util;

import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;

public class TestRandom implements Random {
	@Override
	public Random split() {
		return null;
	}

	@Override
	public RandomSplitter nextSplitter() {
		return null;
	}

	@Override
	public void setSeed(long seed) {

	}

	@Override
	public int nextInt() {
		return 0;
	}

	@Override
	public int nextInt(int bound) {
		return 0;
	}

	@Override
	public long nextLong() {
		return 0;
	}

	@Override
	public boolean nextBoolean() {
		return false;
	}

	@Override
	public float nextFloat() {
		return 0;
	}

	@Override
	public double nextDouble() {
		return 0;
	}

	@Override
	public double nextGaussian() {
		return 0;
	}
}
