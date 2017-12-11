package demo61;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import demo61.Process.PCB;
import demo61.Resource.RCB;
import demo61.interfaces.ResourceManager;
import demo61.interfaces.Scheduler;
import demo61.interfaces.Timer;

import static demo61.Commons.*;

public class ProcessManager implements demo61.interfaces.ProcessManager,  Scheduler, Timer, ResourceManager {

	private Queue<PCB> initProcess;
	private Queue<PCB> userProcess;
	private Queue<PCB> systemProcess;
	private Queue<PCB> blockedProcess;
	
	private PCB currentProcess;
	
	private RCB r1;
	private RCB r2;
	private RCB r3;
	private RCB r4;
	
	public ProcessManager() {
		initProcess = new LinkedList<>();
		userProcess = new LinkedList<>();
		systemProcess = new LinkedList<>();
		blockedProcess = new LinkedList<>();
		
		Resource resource1 = new Resource("r1", 1);
		r1 = resource1.rcb;
		Resource resource2 = new Resource("r2", 2);
		r2 = resource2.rcb;
		Resource resource3 = new Resource("r3", 3);
		r3 = resource3.rcb;
		Resource resource4 = new Resource("r4", 4);
		r4 = resource4.rcb;
		createProcess("init", PRIORITY_INIT);
	}
	
	
	@Override
	public void requestResource(String res, int nums) {
		Resource.RCB rcb = null;
        if (res.equals(r1.rid)){
            rcb = r1;
        }else if (res.equals(r2.rid)){
            rcb = r2;
        }else if (res.equals(r3.rid)){
            rcb = r3;
        }else if (res.equals(r4.rid)){
            rcb = r4;
        }
        if(rcb.free < nums) {
        	PCB pcb = null;
        	if(currentProcess.priority == PRIORITY_INIT) {
        		pcb = initProcess.poll();
        	}
        	else if(currentProcess.priority == PRIORITY_SYSTEM) {
        		pcb = systemProcess.poll();
        	}
        	else {
        		pcb = userProcess.poll();
        	}
        	pcb.status = STATUS_BLOCK;
        	pcb.block = rcb;
        	pcb.blockNum = nums;
        	pcb.resource.add(rcb);
        	pcb.resourceNum.add(nums);
        	blockedProcess.offer(pcb);
        }
        else {
        	currentProcess.resource.add(rcb);
        	currentProcess.resourceNum.add(nums);
        	rcb.free -= nums;
        }
		schedule();
	}

	@Override
	public void releaseResource(String rid, int nums) {
		RCB rcb = null;
		if(r1.rid.equals(rid)) {
			rcb = r1;
		}
		else if(r2.rid.equals(rid)) {
			rcb = r2;
		}
		else if(r3.rid.equals(rid)) {
			rcb = r3;
		}
		else if(r4.rid.equals(rid)) {
			rcb = r4;
		}
		if(rcb == null)
			return;
		rcb.free += nums;
		schedule();
	}
	
	public void releaseResourceNoSchedule(String rid, int nums) {
		RCB rcb = null;
		if(r1.rid.equals(rid)) {
			rcb = r1;
		}
		else if(r2.rid.equals(rid)) {
			rcb = r2;
		}
		else if(r3.rid.equals(rid)) {
			rcb = r3;
		}
		else if(r4.rid.equals(rid)) {
			rcb = r4;
		}
		if(rcb == null)
			return;
		rcb.free += nums;
	}
	
	

	@Override
	public void timeOut() {
		schedule();
	}

	@Override
	public void schedule() {
		Iterator<PCB> iterator = blockedProcess.iterator();
		while(iterator.hasNext()) {
			PCB pcb = iterator.next();
			if(pcb.block.free >= pcb.blockNum) {
				iterator.remove();
				if(pcb.priority == PRIORITY_INIT) {
					initProcess.offer(pcb);
				}
				else if(pcb.priority == PRIORITY_SYSTEM) {
					systemProcess.offer(pcb);
				}
				else if(pcb.priority == PRIORITY_USER) {
					userProcess.offer(pcb);
				}
			}
		}
		PCB pcb = null;
		if(!systemProcess.isEmpty())
			pcb = systemProcess.poll();
		else if(!userProcess.isEmpty())
			pcb = userProcess.poll();
		else if(!initProcess.isEmpty())
			pcb = initProcess.poll();
		if(currentProcess != null)
			currentProcess.status = STATUS_READY;
		currentProcess = pcb;
		if(currentProcess != null) {
			currentProcess.status = STATUS_RUNNING;
			System.out.println("current process is "+currentProcess.pid);
		}
		else {
			System.out.println("there is no process");
		}
		
	}

	@Override
	public void createProcess(String pid, int priority) {
		Process process = new Process(pid, priority, STATUS_READY, currentProcess);
		if(priority == PRIORITY_INIT) {
			initProcess.offer(process.pcb);
		}
		else if(priority == PRIORITY_USER) {
			userProcess.offer(process.pcb);
		}
		else {
			systemProcess.offer(process.pcb);
		}
		schedule();
	}

	@Override
	public void destoryProcess(String pid) {
		if(isDestoryProcess(initProcess, pid)) {
			schedule();
			return;
		}
		if(isDestoryProcess(systemProcess, pid)) {
			schedule();
			return;
		}
		if(isDestoryProcess(userProcess, pid)) {
			schedule();
			return;
		}
		if(isDestoryProcess(blockedProcess, pid)) {
			schedule();
			return;
		}
		
	}
	
	private boolean isDestoryProcess(Queue<PCB> process, String pid) {
		Iterator<PCB> iterator = process.iterator();
		while(iterator.hasNext()) {
			PCB pcb = iterator.next();
			if(pcb.pid.equals(pid)) {
				iterator.remove();
				LinkedList<RCB> temp = (LinkedList<RCB>)pcb.resource;
				for(int i=0; i<temp.size(); i++) {
					releaseResourceNoSchedule(temp.get(i).rid, pcb.resourceNum.get(i));
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void listAllProcess() {
		listProcess(initProcess);
		listProcess(userProcess);
		listProcess(systemProcess);
		listProcess(blockedProcess);
	}
	
	private void listProcess(Queue<PCB> process) {
		for(PCB pcb : process) {
			System.out.println(pcb);
		}
	}

}


