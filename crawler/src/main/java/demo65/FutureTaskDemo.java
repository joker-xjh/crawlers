package demo65;

import java.sql.Connection;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

public class FutureTaskDemo {
	
	private ConcurrentHashMap<String, FutureTask<Connection>> connectionPool = new ConcurrentHashMap<>();
	
	public Connection getConnection(String key) throws Exception{
		FutureTask<Connection> futureTask = connectionPool.get(key);
		if(futureTask != null)
			return futureTask.get();
		Callable<Connection> callable = new Callable<Connection>() {
			@Override
			public Connection call() throws Exception {
				return null;
			}
		};
		
		FutureTask<Connection> newTask = new FutureTask<>(callable);
		futureTask = connectionPool.putIfAbsent(key, newTask);
		if(futureTask == null) {
			futureTask = newTask;
			futureTask.run();
		}
		
		return futureTask.get();
	}

}
