package demo21;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CountNode implements Node{
	private List<Node> key;
	private AtomicInteger count;
	
	private CountNode pre;
	private CountNode next;
	
	public CountNode(List<Node> key, int count) {
		this.key = key;
		this.count = new AtomicInteger(count);
	}
	
	public int increment() {
		return count.getAndIncrement();
	}
	
	public List<Node> getKey(){
		return key;
	}	
	
	public int getCount() {
		return count.get();
	}
	
	public CountNode getNext() {
		return next;
	}
	
	public void setNext(CountNode next) {
		this.next = next;
	}
	
	public CountNode getPre() {
		return pre;
	}
	
	public void setPre(CountNode pre) {
		this.pre = pre;
	}
	
	
	
	

}
