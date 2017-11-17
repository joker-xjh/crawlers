package demo51.interfaces;

public interface TaskManager {
	
	boolean addTask(YTask task);
	
	YTask removeTask(Integer taskNo);
	
	void removeTask(YTask task);
	
	YTask getTask(Integer taskNo);
	
	void start();

}
