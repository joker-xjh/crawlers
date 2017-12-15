package JVM.basicinfo.attribute;

import java.io.InputStream;

import JVM.ConstantPool;
import JVM.basicinfo.BasicInfo;
import JVM.basictype.U1;
import JVM.basictype.U2;
import JVM.basictype.U4;
import JVM.constantinfo.ConstantUtf8;

public class AttributeInfo extends BasicInfo{
	
	public static final String CODE = "Code";
	
	public int nameIndex;
	public int length;
	public short[] info;

	public AttributeInfo(ConstantPool constantPool, int nameIndex) {
		super(constantPool);
		this.nameIndex = nameIndex;
	}

	@Override
	public void read(InputStream in) {
		length = (int) U4.read(in);
		info = new short[length];
		for(int i=0; i<length; i++)
			info[i] = U1.read(in);
	}
	
	public static AttributeInfo getAttribute(ConstantPool constantPool, InputStream in) {
		int nameIndex = U2.read(in);
		String name = ((ConstantUtf8)constantPool.cpInfos[nameIndex]).value;
		switch(name) {
			case CODE :
				
		}
		
		return new AttributeInfo(constantPool, nameIndex);
	}
	
	
	
	

}
