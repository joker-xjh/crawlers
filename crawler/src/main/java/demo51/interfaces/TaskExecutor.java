package demo51.interfaces;

import java.util.Collection;

public interface TaskExecutor {
	
	void addTask(YTask task);
	
	void addTasks(Collection<? extends YTask> tasks);
	
	void start();

}
