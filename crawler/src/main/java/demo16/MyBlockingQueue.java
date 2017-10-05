package demo16;

import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyBlockingQueue <T>{
	
	private Queue<T> queue;
	
	private int capacity;
	
	private AtomicInteger count = new AtomicInteger(0);
	
	private ReentrantLock putLock = new ReentrantLock();
	
	private Condition full = putLock.newCondition();
	
	private ReentrantLock takeLock = new ReentrantLock();
	
	private Condition empty = takeLock.newCondition();
	
	
	private void signalEmpty() {
		ReentrantLock takeLock = this.takeLock;
		takeLock.lock();
		try {
			empty.signal();
		} finally {
			takeLock.unlock();
		}
	}
	
	private void signalFull() {
		ReentrantLock putLock = this.putLock;
		putLock.lock();
		try {
			full.signal();
		} finally {
			putLock.unlock();
		}
	}
	
	
	private void fullyLock() {
		putLock.lock();
		takeLock.lock();
	}
	
	private void fullyUnlock() {
		putLock.unlock();
		takeLock.unlock();
	}
	
	private void insert(T obj) {
		queue.add(obj);
	}
	
	private T extract() {
		return queue.poll();
	}
	
	public MyBlockingQueue(int capacity) {
		if(capacity <= 0)
			throw new IllegalArgumentException();
		this.capacity = capacity;
	}
	
	public MyBlockingQueue() {
		this(16);
	}
	
	public int size() {
		return count.get();
	}
	
	public void put(T obj) {
		if(obj == null)
			throw new IllegalArgumentException();
		int c = -1;
		final ReentrantLock putLock = this.putLock;
		final AtomicInteger count = this.count;
		putLock.lock();
		try {
			while(count.get() == capacity) {
				full.await();
			}
			insert(obj);
			c = count.getAndIncrement();
			if(c + 1 <capacity )
				full.signal();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			putLock.unlock();
		}
		if(c == 0)
			signalEmpty();
	}
	
	
	public boolean offer(T obj, long timeout, TimeUnit timeUnit) {
		if(obj == null)
			throw new IllegalArgumentException();
		final ReentrantLock putLock = this.putLock;
		final AtomicInteger count = this.count;
		int c = -1;
		long nanos = timeUnit.toNanos(timeout);
		putLock.lock();
		try {
			while(true) {
				if(count.get() < capacity) {
					insert(obj);
					c = count.getAndIncrement();
					if(c +1 <capacity)
						full.signal();
					break;
				}
				if(nanos <= 0)
					return false;
				try {
					nanos = full.awaitNanos(nanos);
				} catch (InterruptedException e) {
					e.printStackTrace();
					full.signal();
				}
			}
		} finally {
			putLock.unlock();
		}
		if(c == 0)
			signalEmpty();
		return true;
	}
	
	
	public T take() {
		T obj = null;
		int c = -1;
		final ReentrantLock takeLock = this.takeLock;
		final AtomicInteger count = this.count;
		takeLock.lock();
		try {
			while(count.get() == 0) {
				try {
					empty.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			obj = extract();
			c = count.getAndDecrement();
			if( c > 1)
				empty.signal();
			
		} finally {
			takeLock.unlock();
		}
		if(c == capacity)
			signalFull();
		return obj;
	}
	
	
	
	public void clear() {
		fullyLock();
		queue.clear();
		fullyUnlock();
	}
		

}
