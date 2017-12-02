package demo57;

public class Peterson {
	
	private boolean[] flag = new boolean[2];
	
	private int turn;
	
	public void p0() {
		while(true) {
			flag[0] = true;
			turn = 1;
			while(flag[1] && turn == 1);//自旋锁
			
			 /** critical section 临界区 **/
			
			flag[0] = false;
		}
	}
	
	public void p1() {
		while(true) {
			flag[1] = true;
			turn = 0;
			while(flag[0] && turn == 0);//自旋锁
			
			/** critical section 临界区 **/
			
			flag[1] = false;
		}
	}

}
