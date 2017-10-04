package demo14.classloader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class CustomerJarLoader extends ClassLoader{
	
	private Set<String> dynaclazns;
	
	public CustomerJarLoader(String jarPath) {
		dynaclazns = new HashSet<>();
		loadClassByMe(jarPath);
	}
	
	private void loadClassByMe(String jarPath) {
		try {
			@SuppressWarnings("resource")
			JarFile jarFile = new JarFile(jarPath);
			Enumeration<JarEntry> jarEntries = jarFile.entries();
			while(jarEntries.hasMoreElements()) {
				JarEntry jarEntry = jarEntries.nextElement();
				if(jarEntry.getName().endsWith(".class")) {
					String jarClassName = jarEntry.getName().substring(0,jarEntry.getName().lastIndexOf('.'));
					String className = jarClassName.replace('/', '.');
					BufferedInputStream inputStream = new BufferedInputStream(jarFile.getInputStream(jarEntry));
					instantiateClass(className, inputStream, jarFile.getInputStream(jarEntry).available());
					dynaclazns.add(className);
					inputStream.close();
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	private Class<?> instantiateClass(String name, InputStream inputStream, long length) throws IOException{
		byte[] data = new byte[(int) length];
		inputStream.read(data);
		return defineClass(name, data, 0, data.length);
	}
	
	public Set<String> getDynaclazns() {
		return dynaclazns;
	}
	
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		Class<?> cls = null;
		cls = findLoadedClass(name);
		if (!this.dynaclazns.contains(name) && cls == null)
			cls = getSystemClassLoader().loadClass(name);
		if (cls == null)
			throw new ClassNotFoundException(name);
		if (resolve)
			resolveClass(cls);
		return cls;
	}

}
