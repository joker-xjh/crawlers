package demo18;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProductQueue<T> {
	
	private T[] queue;
	
	private final Lock lock = new ReentrantLock();
	
	private Condition notFull = lock.newCondition();
	
	private Condition notEmpty = lock.newCondition();
	
	private int head, tail, count;
	
	@SuppressWarnings("unchecked")
	public ProductQueue(int capacity) {
		queue = (T[]) new Object[capacity];
	}
	
	public ProductQueue() {
		this(10);
	}
	
	
	public void put(T obj) throws InterruptedException{
		lock.lock();
		try {
			while(count == getCapacity()) {
				notFull.await();
			}
			queue[tail++] = obj;
			if(tail == getCapacity())
				tail = 0;
			count++;
			notEmpty.signalAll();
		} finally {
			lock.unlock();
		}
	}
	
	
	public T take() throws InterruptedException {
		lock.lock();
		try {
			while(count == 0) {
				notEmpty.await();
			}
			T obj = queue[head];
			queue[head++] = null;
			if(head == getCapacity())
				head=0;
			count--;
			notFull.signalAll();
			return obj;
		} finally {
			lock.unlock();
		}
	}
	
	
	public int size() {
		lock.lock();
		try {
			return count;
		} finally {
			lock.unlock();
		}
	}
	
	
	
	public int getCapacity(){
		return queue.length;
	}
	
	
	

}
