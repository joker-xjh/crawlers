package JVM.basictype;

import java.io.IOException;
import java.io.InputStream;

public class U4 {
	
	public static long read(InputStream in) {
		byte[] buf = new byte[4];
		try {
			in.read(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		long val = 0;
		for(int i=0; i<buf.length; i++) {
			val <<= 8;
			val |= buf[i] & 0xff;
		}
		return val;
	}

}
