package demo23;

import java.io.File;
import java.util.Date;

public class FileFolderCleaner {
	
	private String filePath;
	private int timeCell;
	
	public FileFolderCleaner(String filePath, int timeCell) {
		this.timeCell = timeCell;
		this.filePath = filePath;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public void setTimeCell(int timeCell) {
		this.timeCell = timeCell;
	}
	
	
	public boolean judger(File file) {
		long modify = file.lastModified();
		Date now = new Date();
		long nowTime = now.getTime();
		int day = (int) ((nowTime -modify) / (24 * 3600 * 1000));
		if(day >= timeCell)
			return true;
		return false;
	}
	
	public void deleter() {
		File file = new File(filePath);
		if(!file.exists())
			return;
		if(!file.isDirectory())
			return;
		File[] files = file.listFiles();
		for(int i=0; i<files.length; i++) {
			File child = files[i];
			if(child.isFile()) {
				if(judger(child)) {
					System.out.println("清理文件: "+child.getPath());
					child.delete();
				}
			}
			else if(child.isDirectory()) {
				if(child.list().length == 0) {
					if(this.judger(child))
						child.delete();
				}
				setFilePath(child.getAbsolutePath());
				this.deleter();
			}
		}
	}
	
	
	
	
	
	

}
