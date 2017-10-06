package demo19.iterator;

public interface ListIterator<T> extends Iterator<T> {

	boolean hasPrevious();
	T previous();
	
	int previousIndex();
	int nextIndex();
	
	boolean add(T obj);
	boolean set(T obj);
	
}
