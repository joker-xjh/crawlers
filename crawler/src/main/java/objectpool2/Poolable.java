package objectpool2;

public class Poolable<T> implements AutoCloseable {
	
	private final T object;
	
	private ObjectPool<T> pool;
	
	private final int partition;
	
	private long lastAccessT;
	
	public Poolable(T object, ObjectPool<T> pool, int partition) {
		this.object = object;
		this.pool = pool;
		this.partition = partition;
		lastAccessT = System.currentTimeMillis();
	}
	
	public T getObject() {
		return object;
	}
	
	public ObjectPool<T> getPool() {
		return pool;
	}
	
	public int getPartition() {
		return partition;
	}
	
	public long getLastAccessT() {
		return lastAccessT;
	}
	
	public void setLastAccessT(long lastAccessT) {
		this.lastAccessT = lastAccessT;
	}
	
	public void returnObject() {
		pool.returnObject(this);
	}
	

	@Override
	public void close() throws Exception {
		this.returnObject();
	}

}
