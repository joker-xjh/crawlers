package objectpool2;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ObjectPool<T> {
	
	private volatile boolean shutdown;
	
	private PoolConfig poolConfig;
	
	private ObjectPoolPartition<T>[] partitions;
	
	private ObjectFactory<T> objectFactory;
	
	private Scavenger scavenger;
	
	@SuppressWarnings("unchecked")
	public ObjectPool(PoolConfig poolConfig, ObjectFactory<T> objectFactory) {
		this.poolConfig = poolConfig;
		this.objectFactory = objectFactory;
		this.partitions = new ObjectPoolPartition[poolConfig.getPartitionSize()];
		for(int i=0; i<poolConfig.getPartitionSize(); i++) {
			partitions[i] = new ObjectPoolPartition<>(this, poolConfig, i, new ArrayBlockingQueue<Poolable<T>>(poolConfig.getMaxSize()), objectFactory);
		}
		if(poolConfig.getScavengeIntervalMilliseconds() > 0) {
			scavenger = new Scavenger();
			scavenger.start();
		}
	}
	
	public Poolable<T> borrowObject(){
		return borrowObject(true);
	}
	
	public Poolable<T> borrowObject(boolean blocking){
		for(int i=0; i<3; i++) {
			Poolable<T> obj = getObject(blocking);
			if(objectFactory.validate(obj.getObject()))
				return obj;
			else
				partitions[obj.getPartition()].decreaseObject(obj);
		}
		throw new RuntimeException("Cannot find a valid object");
		
	}
	
	
	
	
	
	private Poolable<T> getObject(boolean blocking){
		int partition = (int) (Thread.currentThread().getId() % poolConfig.getPartitionSize());
		ObjectPoolPartition<T> subPool = partitions[partition];
		Poolable<T> obj = subPool.getObjectQueue().poll();
		if(obj == null) {
			subPool.increaseObjects(1);
			try {
                if (blocking) {
                    obj = subPool.getObjectQueue().take();
                } else {
                    obj = subPool.getObjectQueue().poll(poolConfig.getMaxWaitMilliseconds(), TimeUnit.MILLISECONDS);
                    if (obj == null) {
                        throw new RuntimeException("Cannot get a free object from the pool");
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e); // will never happen
            }
		}
		obj.setLastAccessT(System.currentTimeMillis());
		return obj;
	}
	
	public void returnObject(Poolable<T> obj) {
		ObjectPoolPartition<T> partition = partitions[obj.getPartition()];
		try {
			partition.getObjectQueue().put(obj);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	public int getSize() {
		int size = 0;
		for(int i=0; i<poolConfig.getPartitionSize(); i++) {
			size += partitions[i].getTotalCount();
		}
		return size;
	}
	
	
	public synchronized int shutdown() {
		int removed = 0;
		shutdown = true;
		for(ObjectPoolPartition<T> partition : partitions) {
			removed += partition.shutdown();
		}
		return removed;
	}
	

	
	private class Scavenger extends Thread{

		@Override
		public void run() {
			int partition = 0;
			while(!shutdown) {
				try {
					Thread.sleep(poolConfig.getMaxIdleMilliseconds());
					partition = ++partition % poolConfig.getPartitionSize();
					partitions[partition].scavenge();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
