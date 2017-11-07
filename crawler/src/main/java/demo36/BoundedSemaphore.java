package demo36;

public class BoundedSemaphore {
	
	private int signals;
	
	private int bound;
	
	public BoundedSemaphore(int bound) {
		this.bound = bound;
	}
	
	public synchronized void take() throws InterruptedException {
		while(signals == bound)
			wait();
		signals++;
		notify();
	}
	
	public synchronized void release() throws InterruptedException {
		while(signals == 0)
			wait();
		signals--;
		notify();
	}

}
