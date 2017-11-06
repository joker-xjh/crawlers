package demo35;

import java.util.HashMap;
import java.util.Map;

public class MyReadWriteLock2 {
	
	private Map<Thread, Integer> readingThreads = new HashMap<>();
	private int writers;
	private int writeRequests;
	
	public synchronized void lockRead() throws InterruptedException {
		Thread current = Thread.currentThread();
		while(!canGrantReadAccess(current))
			wait();
		readingThreads.put(current, readingThreads.getOrDefault(current, 0)+1);
	}
	
	public synchronized void unlockRead() throws InterruptedException {
		Thread current = Thread.currentThread();
		int count = readingThreads.get(current);
		if(count == 1)
			readingThreads.remove(current);
		else
			readingThreads.put(current, count-1);
		notifyAll();
	}
	
	
	private boolean canGrantReadAccess(Thread current) {
		if(writers > 0)
			return false;
		if(isReader(current))
			return true;
		if(writeRequests > 0)
			return false;
		return true;
	}
	
	private boolean isReader(Thread current) {
		return readingThreads.get(current) != null;
	}
	
	

}
