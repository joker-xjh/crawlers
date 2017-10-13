package demo27;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class MyLinkedBlockingQueue<T> {
	
	static class Node<T> {
		T val;
		Node<T> next;
		public Node(T val) {
			this.val = val;
		}
	}
	
	private int capacity;
	
	private final AtomicInteger count = new AtomicInteger(0);
	
	private transient Node<T> head;
	
	private transient Node<T> tail;
	
	private final ReentrantLock putLock = new ReentrantLock();
	
	private final Condition notFull = putLock.newCondition();
	
	private final ReentrantLock takeLock = new ReentrantLock();
	
	private final Condition notEmpty = takeLock.newCondition();
	
	
	
	public MyLinkedBlockingQueue(int capacity) {
		if(capacity <= 0)
			throw new IllegalArgumentException();
		this.capacity = capacity;
		head = tail = new Node<>(null);
	}
	
	public MyLinkedBlockingQueue() {
		this(Integer.MAX_VALUE);
	}
	
	public MyLinkedBlockingQueue(Collection<? extends T> collection) {
		this(Integer.MAX_VALUE);
		final ReentrantLock putLock = this.putLock;
		int n = 0;
		putLock.lock();
		try {
			for(T e:collection) {
				if(e == null)
					throw new NullPointerException();
				if(n == capacity)
					throw new IllegalStateException("Queue full");
				enQueue(e);
				n++;
			}
			count.set(n);
		} finally {
			putLock.unlock();
		}
	}
	
	
	
	
	public void put(T e) throws InterruptedException {
		if(e == null)
			throw new NullPointerException();
		final ReentrantLock putLock = this.putLock;
		final AtomicInteger count = this.count;
		int c = -1;
		putLock.lockInterruptibly();
		try {
			while(count.get() == this.capacity)
				notFull.await();
			enQueue(e);
			c = count.getAndIncrement();
			if(c + 1 < capacity)
				notFull.signal();
			
		} finally {
			putLock.unlock();
		}
		if(c == 0)
			singalNotEmpty();
	}
	
	
	private void enQueue(T e) {
		tail = tail.next = new Node<>(e);
	}
	
	private void singalNotEmpty() {
		final ReentrantLock takeLock = this.takeLock;
		takeLock.lock();
		try {
			notEmpty.signal();
		} finally {
			takeLock.unlock();
		}
	}
	
	
	public T take() throws InterruptedException {
		T e = null;
		final ReentrantLock takeLock = this.takeLock;
		final AtomicInteger count = this.count;
		int c = -1;
		takeLock.lockInterruptibly();
		try {
			while(count.get() == 0)
				notEmpty.await();
			e = deQueue();
			c = count.getAndDecrement();
			if(c > 1)
				notEmpty.signal();
		} finally {
			takeLock.unlock();
		}
		if(c == capacity)
			signalNotFull();
		return e;
	}
	
	
	private T deQueue() {
		Node<T> h = head;
		head = head.next;
		T e = head.val;
		head.val = null;
		h.next = h;
		return e;
	}
	
	private void signalNotFull() {
		final ReentrantLock putLock = this.putLock;
		putLock.lock();
		try {
			notFull.signal();
		} finally {
			putLock.unlock();
		}
	}
	
	
	public boolean offer(T e) {
		if(e == null)
			throw new NullPointerException();
		if(count.get() == capacity)
			return false;
		int c = -1;
		final ReentrantLock putLock = this.putLock;
		try {
			if(count.get() < capacity) {
				enQueue(e);
				c = count.getAndIncrement();
				if(c+1<capacity)
					notFull.signal();
			}
		} finally {
			putLock.unlock();
		}
		if(c == 0)
			singalNotEmpty();
		return c>=0;
	}
	
	
	public boolean offer(T e, long timeout, TimeUnit timeUnit) throws InterruptedException {
		if(e == null)
			throw new NullPointerException();
		long nanos = timeUnit.toNanos(timeout);
		final ReentrantLock putLock = this.putLock;
		final AtomicInteger count = this.count;
		int c = -1;
		putLock.lockInterruptibly();
		try {
			while(count.get() == capacity) {
				if(nanos <= 0)
					return false;
				nanos = notFull.awaitNanos(nanos);
			}
			enQueue(e);
			c = count.getAndIncrement();
			if(c+1 < capacity)
				notFull.signal();
		} finally {
			putLock.unlock();
		}
		if(c == 0)
			singalNotEmpty();
		return true;
	}
	
	
	public T poll() {
		if(count.get() == 0)
			return null;
		final ReentrantLock takeLock = this.takeLock;
		int c = -1;
		T e = null;
		takeLock.lock();
		try {
			if(count.get() > 0) {
				e = deQueue();
				c = count.getAndDecrement();
				if(c > 1)
					notEmpty.signal();
			}
		} finally {
			takeLock.unlock();
		}
		if(c == capacity)
			signalNotFull();
		return e;
	}
	
	
	public T poll(long timeout, TimeUnit timeUnit) throws InterruptedException {
		T e = null;
		final AtomicInteger count = this.count;
		final ReentrantLock takeLock = this.takeLock;
		takeLock.lockInterruptibly();
		long nanos = timeUnit.toNanos(timeout);
		int c = -1;
		try {
			while(count.get() == 0) {
				if(nanos <= 0)
					return null;
				nanos = notEmpty.awaitNanos(nanos);
			}
			e = deQueue();
			c = count.getAndDecrement();
			if(c > 1)
				notEmpty.signal();
		} finally {
			takeLock.unlock();
		}
		if(c == capacity)
			signalNotFull();
		return e;
	}
	
	
	public T peek() {
		if(count.get() == 0)
			return null;
		final ReentrantLock takeLock = this.takeLock;
		takeLock.lock();
		try {
			Node<T> first = head.next;
			if(first == null)
				return null;
			else
				return first.val;
		} finally {
			takeLock.unlock();
		}
	}
	
	void unlink(Node<T> p, Node<T> trail) {
		p.val = null;
		trail.next = p.next;
		if(p == tail)
			tail = trail;
		if(count.getAndDecrement() == capacity)
			notFull.signal();
	}
	
	
	private void fullyLock() {
		this.takeLock.lock();
		this.putLock.lock();
	}
	
	private void fullyUnlock() {
		this.takeLock.lock();
		this.putLock.lock();
	}
	
	
	public boolean remove(Object o) {
		if(o == null)
			return false;
		fullyLock();
		try {
			for(Node<T> trail = head, p=head.next; p!= null; trail = p, p=p.next) {
				if(o.equals(p.val)) {
					unlink(p, trail);
					return true;
				}
			}
		} finally {
			fullyUnlock();
		}
		
		return false;
	}
	
	
	public Object[] toArray() {
		fullyLock();
		try {
			int size = count.get();
			Object[] array = new Object[size];
			int i = 0;
			for(Node<T> node = head.next; node != null; node = node.next)
				array[i++] = node.val; 
			return array;
		} finally {
			fullyUnlock();
		}
	}
	
	
	
	
	public int size() {
		return count.get();
	}
	
	
	public int remainingCapacity() {
		return capacity - this.count.get();
	}
	
	public boolean contains(Object o) {
		if(o == null)
			return false;
		fullyLock();
		try {
			for(Node<T> node = head.next; node!= null; node=node.next) {
				if(o.equals(node.val))
					return true;
			}
			return false;
		} finally {
			fullyUnlock();
		}
	}
	
	
	public void clear() {
		fullyLock();
		try {
			for(Node<T> p,h = head; (p=h.next)!= null; h=p) {
				h.next = h;
				p.val = null;
			}
			head = tail;
			if(count.getAndSet(0) == capacity)
				notFull.signal();
		} finally {
			fullyUnlock();
		}
	}
	
	
	@Override
	public String toString() {
		fullyLock();
		try {
			Node<T> p = head.next;
			if(p == null)
				return "[]";
			StringBuilder sb = new StringBuilder();
			sb.append('[');
			while(true) {
				sb.append(( p.val== this ? "(this Collection)" : p.val) );
				p = p.next;
				if(p == null) {
					sb.append(']');
					return sb.toString();
				}
				sb.append(',').append(' ');
			}
			
		} finally {
			fullyUnlock();
		}
	}
	
	 public int drainTo(Collection<? super T> c, int maxElements) {
		 if(c == null)
			 throw new NullPointerException();
		 if(c == this)
			 throw new IllegalArgumentException();
		 if(maxElements <= 0)
			 return 0;
		 boolean signalNotFull = false;
		 final ReentrantLock takeLock = this.takeLock;
		 takeLock.lock();
		 try {
			 int n = Math.min(maxElements, count.get());
			 Node<T> node = head;
			 int i = 0;
			 try {
				 while(i < n) {
					 Node<T> p = node.next;
					 c.add(p.val);
					 p.val = null;
					 node.next = node;
					 node = p;
					 i++;
				 }
				 return n;
			} finally {
				if(i > 0) {
					head = node;
					signalNotFull = (count.getAndAdd(-i) == capacity);
				}
			}
		} finally {
			takeLock.unlock();
			if(signalNotFull)
				signalNotFull();
		}
	 }
	
	
	
	
	
	
	
	

}
