package demo55;

import java.util.Arrays;
import java.util.Random;

public class RandomDemo {
	
	private static final String SPECIAL_CHARS = "!@#$%^&*_=+-/";
	
	public static String randomPassword(int length) {
		char[] password = new char[length];
		Random random = new Random();
		for(int i=0; i<password.length; i++) {
			password[i] = (char) ('0' + random.nextInt(10));
		}
		return new String(password);
	}
	
	private static char nextChar(Random random) {
		switch(random.nextInt(4)) {
			case 0 :
				return (char) ('A' + random.nextInt(26));
			case 1 :
				return (char) ('a' + random.nextInt(26));
			case 2 :
				return (char) ('0' + random.nextInt(10));
			default :
				return SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length()));
		}
	}
	
	public static String randomPassword2(int length) {
		char[] password = new char[length];
		Random random = new Random();
		for(int i=0; i<length; i++)
			password[i] = nextChar(random);
		return new String(password);
	}
	
	private static void swap(int[] array, int i, int j) {
		int temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}
	
	public static void shuffle(int[] array) {
		Random random = new Random();
		for(int i=array.length; i>1; i--) {
			swap(array, i-1, random.nextInt(i));
		}
	}
	
	class Pair {
		
		private Object item;
		private int weight;
		
		public Pair(Object item, int weight) {
			this.item = item;
			this.weight = weight;
		}
		
		public Object getItem() {
			return item;
		}
		
		public int getWeight() {
			return weight;
		}
		
	}
	
	class WeightRandom {
		
		private Pair[] pairs;
		private Random random;
		private double[] cumulativeProbabilities;
		
		public WeightRandom(Pair[] pairs) {
			random = new Random();
			this.pairs = pairs;
			cumulativeProbabilities = new double[pairs.length];
			prepare();
		}
		
		private void prepare() {
			int weights = 0;
			for(Pair pair : pairs)
				weights += pair.getWeight();
			int sum = 0;
			for(int i=0; i<pairs.length; i++) {
				Pair pair = pairs[i];
				sum += pair.getWeight();
				cumulativeProbabilities[i] = (double)sum / weights;
			}
		}
		
		public Object nextItem() {
			double val = random.nextDouble();
			int index = Arrays.binarySearch(cumulativeProbabilities, val);
			if(index < 0)
				index = -index -1;
			return pairs[index].getItem();
		}
		
	}
	
	
	
	

	public static void main(String[] args) {
		System.out.println(randomPassword2(10));
	}

}
