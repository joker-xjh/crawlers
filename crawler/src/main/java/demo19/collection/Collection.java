package demo19.collection;

import demo19.iterator.Iterator;

public interface Collection<T> {
	
	boolean add(T obj);
	boolean remove(T obj);
	boolean contains(T obj);
	
	Iterator<T> iterator();
	
	boolean isEmpty();
	
	int size();
	
	T[] toArray();
	

}
