package demo57;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

public class FIFOMutex {
	
	private final AtomicBoolean lock = new AtomicBoolean(false);
	
	private final Queue<Thread> waiters = new ConcurrentLinkedQueue<>();
	
	public void lock() {
		boolean wasInterrupted = false;
		Thread current = Thread.currentThread();
		waiters.add(current);
		while(waiters.peek() != current || !lock.compareAndSet(false, true)) {
			LockSupport.park(this);
			if(Thread.interrupted())
				wasInterrupted = true;
		}
		waiters.remove();
		if(wasInterrupted)
			current.interrupt();
	}
	
	public void unlock() {
		lock.compareAndSet(true, false);
		LockSupport.unpark(waiters.peek());
	}

}
