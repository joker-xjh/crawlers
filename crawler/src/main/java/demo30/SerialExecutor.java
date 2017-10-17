package demo30;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executor;

public class SerialExecutor {
	
	private final Queue<Runnable> queue;
	
	private final Executor executor;
	
	public SerialExecutor(Executor executor) {
		this.executor = executor;
		queue = new LinkedList<>();
	}
	
	private Runnable runnable;
	
	public synchronized void execute(final Runnable runnable) {
		queue.offer(new Runnable() {
			@Override
			public void run() {
				try {
					runnable.run();
				} finally {
					scheduleNext();
				}
			}
		});
		if(runnable == null)
			scheduleNext();
	}
	
	protected synchronized void scheduleNext() {
		if((runnable = queue.poll()) != null) {
			executor.execute(runnable);
		}
	}
	

}
