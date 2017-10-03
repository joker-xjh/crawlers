package demo12;

import java.util.LinkedList;
import java.util.List;

public class BlockingQueue <T>{
	
	private List<T> queue;
	private int capacity;
	
	public BlockingQueue(int capacity) {
		this.capacity = capacity;
		queue = new LinkedList<>();
	}
	
	public synchronized void put(T element) {
		try {
			while(queue.size() == capacity)
				wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (queue.size() == 0){
            notifyAll();
        }
		queue.add(element);
	}
	
	public synchronized T take() {
		try {
			while(queue.size() == 0)
				wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(queue.size() == capacity)
			notifyAll();
		return queue.remove(0);
	}

}
