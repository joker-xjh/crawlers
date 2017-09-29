package objectpool2;

public interface ObjectFactory<T> {
	
	T create();
	
	void destroy(T obj);
	
	boolean validate(T obj);

}
