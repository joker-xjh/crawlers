package JVM.basicinfo;

import java.io.InputStream;

import JVM.ConstantPool;
import JVM.basicinfo.attribute.AttributeInfo;
import JVM.basictype.U2;

public class MemberInfo extends BasicInfo{
	
	public int accessFlag;
	public int nameIndex;
	public int descriptorIndex;
	public int attributeCount;
	public AttributeInfo[] attributeInfos;

	public MemberInfo(ConstantPool constantPool) {
		super(constantPool);
	}

	@Override
	public void read(InputStream in) {
		accessFlag = U2.read(in);
		nameIndex = U2.read(in);
		descriptorIndex = U2.read(in);
		attributeCount = U2.read(in);
		attributeInfos = new AttributeInfo[attributeCount];
		for(int i=0; i<attributeCount; i++) {
			attributeInfos[i] = AttributeInfo.getAttribute(constantPool, in);
			attributeInfos[i].read(in);
		}
	}

}
