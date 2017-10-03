package demo11;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MyClassLoads extends ClassLoader{
	
	private static MyClassLoads myClassLoads = new MyClassLoads();
	
	private MyClassLoads() {
		
	}
	
	public static MyClassLoads getInstance() {
		return myClassLoads;
	}
	
	
	public Object findNewClass(String classPath) {
		try {
			byte[] data = getBytes(classPath);
			return defineClass(null, data, 0, data.length).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	@SuppressWarnings("unchecked")
	public static <T> T reloadClass(Object object) {
		return (T)object;
	}
	
	
	private byte[] getBytes(String file) throws IOException{
		File f = new File(file);
		byte[] data = new byte[(int) f.length()];
		FileInputStream in = new FileInputStream(f);
		in.read(data);
		in.close();
		return data;
	}

}
