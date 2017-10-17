package demo30;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class MutextLock implements Lock{
	
	private Sync sync = new Sync();

	@Override
	public void lock() {
		sync.acquire(1);
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		sync.acquireInterruptibly(1);
	}

	@Override
	public boolean tryLock() {
		return sync.tryAcquire(1);
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		return sync.tryAcquireNanos(1, unit.toNanos(time));
	}

	@Override
	public void unlock() {
		sync.tryRelease(1);
	}

	@Override
	public Condition newCondition() {
		return sync.newCondition();
	}
	
	static class Sync extends AbstractQueuedSynchronizer {

		private static final long serialVersionUID = -79745347556413938L;
		
		public boolean tryAcquire(int acquires) {
			int c = getState();
			Thread current = Thread.currentThread();
			if(c == 0) {
				if(compareAndSetState(0, acquires)) {
					setExclusiveOwnerThread(current);
					return true;
				}
			}
			else if(current == getExclusiveOwnerThread()) {
				int nextc = c + acquires;
				if(nextc < 0)
					throw new Error("Maximum lock count exceeded");
				setState(nextc);
				return true;
			}
			
			return false;
		}
		
		public boolean tryRelease(int release) {
			if(Thread.currentThread() != getExclusiveOwnerThread())
				throw new IllegalMonitorStateException();
			int c = getState() - release;
			boolean free = false;
			if(c == 0) {
				free = true;
				setExclusiveOwnerThread(null);
			}
			setState(c);
			return free;
		}
		
		public Condition newCondition() {
			return new ConditionObject();
		}
		
		
		
	}
}
