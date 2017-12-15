package JVM.constantinfo;

import java.io.InputStream;

import JVM.ConstantInfo;
import JVM.basictype.U2;

public class ConstantInvokeDynamic extends ConstantInfo{
	
	public int bootstrapMethodAttrIndex;
	public int nameAndTypeIndex;

	@Override
	public void read(InputStream in) {
		bootstrapMethodAttrIndex = U2.read(in);
		nameAndTypeIndex = U2.read(in);
	}

}
