package demo37;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyReadWriteLock {
	
	private Set<Thread> readingThreads = new HashSet<>();
	
	private Thread writingThread;
	
	private ReentrantLock lock = new ReentrantLock();
	
	private Condition condition = lock.newCondition();
	
	public void readLock() throws InterruptedException {
		lock.lock();
		try {
			while(writingThread != null)
				condition.wait();
			readingThreads.add(Thread.currentThread());
		} finally {
			lock.unlock();
		}
	}
	
	public void readUnlock() throws InterruptedException {
		lock.lock();
		try {
			Thread current = Thread.currentThread();
			if(!readingThreads.contains(current))
				return;
			if(readingThreads.isEmpty())
				condition.signal();
		} finally {
			lock.unlock();
		}
	}
	
	
	public void writeLock() throws InterruptedException {
		lock.lock();
		try {
			Thread current = Thread.currentThread();
			if(current == writingThread)
				return;
			while(writingThread != null || !readingThreads.isEmpty())
				condition.await();
			writingThread = current;
		} finally {
			lock.unlock();
		}
	}
	
	public void writeUnlock() throws InterruptedException {
		lock.lock();
		try {
			Thread current = Thread.currentThread();
			if(writingThread == current) {
				writingThread = null;
				condition.signal();
			}
		} finally {
			lock.unlock();
		}
	}
	
	
	
	
	

}
