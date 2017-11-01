package demo34;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.RandomAccess;

public class MyCollections {
	
	public static <T> int binarySearch(List<? extends Comparable<? super T>> list, T key){
		if(list instanceof RandomAccess)
			return indexedBinarySearch(list, key);
		else
			return iteratorBinarySearch(list, key);
	}
	
	private static <T> int indexedBinarySearch(List<? extends Comparable<? super T>> list, T key) {
		int low = 0;
		int high = list.size() - 1;
		while(low <= high) {
			int mid = (low + high) >>> 1;
			Comparable<? super T> midVal = list.get(mid);
			int cmp = midVal.compareTo(key);
			if(cmp < 0)
				low = mid +1;
			else if(cmp > 0)
				high = mid - 1;
			else
				return mid;
		}
		return -(low+1);
	}
	
	private static <T> int  iteratorBinarySearch(List<? extends Comparable<? super T>> list, T key) {
		int low = 0;
		int high = list.size() - 1;
		ListIterator<? extends Comparable<? super T>> i = list.listIterator();
		while(low <= high) {
			int mid = (low + high) >>> 1;
			Comparable<? super T> midVal = get(i, mid);
			int cmp = midVal.compareTo(key);
			if(cmp < 0)
				low = mid + 1;
			else if(cmp > 0)
				high = mid - 1;
			else
				return mid;
		}
		return -(low+1);
	}
	
	
	private static <T> T get(ListIterator<? extends T> i, int index) {
		T obj = null;
		int pos = i.nextIndex();
		if(pos <= index) {
			do {
				obj = i.next();
			} while (pos++ < index);
		}
		else {
			do {
				obj = i.previous();
			} while (pos-- > index);
		}
		return obj;
	}
	
	
	@SuppressWarnings("unchecked")
	public static <T extends Comparable<? super T>> void sort(List<T> list) {
		Object[] a = list.toArray();
		Arrays.sort(a);
		ListIterator<T> i = list.listIterator();
		for(int j=0; j<a.length; j++) {
			i.next();
			i.set((T)a[j]);
		}
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void swap(List<?> list, int i, int j) {
		final List l = list;
		l.set(i, l.set(j, l.get(i)));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void reserve(List<?> list) {
		int size = list.size();
		if(list instanceof RandomAccess) {
			for(int i=0,mid=size>>1,j=size-1; i<mid; i++,j--)
				swap(list, i, j);
		}
		else {
			ListIterator fwd = list.listIterator();
			ListIterator rev = list.listIterator(size);
			for(int i=0, mid = size>>1; i<mid; i++ ) {
				Object temp = fwd.next();
				fwd.set(rev.previous());
				rev.set(temp);
			}
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public static void shuffle(List<?> list, Random random) {
		int size = list.size();
		if(list instanceof RandomAccess) {
			for(int i=size; i>1; i--) {
				swap(list, i-1, random.nextInt(i));
			}
		}
		else {
			Object[] arr = list.toArray();
			for(int i=size; i>1; i--) {
				//swap(arr, i-1, random.nextInt(i));
			}
			@SuppressWarnings("rawtypes")
			ListIterator iterator = list.listIterator();
			for(int i=0; i<size; i++) {
				iterator.next();
				iterator.set(arr[i]);
			}
		}
	}
	
	@SuppressWarnings("unused")
	private static class EmptyList<E> extends AbstractList<E> implements RandomAccess {

		@Override
		public E get(int index) {
			throw new IndexOutOfBoundsException("Index: "+index);
		}
		
		public boolean isEmpty() {
			return true;
		}

		@Override
		public boolean contains(Object o) {
			return false;
		}
		
		@Override
		public Object[] toArray() {
			return new Object[0];
		}
		
		@Override
		public int size() {
			return 0;
		}
		
		@Override
		public int hashCode() {
			return 1;
		}
		
		@Override
		public boolean equals(Object o) {
			return (o instanceof List) && ((List<?>)o).isEmpty();
		}
		
	}
	
	
	
	
	
	
	
	

}
