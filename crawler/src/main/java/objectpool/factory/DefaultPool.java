package objectpool.factory;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import demo9.interfaces.ObjectFactory;
import demo9.interfaces.Validator;
import objectpool.interfaces.Pool;

public class DefaultPool<T> extends AbstractPool<T> implements Pool<T>{
	
	private int capacity;
	
	private ObjectFactory<T> objectFactory;
	
	private Validator<T> validator;
	
	private Queue<T> objects;
	
	private Semaphore semaphore;
	
	private volatile boolean shutdown;
	
	
	
	public DefaultPool(int capacity, ObjectFactory<T> objectFactory, Validator<T> validator) {
		this.capacity = capacity;
		this.objectFactory = objectFactory;
		this.validator = validator;
		objects = new LinkedList<>();
		semaphore = new Semaphore(capacity);
		init();
	}
	
	private void init() {
		for(int i=0; i<capacity; i++) {
			objects.offer(objectFactory.createNew());
		}
	}
	
	private void clear() {
		for(T obj : objects) {
			validator.invalidate(obj);
		}
	}
	

	@Override
	public T get() {
		if(shutdown) {
			throw new IllegalStateException("object pool is shutdown");
		}
		if(semaphore.tryAcquire())
			return objects.poll();
		return null;
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
		objects.offer(obj);
		semaphore.release();
	}
	
	
	public int getCapacity() {
		return capacity;
	}

}
