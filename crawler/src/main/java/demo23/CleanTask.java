package demo23;

public class CleanTask extends Thread{
	
	private String disk;
	private String folderPath;
	
	public CleanTask(String disk, String folderPath) {
		this.disk = disk;
		this.folderPath = folderPath;
	}
	
	
	@Override
	public void run() {
		while(true) {
			TimeCellMaker timeCellMaker = new TimeCellMaker(disk, folderPath);
			int timeCell = timeCellMaker.getTimeCell();
			FileFolderCleaner cleaner = new FileFolderCleaner(folderPath, timeCell);
			cleaner.deleter();
			try {
				Thread.sleep(3600 * 10 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

}
