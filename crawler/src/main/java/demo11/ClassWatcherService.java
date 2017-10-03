package demo11;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public class ClassWatcherService implements Runnable{
	
	private WatchService watcher;
	
	private static ClassWatcherService watchservice = new ClassWatcherService();
	
	// 监控目录
	private static String path = "";
	
	private Object classTemp;
	
	private boolean isRunner ;
	
	private ClassWatcherService() {
		try {
			watcher = FileSystems.getDefault().newWatchService();
			Paths.get(path).register(watcher, ENTRY_CREATE,ENTRY_MODIFY);
		} catch (IOException e) {
			
		}
	}
	
	
	public static ClassWatcherService getInstance() {
		return watchservice;
	}
	
	public ClassWatcherService startServer() {
		if(!isRunner)
			new Thread(this).start();
		return this;
	}
	
	private void handleEvent() {
		while(true) {
			try {
				WatchKey key = watcher.take();
				for(WatchEvent<?> event : key.pollEvents()) {
					Kind<?> kind = event.kind();
					if(kind == OVERFLOW)
						continue;
					@SuppressWarnings("unchecked")
					WatchEvent<Path> e = (WatchEvent<Path>) event;
					Path fileName = e.context();
					System.err.println("发现目录下有Class发生变化.进行热加载" + path + "\\" + fileName);
					classTemp = MyClassLoads.getInstance().findNewClass(path+"\\"+fileName);
					System.out.println(classTemp);
				}
				if(!key.reset())
					break;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	

	@Override
	public void run() {
		if(!isRunner) {
			isRunner = !isRunner;
			handleEvent();
		}
		System.out.println("文件监听已运行");
	}

}
