package demo53;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Block {
	
	private static final String RN = "\r\n";
	
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	private int blockName;
	
	private File blockFile;
	
	private File blockBitMap;
	
	private File recover;
	
	private FileWriter bitWriter;
	
	private FileWriter recoverWriter;
	
	private int fileCount;
	
	private long space;
	
	private int[][] bitMap = new int[32][32];
	
	private Map<String, int[][]> filesBit = new HashMap<>();
	
	private Set<File> files = new HashSet<>();
	
	public Block(int name, File file, boolean rec) throws IOException{
		this.blockName = name;
		blockFile = file;
		blockFile.mkdir();
		blockBitMap = new File(blockFile.getPath() + File.separator + blockName + "BitMap&&Fat.txt");
		recover = new File(blockFile.getPath() + File.separator + "recover.txt");
		if(!rec) {
			blockBitMap.createNewFile();
			bitWriter = new FileWriter(blockBitMap);
			for(int i=0; i<32; i++) {
				for(int j=0; j<32; j++) {
					bitMap[i][j] = 0;
					bitWriter.write("0");
				}
				bitWriter.write(RN);
			}
			bitWriter.flush();
			
			recover.createNewFile();
			recoverWriter = new FileWriter(recover);
			recoverWriter.write(String.valueOf(space) + RN);
			recoverWriter.write(String.valueOf(fileCount) + RN);
			for(int i=0; i<32; i++) {
				for(int j=0; j<32; j++) {
					if(bitMap[i][j] == 0)
						recoverWriter.write("0" + RN);
					else
						recoverWriter.write("1" + RN);
				}
			}
			recoverWriter.flush();
		}
		else {
			try (BufferedReader reader = new BufferedReader(new FileReader(recover))){
				space = Long.parseLong(reader.readLine());
				fileCount = Integer.parseInt(reader.readLine());
				for(int i=0; i<32; i++) {
					for(int j=0; j<32; j++) {
						if(Integer.parseInt(reader.readLine()) == 0)
							bitMap[i][j] = 0;
						else
							bitMap[i][j] = 1;
					}
				}
				
				String temp = "";
				while((temp = reader.readLine()) != null){
					File myFile = new File(blockFile.getPath() + File.separator + temp);
					files.add(myFile);
					int[][] tempBit = new int[32][32];
					for(int i=0; i<32; i++) {
						for(int j=0; j<32; j++) {
							if(reader.readLine() == "0")
								tempBit[i][j] = 0;
							else
								tempBit[i][j] = 1;
						}
					}
					filesBit.put(myFile.getName(), tempBit);
				}
				
			} catch (IOException e) {
				System.err.println("ERROR");
				System.exit(0);
			}
		}
	}
	
	
	public File getBlockFile() {
		return blockFile;
	}
	
	public void putFCB(File file, long capacity) throws IOException {
		FileWriter writer = new FileWriter(file);
		writer.write("File" + RN);
		writer.write(String.valueOf(capacity) + RN);
		writer.write("Name: "+file.getName() + RN);
		writer.write("Path: "+file.getPath() + RN);
		String time = simpleDateFormat.format(new Date(file.lastModified()));
		writer.write("Last Modify: "+time + RN);
		writer.write("--------------------------edit file blew ------------------------------"+ RN);
		writer.close();
	}
	
	
	public void rewriteBitMap() throws IOException {
		bitWriter = new FileWriter(blockBitMap);
		for(int i=0; i<32; i++) {
			for(int j=0; j<32; j++) {
				if(bitMap[i][j] == 0)
					bitWriter.write("0");
				else
					bitWriter.write("1");
			}
			bitWriter.write(RN);
		}
		
		for(File file : files) {
			String name = file.getName();
			bitWriter.write(name + ":");
			int[][] tempBit = filesBit.get(name);
			for(int i=0; i<32; i++) {
				for(int j=0; j<32; j++) {
					if(tempBit[i][j] == 1)
						bitWriter.write(String.valueOf(i*32 + j) + " ");
				}
			}
			bitWriter.write(RN);
		}
		bitWriter.flush();
	}
	
	
	public void rewriteRecover() throws IOException {
		recoverWriter = new FileWriter(recover);
		recoverWriter.write(String.valueOf(space) + RN);
		recoverWriter.write(String.valueOf(fileCount) + RN);
		for(int i=0; i<32; i++) {
			for(int j=0; j<32; j++) {
				if(bitMap[i][j] == 0)
					recoverWriter.write("0"+RN);
				else
					recoverWriter.write("1"+RN);
			}
		}
		for(File file : files) {
			String name = file.getName();
			int[][] tempBit = filesBit.get(name);
			recoverWriter.write(name + RN);
			for(int i=0; i<32; i++) {
				for(int j=0; j<32; j++) {
					if(tempBit[i][j] == 0)
						recoverWriter.write("0"+RN);
					else
						recoverWriter.write("1"+RN);
				}
			}
		}
		recoverWriter.flush();
	}
	
	public boolean createFile(File file, long capacity) throws IOException {
		files.add(file);
		file.createNewFile();
		int[][] bit = new int[32][32];
		for(int i=0; i<32; i++) {
			for(int j=0; j<32; j++) {
				bit[i][j] = 0;
			}
		}
		BufferedReader reader = new BufferedReader(new FileReader(blockBitMap));
		for(int i=0; i<32; i++) {
			String line = reader.readLine();
			for(int j=0; j<32; j++) {
				if(line.charAt(j) == '0') {
					capacity--;
					bit[i][j] = 1;
					bitMap[i][j] = 1;
				}
			}
		}
		reader.close();
		if(capacity > 0) {
			System.err.println("No space for "+file.getName());
			file.delete();
			for(int i=0; i<32; i++) {
				for(int j=0; j<32; j++) {
					if(bit[i][j] == 1)
						bitMap[i][j] = 0;
				}
			}
			return false;
		}
		else {
			fileCount++;
			space += capacity;
			filesBit.put(file.getName(), bit);
			putFCB(file, capacity);
			rewriteBitMap();
			rewriteRecover();
			return true;
		}
	}
	
	
	public boolean deleteFile(File file, long capacity) {
		if (file.getName().equals("1") || file.getName().equals("2") || file.getName().equals("3") || file.getName().equals("4")
                || file.getName().equals("5") || file.getName().equals("6") || file.getName().equals("7") || file.getName().equals("8")
                || file.getName().equals("9") || file.getName().equals("10") || file.getName().equals("1BitMap&&Fat.txt")
                || file.getName().equals("2BitMap&&Fat.txt") || file.getName().equals("3BitMap&&Fat.txt")
                || file.getName().equals("4BitMap&&Fat.txt") || file.getName().equals("5BitMap&&Fat.txt")
                || file.getName().equals("5BitMap&&Fat.txt") || file.getName().equals("6BitMap&&Fat.txt")
                || file.getName().equals("7BitMap&&Fat.txt") || file.getName().equals("8BitMap&&Fat.txt")
                || file.getName().equals("9BitMap&&Fat.txt") || file.getName().equals("10BitMap&&Fat.txt")
                || file.getName().equals("recover.txt")){
			System.err.println("The dir is protected!!");
            return false;
        }
		try {
			if(file.isFile()) {
				file.delete();
				space -= capacity;
				fileCount--;
				int[][] bit = filesBit.get(file.getName());
				for(int i=0; i<32; i++) {
					for(int j=0; j<32; j++) {
						if(bit[i][j] == 1 && bitMap[i][j] == 1)
							bitMap[i][j] = 0;
					}
				}
				filesBit.remove(file.getName());
				files.remove(file);
			}
			else {
				File[] subFiles = file.listFiles();
				for(File subFile : subFiles)
					deleteFile(subFile, capacity);
				if(file.exists())
					file.delete();
			}
			return true;
		} catch (Exception e) {
			System.err.println("error while deleting "+file.getName());
			return false;
		}
	}
	
	
	public boolean renameFile(File file, String name, long capacity) throws IOException {
		String oldName = file.getName();
		File oldFile = file;
		String parentName = file.getParent();
		File rename = null;
		if(file.isFile()) {
			rename = new File(parentName + File.separator + name + ".txt");
			if(file.renameTo(rename)) {
				int[][] bit = filesBit.get(oldName);
				files.remove(oldFile);
				filesBit.remove(oldName);
				files.add(file);
				filesBit.put(file.getName(), bit);
				return true;
			}
			return false;
		}
		else {
			rename = new File(parentName + File.separator + name);
			file.renameTo(rename);
			return true;
		}
		
	}
	
	
	
	public int getFileCount() {
		return fileCount;
	}
	
	
	public long getSpace() {
		return space;
	}
	

}
