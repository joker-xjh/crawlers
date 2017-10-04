package demo13;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class URLClassLoader extends ClassLoader{

	private String address;
	
	public URLClassLoader(String address) {
		this.address = address;
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		String classPath = address + name.replace('.', '/') + ".class";
		URL url = null;
		try {
			url = new URL(classPath);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return super.findClass(name);
		}
		try (BufferedInputStream in = new BufferedInputStream(url.openConnection().getInputStream());
				ByteArrayOutputStream out = new ByteArrayOutputStream();){
			byte[] buff = new byte[1024];
			int read = -1;
			while((read = in.read(buff))!=-1) {
				out.write(buff, 0, read);
			}
			out.flush();
			byte[] data = out.toByteArray();
			Class<?> clazz = defineClass(name, buff, 0, data.length);
			if(clazz != null)
				return clazz;
			else
				return super.findClass(name);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return super.findClass(name);
	}
	
	
}
