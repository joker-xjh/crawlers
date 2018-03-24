package demo65;

public class MyLock {
	
	private Object monitor = new Object();
	
	private boolean flag = false;
	
	public void doWait() {
		synchronized (monitor) {
			while(!flag) {
				try {
					monitor.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			monitor = false;
		}
	}
	
	
	public void doSignal() {
		synchronized (monitor) {
			flag = true;
			monitor.notify();
		}
	}
	
}
