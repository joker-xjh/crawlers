package demo6;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class ObjectPool<T> {
	
	private ConcurrentLinkedQueue<T> pool;
	
	private ScheduledExecutorService executorService;
	
	public ObjectPool(int minSize) {
		initialize(minSize);
	}
	
	public ObjectPool(final int minSize, final int maxSize, long validationInternal) {
		initialize(minSize);
		executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleWithFixedDelay(
				new Runnable() {
					@Override
					public void run() {
						int size = pool.size();
						if(size < minSize) {
							int added = minSize - size;
							for(int i=0; i<added; i++)
								pool.add(createObject());
						}
						else if(size > maxSize) {
							int removed = size - maxSize;
							for(int i=0; i<removed; i++)
								pool.poll();
						}
					}
				}
				, validationInternal, validationInternal, TimeUnit.SECONDS);
	}
	
	public T getObject() {
		T obj;
		if((obj = pool.poll()) == null)
			obj = createObject();
		return obj;
	}
	
	public void returnObject(T object) {
		if(object == null)
			return;
		pool.offer(object);
	}
	
	
	
	
	//Shutdown this pool.
	public void shutDown() {
		if(executorService != null)
			executorService.shutdown();
	}
	
	//initialize pool
	private void initialize(int size) {
		pool = new ConcurrentLinkedQueue<>();
		for(int i=0; i<size; i++) {
			pool.add(createObject());
		}
	}
	
	// create a object
	protected abstract T createObject();

}
