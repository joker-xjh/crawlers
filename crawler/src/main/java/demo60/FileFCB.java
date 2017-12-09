package demo60;

public class FileFCB {
	
	private String fileName;
	private int ID;
	private int parentID;
	private long createDate;
	private long modifyDate;
	private FCB_Type fcb_Type;
	private int fileSize;
	private FileFat fileFat;
	
	public FileFCB(String name) {
		this.fileName = name;
		ID = hashCode();
		createDate = modifyDate = System.currentTimeMillis();
	}
	
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public int getParentID() {
		return parentID;
	}
	public void setParentID(int parentID) {
		this.parentID = parentID;
	}
	public long getCreateDate() {
		return createDate;
	}
	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}
	public long getModifyDate() {
		return modifyDate;
	}
	public void setModifyDate(long modifyDate) {
		this.modifyDate = modifyDate;
	}
	public FCB_Type getFcb_Type() {
		return fcb_Type;
	}
	public void setFcb_Type(FCB_Type fcb_Type) {
		this.fcb_Type = fcb_Type;
	}
	public int getFileSize() {
		return fileSize;
	}
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
	public FileFat getFileFat() {
		return fileFat;
	}
	public void setFileFat(FileFat fileFat) {
		this.fileFat = fileFat;
	}
	
	

}
