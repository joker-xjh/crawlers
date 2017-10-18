package demo31;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class MySemaphore {
	
	abstract static class Sync extends AbstractQueuedSynchronizer {
		private static final long serialVersionUID = -873081367018335365L;
		
		public Sync(int permits) {
			setState(permits);
		}
		
		final int getPermits() {
			return getState();
		}
		
		final int nonfairTryAcquireShared(int acquires) {
			for(;;) {
				int available = getState();
				int remaining = available - acquires;
				if(remaining <0 || compareAndSetState(available, remaining))
					return remaining;
			}
		}
		
		protected boolean tryReleaseShared(int release) {
			for(;;) {
				int current = getState();
				int next = current + release;
				if(next < current)
					throw new Error();
				if(compareAndSetState(current, next))
					return true;
			}
		}
		
		protected void reducePermit(int reduce) {
			for(;;) {
				int current = getState();
				int next = current - reduce;
				if(next > current)
					throw new Error();
				if(compareAndSetState(current, next))
					return;
			}
		}
		
		final int drainPermits() {
			for(;;) {
				int current = getState();
				if(current == 0 || compareAndSetState(current, 0))
					return current;
			}
		}
		
		
	}
	
	
	static final class NonfairSync extends Sync {
		private static final long serialVersionUID = 8127536783372803317L;

		public NonfairSync(int permits) {
			super(permits);
		}
		
		@Override
		protected int tryAcquireShared(int arg) {
			return nonfairTryAcquireShared(arg);
		}
		
	}
	
	static final class FairSync extends Sync {
		private static final long serialVersionUID = -2089731031812445803L;

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
	
	public void acquire() throws InterruptedException {
		sync.acquireSharedInterruptibly(1);
	}
	
	public void acquireUninterruptibly() {
		sync.acquireShared(1);
	}
	
	public boolean tryAcquire() {
		return sync.nonfairTryAcquireShared(1) >= 0;
	}
	
	public boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException {
		return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
	}
	
	public void release() {
		sync.releaseShared(1);
	}
	
	public boolean isFair() {
		return sync instanceof FairSync;
	}
	
	

}
