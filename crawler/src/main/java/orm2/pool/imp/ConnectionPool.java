package orm2.pool.imp;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import orm2.pool.interfaces.ObjectFactory;

public class ConnectionPool extends AbstractPool<Connection>{
	
	private ObjectFactory<Connection> factory;
	private Queue<Connection> queue;
	private int coreSize;
	private Semaphore permit;
	
	public ConnectionPool(int capacity) {
		factory = new ConnectionFactory();
		queue = new LinkedList<>();
		this.coreSize = capacity;
		permit = new Semaphore(capacity);
		init(capacity);
	}
	
	private void init(int coreSize) {
		for(int i=0; i<coreSize; i++) {
			queue.add(factory.create());
		}
	}
	

	@Override
	public Connection get() {
		if(permit.tryAcquire()) {
			return queue.poll();
		}
		return null;
	}

	@Override
	public void close() {
		shutdown = true;
		for(Connection connection : queue)
			factory.destroy(connection);
	}

	@Override
	protected boolean isValid(Connection obj) {
		return factory.isValid(obj);
	}

	@Override
	protected void returnToPool(Connection obj) {
		boolean b = queue.offer(obj);
		if(b)
			permit.release();
	}

	@Override
	protected void destroy(Connection obj) {
		factory.destroy(obj);
	}
	
	public int getPoolSize() {
		return coreSize;
	}

}
