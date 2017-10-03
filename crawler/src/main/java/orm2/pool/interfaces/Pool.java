package orm2.pool.interfaces;

public interface Pool<T> {

	T get();
	
	void release(T obj);
	
	void close();
	
	
}
