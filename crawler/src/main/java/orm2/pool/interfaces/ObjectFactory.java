package orm2.pool.interfaces;

public interface ObjectFactory<T> {

	T create();
	
	boolean isValid(T obj);
	
	void destroy(T obj);
	
}
