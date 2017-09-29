package objectpool.factory;

import objectpool.interfaces.Pool;

public abstract class AbstractPool<T> implements Pool<T> {
	
	@Override
	public void release(T obj) {
		if(isValid(obj)) {
			returnToPool(obj);
		}
	}
	
	protected abstract boolean isValid(T obj);
	
	protected abstract void returnToPool(T obj);

}
