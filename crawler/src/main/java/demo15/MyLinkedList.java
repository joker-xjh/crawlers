package demo15;

public class MyLinkedList<T> {
	
	static class Node<T> {
		T val;
		Node<T> pre;
		Node<T> next;
		
		public Node(T val, Node<T> pre, Node<T> next) {
			this.val = val;
			this.pre = pre;
			this.next = next;
		}
		
		public T getVal() {
			return val;
		}
	}
	
	private Node<T> head = new Node<>(null, null, null);
	
	
	private int size = 0;
	
	public MyLinkedList() {
		head.pre = head;
		head.next = head;
	}
	
	
	public void add(T val) {
		Node<T> node = new Node<>(val, head.pre, head);
		node.next.pre = node;
		node.pre.next = node;
		size++;
	}
	
	
	public T getFirst() {
		Node<T> first = head.next;
		return first.getVal();
	}
	
	public T getLast() {
		Node<T> last = head.pre;
		return last.getVal();
	}
	
	private Node<T> getNode(int index){
		if(index >= size || index < 0)
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: "
					+ size);
		int count = 0;
		Node<T> search = head.next;
		while(count < index) {
			search = search.next;
			count++;
		}
		return search;
	}
	
	public T get(int index) {
		Node<T> node = getNode(index);
		return node.getVal();
	}
	
	
	public void delete(int index) {
		if(index >= size || index < 0)
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: "
					+ size);
		Node<T> delete = getNode(index);
		delete.pre.next = delete.next;
		delete.next.pre = delete.pre;
		delete.next = delete.pre = null;
		delete.val = null;
		size--;
	}
	
	public int getSize() {
		return size;
	}
	
}
