package demo32;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class FileSearchTask {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		String path = args[0];
		String key = args[1];
		int count = 0;
		List<FutureTask<Integer> > list = new ArrayList<>();
		File[] files = new File(path).listFiles();
		for(File file : files) {
			MatchCount matchCount = new MatchCount(file, key);
			FutureTask<Integer> futureTask = new FutureTask<>(matchCount);
			list.add(futureTask);
			Thread thread = new Thread(futureTask);
			thread.start();
		}
		for(FutureTask<Integer> futureTask : list) {
			count += futureTask.get();
		}
		System.out.println(count);
	}
	
	private static class MatchCount implements Callable<Integer> {
		File file;
		String keyWord;
		Integer count = 0;
		
		public MatchCount(File file, String key) {
			this.file = file;
			this.keyWord = key;
		}

		@Override
		public Integer call() throws Exception {
			if(search(file))
				count++;
			return count;
		}
		
		public boolean search(File file) {
			boolean found = false;
			try (Scanner scanner = new Scanner(new FileInputStream(file))){
				while(!found && scanner.hasNextLine()) {
					String line = scanner.nextLine();
					if(line.contains(keyWord))
						found = true;
				}
			} catch (IOException e) {
				
			}
			
			return found;
		}
		
	}
	

}
