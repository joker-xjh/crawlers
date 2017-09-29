package demo9.factorys;

import demo9.interfaces.ObjectFactory;
import demo9.interfaces.Validator;

//pool factory
public class PoolFactory {
	
	private PoolFactory() {
		
	}
	
	public static <T> T newBoundedBlockingPool(int size, ObjectFactory<T> factory, Validator<T> validator) {
		return newBoundedBlockingPool(size, factory, validator);
	}
	
	public static <T> T newBoundedPool(int size, ObjectFactory<T> objectFactory, Validator<T> validator) {
		return newBoundedPool(size, objectFactory, validator);
	}
	
	

}
