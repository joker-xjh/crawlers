package demo34;

public class MyLock {
	
	private volatile boolean lock = false;
	
	public synchronized void lock() throws InterruptedException {
		while(lock) {
			wait();
		}
		lock = true;
	}
	
	public synchronized void unlock() {
		lock = false;
		notify();
	}

}
