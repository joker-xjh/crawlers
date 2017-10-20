package demo30;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractOwnableSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;

@SuppressWarnings("all")
public class AQS extends AbstractOwnableSynchronizer{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6113471596213930466L;

	//AbstractQueuedSynchronizer
	
	static final class Node {
		static final Node SHARED = new Node();
		
		static final Node EXCLUSIVE = null;
		
		static final int CANCELED = 1;
		
		static final int SIGNAL = -1;
		
		static final int CONDITION = -2;
		
		static final int PROPAGATE = -3;
		
		volatile int waitStatus;
		
		volatile Node pre;
		
		volatile Node next;
		
		volatile Thread thread;
		
		Node nextWaiter;
		
		final boolean isShared() {
			return nextWaiter == SHARED;
		}
		
		Node() {
			
		}
		
		Node (Thread thread, Node mode){
			this.thread = thread;
			this.nextWaiter = mode;
		}
		
		Node(Thread thread, int status) {
			this.thread = thread;
			this.waitStatus = status;
		}
		
		Node predecessor() {
			return pre;
		}
		
	}
	
	
	private transient volatile Node head;
	
	private transient volatile Node tail;
	
	private transient volatile int state;
	
	private  transient volatile Thread exclusiveOwnerThread;
	
	protected int getState() {
		return state;
	}
	
	protected void setState(int state) {
		this.state = state;
	}
	
	protected final boolean hasQueuedPredecessors() {
		Node t = tail;
		Node h = head;
		Node s;
		return h!=t && ((s=h.next)==null || s.thread != Thread.currentThread());
	}
	
