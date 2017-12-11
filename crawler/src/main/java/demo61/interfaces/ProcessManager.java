package demo61.interfaces;

public interface ProcessManager {
	
	void createProcess(String pid, int priority);
	void destoryProcess(String pid);
	void listAllProcess();

}
