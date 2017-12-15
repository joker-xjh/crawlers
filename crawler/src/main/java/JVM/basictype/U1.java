package JVM.basictype;

import java.io.IOException;
import java.io.InputStream;

public class U1 {
	
	public static short read(InputStream in) {
		byte[] buf = new byte[1];
		try {
			in.read(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		short val = (short) (buf[0] & 0xff);
		return val;
	}

}
