package demo14.classloader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import demo14.api.IClassLoader;

public class CustomerJarUrlLoader implements IClassLoader{
	
	private URLClassLoader classloader;
	
	public CustomerJarUrlLoader(String filePath) throws IOException {
		URL url = new URL("file:"+filePath);
		classloader = new URLClassLoader(new URL[]{url});
	}
	
	public CustomerJarUrlLoader(String filePath, ClassLoader parent) throws IOException {
		URL url = new URL("file:"+filePath);
		classloader = new URLClassLoader(new URL[]{url}, parent);
	}
	
	public void addJar(String jarPath) {
		try {
			File file = new File(jarPath);
			URL url = file.toURI().toURL();
			Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
			boolean accessable = method.isAccessible();
			method.setAccessible(true);
			method.invoke(classloader, url);
			method.setAccessible(accessable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	

	@Override
	public Class<?> loadClass(String packageClassName) {
		Class<?> clazz = null;
		try {
			clazz = classloader.loadClass(packageClassName);
			return clazz;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
