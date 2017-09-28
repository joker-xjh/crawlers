package demo9.factorys;

import demo9.interfaces.Pool;

public abstract class AbstractPool<T> implements Pool<T> {
	
	@Override
	public void release(T obj) {
		if(isValid(obj)) {
			returnToPool(obj);
		}
		else {
			handleInvalid(obj);
		}
	}
	
	
	protected abstract void returnToPool(T obj);
	
	protected abstract boolean isValid(T obj);
	
	protected abstract void handleInvalid(T obj);

}
