package demo60;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class HardDrive {
	
	private Set<Integer> trackSet = new HashSet<>();
	private int[] tracks;
	private int headAt;
	private boolean direction; // true 左  false 右 
	private int distance;
	private int method;
	
	public HardDrive(int[] tracks, int headAt, boolean direction, int method) {
		this.tracks = tracks;
		this.headAt = headAt;
		this.direction = direction;
		this.method = method;
		for(int i:tracks)
			trackSet.add(i);
	}
	
	public void process() {
		switch(method) {
			case 0 :
				FCFS();
				break;
			case 1:
				SSTF();
				break;
			case 2:
				SCAN();
				break;
		}
	}
	
	
	
	
	
	private void FCFS() {
		System.out.print("寻道顺序为：" + headAt + " ");
		for(int i : tracks) {
			System.out.print(i + " ");
			this.distance += Math.abs(headAt - i);
		}
		System.out.println();
		System.out.println("总路程="+distance);
	}
	
	
	private void SSTF() {
		System.out.print("寻道顺序为：" + headAt + " ");
		while(!trackSet.isEmpty()) {
			int chose = Integer.MAX_VALUE;
			for(int track : trackSet) {
				if(Math.abs(track - headAt) < Math.abs(chose - track))
					chose = track;
			}
			System.out.print(chose + " ");
			distance += Math.abs(headAt - chose);
			headAt = chose;
			trackSet.remove(chose);
		}
		System.out.println();
		System.out.println("总路程="+distance);
	}
	
	
	private void SCAN() {
		 System.out.print("寻道顺序为：" + headAt + " ");
		 int[] temp = tracks.clone();
		 Arrays.sort(temp);
		 int index = Arrays.binarySearch(temp, headAt);
		 if(index < 0)
			 index = -index - 1;
		 if(direction) {
			 for(int i=index; i>=0; i--) {
				 System.out.print(temp[i] + " ");
				 this.distance += Math.abs(headAt - temp[i]);
				 headAt = temp[i];
			 }
			 for(int i = index+1; i<temp.length; i++) {
				 System.out.print(temp[i] + " ");
				 this.distance += Math.abs(headAt - temp[i]);
				 headAt = temp[i];
			 }
		 }
		 else {
			 for(int i = index+1; i<temp.length; i++) {
				 System.out.print(temp[i] + " ");
				 this.distance += Math.abs(headAt - temp[i]);
				 headAt = temp[i];
			 }
			 for(int i=index; i>=0; i--) {
				 System.out.print(temp[i] + " ");
				 this.distance += Math.abs(headAt - temp[i]);
				 headAt = temp[i];
			 }
		 }
		 System.out.println();
		 System.out.println("总路程="+distance);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}
