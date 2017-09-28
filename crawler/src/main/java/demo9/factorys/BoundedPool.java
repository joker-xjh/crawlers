package demo9.factorys;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import demo9.interfaces.ObjectFactory;
import demo9.interfaces.Validator;

public class BoundedPool<T> extends AbstractPool<T>{
	
	private int size;
	
	private Queue<T> objects;
	
	private Validator<T> validator;
	
	private ObjectFactory<T> objectFactory;
	
	private volatile boolean shutdown;
 	
	private Semaphore permits;
	
	public BoundedPool(int size, Validator<T> validator, ObjectFactory<T> objectFactory) {
		this.size = size;
		this.validator = validator;
		this.objectFactory = objectFactory;
		objects = new LinkedList<>();
		permits = new Semaphore(size);
		init(size);
	}
	
	private void init(int size) {
		for(int i=0; i<size; i++)
		{
			objects.add(objectFactory.createNew());
		}
	}
	
	private void clear() {
		for(T obj : objects) {
			validator.invalidate(obj);
		}
	}
	
	
	public int getSize() {
		return size;
	}
	

	@Override
	public T get() {
		if(shutdown)
			throw new IllegalStateException("Object Pool is shutdown");
		if(permits.tryAcquire()) {
			return objects.poll();
		}
		return null;
	}

	@Override
	public void shutdown() {
		shutdown = true;
		clear();
	}

	@Override
	protected void returnToPool(T obj) {
		boolean b = objects.add(obj);
		if(b) {
			permits.release();
		}
	}

	@Override
	protected boolean isValid(T obj) {
		return validator.isValid(obj);
	}

	@Override
	protected void handleInvalid(T obj) {
		
		
	}
	
	

}
