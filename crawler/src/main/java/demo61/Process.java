package demo61;

import java.util.List;

import demo61.Resource.RCB;

public class Process {
	
	String pid;
	PCB pcb;
	
	public Process(String pid, int priority, String status, PCB parent) {
		this.pid = pid;
		this.pcb = new PCB(pid, priority, status, parent);
	}
	
	static class PCB {
		String pid;
		int priority;
		String status;
		PCB parent;
		List<RCB> resource;
		List<Integer> resourceNum;
		RCB block;
		int blockNum;
		
		public PCB(String pid, int priority, String status, PCB parent) {
			this.pid = pid;
			this.priority = priority;
			this.status = status;
			this.parent = parent;
		}

		@Override
		public String toString() {
			return "PCB [pid=" + pid + ", priority=" + priority + ", status=" + status + ", parent=" + parent + "]";
		}
		
	}

}
