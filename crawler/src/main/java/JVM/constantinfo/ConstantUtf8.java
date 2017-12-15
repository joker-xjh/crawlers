package JVM.constantinfo;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import JVM.ConstantInfo;
import JVM.basictype.U2;

public class ConstantUtf8 extends ConstantInfo{
	
	public String value;

	@Override
	public void read(InputStream in) {
		int length = U2.read(in);
		byte[] buf = new byte[length];
		try {
			value = new String(buf, "utf8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}
