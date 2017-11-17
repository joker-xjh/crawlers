package demo51.interfaces;

public interface YTask {
	
	Integer getTaskNo();
	
	void executeTask();
	
	long getExecutionTime();
	
	void afterExecution(TaskManager taskManager);

}
