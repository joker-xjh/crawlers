package demo19.collection;

import demo19.iterator.ListIterator;

public interface List<T> extends Collection<T> {
	
	boolean add(int index, T obj);
	
	T set(int index, T obj);
	
	T remove(int index);
	
	int indexOf(T obj);
	
	int lastIndexOf(T obj);
	
	ListIterator<T> listIterator();
	
	

}
