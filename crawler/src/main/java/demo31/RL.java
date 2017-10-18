package demo31;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class RL implements Lock{
	
	abstract static class Sync extends AbstractQueuedSynchronizer{

		private static final long serialVersionUID = 3317647840403815641L;
		
		abstract void lock();
		
		final boolean nonfairTryAcquire(int acquires) {
			int state = getState();
			Thread current = Thread.currentThread();
			if(state == 0) {
				if(compareAndSetState(0, state)) {
					setExclusiveOwnerThread(current);
					return true;
				}
			}
			else if(getExclusiveOwnerThread() == current) {
				int nextc = state + acquires;
				if(nextc < 0)
					throw new Error("Maximum lock count exceeded");
				setState(nextc);
				return true;
			}
			return false;
		}
		
		protected boolean tryRelease(int release) {
			boolean free = false;
			int state = getState();
			Thread current = Thread.currentThread();
			if(current != getExclusiveOwnerThread())
				throw new IllegalMonitorStateException();
			int nextc = state - release;
			if(nextc == 0) {
				setExclusiveOwnerThread(null);
				free = true;
			}
			setState(nextc);
			return free;
		}
		
		protected final boolean isHeldExclusively() {
			return getExclusiveOwnerThread() == Thread.currentThread();
		}
		
		final ConditionObject newCondition() {
			return new ConditionObject();
		}
		
		final Thread getOwner() {
			return getState() == 0 ? null : getExclusiveOwnerThread();
		}
		
		final boolean isLocked() {
			return getState() == 0;
		}
		
		final int getHoldCount() {
			return isHeldExclusively() ? getState() : 0;
		}
	}
	
	
	static final class NonfairSync extends Sync{
		private static final long serialVersionUID = -6144538652754408751L;

		@Override
		void lock() {
			if(compareAndSetState(0, 1))
				setExclusiveOwnerThread(Thread.currentThread());
			else
				acquire(1);
		}
		
		protected final boolean tryAcquire(int acquires) {
			return nonfairTryAcquire(acquires);
		}
		
	}
	
	static final class FairSync extends Sync {
		private static final long serialVersionUID = 2575106702568403496L;

		@Override
		void lock() {
			acquire(1);
		}
		
		protected final boolean tryAcquire(int acquires) {
			int c = getState();
			Thread current = Thread.currentThread();
			if(c == 0) {
				if(!hasQueuedPredecessors() && compareAndSetState(0, acquires)) {
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
		
	}
	
	
	private Sync sync;
	
	public RL() {
		sync = new NonfairSync();
	}
	
	public RL(boolean fair) {
		sync = fair ? new FairSync() :  new NonfairSync();
	}
	
	@Override
	public void lock() {
		sync.lock();
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		sync.acquireInterruptibly(1);
	}

	@Override
	public boolean tryLock() {
		return sync.nonfairTryAcquire(1);
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		return sync.tryAcquireNanos(1, unit.toNanos(time));
	}

	@Override
	public void unlock() {
		sync.release(1);
	}

	@Override
	public Condition newCondition() {
		return sync.newCondition();
	}
}
