package demo29;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SemaphoreOnLock {
	
	private final Lock lock = new ReentrantLock();
	
	private final Condition permitsAvailable = lock.newCondition();
	
	private int permits;
	
	public SemaphoreOnLock(int permits) {
		this.permits = permits;
	}
	
	public void acquire() throws InterruptedException {
		lock.lock();
		try {
			while(permits <= 0)
				permitsAvailable.await();
			permits--;
		} finally {
			lock.unlock();
		}
	}
	
	public void release() {
		lock.lock();
		try {
			permits++;
			permitsAvailable.signal();
		} finally {
			lock.unlock();
		}
	}
	
	

}
