package demo13;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class FileSystemClassloader extends ClassLoader{

	private String rootDir;
	
	public FileSystemClassloader(String rootDir) {
		this.rootDir = rootDir;
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		String classPath = rootDir + name.replace('.', '/') + ".class";
		try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(classPath));
				ByteArrayOutputStream out = new ByteArrayOutputStream();){
			byte[] buff = new byte[1024];
			int read = -1;
			while((read = in.read(buff)) != -1) {
				out.write(buff, 0, read);
			}
			out.flush();
			byte[] data = out.toByteArray();
			Class<?> clazz = defineClass(name, data, 0, data.length);
			if(clazz != null)
				return clazz;
			else
				return super.findClass(name);
		} catch (IOException e) {
			e.printStackTrace();
			return super.findClass(name);
		}
		
	}
	
	
}
