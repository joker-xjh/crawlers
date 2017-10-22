package demo32;

import java.util.concurrent.Callable;

public class MyRunnableAdapter<T> implements Callable<T>{
	
	Runnable runnable;
	T result;
	
	public MyRunnableAdapter(Runnable runnable, T result) {
		this.runnable = runnable;
		this.result = result;
	}

	@Override
	public T call() throws Exception {
		runnable.run();
		return result;
	}

}
