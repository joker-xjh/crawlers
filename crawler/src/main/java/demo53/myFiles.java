package demo53;

import java.io.File;

public class myFiles {
	
	private int blockName;
	
	private File file;
	
	private String fileName;
	
	private long space;
	
	public myFiles(File myFile, int blockName, long capacity) {
		file = myFile;
		this.blockName = blockName;
		space = capacity;
		fileName = file.getName();
	}
	
	public boolean renameFile(String rename) {
		boolean b = false;
		String parent = file.getParent();
		File temp = new File(parent + File.separator + rename);
		if(file.renameTo(temp)) {
			file = temp;
			b = true;
		}
		return b;
	}
	
	public File getFile() {
		return file;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public int getBlockName() {
		return blockName;
	}
	
	public long getSpace() {
		return space;
	}
	
	@Override
	public String toString() {
		return fileName;
	}

}
