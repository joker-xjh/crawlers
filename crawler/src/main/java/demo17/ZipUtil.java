package demo17;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
	
	private ZipUtil() {
		
	}
	
	
	public static void zipFile(File srcFile, File zipFile, boolean overwrite) throws IOException {
		if(srcFile == null || zipFile == null) {
			throw new IllegalArgumentException("zipFile和srcFile不能为空!");
		}
		
		if(zipFile.exists() && !overwrite) {
			throw new IOException(zipFile.getAbsolutePath()+"文件已存在，参数设定了不能覆盖。");
		}
		
		if(!zipFile.exists()) {
			zipFile.createNewFile();
		}
		
		try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(srcFile));
				ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(srcFile)));){
			byte[] buff = new byte[2048];
			ZipEntry zipEntry = new ZipEntry(srcFile.getName());
			zipOut.putNextEntry(zipEntry);
			
			int read = -1;
			while((read = in.read(buff)) != -1) {
				zipOut.write(buff, 0, read);
			}
			zipOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void zipFile(File srcFile, File zipFile) throws IOException {
		zipFile(srcFile, zipFile, true);
	}
	
	public static void zipFile(File srcFile) throws IOException {
		zipFile(srcFile, new File(srcFile.getAbsolutePath() + ".zip"), true);
	}
	
	
	public static void zipDirectory(File srcDir, File zipFile, boolean overwrite) throws IOException {
		if(srcDir == null || zipFile == null) {
			throw new IllegalArgumentException("zipFile和srcDir不能为空!");
		}
		if(zipFile.exists() && !overwrite) {
			throw new IOException(zipFile.getAbsolutePath()+"文件已经存在，参数设定了不能覆盖。");
		}
		if(!zipFile.exists()) {
			zipFile.createNewFile();
		}
		try (ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)))){
			zipDirectory(zipOut, srcDir, srcDir.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void zipDirectory(ZipOutputStream zipOut, File file, String base) throws IOException{
		if(file.isDirectory()) {
			File[] files = file.listFiles();
			zipOut.putNextEntry(new ZipEntry(base+"/"));
			base = (base.length() == 0) ? "" : base+"/";
			for(File child : files) {
				zipDirectory(zipOut, child, base+child.getName());
			}
		}
		else {
			zipOut.putNextEntry(new ZipEntry(base));
			try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));){
				byte[] buff = new byte[2048];
				int read = -1;
				while((read = in.read(buff)) != -1) {
					zipOut.write(buff, 0, read);
				}
				zipOut.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	
	public static void zipDirectory(File srcDir) throws IOException {
		zipDirectory(srcDir, new File(srcDir.getAbsolutePath()+".zip"), true);
	}
	
	
	public static void zipFiles(File[] files, File zipFile, boolean overwrite) throws IOException {
		if (zipFile == null || files == null) {
            throw new IllegalArgumentException("zipFile和srcDir不能为空!");
        }
		
		if(files.length == 0) {
			throw new IOException("不能对一个空的文件列表进行压缩");
		}
		
		if(zipFile.exists() && !overwrite) {
			throw new IOException(zipFile.getAbsolutePath() + "文件已存在，参数设定了不能覆盖。");
		}
		
		if(!zipFile.exists()) {
			zipFile.createNewFile();
		}
		
		try (ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));){
			byte[] buff = new byte[2048];
			for(File file : files) {
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
				int read = -1;
				while((read = in.read(buff)) != -1) {
					zipOut.write(buff, 0, read);
				}
				zipOut.flush();
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void zipFiles(File[] files, File zipFile) throws IOException {
		zipFiles(files, zipFile, true);
	}
	
	
	public static void zipFiles(List<File> files, File zipFile, boolean overwrite) throws IOException {
		zipFiles(files.toArray(new File[files.size()]), zipFile, overwrite);
	}
	
	
	public static void zipFiles(List<File> files, File zipFile) throws IOException {
		zipFiles(files, zipFile,true);
	}
	
	

}
