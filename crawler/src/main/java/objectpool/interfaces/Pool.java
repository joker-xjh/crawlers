package objectpool.interfaces;

public interface Pool<T> {
	
	T get();
	
	void release(T obj);
	
	void shutdown();

}
