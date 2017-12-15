package JVM.basictype;

import java.io.IOException;
import java.io.InputStream;

public class U2 {
	
	public static int read(InputStream in) {
		byte[] buf = new byte[2];
		try {
			in.read(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int val = 0;
		for(int i=0; i<buf.length; i++) {
			val <<= 8;
			val |= buf[i] & 0xff;
		}
		return val;
	}

}
