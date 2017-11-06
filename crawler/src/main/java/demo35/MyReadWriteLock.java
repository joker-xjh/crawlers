package demo35;

public class MyReadWriteLock {
	
	private int readers;
	private int writers;
	private int writerRequests;
	
	public synchronized void readLock () throws InterruptedException {
		while(writers > 0 || writerRequests > 0)
			wait();
		readers++;
	}
	
	public synchronized void unlockRead () throws InterruptedException {
		readers--;
		notifyAll();
	}
	
	
	public synchronized void writeLock() throws InterruptedException {
		writerRequests++;
		while(readers > 0 || writers > 0)
			wait();
		writerRequests--;
		writers++;
	}
	
	public synchronized void unlockWrite() throws InterruptedException {
		writers--;
		notifyAll();
	}
	

}
