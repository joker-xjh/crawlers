package JVM.constantinfo;

import java.io.InputStream;

import JVM.ConstantInfo;
import JVM.basictype.U2;

public class ConstantNameAndType extends ConstantInfo{
	
	public int nameIndex;
	public int descIndex;

	@Override
	public void read(InputStream in) {
		nameIndex = U2.read(in);
		descIndex = U2.read(in);
	}

}
