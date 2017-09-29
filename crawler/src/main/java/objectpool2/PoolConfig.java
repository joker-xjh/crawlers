package objectpool2;

public class PoolConfig {

	private int maxWaitMilliseconds = 5000;

	private int maxIdleMilliseconds = 300000;

	private int maxSize = 20;

	private int minSize = 5;

	private int partitionSize = 4;

	private int scavengeIntervalMilliseconds = 1000 * 60 * 2;

	private double scavengeRatio = 0.5;

	public int getMaxSize() {
		return maxSize;
	}

	public int getMaxWaitMilliseconds() {
		return maxWaitMilliseconds;
	}

	public PoolConfig setMaxWaitMilliseconds(int maxWaitMilliseconds) {
		this.maxWaitMilliseconds = maxWaitMilliseconds;
		return this;
	}

	public PoolConfig setMaxSize(int maxSize) {
		this.maxSize = maxSize;
		return this;
	}

	public int getMinSize() {
		return minSize;
	}

	public PoolConfig setMinSize(int minSize) {
		this.minSize = minSize;
		return this;
	}

	public int getMaxIdleMilliseconds() {
		return maxIdleMilliseconds;
	}

	public PoolConfig setMaxIdleMilliseconds(int maxIdleMilliseconds) {
		this.maxIdleMilliseconds = maxIdleMilliseconds;
		return this;
	}

	public int getPartitionSize() {
		return partitionSize;
	}

	public PoolConfig setPartitionSize(int partitionSize) {
		this.partitionSize = partitionSize;
		return this;
	}

	public int getScavengeIntervalMilliseconds() {
		return scavengeIntervalMilliseconds;
	}

	public PoolConfig setScavengeIntervalMilliseconds(int scavengeIntervalMilliseconds) {
		this.scavengeIntervalMilliseconds = scavengeIntervalMilliseconds;
		return this;
	}

	public double getScavengeRatio() {
		return scavengeRatio;
	}

	public PoolConfig setScavengeRatio(double scavengeRatio) {
		if (scavengeRatio <= 0 || scavengeRatio > 1) {
			throw new IllegalArgumentException("Invalid scavenge ratio: " + scavengeRatio);
		}
		this.scavengeRatio = scavengeRatio;
		return this;
	}

}
