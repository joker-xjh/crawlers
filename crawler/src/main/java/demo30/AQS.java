package demo30;

import java.util.concurrent.locks.AbstractOwnableSynchronizer;
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
		
		Node(){}
		
		Node (Thread thread, Node mode){
			this.thread = thread;
			this.nextWaiter = mode;
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
			}
			
		} finally {
			if(failed);
		}
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
		
		
		public final boolean release(int arg) {
			if(tryRelease(arg)) {
				Node h = head;
				if(h!=null && h.waitStatus != 0)
					unparkSuccessor(h);
				return true;
			}
			return false;
		}
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	

}
