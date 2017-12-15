package JVM.constantinfo;

import java.io.InputStream;

import JVM.ConstantInfo;
import JVM.basictype.U2;

public class ConstantMemberRef extends ConstantInfo {
	
	public int classIndex;
	public int nameAndTypeIndex;

	@Override
	public void read(InputStream in) {
		classIndex = U2.read(in);
		nameAndTypeIndex = U2.read(in);
	}

}
