package demo9.interfaces;

public interface Validator<T> {
	
	boolean isValid(T obj);
	
	void invalidate(T obj);

}
