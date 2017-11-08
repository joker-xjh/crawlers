package demo37;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadControl {
	
	private Lock lock = new ReentrantLock();
	
	private Condition condition = lock.newCondition();
	
	private boolean paused, cancal;
	
	public void paused() {
		lock.lock();
		try {
			paused = true;
		} finally {
			lock.unlock();
		}
	}
	
	public void resume() {
		lock.lock();
		try {
			if(!paused)
				return;
			paused = false;
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}
	
	public void cancal() {
		lock.lock();
		try {
			if(cancal)
				return;
			cancal = true;
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}
	
	
	public void waitIfPaused() throws InterruptedException {
		lock.lock();
		try {
			while(paused && !cancal) {
				condition.await();
			}
		} finally {
			lock.unlock();
		}
	}
	
	public boolean isCancel() {
		return cancal;
	}
	
	
	

}
