package demo63;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DiskClassLoader extends ClassLoader{

	private String path;
	
	public DiskClassLoader(String path) {
		this.path = path;
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		String fileName = getFileName(name);
		File file = new File(path, fileName);
		try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
			 ByteArrayOutputStream out = new ByteArrayOutputStream()){
			int length = -1;
			byte[] buf = new byte[1024];
			while((length = in.read(buf)) != -1) {
				out.write(buf, 0, length);
			}
			byte[] data = out.toByteArray();
			return defineClass(name, data, 0, data.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return super.findClass(name);
	}
	
	private String getFileName(String name) {
		int index = name.lastIndexOf(".");
		if(index == -1) {
			return name + ".class";
		}
		else {
			return name.substring(index) + ".class";
		}
	}
	
}
