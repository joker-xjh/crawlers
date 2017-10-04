package demo14.classloader;

import java.io.IOException;

import demo14.api.IClassLoader;

public class ClassLoaderFactory {
	
	private ClassLoaderFactory() {
		
	}
	
	
	public static IClassLoader createClassLoader(Class<?> clazz, String jarPath) throws IOException {
		return createClassLoader(clazz, jarPath, null);
	}
	
	public static IClassLoader createClassLoader(Class<?> clazz, String jarPath, ClassLoader parent) throws IOException {
		if(clazz == CustomerJarUrlLoader.class) {
			if(parent == null) {
				return new CustomerJarUrlLoader(jarPath);
			}
			else {
				return new CustomerJarUrlLoader(jarPath, parent);
			}
		}
		else {
			System.out.println("没有找到IClassLoader相关的实现类");
		}
		
		return null;
	}

}
