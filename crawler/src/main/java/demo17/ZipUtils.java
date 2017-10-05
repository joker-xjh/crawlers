package demo17;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtils {
	
	private ZipUtils() {
		
	}
	
	private static ExecutorService pool = Executors.newCachedThreadPool();
	
	@SuppressWarnings({ "unchecked" })
	public static void unzip(String zipFile, String outputDirectory) throws IOException {
		ZipFile zFile = new ZipFile(zipFile);
		List<ZipEntry> list = new ArrayList<>();
		Enumeration<ZipEntry> zEnumeration = (Enumeration<ZipEntry>) zFile.entries();
		while(zEnumeration.hasMoreElements()) {
			ZipEntry zipEntry = zEnumeration.nextElement();
			if(zipEntry.isDirectory()) {
				String fileName = zipEntry.getName().substring(0, zipEntry.getName().length()-1);
				String directoryPath = outputDirectory + File.separator + fileName;
				File file = new File(directoryPath);
				file.mkdir();
			}
			list.add(zipEntry);
		}
		unzip(zFile, list, outputDirectory);
	}
	
	private static void unzip(ZipFile zipFile, List<ZipEntry> list, String outputDirectory) throws IOException {
		for(ZipEntry zipEntry : list) {
			pool.execute(new MultiThreadEntry(zipFile, zipEntry, outputDirectory));
		}
	}
	
	
	@SuppressWarnings("unused")
	private static void deleteFile(String file) {
		File f = new File(file);
		if(f.isFile() && f.exists())
			f.delete();
	}
	
	
	
	
	 static class MultiThreadEntry implements Runnable{
		
		private static final int  BUFF_SIZE = 4096;
		private ZipEntry zipEntry;
		private String outputDirectory;
		private ZipFile zipFile;
		
		public MultiThreadEntry(ZipFile zipFile, ZipEntry zipEntry, String outputDirectory) throws IOException {
			this.zipEntry = zipEntry;
			this.zipFile = zipFile;
			this.outputDirectory = outputDirectory;
		}

		@Override
		public void run() {
			if(zipEntry.isDirectory())
				return;
			byte[] buff = new byte[BUFF_SIZE];
			String entryName = zipEntry.getName();
			String path = outputDirectory + File.separator + entryName;
			try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(path));
					BufferedInputStream in = new BufferedInputStream(zipFile.getInputStream(zipEntry));){
				int read = -1;
				while((read = in.read(buff)) != -1) {
					out.write(buff, 0, read);
				}
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	
	
	
	

}
