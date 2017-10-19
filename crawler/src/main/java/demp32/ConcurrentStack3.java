package demp32;

import java.util.concurrent.atomic.AtomicReference;

public class ConcurrentStack3<T> {
	
	private static class Node<T> {
		T val;
		Node<T> next;
		
		public Node(T val) {
			this.val = val;
		}
	}
	
	private AtomicReference<Node<T>> head = new AtomicReference<Node<T>>();
	
	public void push(T val) {
		Node<T> node = new Node<T>(val);
		Node<T> oldHead;
		do {
			oldHead = head.get();
			node.next = oldHead;
		} while (!head.compareAndSet(oldHead, node));
	}
	
	public T pop() {
		Node<T> curHead;
		Node<T> nextHead;
		do {
			curHead = head.get();
			if(curHead == null)
				return null;
			nextHead = curHead.next;
		} while (!head.compareAndSet(curHead, nextHead));
		return curHead.val;
	}
	

}
