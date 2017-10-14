package demo28;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantLock;


public class MyCopyOnWriteArrayList<E> {
	
	private final ReentrantLock lock = new ReentrantLock();
	
	private transient volatile Object[] array;
	
	final Object[] getArray() {
		return array;
	}
	
	final void setArray(Object[] a) {
		array = a;
	}
	
	public MyCopyOnWriteArrayList() {
		setArray(new Object[0]);
	}
	
	
	public int size() {
		return getArray().length;
	}
	
	public boolean isEmpty() {
		return size() == 0;
	}
	
	private static int indexOf(Object o, Object[] array, int from, int to) {
		if(o == null) {
			for(int i=from; i<to; i++) {
				if(array[i] == null)
					return i;
			}
		}
		else {
			for(int i=from; i<to; i++) {
				if(o.equals(array[i]))
					return i;
			}
		}
		return -1;
	}
	
	
	private static int lastIndexOf(Object o, Object[] array, int index) {
		if(o == null) {
			for(int i=index; i>=0; i--) {
				if(array[i] == null)
					return i;
			}
		}
		else {
			for(int i=index; i>=0; i--) {
				if(o.equals(array[i]))
					return i;
			}
		}
		return -1;
	}
	
	public boolean contains(Object o) {
		Object[] elements = getArray();
		return indexOf(o, elements, 0, elements.length) >= 0;
	}
	
	public int indexOf(Object o) {
		Object[] elements = getArray();
		return indexOf(o, elements, 0, elements.length);
	}
	
	public int indexOf(Object o, int index) {
		Object[] elements = getArray();
		return indexOf(o, elements, index, elements.length);
	}
	
	public int lastIndexOf(Object o) {
		Object[] elements = getArray();
		return lastIndexOf(o, elements, elements.length-1);
	}
	
	public int lastIndexOf(Object o, int index) {
		Object[] elements = getArray();
		return lastIndexOf(o, elements, index);
	}
	
	public Object[] toArray() {
		Object[] elements = getArray();
		return Arrays.copyOf(elements, elements.length);
	}
	
	
	@SuppressWarnings("unchecked")
	private E get(Object[] elements, int index) {
		return (E) elements[index];
	}
	
	
	public E get(int index) {
		return get(getArray(), index);
	}
	
