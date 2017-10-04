package demo13;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class EncryptUtil {
	
	public static void encrypt(String src, String dest) {
		try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(src));
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest))){
			int read = -1;
			while((read = in.read())!=-1) {
				out.write(read ^ 0xff);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
