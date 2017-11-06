package demo35;

import java.util.HashMap;
import java.util.Map;

public class MyReadWriteLock4 {
	private Map<Thread, Integer> readingThreads = new HashMap<>();
	
	private int writeAccess;
	
	private int writeRequest;
	
	private Thread writingThread;
	
	public synchronized void lockRead() throws InterruptedException {
		Thread current = Thread.currentThread();
		while(!canGrantReadAccess(current))
			wait();
		readingThreads.put(current, readingThreads.getOrDefault(current, 0)+1);
	}
	
	public synchronized void unlockRead() {
		Thread current = Thread.currentThread();
		if(!isReader(current))
			throw new IllegalMonitorStateException();
		int count = readingThreads.get(current);
		if(count == 1)
			readingThreads.remove(current);
		else
			readingThreads.put(current, count-1);
		notifyAll();
	}
	
	
	private boolean canGrantReadAccess(Thread current) {
		if(isWriter(current))
			return true;
		if(hasWriter())
			return false;
		if(isReader(current))
			return true;
		if(hasWriteRequests())
			return false;
		return true;
	}
	
	
	public synchronized void lockWrite() throws InterruptedException {
		writeRequest++;
		Thread current = Thread.currentThread();
		while(!canGrantWriteAccess(current))
			wait();
		writeRequest--;
		writeAccess++;
		writingThread = current;
	}
	
	
	public synchronized void unlockWrite() throws InterruptedException {
		Thread current = Thread.currentThread();
		if(!isWriter(current))
			throw new IllegalMonitorStateException();
		writeAccess--;
		if(writeAccess == 0)
			writingThread = null;
		notifyAll();
	}
	
	
	private boolean canGrantWriteAccess(Thread current) {
		if(isOnlyReader(current))
			return true;
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
	
	private boolean isReader(Thread current) {
		return readingThreads.get(current) != null;
	}
	
	private boolean isOnlyReader(Thread current) {
		return readingThreads.size() == 1 && readingThreads.get(current) != null;
	}
	
	private boolean hasWriter() {
		return writingThread != null;
	}
	
	private boolean isWriter(Thread current) {
		return current == writingThread;
	}
	
	private boolean hasWriteRequests() {
		return writeRequest > 0;
	}

}
