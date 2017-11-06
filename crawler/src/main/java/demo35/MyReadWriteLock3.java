package demo35;

import java.util.HashMap;
import java.util.Map;

public class MyReadWriteLock3 {
	
	private Map<Thread, Integer> readingThreads = new HashMap<>();
	
	private int writeAccess;
	
	@SuppressWarnings("unused")
	private int writeRequests;
	
	private Thread writingThread;
	
	public synchronized void lockWrite() throws InterruptedException {
		writeRequests++;
		Thread current = Thread.currentThread();
		while(!canGrantWriteAccess(current))
			wait();
		writeRequests--;
		writeAccess++;
		writingThread = current;
	}
	
	public synchronized void unlockWrite() throws InterruptedException {
		writeAccess--;
		if(writeAccess == 0)
			writingThread = null;
		notifyAll();
	}
	
	
	private boolean canGrantWriteAccess(Thread current) {
		if(hasReaders())
			return false;
		if(writingThread == null)
			return true;
		if(!isWriter(current))
			return false;
		return true;
	}
	
	private boolean hasReaders() {
		return readingThreads.size() > 0;
	}
	
	private boolean isWriter(Thread current) {
		return current == writingThread;
	}

}
