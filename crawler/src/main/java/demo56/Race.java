package demo56;

import java.util.concurrent.CountDownLatch;

public class Race {
	
	public static void race(int racers) throws InterruptedException {
		CountDownLatch ready = new CountDownLatch(racers);
		CountDownLatch start = new CountDownLatch(1);
		CountDownLatch end = new CountDownLatch(racers);
		for(int i=0; i<racers; i++) {
			final int num = i;
			new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println("ready:"+num);
					ready.countDown();
					try {
						start.await();
					} catch (InterruptedException  e) {
						Thread.currentThread().interrupt();
					}
					finally {
						System.out.println("end: " + num);
						end.countDown();
					}
				}
			}).start();
		}
		
		ready.await();
		System.out.println("********************** all ready!!! **********************");
		start.countDown();
		end.await();
		System.out.println("********************** all end!!! **********************");
	}

	public static void main(String[] args) {
		try {
			race(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
