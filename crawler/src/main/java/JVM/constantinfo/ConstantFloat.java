package JVM.constantinfo;

import java.io.InputStream;

import JVM.ConstantInfo;
import JVM.basictype.U4;

public class ConstantFloat extends ConstantInfo{
	
	public long value;

	@Override
	public void read(InputStream in) {
		value = U4.read(in);
	}

}
