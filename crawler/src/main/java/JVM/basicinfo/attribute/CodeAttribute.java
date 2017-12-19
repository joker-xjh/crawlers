package JVM.basicinfo.attribute;

import java.io.InputStream;

import JVM.ConstantPool;
import JVM.basictype.U1;
import JVM.basictype.U2;
import JVM.basictype.U4;

public class CodeAttribute extends AttributeInfo{
	
	public int maxStack;
	public int maxLocals;
	public int codeLength;
	public short[] code;
	public int exceptionTableLength;
	public ExceptionTable[] exceptionTables;
	public int attributes_count;
	public AttributeInfo[] attributeInfos;

	public CodeAttribute(ConstantPool constantPool, int nameIndex) {
		super(constantPool, nameIndex);
	}
	
	@Override
	public void read(InputStream in) {
		length = (int) U4.read(in);
		maxStack = U2.read(in);
		maxLocals = U2.read(in);
		codeLength = (int) U4.read(in);
		code = new short[codeLength];
		for(int i=0; i<codeLength; i++)
			code[i] = U1.read(in);
		exceptionTableLength = U2.read(in);
		exceptionTables = new ExceptionTable[exceptionTableLength];
		for(int i=0; i<exceptionTableLength; i++) {
			exceptionTables[i] = new ExceptionTable();
			exceptionTables[i].read(in);
		}
		attributes_count = U2.read(in);
		attributeInfos = new AttributeInfo[attributes_count];
		for(int i=0; i<attributes_count; i++) {
			attributeInfos[i] = AttributeInfo.getAttribute(constantPool, in);
			attributeInfos[i].read(in);
		}
	}
		

}
