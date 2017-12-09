package demo60;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FileManage {
	
	private List<FileFCB> fileFCBs;
	
	public FileManage() {
		fileFCBs = new ArrayList<>();
		FileFCB rootFCB = new FileFCB("root");
		rootFCB.setParentID(Constants.PARENT_OF_ROOT);
		rootFCB.setFcb_Type(FCB_Type.directory);
		fileFCBs.add(rootFCB);
	}
	
	public List<FileFCB> getFileFCBs() {
		return fileFCBs;
	}
	
	
	public FileFCB getRoot() {
		return fileFCBs.get(0);
	}
	
	public void formatFileSystem() {
		FileFCB root = fileFCBs.get(0);
		fileFCBs.clear();
		root.setFcb_Type(FCB_Type.directory);
		root.setParentID(Constants.PARENT_OF_ROOT);
	}
	
	private FileFCB searchFCBByID(int ID) {
		FileFCB fcb = null;
		for(FileFCB temp : fileFCBs) {
			if(temp.getID() == ID) {
				fcb = temp;
				break;
			}
		}
		return fcb;
	}
	
	
	private void searchFCBByParentID(int parentID, List<FileFCB> list) {
		for(FileFCB fcb : fileFCBs) {
			if(fcb.getParentID() == parentID)
				list.add(fcb);
		}
	}
	
	
	private void recursiveDeleteDir(int dirID, List<FileFCB> deleteList) {
		int beforeSize = deleteList.size();
		searchFCBByID(dirID);
		int afterSize = deleteList.size();
		if(beforeSize == afterSize)
			return;
		for(int i=beforeSize; i<afterSize; i++) {
			FileFCB fcb = fileFCBs.get(i);
			if(fcb.getFcb_Type() == FCB_Type.directory) {
				recursiveDeleteDir(fcb.getID(), deleteList);
				fileFCBs.remove(fcb);
			}
				
		}
	}
	
	private boolean isDupilicationOfName(String fileName, int dirID, FCB_Type fcb_Type) {
		for(FileFCB fcb : fileFCBs) {
			if(fcb.getParentID() == dirID &&
				fcb.getFcb_Type() == fcb_Type &&
				fcb.getFileName().equals(fileName))
				return true;
		}
		return false;
	}
	
	private boolean isNameLegal(String fileName) {
		if(fileName == null || fileName.length() == 0 || fileName.length() >= 255)
			return false;
		String regex = "^[a-zA-Z_]+[a-zA-Z0-9_]*";
		return !Pattern.compile(regex).matcher(fileName).matches();
	}
	
	private String[] splitString(String buffer) {
		int count = buffer.length() / Constants.CLUSTER_SIZE;
		int surplus = buffer.length() % Constants.CLUSTER_SIZE;
		if(surplus != 0)
			count++;
		String[] array = new String[count];
		for(int i=0; i<count; i++) {
			if(i< count - 1) {
				array[i] = buffer.substring(i * Constants.CLUSTER_SIZE, (i+1) * Constants.CLUSTER_SIZE);
			}
			else {
				array[i] = buffer.substring(i*Constants.CLUSTER_SIZE);
			}
		}
		return array;
	}
	
	public int getDirSize(int dirID) {
		int size = 0;
		for(FileFCB fcb : fileFCBs) {
			if(fcb.getParentID() == dirID)
				size += fcb.getFileSize();
		}
		return size;
	}
	
	public void fresh() {
		for(FileFCB fcb : fileFCBs) {
			if(fcb.getFcb_Type() == FCB_Type.directory) {
				int newSize = getDirSize(fcb.getID());
				fcb.setFileSize(newSize);
			}
		}
	}
	
	
	public int getSubFileNum(int dirID) {
		List<FileFCB> list = new ArrayList<>();
		searchFCBByParentID(dirID, list);
		return list.size();
	}
	
	public String readFile(int fileID) {
		StringBuilder data = new StringBuilder();
		FileFCB fcb = searchFCBByID(fileID);
		FileFat fat = fcb.getFileFat();
		while(fat.getNext() != Constants.END_OF_FAT) {
			data.append(fat.getData());
			fat = fat.getNext();
		}
		data.append(fat.getData());
		return data.toString();
	}
	
	
	public int getRootSize() {
		int size = 0;
		FileFCB root = fileFCBs.get(0);
		for(FileFCB fcb : fileFCBs) {
			if(fcb.getParentID() == root.getID())
				size += fcb.getFileSize();
		}
		return size;
	}
	
	
	public Status_Type saveFile(int fileID, String data) {
		FileFCB fcb = searchFCBByID(fileID);
		if(getRootSize() - fcb.getFileSize() + data.length() >= Constants.MEMORY_SIZE)
			return Status_Type.memory_lack;
		String[] array = splitString(data);
		int length = array.length;
		FileFat[] fats = new FileFat[length];
		FileFat pre = null;
		for(int i=0; i<length; i++) {
			fats[i] = new FileFat();
			fats[i].setData(array[i]);
			if(pre != null) {
				pre.setNext(fats[i]);
			}
			pre = fats[i];
		}
		fcb.setFileFat(fats[0]);
		fcb.setModifyDate(System.currentTimeMillis());
		return Status_Type.all_right;
	}
	
	
	public Status_Type move(int fileID, int parentID) {
		List<FileFCB> targetSubFiles = new ArrayList<>();
		FileFCB moved = searchFCBByID(fileID);
		searchFCBByParentID(parentID, targetSubFiles);
		for(FileFCB fcb : targetSubFiles) {
			if(fcb.getFcb_Type() == moved.getFcb_Type() && fcb.getFileName().equals(moved.getFileName()))
				return Status_Type.dupilication_of_name;
		}
		moved.setParentID(parentID);
		fresh();
		moved.setModifyDate(System.currentTimeMillis());
		return Status_Type.all_right;
	}
	
	
	public Status_Type rename(int fileID, String newName) {
		if(isNameLegal(newName))
			return Status_Type.illegal_name;
		FileFCB fcb = searchFCBByID(fileID);
		if(isDupilicationOfName(newName, fcb.getParentID(), fcb.getFcb_Type()))
			return Status_Type.dupilication_of_name;
		fcb.setFileName(newName);
		fcb.setModifyDate(System.currentTimeMillis());
		return Status_Type.all_right;
	}
	
	
	public void deleteFile(int fileID) {
		FileFCB delete = searchFCBByID(fileID);
		if(delete == null)
			return;
		delete.setFileFat(null);
		fileFCBs.remove(delete);
	}
	
	public void deleteDir(int dirID) {
		List<FileFCB> deletes = new ArrayList<>();
		recursiveDeleteDir(dirID, deletes);
		fileFCBs.removeAll(deletes);
	}
	
	public Status_Type createFile(String fileName, int parentID) {
		if(isNameLegal(fileName))
			return Status_Type.illegal_name;
		if(isDupilicationOfName(fileName, parentID, FCB_Type.file))
			return Status_Type.dupilication_of_name;
		FileFCB fcb = new FileFCB(fileName);
		fcb.setFcb_Type(FCB_Type.file);
		fcb.setParentID(parentID);
		fileFCBs.add(fcb);
		return Status_Type.all_right;
	}
	
	
	public Status_Type createDir(String fileName, int parentID) {
		if(isNameLegal(fileName))
			return Status_Type.illegal_name;
		if(isDupilicationOfName(fileName, parentID, FCB_Type.directory))
			return Status_Type.dupilication_of_name;
		FileFCB fcb = new FileFCB(fileName);
		fcb.setFcb_Type(FCB_Type.directory);
		fcb.setParentID(parentID);
		fileFCBs.add(fcb);
		return Status_Type.all_right;
	}
	
	

}
