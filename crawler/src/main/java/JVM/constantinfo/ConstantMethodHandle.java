package JVM.constantinfo;

import java.io.InputStream;

import JVM.ConstantInfo;
import JVM.basictype.U1;
import JVM.basictype.U2;

public class ConstantMethodHandle extends ConstantInfo {
	
	public short referenceKind;
	public int referenceIndex;

	@Override
	public void read(InputStream in) {
		referenceKind = U1.read(in);
		referenceIndex = U2.read(in);
	}

}
