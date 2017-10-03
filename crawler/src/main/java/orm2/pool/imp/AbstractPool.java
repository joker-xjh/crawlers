package orm2.pool.imp;

import orm2.pool.interfaces.Pool;

public abstract class AbstractPool<T> implements Pool<T> {
	
	protected volatile boolean shutdown;
	
	@Override
	public void release(T obj) {
		if(isValid(obj)) {
			if(shutdown) {
				destroy(obj);
			}
			else {
				returnToPool(obj);
			}
		}
	}
	
	protected abstract boolean isValid(T obj);
	
	protected abstract void returnToPool(T obj);
	
	protected abstract void destroy(T obj);
	

}
