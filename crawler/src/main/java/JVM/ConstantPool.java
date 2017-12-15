package JVM;

import java.io.InputStream;

import JVM.basictype.U1;
import JVM.constantinfo.ConstantClass;
import JVM.constantinfo.ConstantMemberRef;
import JVM.constantinfo.ConstantNameAndType;
import JVM.constantinfo.ConstantUtf8;

import static JVM.ConstantInfo.*;

public class ConstantPool {
	
	public int constant_pool_count;
	public ConstantInfo[] cpInfos;
	
	public ConstantPool(int count) {
		this.constant_pool_count = count;
		cpInfos = new ConstantInfo[count];
	}
	
	public void read(InputStream in) {
		for(int i=1; i<constant_pool_count; i++) {
			short tag = U1.read(in);
			ConstantInfo constantInfo = ConstantInfo.getConstantInfo(tag);
			constantInfo.tag = tag;
			cpInfos[i] = constantInfo;
			if(tag == ConstantInfo.CONSTANT_Double || tag == ConstantInfo.CONSTANT_Long)
				i++;
		}
	}
	
	public static void printConstanPoolInfo(ConstantPool constantPool) {
		if(constantPool == null)
			return;
		System.out.println("ConstantPool:"+constantPool.constant_pool_count);
		for(int i=1; i<constantPool.constant_pool_count; i++) {
			ConstantInfo constantInfo = constantPool.cpInfos[i];
			if(constantInfo instanceof ConstantMemberRef) {
				ConstantMemberRef constantMemberRef = (ConstantMemberRef)constantInfo;
				switch(constantMemberRef.tag) {
					case CONSTANT_Fieldref:
						System.out.println("#"+i+" fieldref:"+((ConstantUtf8)constantPool.cpInfos[constantMemberRef.nameAndTypeIndex]).value);
						continue;
					case CONSTANT_Methodref:
						System.out.println("#"+i+" methodref:"+((ConstantUtf8)constantPool.cpInfos[constantMemberRef.nameAndTypeIndex]).value);
						continue;
					default :
						continue;
				}
			}
			else if(constantInfo instanceof ConstantNameAndType) {
				ConstantNameAndType nameAndType = (ConstantNameAndType)constantInfo;
				System.out.println("#"+i+" NameAndType:"+((ConstantUtf8)constantPool.cpInfos[nameAndType.nameIndex]).value);
			}
			else if(constantInfo instanceof ConstantClass) {
				ConstantClass constantClass = (ConstantClass)constantInfo;
				System.out.println("#"+i+" class:"+((ConstantUtf8)constantPool.cpInfos[constantClass.nameIndex]).value);
			}
			else if(constantInfo instanceof ConstantUtf8) {
				ConstantUtf8 utf8 = (ConstantUtf8)constantInfo;
				System.out.println("#"+i+" UTF-8:"+utf8.value);
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	

}
