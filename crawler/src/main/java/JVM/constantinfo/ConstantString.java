package JVM.constantinfo;

import java.io.InputStream;

import JVM.ConstantInfo;
import JVM.basictype.U2;

public class ConstantString extends ConstantInfo{
	
	int nameIndex;

	@Override
	public void read(InputStream in) {
		nameIndex = U2.read(in);
	}

}
