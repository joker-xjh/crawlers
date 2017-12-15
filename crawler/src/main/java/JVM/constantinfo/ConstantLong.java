package JVM.constantinfo;

import java.io.InputStream;

import JVM.ConstantInfo;
import JVM.basictype.U4;

public class ConstantLong extends ConstantInfo{
	
	public long highValue;
	public long lowValue;

	@Override
	public void read(InputStream in) {
		highValue = U4.read(in);
		lowValue = U4.read(in);
	}

}
