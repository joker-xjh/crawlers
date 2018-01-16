package demo63;

public class ClockPageSwap {
	
	private class Node {
		boolean chance = true;
		@SuppressWarnings("unused")
		Object value;
		Node next;
	}
	
	private static final int CLOCK_SIZE = 12;
	
	private Node head;
	private Node tail;
	private int size;
	
	private Node pointer;
	
	public void swap(Object value) {
		if(size < CLOCK_SIZE) {
			if(head == null) {
				head = new Node();
				head.value = value;
				tail = head;
				pointer = head;
			}
			else {
				Node oldTail = tail;
				tail = new Node();
				tail.value = value;
				oldTail.next = tail;
			}
			tail.next = head;
			size++;
		}
		else {
			Node node = pointer;
			while(true) {
				if(node.chance) {
					node.chance = false;
					node = node.next;
				}
				else {
					node.value = value;
					node.chance = true;
					pointer = node;
					break;
				}
			}
		}
	}
	
	

}
