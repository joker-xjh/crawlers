package demp32;

import java.util.concurrent.atomic.AtomicReference;

public class ConcurrentStack2<T> {
	
	private static class Node<T> {
		T val;
		Node<T> next;
		public Node(T val) {
			this.val = val;
		}
	}
	
	private AtomicReference<Node<T>> head = new AtomicReference<Node<T>>();
	
	public void push(T val) {
		Node<T> newHead = new Node<T>(val);
		Node<T> oldHead;
		do {
			oldHead = head.get();
			newHead.next = oldHead;
		} while ( !head.compareAndSet(oldHead, newHead));
	}
	
	public T pop() {
		Node<T> newHead;
		Node<T> oldHead;
		do {
			oldHead = head.get();
			if(oldHead == null)
				return null;
			newHead = oldHead.next;
		} while (!head.compareAndSet(oldHead, newHead));
		return oldHead.val;
	}
	
	
	
	
	
	

}
