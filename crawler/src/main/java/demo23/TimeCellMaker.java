package demo23;

import java.io.File;
import java.util.Date;

public class TimeCellMaker {
	
	private int timeCell;
	
	private double emptyRate = 0.4;
	
	private String witchDisk;
	
	private String witchFolder;
	
	
	public TimeCellMaker(String witchDisk, String witchFolder) {
		this.witchDisk = witchDisk;
		this.witchFolder = witchFolder;
	}
	
	public TimeCellMaker(String witchDisk, String witchFolder, double emptyRate) {
		this.witchDisk = witchDisk;
		this.witchFolder = witchFolder;
		this.emptyRate = emptyRate;
	}
	
	
	private long folderSize(int dayAgo) {
		File file = new File(witchFolder);
		if(!file.exists())
			return 0;
		if(!file.isDirectory())
			return 0;
		long size = 0;
		File[] children = file.listFiles();
		for(int i=0; i<children.length; i++) {
			File child = children[i];
			if(child.isFile()) {
				if(this.judger(child) >= dayAgo)
					size += child.length();
			}
			else if(child.isDirectory()){
				this.setWitchFolder(child.getAbsolutePath());
				size += this.folderSize(dayAgo);
			}
		}
		
		return size;
	}
	
	
	private int timeCellCalculator() {
		File disk = new File(witchDisk+":");
		timeCell = 60;
		long freeSize = disk.getFreeSpace();
		long totalSize = disk.getTotalSpace();
		double freeRate = ((double) (freeSize))/((double)(totalSize));
		while(freeRate < emptyRate && timeCell > 10) {
			timeCell--;
			freeRate = ((double) ((freeSize + this.folderSize(timeCell)))) / ((double)(totalSize));
		}
		return timeCell;
	}
	
	public int getTimeCell() {
		timeCell = timeCellCalculator();
		return timeCell;
	}
	
	
	public void setEmptyRate(double emptyRate) {
		this.emptyRate = emptyRate;
	}
	
	public void setWitchFolder(String witchFolder) {
		this.witchFolder = witchFolder;
	}
	
	private int judger(File witchFolder) {
		long modify = witchFolder.lastModified();
		Date now = new Date();
		long nowTime = now.getTime();
		int dayAgo = (int) ((nowTime - modify) / (24 * 3600 * 1000));
		return dayAgo;
	}
	

}
