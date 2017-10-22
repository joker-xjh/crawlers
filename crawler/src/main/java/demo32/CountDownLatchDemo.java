package demo32;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountDownLatchDemo {

	public static void main(String[] args) {
		CountDownLatch latch = new CountDownLatch(2);
		ExecutorService pool = Executors.newFixedThreadPool(5);
		pool.execute(new Worker(latch));
		pool.execute(new Worker(latch));
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//..........
	}
	
	private static class Worker implements Runnable {
		
		private CountDownLatch latch;
		
		public Worker(CountDownLatch latch) {
			this.latch = latch;
		}

		@Override
		public void run() {
			doSomething();
			latch.countDown();
		}
		
		public void doSomething() {
			
		}
		
	}

}
