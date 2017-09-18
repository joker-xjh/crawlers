package Core.util;

import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Logger;

public class ThreadPoolMonitor implements Runnable{
	
	private static Logger logger = SimpleLogger.getSimpleLogger(ThreadPoolMonitor.class);
	private ThreadPoolExecutor executor;
	public static volatile boolean isStop = false;
	private String name = "";
	
	public ThreadPoolMonitor(String name, ThreadPoolExecutor executor) {
		this.name = name;
		this.executor = executor;
	}
	
	
	@Override
	public void run() {
		while(!isStop) {
			 logger.debug(name +
	                    String.format("[monitor] [%d/%d] Active: %d, Completed: %d, queueSize: %d, Task: %d, isShutdown: %s, isTerminated: %s",
	                            this.executor.getPoolSize(),
	                            this.executor.getCorePoolSize(),
	                            this.executor.getActiveCount(),
	                            this.executor.getCompletedTaskCount(),
	                            this.executor.getQueue().size(),
	                            this.executor.getTaskCount(),
	                            this.executor.isShutdown(),
	                            this.executor.isTerminated()));
			 try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				logger.error("InterruptedException",e);
			}
			 
		}
		
	}
	

}
