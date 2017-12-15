package JVM.constantinfo;

import java.io.InputStream;

import JVM.ConstantInfo;
import JVM.basictype.U2;

public class ConstantMethodType extends ConstantInfo{
	
	int descType;

	@Override
	public void read(InputStream in) {
		descType = U2.read(in);
	}

}
