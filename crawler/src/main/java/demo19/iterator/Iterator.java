package demo19.iterator;

public interface Iterator<T> {
	
	boolean hasNext();
	T next();
	void remove();
}
