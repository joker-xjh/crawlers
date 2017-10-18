package demo31;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class CDL {
	
	private final class Sync extends AbstractQueuedSynchronizer {
		private static final long serialVersionUID = -667587037803742177L;

		Sync(int count) {
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
				int c = getCount();
				if(c == 0)
					return false;
				int nextc = c-1;
				if(compareAndSetState(c, nextc))
					return nextc == 0;
			}
		}
	}
	
	private Sync sync;
	
	public CDL(int count) {
		if(count < 0)
			throw new IllegalArgumentException("count < 0");
		sync = new Sync(count);
	}
	
	public void await() throws InterruptedException {
		sync.acquireInterruptibly(1);
	}
	
	public void await(long time, TimeUnit unit) throws InterruptedException {
		sync.tryAcquireNanos(1, unit.toNanos(time));
	}
	
	
	public void countdown() {
		sync.releaseShared(1);
	}
	
	public long getCount() {
		return sync.getCount();
	}
	
	
	

}
