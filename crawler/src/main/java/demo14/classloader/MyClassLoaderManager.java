package demo14.classloader;

import java.io.IOException;

public class MyClassLoaderManager {
	
	private CustomerJarUrlLoader jarUrlLoader;
	
	private Class<?> classLoaderType = CustomerJarUrlLoader.class;
	
	public Class<?> getClassLoaderType() {
		return classLoaderType;
	}
	
	private MyClassLoaderManager() {
		
	}
	
	private static MyClassLoaderManager myClassLoaderManager = new MyClassLoaderManager();
	
	public static MyClassLoaderManager getInstance() {
		return myClassLoaderManager;
	}
	
	
	public synchronized void reloadJar(String jarPath) {
		if(jarUrlLoader == null)
			try {
				jarUrlLoader = (CustomerJarUrlLoader) ClassLoaderFactory.createClassLoader(classLoaderType, jarPath);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		jarUrlLoader.addJar(jarPath);
	}
	
	public synchronized void reloadJar(String jarPath, ClassLoader parent) {
		if(jarUrlLoader == null)
			try {
				jarUrlLoader = (CustomerJarUrlLoader) ClassLoaderFactory.createClassLoader(classLoaderType, jarPath,parent);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		jarUrlLoader.addJar(jarPath);
	}
	
	
	public Class<?> findClass(String packageClassName){
		if(jarUrlLoader != null) {
			return jarUrlLoader.loadClass(packageClassName);
		}
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	public <T> T getObject(Class<T> t) {
		T instance = null;
		try {
			String className = t.getName();
			Class<?> clazz = findClass(className);
			instance = (T) clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return instance;
	}
	
	
	
	
	
	

}
