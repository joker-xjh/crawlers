package demo51.imp;

import java.util.Collection;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import demo51.interfaces.TaskExecutor;
import demo51.interfaces.TaskManager;
import demo51.interfaces.YTask;

public class TimedTaskExecutor implements TaskExecutor{
	
	private PriorityQueue<YTask> queue;
	
	private Thread executionThread;
	
	private volatile TaskManager taskManager;
	
	private int countOfConcurrent;
	
	public TimedTaskExecutor(TaskManager taskManager, int capacity, int countOfConcurrent) {
		this.taskManager = taskManager;
        this.countOfConcurrent = countOfConcurrent;
        init(capacity);	
	}
	
	private void init(int capacity) {
		initExecutor();
		initQueue(capacity);
	}
	
	private void initExecutor() {
		executionThread = new Thread(new TaskHandler(countOfConcurrent));
	}
	
	
	 private void initQueue(int capacity) {
	        queue = new PriorityQueue<YTask>(capacity,
	                (YTask task1, YTask task2) -> (int) (task1.getExecutionTime() - task2.getExecutionTime()));
	 }
	

	@Override
	public void addTask(YTask task) {
		synchronized (queue) {
			queue.add(task);
			queue.notify();
		}
	}

	@Override
	public void addTasks(Collection<? extends YTask> tasks) {
		synchronized (queue) {
			queue.addAll(tasks);
			queue.notify();
		}
	}

	@Override
	public void start() {
		executionThread.start();
	}
	
	
	private class TaskHandler implements Runnable {
		
		private ExecutorService threadPool = null;
		
		 public TaskHandler(int poolSize) {
	            threadPool = Executors.newFixedThreadPool(poolSize);
	     }


		@Override
		public void run() {
			while(true) {
				synchronized (queue) {
					while(queue.isEmpty())
						try {
							queue.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					
					 while (!queue.isEmpty() && queue.peek().getExecutionTime() <= System.currentTimeMillis()) {
	                        YTask curTask = queue.poll();
	                        threadPool.execute(() -> {
	                            curTask.executeTask();
	                            curTask.afterExecution(taskManager);
	                        });
	                    }
					 if(!queue.isEmpty()) {
						 YTask task = queue.peek();
						 try {
							queue.wait(task.getExecutionTime() - System.currentTimeMillis());
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					 }
					 
				}
			}
		}
		
	}
	
	
	
	
	
	
	
	
	
	

}
