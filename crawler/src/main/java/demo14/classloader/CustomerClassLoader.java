package demo14.classloader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class CustomerClassLoader extends ClassLoader{

	private String baseDir;
	
	private Set<String> dynaclazns;
	
	public CustomerClassLoader(String baseDir, String[] clazzes) {
		this.baseDir = baseDir;
		dynaclazns = new HashSet<>();
		loadClassByMe(clazzes);
	}
	
	private void loadClassByMe(String[] clazzes) {
		for(int i=0; i<clazzes.length; i++) {
			try {
				loadDirectly(clazzes[i]);
				dynaclazns.add(clazzes[i]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private Class<?> loadDirectly(String name) throws IOException {
		String classPath = baseDir + name.replace('.', File.separatorChar) + ".class";
		File file = new File(classPath);
		BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(classPath));
		Class<?> clazz = instantiateClass(name, inputStream, (int)file.length());
		return clazz;
	}
	
	
	
	private Class<?> instantiateClass(String name, InputStream inputStream, int length) throws IOException {
		byte[] data = new byte[length];
		inputStream.read(data);
		inputStream.close();
		return defineClass(name, data, 0, length);
	}
	
	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		Class<?> clazz = findLoadedClass(name);
		if(!dynaclazns.contains(name) && clazz == null) {
			clazz = getSystemClassLoader().loadClass(name);
		}
		if(clazz == null)
			throw new ClassNotFoundException(name);
		if(resolve)
			resolveClass(clazz);
		return clazz;
	}
	
	
	
	
	
	
}
