package demo21;

public class OrderNode implements Node{
	
	private Object key;
	private Object value;
	
	private long timeout;
	private long timestamp;
	
	private OrderNode pre;
	private OrderNode next;
	
	public OrderNode(Object key, Object value, long timeout) {
		this.key = key;
		this.value = value;
		this.timeout = timeout;
		timestamp = System.currentTimeMillis();
	}
	
	public boolean isTimeout() {
		if(timeout == -1)
			return false;
		long now = System.currentTimeMillis();
		if(timestamp + timeout > now)
			return false;
		return true;
	}

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public OrderNode getPre() {
		return pre;
	}

	public void setPre(OrderNode pre) {
		this.pre = pre;
	}

	public OrderNode getNext() {
		return next;
	}

	public void setNext(OrderNode next) {
		this.next = next;
	}
	
	

}
