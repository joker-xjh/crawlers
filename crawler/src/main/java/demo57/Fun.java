package demo57;


public class Fun {

	static class Rectangle {
		int x, y;
		int length, high;
		public Rectangle(int x, int y, int length, int high) {
			this.x = x;
			this.y = y;
			this.length = length;
			this.high = high;
		}
	}

	public static void main(String[] args) {
		int X = 3, Y = 50;
		int[][] board = new int[X][Y];
		Rectangle rectangle = new Rectangle(0, 0, 5, 2);
		while(rectangle.y <= Y) {
			if(rectangle.y == Y)
				rectangle.y = 0;
			fill(rectangle, board);
			print(board);
			disapper(rectangle, board);
			moveRight(rectangle, 2);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void print(int[][] array) {
		for(int i=0; i<array.length; i++) {
			int temp = array[i].length;
			for(int j=0; j<temp; j++) {
				int point = array[i][j];
				if(point == 1)
					System.out.print(point+" ");
				else
					System.out.print(" "+ " ");
			}
			System.out.println();
		}
	}
	
	private static void fill(Rectangle rectangle, int[][] board) {
		for(int i=rectangle.x; i<rectangle.x + rectangle.high && i<board.length; i++) {
			for(int j=rectangle.y; j<rectangle.y + rectangle.length && j < board[0].length; j++) {
				board[i][j] = 1;
			}
		}
	}
	
	private static void disapper(Rectangle rectangle, int[][] board) {
		for(int i=rectangle.x; i<rectangle.x + rectangle.high && i<board.length; i++) {
			for(int j=rectangle.y; j<rectangle.y + rectangle.length && j < board[0].length; j++) {
				board[i][j] = 0;
			}
		}
	}
	
	private static void moveRight(Rectangle rectangle, int step) {
		rectangle.y += step;
	}
}
