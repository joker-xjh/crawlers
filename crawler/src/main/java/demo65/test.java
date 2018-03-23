package demo65;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class test {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		Future<Integer> f1 = executorService.submit(new Callable<Integer>() {

			@Override
			public Integer call() throws Exception {
				
				Future<Integer> f2 = executorService.submit(new Callable<Integer>() {
					@Override
					public Integer call() throws Exception {
						return 1;
					}
				});
				
				f2.get();
				return 1;
			}
			
		});
		System.out.println(f1.get());
		executorService.shutdown();

	}

}
