package demo20;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("all")
public class ThreadLocalConManager<T> {
	
	
	private final Map<Thread, ConnectionWrapper> conMap = new ConcurrentHashMap<>();
	
	private ThreadLocal<ConnectionWrapper> threadLocal = new ThreadLocal<>();
	
	
	private ConnectionFactory<T> factory;
	
	private long liveTime;
	
	public ThreadLocalConManager(long liveTime, ConnectionFactory<T> factory) {
		this.liveTime = liveTime;
		this.factory = factory;
	}
	
	
	public Connection<T> getConnection(){
		ConnectionWrapper wrapper = threadLocal.get();
		if(wrapper == null) {
			wrapper = new ConnectionWrapper(factory.getConnection(), System.currentTimeMillis());
			conMap.put(Thread.currentThread(), wrapper);
			threadLocal.set(wrapper);
		}
		
		return wrapper.getConnection();
	}
	
	
	
	
	private class ConnectionWrapper {
		private volatile Connection<T> connection;
		private long timestamp;
		
		public ConnectionWrapper(Connection<T> connection, long timestamp) {
			this.timestamp = timestamp;
			this.connection = connection;
		}
		
		public Connection<T> getConnection() {
			return connection;
		}
		
		public long getTimestamp() {
			return timestamp;
		}
		
		public void setConnection(Connection<T> connection) {
			this.connection = connection;
		}
		
		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}
		
	}

}
