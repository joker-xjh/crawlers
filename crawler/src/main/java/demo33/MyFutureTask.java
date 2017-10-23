package demo33;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.LockSupport;

import org.apache.commons.lang.ObjectUtils.Null;

@SuppressWarnings("all")
public class MyFutureTask<V> implements RunnableFuture<V> {
	
	static final class WaitNode{
		volatile WaitNode next;
		volatile Thread thread;
		public WaitNode() {
			thread = Thread.currentThread();
		}
	}
	
	private volatile int state;
	private final static int NEW = 0;
	private final static int COMPLETING = 1;
	private final static int NORMAL = 2;
	private final static int EXCEPTIONAL = 3;
	private static final int CANCELLED    = 4;
	private static final int INTERRUPTING = 5;
	private static final int INTERRUPTED  = 6;
	
	private Callable<V> callable;
	
	private Object outcome;
	
	private volatile Thread runner;
	
	private volatile WaitNode waiters;
	
	
	public MyFutureTask(Callable<V> callable) {
		if(callable == null)
			throw new NullPointerException();
		this.callable = callable;
		this.state = NEW;
	}
	
	public MyFutureTask(Runnable runnable, V result) {
		callable = Executors.callable(runnable, result);
		this.state = NEW;
	}
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		if(state != NEW)
			return false;
		if(mayInterruptIfRunning) {
			if(!UNSAFE.compareAndSwapInt(this, stateOffset, NEW, INTERRUPTING))
				return false;
			Thread t = runner;
			if(t!=null)
				t.interrupt();
			UNSAFE.compareAndSwapInt(this, stateOffset, INTERRUPTING, INTERRUPTED);
		}
		else if(!UNSAFE.compareAndSwapInt(this, stateOffset, NEW, CANCELLED))
			return false;
		finishCompletion();
		return true;
	}

	@Override
	public boolean isCancelled() {
		return state >= CANCELLED;
	}

	@Override
	public boolean isDone() {
		return state != NEW;
	}

	@Override
	public V get() throws InterruptedException, ExecutionException {
		int s = state;
		if(s <= COMPLETING)
			s = awaitDone(false, 0);
		return report(s);
	}
	
	private int awaitDone(boolean timed, long nanos) throws InterruptedException{
		final long deadline = timed ? System.nanoTime() + nanos : 0;
		WaitNode q = null;
		boolean queued = false;
		for(;;) {
			if(Thread.interrupted()) {
				throw new InterruptedException();
			}
			int s = state;
			if(s > COMPLETING) {
				if(q != null)
					q.thread = null;
				return s;
			}
			else if(s == COMPLETING)
				Thread.yield();
			else if(q == null)
				q = new WaitNode();
			else if(!queued)
				queued = UNSAFE.compareAndSwapObject(this, waitersOffset,
	                    q.next = waiters, q);
			else if(timed) {
				nanos = deadline - System.nanoTime();
				if(nanos <= 0)
					return state;
				LockSupport.parkNanos(this, nanos);
			}
			else {
				LockSupport.park(this);
			}
		}
	}
	
	
	

	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		if(unit == null)
			throw new NullPointerException();
		int s = state;
		if(s <= COMPLETING &&
			(s=awaitDone(true, unit.toNanos(timeout))) <= COMPLETING)
			throw new TimeoutException();
		return report(s);
	}

	@Override
	public void run() {
		if(state != NEW || 
		   !UNSAFE.compareAndSwapObject(this, runnerOffset, null, Thread.currentThread()))
			return;
		try {
			Callable<V> callable =this.callable;
			if(callable != null && state == NEW) {
				V result;
				boolean ran;
				try {
					result = callable.call();
					ran = true;
				} catch (Throwable e) {
					result = null;
					ran = false;
					setException(e);
				}
				if(ran)
					set(result);
			}
			
			
		} finally {
			runner = null;
			int s = state;
			if(s >= INTERRUPTING) {
				//........
			}
		}
		
	}
	
	
	protected void set(V v) {
		if(UNSAFE.compareAndSwapInt(this, stateOffset, NEW, COMPLETING)) {
			outcome = v;
			UNSAFE.putOrderedInt(this, stateOffset, NORMAL);
			finishCompletion();
		}
	}
	
	protected void setException(Throwable throwable) {
		if(UNSAFE.compareAndSwapInt(this, stateOffset, NEW, COMPLETING)) {
			outcome = throwable;
			UNSAFE.putOrderedInt(this, stateOffset, EXCEPTIONAL);
			finishCompletion();
		}
	}
	
	
	private void finishCompletion() {
		for(WaitNode q; (q=waiters)!=null;) {
			if(UNSAFE.compareAndSwapObject(this, waitersOffset, q, null)) {
				for(;;) {
					Thread t = q.thread;
					if(t!=null) {
						q.thread=null;
						LockSupport.unpark(t);
					}
					WaitNode next = q.next;
					if(next == null)
						break;
					q.next = null;
					q = next;
				}
				
				break;
			}
			
		}
	}
	
	
	private V report(int state) throws ExecutionException {
		if(state == NORMAL)
			return (V) outcome;
		else if(state >= CANCELLED)
			throw new CancellationException();
		else
			throw new ExecutionException((Throwable)outcome);
	}
	
	
	private static final sun.misc.Unsafe UNSAFE;
    private static final long stateOffset;
    private static final long runnerOffset;
    private static final long waitersOffset;
    static {
        try {
            UNSAFE = sun.misc.Unsafe.getUnsafe();
            Class<?> k = FutureTask.class;
            stateOffset = UNSAFE.objectFieldOffset
                (k.getDeclaredField("state"));
            runnerOffset = UNSAFE.objectFieldOffset
                (k.getDeclaredField("runner"));
            waitersOffset = UNSAFE.objectFieldOffset
                (k.getDeclaredField("waiters"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }
	

}
