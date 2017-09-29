package objectpool.interfaces;

public interface Validator<T> {
	
	boolean isValid(T obj);
	
	void clearObject(T obj);

}
