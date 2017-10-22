package demo32;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountDownLatchDemo2 {

	public static void main(String[] args) {
		CountDownLatch start = new CountDownLatch(1);
		CountDownLatch latch = new CountDownLatch(3);
		ExecutorService pool = Executors.newCachedThreadPool();
		pool.execute(new Worker(latch, start));
		pool.execute(new Worker(latch, start));
		pool.execute(new Worker(latch, start));
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		start.countDown();
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("over");

		//.........
	}
	
	private static class Worker implements Runnable {
		
		private CountDownLatch latch;
		private CountDownLatch start;
		
		public Worker(CountDownLatch latch, CountDownLatch start) {
			this.latch = latch;
			this.start = start;
		}

		@Override
		public void run() {
			try {
				start.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			doSomething();
			latch.countDown();
		}
		
		public void doSomething() {
			System.out.println(Thread.currentThread().getName());
		}
		
	}

}
