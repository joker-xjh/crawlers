package demo27;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyArrayBlockingQueue<E> {
	
	final Object[] items;
	
	int takeIndex;
	
	int putIndex;
	
	int count;
	
	final ReentrantLock lock;
	
	private final Condition notEmpty;
	
	private final Condition notFull;
	
	public MyArrayBlockingQueue(int capacity, boolean fair) {
		if(capacity <= 0)
			throw new IllegalArgumentException();
		items = new Object[capacity];
		lock = new ReentrantLock(fair);
		notEmpty = lock.newCondition();
		notFull = lock.newCondition();
	}
	
	
	public MyArrayBlockingQueue(int capacity) {
		this(capacity, false);
	}
	
	private void checkNull(E e) {
		if(e == null)
			throw new NullPointerException();
	}
	
	
	public boolean offer(E e) {
		checkNull(e);
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			if(count == items.length)
				return false;
			enQueue(e);
			return true;
		} finally {
			lock.unlock();
		}
	}
	
	private void enQueue(E e) {
		final Object[] items = this.items;
		items[putIndex] = e;
		if(++putIndex == items.length)
			putIndex = 0;
		count++;
		notEmpty.signal();
	}
	
	public void put(E e) throws InterruptedException {
		checkNull(e);
		final ReentrantLock lock = this.lock;
		lock.lockInterruptibly();
		try {
			while(count == items.length)
				notFull.await();
			enQueue(e);
		} finally {
			lock.unlock();
		}
	}
	
	public E poll() {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			return count == 0 ? null : deQueue();
		} finally {
			lock.unlock();
		}
	}
	
	
	
	private E deQueue() {
		final Object[] array = this.items;
		@SuppressWarnings("unchecked")
		E e = (E) array[takeIndex];
		array[takeIndex] = null;
		if(++takeIndex == array.length)
			takeIndex = 0;
		count--;
		notFull.signal();
		return e;
	}
	
	public boolean remove(Object o) {
		if(o == null)
			return false;
		final ReentrantLock lock = this.lock;
		final Object[] items = this.items;
		lock.lock();
		try {
			if(count > 0) {
				int putIndex = this.putIndex;
				int i = this.takeIndex;
				do {
					if(o.equals(items[i])) {
						removeAt(i);
						return true;
					}
					if(++i == items.length)
						i = 0;
				} while (i!=putIndex);
			}
			return false;
		} finally {
			lock.unlock();
		}
	}
	
	
	
	
	void removeAt(int removeIndex) {
		final Object[] items = this.items;
		if(removeIndex == takeIndex) {
			items[removeIndex] = null;
			count--;
			if(++takeIndex == items.length)
				takeIndex = 0;
		}
		else {
			int putIndex = this.putIndex;
			for(int i=removeIndex;;) {
				int next = i+1;
				if(next == items.length)
					next=0;
				if(next != putIndex) {
					items[i] = items[next];
					i=next;
				}
				else {
					items[i] = null;
					this.putIndex = i;
					break;
				}
			}
			count--;
		}
		notFull.signal();
	}
	
	
	public E take() throws InterruptedException {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			while(count == 0)
				notEmpty.await();
			return deQueue();
		} finally {
			lock.unlock();
		}
	}
	
	
	public E peek() {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			if(count == 0)
				return null;
			return ItemAt(takeIndex);
		} finally {
			lock.unlock();
		}
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	final E ItemAt(int i) {
		return (E) items[i];
	}
	
	
	
	
	
	
	
	
	

}
