package objectpool2;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class ObjectPoolPartition<T> {
	
	private final ObjectPool<T> pool;
	
	private final PoolConfig poolConfig;
	
	private final int partition;
	
	private final BlockingQueue<Poolable<T>> objectQueue;
	
	private final ObjectFactory<T> objectFactory;
	
	private int totalCount;
	
	public ObjectPoolPartition(ObjectPool<T> pool, PoolConfig poolConfig, int partition, BlockingQueue<Poolable<T>> queue, ObjectFactory<T> objectFactory) {
		this.pool = pool;
		this.poolConfig = poolConfig;
		this.partition = partition;
		this.objectQueue = queue;
		this.objectFactory = objectFactory;
		for(int i=0; i<poolConfig.getMinSize(); i++) {
			objectQueue.add(new Poolable<T>(objectFactory.create(), pool, partition));
		}
		totalCount = poolConfig.getMinSize();
	}
	
	
	public BlockingQueue<Poolable<T>> getObjectQueue() {
		return objectQueue;
	}
	
	
	public synchronized int increaseObjects(int num) {
		if(num + totalCount >  poolConfig.getMaxSize()) {
			num = poolConfig.getMaxSize() - totalCount;
		}
		
		try {
			for(int i=0; i<num; i++) {
				objectQueue.put(new Poolable<T>(objectFactory.create(), pool, partition));
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return num;
	}
	
	public synchronized boolean decreaseObject(Poolable<T> obj) {
		objectFactory.destroy(obj.getObject());
		totalCount--;
		return true;
	}
	
	public int getTotalCount() {
		return totalCount;
	}
	
	
	 public synchronized void scavenge() throws InterruptedException {
		 int num = this.totalCount - poolConfig.getMinSize();
		 if(num <= 0)
			 return;
		 Poolable<T> obj = null;
		 long now = System.currentTimeMillis();
		 while(num-- > 0 && (obj = objectQueue.poll()) != null) {
			 if(now - obj.getLastAccessT() > poolConfig.getScavengeIntervalMilliseconds() && 
					 ThreadLocalRandom.current().nextDouble(1) < poolConfig.getScavengeRatio()) {
				 this.decreaseObject(obj);
			 }
			 else {
				 objectQueue.put(obj);
			 }
			 
		 }
	 }
	
	
	
	
	
	
	public synchronized int shutdown() {
		int removed = 0;
		while(this.totalCount > 0) {
			Poolable<T> obj = objectQueue.poll();
			if(obj != null) {
				decreaseObject(obj);
				removed++;
			}
		}
		return removed;
	}
	
	
	

}
