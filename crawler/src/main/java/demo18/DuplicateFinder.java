package demo18;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class DuplicateFinder {
	
	
	
	
	public void checkFiles(List<File> files) {
		if(files.size() < 2) {
			return;
		}
		for(int i=0; i<files.size(); i++) {
			File firstFile = files.get(i);
			if(!firstFile.canRead()) {
				System.out.println("Can't read "+firstFile.getName());
				continue;
			}
			for(int j=i+1; j<files.size(); j++) {
				File secondFile = files.get(j);
				if(!secondFile.canRead()) {
					System.out.println("Can't read "+secondFile.getName());
					continue;
				}
				if(isSame(firstFile, secondFile)) {
					System.out.println(firstFile.getAbsolutePath()+" = "+secondFile.getAbsolutePath());
				}
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	public boolean isSame(File firstFile, File secondFile) {
		long start = System.currentTimeMillis();
		try (BufferedInputStream first = new BufferedInputStream(new FileInputStream(firstFile));
				BufferedInputStream second = new BufferedInputStream(new FileInputStream(secondFile));){
			int firstInt = 0;
			int secondInt = 0;
			while(((firstInt = first.read()) != -1 ) && ((secondInt = second.read())!=-1)) {
				if(firstInt != secondInt) {
					 System.out.println("\tInequal after"+ (System.currentTimeMillis() - start) + " ms");
				}
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return true;
	}

}
