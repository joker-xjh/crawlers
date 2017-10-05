package demo17;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZIPIt {
	
	private List<File> files;
	
	private String zipFileName;
	
	public ZIPIt(List<File> files, String zipFileName) {
		this.files = files;
		this.zipFileName = zipFileName;
	}
	
	public void zipFiles() {
		if(files.size() == 0)
			return;
		zipFileName = zipFileName +".zip";
		File file = new File(zipFileName);
		try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(file));){
			zipOutputStream.setLevel(Deflater.BEST_COMPRESSION);
			byte[] buff = new byte[2048];
			for(File f : files) {
				try(BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));) {
					String fileName = f.getName();
					zipOutputStream.putNextEntry(new ZipEntry(fileName));
					int read = -1;
					while((read = in.read(buff)) != -1) {
						zipOutputStream.write(buff, 0, read);
					}
					zipOutputStream.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
		} catch (Exception e) {
			
		}
	}

}
