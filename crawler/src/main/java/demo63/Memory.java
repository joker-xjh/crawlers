package demo63;

public class Memory {
	
	class Node {
		boolean used;
		int start;
		int length;
		Node next;
	}
	
	private Node head;
	
	private Node next = head;
	
	public Node firstFit(int length) {		
		for(Node node =head; node != null; node=node.next) {
			if(node.used)
				continue;
			if(node.length < length)
				continue;
			if(node.length == length) {
				node.used = true;
				return node;
			}
			else {
				Node newNode = new Node();
				newNode.length = node.length - length;
				newNode.next = node.next;
				node.next = newNode;
				node.used = true;
				return node;
			}
			
		}
		
		return null;
	}
	
	
	public Node nextFit(int length) {
		for(Node node=next; node != null; node = node.next) {
			if(node.used)
				continue;
			if(node.length < length)
				continue;
			if(node.length == length) {
				node.used = true;
				next = node.next;
				return node;
			}
			else {
				Node newNode = new Node();
				newNode.length = node.length - length;
				newNode.next = node.next;
				node.next = newNode;
				node.used = true;
				next = node.next;
				return node;
			}
		}
		
		for(Node node=head; node != null && node!=next; node = node.next) {
			if(node.used)
				continue;
			if(node.length < length)
				continue;
			if(node.length == length) {
				node.used = true;
				next = node.next;
				return node;
			}
			else {
				Node newNode = new Node();
				newNode.length = node.length - length;
				newNode.next = node.next;
				node.next = newNode;
				node.used = true;
				next = node.next;
				return node;
			}
		}
		
		return null;
	}
	
	
	public Node bestFit(int length) {
		Node bestFitNode = null;
		for(Node node = head; node != null; node = node.next) {
			if(node.used)
				continue;
			 if(node.length < length)
				continue;
			if(bestFitNode == null || bestFitNode.length > node.length)
				bestFitNode = node;
		}
		if(bestFitNode == null)
			return null;
		bestFitNode.used = true;
		if(bestFitNode.length == length) {
			return bestFitNode;
		}
		Node newNode = new Node();
		newNode.length = bestFitNode.length - length;
		newNode.next = bestFitNode.next;
		bestFitNode.next = newNode;
		return bestFitNode;
	}

}
