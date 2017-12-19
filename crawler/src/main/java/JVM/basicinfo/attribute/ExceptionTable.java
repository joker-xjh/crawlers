package JVM.basicinfo.attribute;

import java.io.InputStream;

import JVM.basictype.U2;

public class ExceptionTable {
	
	public int startPC;
	public int endPC;
	public int handlerPC;
	public int catchType;
	
	public void read(InputStream in) {
		startPC = U2.read(in);
		endPC = U2.read(in);
		handlerPC = U2.read(in);
		catchType = U2.read(in);
	}

}
