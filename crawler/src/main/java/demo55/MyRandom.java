package demo55;

import java.util.concurrent.atomic.AtomicLong;

public class MyRandom {

	private AtomicLong seed;
	
	private static final long multiplier = 0x5DEECE66DL;
	private static final long addend = 0xBL;
	private static final long mask = (1L << 48) - 1;
	
	private static final AtomicLong seedUniquifier
     = new AtomicLong(8682522807148012L);
	
	
	private static long seedUniquifier() {
		for(;;) {
			long current = seedUniquifier.get();
			long next = current * 181783497276652981L;
			if(seedUniquifier.compareAndSet(current, next))
				return next;
		}
	}
	
	private static long initialScramble(long seed) {
		return (seed ^ multiplier) & mask;
	}
	
	
	public MyRandom(long seed) {
		if(getClass() == MyRandom.class) {
			this.seed = new AtomicLong(initialScramble(seed));
		}
		else {
			this.seed.set(initialScramble(seed));
		}
	}
	
	public MyRandom() {
		this(seedUniquifier() ^ System.nanoTime());
	}
	
	
	protected int next(int bits) {
		long oldSeed, nextSeed;
		do {
			oldSeed = seed.get();
			nextSeed = (oldSeed * multiplier + addend) & mask; 
		} while (!seed.compareAndSet(oldSeed, nextSeed));
		return (int) (nextSeed >>> (48 - bits));
	}
	
	
	public int nextInt() {
		return next(32);
	}
	
	public int nextInt(int bound) {
		if(bound <= 0)
			throw new IllegalArgumentException();
		int r = next(31);
		int m = bound - 1;
		if((m & bound) == 0)
			return (int) (((long)r * bound ) >> 31);
		else {
			return r % bound;
		}
	}
	
	
	public long nextLong() {
		return ((long)next(32) << 32) + next(32);
	}
	
	
	public boolean nextBoolean() {
		return next(1) != 0;
	}
	
	
	
	
	
}
