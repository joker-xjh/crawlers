package JVM;

import java.io.InputStream;

import JVM.constantinfo.ConstantClass;
import JVM.constantinfo.ConstantDouble;
import JVM.constantinfo.ConstantFloat;
import JVM.constantinfo.ConstantInteger;
import JVM.constantinfo.ConstantInvokeDynamic;
import JVM.constantinfo.ConstantLong;
import JVM.constantinfo.ConstantMemberRef;
import JVM.constantinfo.ConstantMethodHandle;
import JVM.constantinfo.ConstantMethodType;
import JVM.constantinfo.ConstantNameAndType;
import JVM.constantinfo.ConstantString;
import JVM.constantinfo.ConstantUtf8;

public abstract class ConstantInfo {
	
    public static final short CONSTANT_Class = 7;
    public static final short CONSTANT_Fieldref = 9;
    public static final short CONSTANT_Methodref = 10;
    public static final short CONSTANT_InterfaceMethodref = 11;
    public static final short CONSTANT_String = 8;
    public static final short CONSTANT_Integer = 3;
    public static final short CONSTANT_Float = 4;
    public static final short CONSTANT_Long = 5;
    public static final short CONSTANT_Double = 6;
    public static final short CONSTANT_NameAndType = 12;
    public static final short CONSTANT_Utf8 = 1;
    public static final short CONSTANT_MethodHandle = 15;
    public static final short CONSTANT_MethodType = 16;
    public static final short CONSTANT_InvokeDynamic = 18;
    
	public short tag;
	
	
	public abstract void read(InputStream in);
	
	
	public static ConstantInfo getConstantInfo(short tag) {
		switch(tag) {
			case CONSTANT_Class :
				return new ConstantClass();
			case CONSTANT_Fieldref :
			case CONSTANT_Methodref:
			case CONSTANT_InterfaceMethodref:
				return new ConstantMemberRef();
			case CONSTANT_String :
				return new ConstantString();
			case CONSTANT_Integer:
				return new ConstantInteger();
			case CONSTANT_Float:
				return new ConstantFloat();
			case CONSTANT_Long:
				return new ConstantLong();
			case CONSTANT_Double:
				return new ConstantDouble();
			case CONSTANT_NameAndType:
				return new ConstantNameAndType();
			case CONSTANT_Utf8:
				return new ConstantUtf8();
			case CONSTANT_MethodHandle:
				return new ConstantMethodHandle();
			case CONSTANT_MethodType:
				return new ConstantMethodType();
			case CONSTANT_InvokeDynamic:
				return new ConstantInvokeDynamic();
		}
		
		return null;
	}
	
	
	
	
	
	
	
	
	

}
