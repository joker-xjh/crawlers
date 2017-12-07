package demo58;

import java.io.InputStream;

public class MyClassLoader extends ClassLoader{
	
	public MyClassLoader(ClassLoader parent) {
		super(parent);
	}
	
	public MyClassLoader() {
		
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		name = name.substring(0, name.lastIndexOf(".")) + ".class";
		try (InputStream in = getClass().getResourceAsStream(name)){
			if(in == null)
				return null;
			byte[] data = new byte[in.available()];
			in.read(data);
			return defineClass(name, data, 0, data.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