	public E set(int index, Object val) {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			Object[] elements = getArray();
			E oldValue = get(elements, index);
			if(val != oldValue) {
				Object[] newElements = Arrays.copyOf(elements, elements.length);
				newElements[index] = val;
				setArray(newElements);
			}
			else {
				setArray(elements);
			}
			return oldValue;
		} finally {
			lock.unlock();
		}
	}
	
	
	public boolean add(Object val) {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			Object[] elements = getArray();
			int len = elements.length;
			Object[] newElements = Arrays.copyOf(elements, len+1);
			newElements[len] = val;
			setArray(newElements);
			return true;
		} finally {
			lock.unlock();
		}
	}
	
	public void add(int index, Object val) {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			Object[] elements = getArray();
			int len = elements.length;
			if(index < 0 ||index > len)
				throw new IndexOutOfBoundsException();
			Object[] newElements;
			int numMoved = len - index;
			if(numMoved == 0) {
				newElements = Arrays.copyOf(elements, len+1);
			}
			else {
				newElements = new Object[len+1];
				System.arraycopy(elements, 0, newElements, 0, index);
				System.arraycopy(elements, index, newElements, index+1, numMoved);
			}
			newElements[index] = val;
			setArray(newElements);
		} finally {
			lock.unlock();
		}
	}
	
	
	public E remove(int index) {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			Object[] elements = getArray();
			E oldValue = get(elements, index);
			int len = elements.length;
			int numMoved = len - index - 1;
			if(numMoved == 0)
				setArray(Arrays.copyOf(elements, len-1));
			else {
				Object[] newElements = new Object[len-1];
				System.arraycopy(elements, 0, newElements, 0, index);
				System.arraycopy(elements, index+1, newElements, index, numMoved);
				setArray(newElements);
			}
			return oldValue;
		} finally {
			lock.unlock();
		}
	}
	
	void removeRange(int from, int to) {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			Object[] elements = getArray();
			int len = elements.length;
			if(from < 0 || to > len ||from > to )
				throw new IndexOutOfBoundsException();
			int newLen = to - from;
			int numMoved = len - to;
			if(numMoved == 0)
				setArray(Arrays.copyOf(elements, newLen));
			else {
				Object[] newElements = new Object[newLen];
				System.arraycopy(elements, 0, newElements, 0, from);
				System.arraycopy(elements, to, newElements, from, numMoved);
				setArray(newElements);
			}
		} finally {
			lock.unlock();
		}
	}
	
	private static boolean eq(Object o1, Object o2) {
		return o1 == null ? o2 == null : o1.equals(o2);
	}
	
	
	public boolean addIfAbsent(Object o) {
		Object[] elements = getArray();
		return indexOf(o, elements, 0, elements.length) >= 0 ? false: addIfAbsent(o, elements);
	}
	
	
	
	private boolean addIfAbsent(Object e, Object[] elements) {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			Object[] current = getArray();
			int len = current.length;
			if(current != elements) {
				int common = Math.min(len, elements.length);
				for(int i=0; i<common; i++)
					if(current[i] != elements[i] && eq(e, current[i]))
						return false;
				if(indexOf(e, current, common, len) >= 0)
					return false;
			}
			Object[] newElements = Arrays.copyOf(elements, len+1);
			newElements[len] = e;
			setArray(newElements);
			return true;
		} finally {
			lock.unlock();
		}
	}
	
	
	public boolean containsAll(Collection<?> collection) {
		Object[] elements = getArray();
		int len = elements.length;
		for(Object e : collection) {
			if(indexOf(e, elements, 0, len) < 0)
				return false;
		}
		return true;
	}
	
	
	public boolean removeAll(Collection<?> collection) {
		if(collection == null)
			throw new NullPointerException();
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			Object[] elements = getArray();
			int len = elements.length;
			Object[] newElements = new Object[len];
			int newLen = 0;
			for(int i=0; i<len; i++) {
				if(!collection.contains(elements[i]))
					newElements[newLen++] = elements[i];
			}
			if(newLen != len) {
				setArray(Arrays.copyOf(newElements, newLen));
				return true;
			}
			return false;
		} finally {
			lock.unlock();
		}
	}
	
	
	public boolean retainAll(Collection<?> collection) {
		if(collection == null)
			throw new NullPointerException();
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			Object[] elements =getArray();
			int len = elements.length;
			Object[] temp = new Object[len];
			int newLen = 0;
			for(int i=0; i<len; i++)
				if(collection.contains(elements[i]))
					temp[newLen++] = elements[i];
			if(newLen != len) {
				setArray(Arrays.copyOf(temp, newLen));
				return true;
			}
			return false;
		} finally {
			lock.unlock();
		}
	}
	
	
	public void clear() {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			setArray(new Object[0]);
		} finally {
			lock.unlock();
		}
	}
	
	public void sort(Comparator<? super E> c) {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			Object[] elements = getArray();
			Object[] newElements = Arrays.copyOf(elements, elements.length);
			@SuppressWarnings("unchecked")
			E[] a = (E[]) newElements;
			Arrays.sort(a, c);
			setArray(newElements);
		} finally {
			lock.unlock();
		}
	}
	
	
	@Override
	public String toString() {
		return Arrays.toString(getArray());
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		if(obj == null)
			return false;
		if(!(obj instanceof Collection<?>))
			return false;
		Collection<?> collection = (Collection<?>)obj;
		Iterator<?> iterator = collection.iterator();
		Object[] elements = getArray();
		int len = elements.length;
		for(int i=0; i<len; i++) {
			if(!iterator.hasNext() || !eq(iterator.next(), elements[i]))
				return false;
		}
		if(iterator.hasNext())
			return false;
		return true;
	}
	
	
	
	@Override
	public int hashCode() {
		int hashCode = 1;
		Object[] elements = getArray();
		int len = elements.length;
		for(int i=0; i<len; i++) {
			Object object = elements[i];
			hashCode = 31 * hashCode + (object == null ? 0 : object.hashCode()); 
		}
		return hashCode;
	}
	
	public Iterator<E> iterator(){
		return new COWIterator<>(getArray(), 0);
	}
	
	public ListIterator<E> listIterator(){
		return new COWIterator<E>(getArray(), 0);
	}
	
	public ListIterator<E> listIterator(int index){
		Object[] elements = getArray();
		int len = elements.length;
		if(index < 0 || index > len)
			throw new IndexOutOfBoundsException();
		return new COWIterator<E>(getArray(), index);
	}
	
	
	static class COWIterator<E> implements ListIterator<E> {
		
		private int cursor;
		private Object[] snapshot;
		
		public COWIterator(Object[] array, int cursor) {
			this.cursor = cursor;
			this.snapshot = array;
		}

		@Override
		public boolean hasNext() {
			return cursor < snapshot.length;
		}

		@SuppressWarnings("unchecked")
		@Override
		public E next() {
			if(!hasNext())
				throw new NoSuchElementException();
			return (E) snapshot[cursor++];
		}

		@Override
		public boolean hasPrevious() {
			return cursor > 0;
		}

		@SuppressWarnings("unchecked")
		@Override
		public E previous() {
			if(!hasPrevious())
				throw new NoSuchElementException();
			return (E) snapshot[--cursor];
		}

		@Override
		public int nextIndex() {
			return cursor;
		}

		@Override
		public int previousIndex() {
			return cursor - 1;
		}

		@Override
		public void remove() {
			 throw new UnsupportedOperationException();
		}

		@Override
		public void set(E e) {
			 throw new UnsupportedOperationException();
		}

		@Override
		public void add(E e) {
			 throw new UnsupportedOperationException();
		}
		
	}
	
	

}
