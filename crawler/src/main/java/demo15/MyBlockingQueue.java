package demo15;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyBlockingQueue<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3297045484758354526L;
	 
	private final int capacity;
	
	/** Current number of elements */
	private final AtomicInteger count = new AtomicInteger(0);
	
	/** Lock held by take, poll, etc */
	private final ReentrantLock takeLock = new ReentrantLock();
	
	/** Wait queue for waiting takes */
	private final Condition notEmpty = takeLock.newCondition();
	
	/** Lock held by put, offer, etc */
	private final ReentrantLock putLock = new ReentrantLock();
	
	/** Wait queue for waiting puts */
	private final Condition notFull = putLock.newCondition();
	
	
	private Queue<T> queue;
	
	
	
	private void signalNotEmpty() {
		final ReentrantLock takeLock = this.takeLock;
		takeLock.lock();
		try {
			notEmpty.signal();
		} finally {
			takeLock.unlock();
		}
	}
	
	private void signalNotFull() {
		final ReentrantLock putLock = this.putLock;
		putLock.lock();
		try {
			notFull.signal();
		} finally {
			putLock.unlock();
		}
	}
	
	private void insert(T obj) {
		queue.offer(obj);
	}
	
	private T extract() {
		return queue.poll();
	}
	
	private void fullyLock() {
		putLock.lock();
		takeLock.lock();
	}
	
	private void fullyUnlock() {
		putLock.unlock();
		takeLock.unlock();
	}
	
	public MyBlockingQueue(int capacity) {
		if(capacity <= 0)
			throw new IllegalArgumentException();
		this.capacity = capacity;
		queue = new LinkedList<>();
	}
	
	public MyBlockingQueue() {
		this(Integer.MAX_VALUE);
	}
	
	public int size() {
		return count.get();
	}
	
	
	public int remainingCapacity() {
		return capacity - count.get();
	}
	
	
	public void put(T obj) throws InterruptedException {
		if(obj == null)
			throw new IllegalArgumentException();
		int c = -1;
		final ReentrantLock putLock = this.putLock;
		final AtomicInteger count = this.count;
		putLock.lock();
		try {
			
			try {
				
				while(count.get() == capacity)
					notFull.await();
				
			} catch (InterruptedException e) {
				notFull.signal();
				throw e;
			}
			
			insert(obj);
			c = count.getAndIncrement();
			if(c+1 < capacity)
				notFull.signal();
		} finally {
			putLock.unlock();
		}
		if(c == 0)
			signalNotEmpty();
	}
	
	
	public boolean offer(T obj, long time, TimeUnit timeUnit) throws InterruptedException {
		if(obj == null || time < 0)
			throw new IllegalArgumentException();
		long millis = timeUnit.toNanos(time);
		final ReentrantLock putLock = this.putLock;
		final AtomicInteger count = this.count;
		putLock.lock();
		int c = -1;
		try {
			while(true) {
				if(count.get() < capacity) {
					insert(obj);
					c = count.getAndIncrement();
					if(c+1 < capacity)
						notFull.signal();
					break;
				}
				try {
					millis = notFull.awaitNanos(millis);
				} catch (InterruptedException e) {
					notFull.signal();
					throw e;
				}
			}
			
			
		} finally {
			putLock.unlock();
		}
		if(c==0)
			signalNotEmpty();
		return true;
	}
	
	
	public boolean offer(T obj) {
		if(obj == null)
			throw new IllegalArgumentException();
		final AtomicInteger count = this.count;
		if(count.get() == capacity)
			return false;
		int c = -1;
		final ReentrantLock putLock = this.putLock;
		putLock.lock();
		try {
			if(count.get() < capacity) {
				insert(obj);
				c = count.getAndIncrement();
				if(c+1 < capacity)
					notFull.signal();
			}
		} finally {
			putLock.unlock();
		}
		if(c==0)
			signalNotEmpty();
		return true;
	}
	
	
	public T take() throws InterruptedException {
		T obj = null;
		int c = -1;
		final AtomicInteger count = this.count;
		final ReentrantLock takeLock = this.takeLock;
		takeLock.lock();
		try {
			try {
				while(count.get() == 0)
					notEmpty.await();
			} catch (InterruptedException e) {
				notEmpty.signal();
				throw e;
			}
			
			obj = extract();
			c = count.getAndDecrement();
			if( c > 0)
				notEmpty.signal();
		} finally {
			takeLock.unlock();
		}
		if(c == capacity)
			signalNotFull();
		return obj;
	}
	
	
	public void clear() {
		fullyLock();
		
		try {
			queue.clear();
			notFull.signalAll();
		} finally {
			fullyUnlock();
		}
	}
	
	
	

}
