package JVM.basicinfo;

import java.io.InputStream;

import JVM.ConstantPool;

public abstract class BasicInfo {
	
	protected ConstantPool constantPool;
	
	public BasicInfo(ConstantPool constantPool) {
		this.constantPool = constantPool;
	}
	
	public abstract void read(InputStream in);

}
