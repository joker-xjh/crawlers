package demo54;

import java.util.concurrent.atomic.AtomicReference;

public class CurrentStack<T> {
	
	private class Node {
		T val;
		Node next;
	}
	
	private AtomicReference<Node> stack = new AtomicReference<>();
	
	public void push(T val) {
		Node node = new Node();
		node.val = val;
		Node old = null;
		do {
			old = stack.get();
			node.next = old;
		} while (!stack.compareAndSet(old, node));
	}
	
	public T pop() {
		Node newTop = null, oldTop = null;
		do {
			oldTop = stack.get();
			if(oldTop == null)
				return null;
			newTop = oldTop.next;
		} while (!stack.compareAndSet(oldTop, newTop));
		return oldTop.val;
	}
	
}
