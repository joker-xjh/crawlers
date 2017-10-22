package demo32;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class MyCountDownLatch {
	
	private static final class Sync extends AbstractQueuedSynchronizer {
		private static final long serialVersionUID = 4237425497100746584L;

		public Sync(int count) {
			setState(count);
		}
		
		int getCount() {
			return getState();
		}
		
		@Override
		protected int tryAcquireShared(int arg) {
			return getState() == 0 ? 1 : -1;
		}
		
		@Override
		protected boolean tryReleaseShared(int arg) {
			for(;;) {
				int current = getState();
				if(current == 0)
					return false;
				int next = current - 1;
				if(compareAndSetState(current, next)) {
					return next == 0;
				}
			}
		}
	}
	
	private Sync sync;
	
	public MyCountDownLatch(int count) {
		if(count <= 0)
			throw new IllegalArgumentException();
		sync = new Sync(count);
	}
	
	public void await() throws InterruptedException {
		sync.acquireSharedInterruptibly(1);
	}
	
	public void countDown() {
		sync.releaseShared(1);
	}
	
	public long getCount() {
		return sync.getCount();
	}

}
