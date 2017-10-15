package demo29;

public class SimulateCAS {
	
	private int value;
	
	public SimulateCAS(int value) {
		this.value = value;
	}
	
	public synchronized int getValue() {
		return value;
	}
	
	public synchronized boolean compareAndSet(int expect, int newValue) {
		if(value == expect) {
			value = newValue;
			return true;
		}
		return false;
	}
	
	

}
