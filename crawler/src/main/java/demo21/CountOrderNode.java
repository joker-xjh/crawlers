package demo21;

import java.util.concurrent.atomic.AtomicInteger;

public class CountOrderNode extends OrderNode{
	
	private AtomicInteger count;

	public CountOrderNode(Object key, Object value, long timeout) {
		super(key, value, timeout);
		count = new AtomicInteger(0);
	}
	
	public int increment() {
		return count.incrementAndGet();
	}
	
	public int getCount() {
		return count.get();
	}

}
