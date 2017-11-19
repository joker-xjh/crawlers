package demo52;

import java.util.concurrent.atomic.AtomicInteger;

public class ThreadId {
	
	private static final AtomicInteger id = new AtomicInteger(0);
	
	private static final ThreadLocal<Integer> THREAD_LOCAL = new ThreadLocal<Integer>() {
		protected Integer initialValue() {
			return id.getAndIncrement();
		};
	};
	
	public static int getId() {
		return THREAD_LOCAL.get();
	}

}
