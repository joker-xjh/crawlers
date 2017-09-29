package objectpool.factory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import demo9.interfaces.ObjectFactory;
import demo9.interfaces.Validator;
import objectpool.interfaces.BlockingPool;

public class DefaultBlockingPool<T> extends AbstractPool<T>  implements BlockingPool<T>{
	
	private int capacity;
	
	private ObjectFactory<T> objectFactory;
	
	private Validator<T> validator;
	
	private BlockingQueue<T> objects;
	
	private boolean shutdown;
	
	private ExecutorService executorService;
	
	public DefaultBlockingPool(int capacity, ObjectFactory<T> objectFactory, Validator<T> validator) {
		this.capacity = capacity;
		this.objectFactory = objectFactory;
		this.validator = validator;
		objects = new ArrayBlockingQueue<>(capacity);
		executorService = Executors.newCachedThreadPool();
		init();
	}
	
	private void init() {
		for(int i=0; i<capacity; i++) {
			objects.add(objectFactory.createNew());
		}
	}
	
	private void clear() {
		for(T obj : objects) {
			validator.invalidate(obj);
		}
	}
	

	@Override
	public T get() {
		if(shutdown)
			throw new IllegalStateException("object pool is shutdown");
		T obj = null;
		while(true) {
			try {
				obj =  objects.take();
				break;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return obj;
	}

	@Override
	public void shutdown() {
		shutdown = true;
		clear();
	}

	@Override
	protected boolean isValid(T obj) {
		return validator.isValid(obj);
	}

	@Override
	protected void returnToPool(T obj) {
		executorService.submit(new objectReturnor(objects, obj));
	}

	@Override
	public T get(long time, TimeUnit timeUnit) {
		if(shutdown)
			throw new IllegalStateException("object pool is shutdown");
		T obj = null;
		try {
			obj = objects.poll(time, timeUnit);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	
	private class objectReturnor implements Runnable{
		private BlockingQueue<T> objects;
		private T obj;
		
		public objectReturnor(BlockingQueue<T> objects, T obj) {
			this.obj = obj;
			this.objects = objects;
		}

		@Override
		public void run() {
			while(true) {
				try {
					objects.put(obj);
					break;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	
	
	
	

}
