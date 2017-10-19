package demp32;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBuffer {
	
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition notEmpty = lock.newCondition();
	private final Condition notFull = lock.newCondition();
	private int put, take, count;
	private Object[] queue;
	
	public BoundedBuffer(int capacity) {
		queue = new Object[capacity];
	}
	
	public void put(Object obj) throws InterruptedException {
		final ReentrantLock lock = this.lock;
		try {
			while(count == queue.length)
				notFull.await();
			queue[put++] = obj;
			if(put == queue.length)
				put = 0;
			count++;
			notEmpty.signal();
		} finally {
			lock.unlock();
		}
	}
	
	
	public Object take() throws InterruptedException {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			while(count == 0)
				notEmpty.await();
			Object obj = queue[take];
			queue[take++] = null;
			if(take == queue.length)
				take = 0;
			count--;
			notFull.signal();
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

}
