package demo32;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class MySemaphore {
	
	abstract static class Sync extends AbstractQueuedSynchronizer {
		private static final long serialVersionUID = -914361308143644802L;
		public Sync(int permits) {
			setState(permits);
		}
		final int getPermits() {
			return getState();
		}
		@Override
		protected boolean tryReleaseShared(int arg) {
			for(;;) {
				int current = getState();
				int remaining = current + arg;
				if(remaining < 0)
					throw new Error("");
				if(compareAndSetState(current, remaining))
					return true;
			}
		}
		
		 final int nonfairTryAcquireShared(int acquires) {
			 for(;;) {
				 int current = getState();
				 int remaining =current - acquires;
				 if(remaining <0 || compareAndSetState(current, remaining))
					 return remaining;
			 }
		 }
		
	}
	
	
	static final class NonfairSync extends Sync {
		private static final long serialVersionUID = -4300331808071555502L;

		public NonfairSync(int permits) {
			super(permits);
		}
		
		@Override
		protected int tryAcquireShared(int arg) {
			return nonfairTryAcquireShared(arg);
		}
		
	}
	
	
	static final class FairSync extends Sync {
		private static final long serialVersionUID = -9071752906538758602L;

		public FairSync(int permits) {
			super(permits);
		}
		
		@Override
		protected int tryAcquireShared(int arg) {
			for(;;) {
				if(hasQueuedPredecessors())
					return -1;
				int current = getState();
				int remaining = current - arg;
				if(remaining < 0 || compareAndSetState(current, remaining))
					return remaining;
			}
		}
		
	}
	
	
	private Sync sync;
	
	public MySemaphore(int permits) {
		sync = new NonfairSync(permits);
	}
	
	public MySemaphore(int permits, boolean fair) {
		sync = fair ? new FairSync(permits) : new NonfairSync(permits);
	}
	
	public void acquire() throws InterruptedException{
		sync.acquireSharedInterruptibly(1);
	}
	
	public void acquireUninterruptibly() {
		sync.acquireShared(1);
	}
	
	public boolean tryAcquire() {
		return sync.nonfairTryAcquireShared(1) >= 0;
	}
	
	public void release() {
		sync.releaseShared(1);
	}

}
