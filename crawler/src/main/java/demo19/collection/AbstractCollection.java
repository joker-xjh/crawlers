package demo19.collection;

import demo19.iterator.Iterator;

public abstract class AbstractCollection<T> implements Collection<T>{
	
	@Override
	public boolean isEmpty() {
		return size() == 0;
	}
	
	@Override
	public boolean contains(T obj) {
		Iterator<T> iterator = iterator();
		if(obj == null) {
			while(iterator.hasNext()) {
				T temp = iterator.next();
				if(temp == null)
					return true;
			}
		}
		else {
			while(iterator.hasNext()) {
				T temp = iterator.next();
				if(obj.equals(temp))
					return true;
			}
		}
		return false;
	}
	
	
	@Override
	public boolean remove(T obj) {
		Iterator<T> iterator = iterator();
		if(obj == null) {
			while(iterator.hasNext()) {
				T temp = iterator.next();
				if(temp == null) {
					iterator.remove();
					return true;
				}
			}
		}
		else {
			while(iterator.hasNext()) {
				T temp = iterator.next();
				if(obj.equals(temp)) {
					iterator.remove();
					return true;
				}
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T[] toArray() {
		T[] array = (T[]) new Object[size()];
		Iterator<T> iterator = iterator();
		int index = 0;
		while(iterator.hasNext()) {
			array[index++] = iterator.next();
		}
		return array;
	}

}