	protected final boolean compareAndSetState(int old, int set) {
		if(state == old) {
			state = set;
			return true;
		}
		return false;
	}
	
	
	public final void acquire(int arg) {
		if(!tryAqquire(arg)
			&& acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
			selfInterrupt();
	}
	
	static void selfInterrupt() {
		Thread.currentThread().interrupt();
	}
	
	protected boolean tryAqquire(int acquires) {
		final Thread current = Thread.currentThread();
		int c = getState();
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
	
	
	private Node addWaiter(Node mode) {
		Node node = new Node(Thread.currentThread(), mode);
		Node pred = tail;
		if(pred != null) {
			node.pre = pred;
			if(compareAndSetTail(pred, node)) {
				pred.next = node;
				return node;
			}
		}
		enq(node);
		return node;
	}
	
	
	private Node enq(Node node) {
		for(;;) {
			Node t = tail;
			if(t == null) {
				if(compareAndSetHead(new Node())) {
					tail = head;
				}
			}
			else {
				node.pre = t;
				if(compareAndSetTail(t, node)) {
					t.next = node;
					return t;
				}
			}
		}
	}
	
	void setHead(Node node) {
		head = node;
	}
	
	final boolean acquireQueued(Node node, int arg) {
		boolean failed = true;
		try {
			boolean interrupted = false;
			for(;;) {
				final Node p = node.predecessor();
				if(p == head && tryAqquire(arg)) {
					setHead(node);
					node.next = null;
					failed = false;
					return interrupted;
				}
				if(shouldParkAfterFailedAcquire(p, node)
					&& parkAndCheckInterrupt())
					interrupted = true;
			}
			
		} finally {
			if(failed);
		}
	}
	
	protected boolean isHeldExclusively() {
		throw new UnsupportedOperationException();
	}
	
	private static boolean shouldParkAfterFailedAcquire(Node pre, Node node) {
		int ws = pre.waitStatus;
		if(ws == Node.SIGNAL) {
			return true;
		}
		if(ws > 0) {
			do {
				node.pre = pre = pre.pre;
			} while (pre.waitStatus > 0);
			pre.next = node;
		}
		else {
			compareAndSetWaitStatus(pre, ws, Node.SIGNAL);
		}
		return false;
	}
	
	private static boolean compareAndSetWaitStatus(Node pre, int old, int set) {
		if(pre.waitStatus == old) {
			pre.waitStatus = set;
			return true;
		}
		return false;
	}
	
	private final boolean parkAndCheckInterrupt() {
		LockSupport.park(this);
		return Thread.interrupted();
	}
	
	private void unparkSuccessor(Node node) {
		Node s = node.next;
		if(s == null || s.waitStatus > 0) {
			s = null;
			for(Node t = tail; t!=null && t!=node; t=t.pre) {
				if(t.waitStatus <= 0)
					s=t;
			}
		}
		if(s != null)
			LockSupport.unpark(s.thread);
	}
	
	
	private boolean compareAndSetTail(Node old, Node set) {
		if(tail == old) {
			tail = set;
			return true;
		}
		return false;
	}
	
	private boolean compareAndSetHead(Node node) {
		if(head == null) {
			head = node;
			return true;
		}
		return false;
	}
	
	public final boolean release(int arg) {
		if(tryRelease(arg)) {
			Node h = head;
			if(h!=null && h.waitStatus != 0)
				unparkSuccessor(h);
			return true;
		}
		return false;
	}
	
	protected boolean tryRelease(int arg) {
		throw new UnsupportedOperationException();
	}
	
	 final class FairSync extends AQS {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 8471415655061808000L;

		final void lock() {
			acquire(1);
		}
		
		
		protected final boolean tryRelease(int releases) {
			int c = getState() - releases;
			if(Thread.currentThread() != getExclusiveOwnerThread())
				throw new IllegalMonitorStateException();
			boolean free = false;
			if(c == 0) {
				free = true;
				setExclusiveOwnerThread(null);
			}
			setState(c);
			return free;
		}
	}
	 
	
	
	public class ConditionObject implements Condition ,Serializable{
		
		private transient Node firstWaiter;
		private transient Node lastWaiter;
		
		private Node addConditionWaiter() {
			Node t = lastWaiter;
			if(t != null && t.waitStatus != Node.CONDITION) {
				unlinkCancelledWaiters();
				t = lastWaiter;
			}
			Node node = new Node(Thread.currentThread(), Node.CONDITION);
			if(t == null)
				firstWaiter = node;
			else
				t.nextWaiter = node;
			lastWaiter = node;
			return node;
		}
		
		private void unlinkCancelledWaiters() {
			Node t = firstWaiter;
			Node pre = null;
			while(t != null) {
				Node next = t.nextWaiter;
				if(t.waitStatus != Node.CONDITION) {
					t.nextWaiter = null;
					if(pre == null)
						firstWaiter = next;
					else
						pre.nextWaiter = next;
					if(next == null)
						lastWaiter = pre;
				}
				else
					pre = t;
				t = next;
			}
		}
		
		final int fullyRelease(Node node) {
			boolean failed = true;
			try {
				int savedStatus = getState();
				if(release(savedStatus)) {
					failed = false;
					return savedStatus;
				}
				else {
					throw new IllegalMonitorStateException();
				}
			} finally {
				if(failed)
					node.waitStatus = Node.CANCELED;
			}
		}
		
		final boolean isOnSyncQueue(Node node) {
			if(node.waitStatus == Node.CONDITION || node.pre == null)
				return false;
			if(node.next != null)
				return true;
			return findNodeFromTail(node);
		}
		
		private boolean findNodeFromTail(Node node) {
			Node t = tail;
			for(;;) {
				if(t == node)
					return true;
				else if(t == null)
					return false;
				t = t.pre;
			}
		}
		
		

		@Override
		public void await() throws InterruptedException {
			if(Thread.interrupted())
				throw new InterruptedException();
			Node node = addConditionWaiter();
			int savedStatus = fullyRelease(node);
			int interruptMode = 0;
			while(!isOnSyncQueue(node)) {
				LockSupport.park(this);
				if((interruptMode = checkInterruptWhileWaiting(node)) !=0)
					break;
			}
			
			if(acquireQueued(node, savedStatus) && interruptMode != -1)
				interruptMode = 1;
			if(node.nextWaiter != null)
				unlinkCancelledWaiters();
			if(interruptMode != 0)
				reportInterruptAfterWait(interruptMode);
		}
		
		private void reportInterruptAfterWait(int interruptMode) throws InterruptedException {
			if(interruptMode == -1)
				throw new InterruptedException();
			else if(interruptMode == 1)
				selfInterrupt();
		}
		
		
		
		private void doSignal(Node node) {
			do {
				if((firstWaiter = node.next) == null)
					lastWaiter = null;
				node.next = null;
			} while (!transferForSignal(node) && (node = firstWaiter) != null);
		}
		
		final boolean transferForSignal(Node node) {
			if(!compareAndSetWaitStatus(node, Node.CONDITION, 0))
				return false;
			Node p = enq(node);
			int ws = p.waitStatus;
			if(ws > 0 || !compareAndSetWaitStatus(p, ws, Node.SIGNAL))
				LockSupport.unpark(node.thread);
			return true;
		}
		
		private int checkInterruptWhileWaiting(Node node) {
			return Thread.interrupted() ? (
					transferAfterCancelledWait(node) ? -1 : 1
					) : 0;
		}
		
		
		final boolean transferAfterCancelledWait(Node node) {
			if(compareAndSetWaitStatus(node, Node.CONDITION, 0)) {
				enq(node);
				return true;
			}
			while(!isOnSyncQueue(node))
				Thread.yield();
			return false;
		}
		

		@Override
		public void awaitUninterruptibly() {
			Node node = addConditionWaiter();
			int savedStatus = fullyRelease(node);
			boolean interrupted = false;
			while(!isOnSyncQueue(node)) {
				LockSupport.park(this);
				if(Thread.interrupted())
					interrupted = true;
			}
			if(acquireQueued(node, savedStatus) || interrupted)
				selfInterrupt();
		}

		@Override
		public long awaitNanos(long nanosTimeout) throws InterruptedException {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean await(long time, TimeUnit unit) throws InterruptedException {
			boolean timeout = false;
			if(Thread.interrupted())
				throw new InterruptedException();
			long nanostime = unit.toNanos(time);
			Node node = addConditionWaiter();
			int savedStatus = fullyRelease(node);
			long deadline = System.nanoTime() + nanostime;
			int interruptMode = 0;
			while(!isOnSyncQueue(node)) {
				if(nanostime <= 0) {
					timeout = transferAfterCancelledWait(node);
					break;
				}
				if((interruptMode = checkInterruptWhileWaiting(node))!=0)
					break;
				nanostime = deadline - System.nanoTime();
			}
			if(acquireQueued(node, savedStatus) && interruptMode != -1)
				interruptMode = 1;
			if(node.nextWaiter != null)
				unlinkCancelledWaiters();
			if(interruptMode != 0)
				reportInterruptAfterWait(interruptMode);
			return !timeout;
		}

		@Override
		public boolean awaitUntil(Date deadline) throws InterruptedException {
			return false;
		}

		@Override
		public void signal() {
			if(!isHeldExclusively())
				throw new IllegalMonitorStateException();
			Node first = firstWaiter;
			if(first != null)
				doSignal(first);
		}

		@Override
		public void signalAll() {
			
		}
		
	}
	
	
	
	
	
	
	
	
	
	

}
