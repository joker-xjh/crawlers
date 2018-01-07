package demo31;

public class PrintChar {

	public static void main(String[] args) {
		for(int i=0; i<65536; i++) {
			char c = (char) i;
			System.out.print(c +"("+i+") ");
			if(i % 10 == 0 && i != 0)
				System.out.println();
		}

	}

}
