package JVM;

import JVM.basicinfo.MemberInfo;

public class ClassFile {
	public long magic;
	public int minorVersion;
	public int majorVersion;
	public int constantPoolLength;
	public ConstantPool constantPool;
	public int accessFlag;
	public String className;
	public String superClassName;
	public int interfaceCount;
	public String[] interfaces;
	public int fieldCount;
	public MemberInfo[] fileds;
	public int methodCount;
	public MemberInfo[] methods;
}
