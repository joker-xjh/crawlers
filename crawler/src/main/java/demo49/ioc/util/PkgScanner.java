package demo49.ioc.util;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import demo49.annotation.Component;

public class PkgScanner {
	
	private String pkgName;
	
	private String pkgPath;
	
	private Class<?> annotationClass;
	
	private ClassLoader classLoader;
	
	public PkgScanner(String pkgName) {
		this.pkgName = pkgName;
		pkgPath = PathUtils.packageToPath(pkgName);
		classLoader = Thread.currentThread().getContextClassLoader();
	}
	
	public PkgScanner(String pkgName, Class<?> annotation) {
		this(pkgName);
		this.annotationClass = annotation;
	}
	
	public List<String> scan() throws IOException{
		List<String> list = loadResource();
		if(annotationClass != null)
			list = filterComponent(list);
		return list;
	}
	
	private List<String> loadResource() throws IOException {
		List<String> list = null;
		Enumeration<URL> urls = classLoader.getResources(pkgPath);
		while(urls.hasMoreElements()) {
			URL url = urls.nextElement();
			list = scanFile(java.net.URLDecoder.decode(url.getPath(), "utf-8"), pkgName);
			break;
		}
		
		return list;
	}
	
	
	
	private List<String> scanFile(String path, String basePkg){
		File file = new File(path);
		List<String> list = new ArrayList<>();
		File[] files = file.listFiles();
		if(files == null)
			return list;
		for(int i=0; i<files.length; i++) {
			File f = files[i];
			if(f.isDirectory()) {
				List<String> sub = scanFile(f.getAbsolutePath(), basePkg+"."+f.getName());
				list.addAll(sub);
			}
			else if(f.getName().endsWith(ResourceType.CLASS_FILE.getTypeString())) {
				String className = PathUtils.trimSuffix(f.getName());
				if(className.indexOf("$") != -1)
					continue;
				String result = basePkg + "." +className;
				list.add(result);
			}
		}
		return list;
	}
	
	
	private List<String> filterComponent(List<String> classList) {
		List<String> list = new ArrayList<>();
		if(classList == null)
			return null;
		for(String name : classList) {
			try {
				Class<?> clazz = Class.forName(name);
				Annotation annotation = clazz.getAnnotation(Component.class);
				if(annotation != null) {
					list.add(name);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

}
