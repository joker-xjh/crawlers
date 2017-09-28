package demo9.factorys;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import demo9.interfaces.BlockingPool;
import demo9.interfaces.ObjectFactory;
import demo9.interfaces.Validator;

public class BoundedBlockingPool<T> extends AbstractPool<T> implements BlockingPool<T> {
	
	private volatile boolean shutdown;

	private BlockingQueue<T> objects;
	
	private Validator<T> validator;
	
	private ObjectFactory<T> objectFactory;
	
	private ExecutorService executorService;
	
	public BoundedBlockingPool(int size, Validator<T> validator, ObjectFactory<T> objectFactory) {
		objects = new ArrayBlockingQueue<>(size);
		this.validator = validator;
		this.objectFactory = objectFactory;
		executorService = Executors.newCachedThreadPool();
		initObject(size);
	}
	
	private void initObject(int size) {
		for(int i=0; i<size; i++) {
			objects.add(objectFactory.createNew());
		}
	}
	
	private void clearObject() {
		for(T obj : objects) {
			validator.invalidate(obj);
		}
	}
	

	@Override
	public void shutdown() {
		shutdown = true;
		clearObject();
	}

	@Override
	public T get() {
		if(shutdown)
			throw new IllegalStateException("Object Pool is shutdown");
		try {
			return objects.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public T get(long time, TimeUnit timeUnit) throws InterruptedException {
		if(shutdown)
			throw new IllegalStateException("Object Pool is shutdown");
		return objects.poll(time, timeUnit);
	}

	@Override
	protected void returnToPool(T obj) {
		executorService.submit(new addTask(obj, objects));
	}

	@Override
	protected boolean isValid(T obj) {
		return validator.isValid(obj);
	}

	@Override
	protected void handleInvalid(T obj) {
		
	}
	
	private class addTask implements Runnable{
		private T obj;
		private BlockingQueue<T> objects;
		
		public addTask(T obj, BlockingQueue<T> objects) {
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
