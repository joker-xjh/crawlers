package demp32;

import java.util.concurrent.atomic.AtomicReference;

public class LinkedQueue<T> {
	
	private static class Node<T> {
		T val;
		AtomicReference<Node<T>> next;
		public Node(T val, Node<T> next) {
			this.val = val;
			this.next = new AtomicReference<Node<T>>(next);
		}
	}

	private Node<T> dummy = new Node<T>(null, null);
	
	private final AtomicReference<Node<T>> head = new AtomicReference<Node<T>>(dummy);
	private final AtomicReference<Node<T>> tail = new AtomicReference<Node<T>>(dummy);
	
	public boolean push(T val) {
		Node<T> node = new Node<T>(val, null);
		while(true) {
			Node<T> curTail = tail.get();
			Node<T> nextTail = curTail.next.get();
			if(curTail == tail.get()) {
				if(nextTail != null) {
					tail.compareAndSet(curTail, nextTail);
				}
				else {
					if(curTail.next.compareAndSet(null, node)) {
						tail.compareAndSet(curTail, node);
						return true;
					}
				}
			}
		}
	}
	
	public T poll() {
		Node<T> curHead;
		Node<T> nextHead;
		do {
			curHead = head.get().next.get();
			if(curHead == dummy)
				return null;
			nextHead = curHead.next.get();
		} while (!head.get().next.compareAndSet(curHead, nextHead));
		return curHead.val;
	}
	
	
	
	
	
	
	
	
	
}
