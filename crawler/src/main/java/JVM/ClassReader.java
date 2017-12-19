package JVM;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import JVM.basicinfo.MemberInfo;
import JVM.basicinfo.attribute.CodeAttribute;
import JVM.basicinfo.instruction.InstructionTable;
import JVM.basictype.U2;
import JVM.basictype.U4;
import JVM.constantinfo.ConstantClass;
import JVM.constantinfo.ConstantUtf8;

public class ClassReader {
	
	public static void readClass(String path) {
		File file = new File(path);
		try (FileInputStream fileInputStream = new FileInputStream(file);
			 BufferedInputStream in = new BufferedInputStream(fileInputStream)){
			
			ClassFile classFile = new ClassFile();
			classFile.magic = U4.read(in);
			classFile.minorVersion = U2.read(in);
			classFile.majorVersion = U2.read(in);
			classFile.constantPoolLength = U2.read(in);
			classFile.constantPool = new ConstantPool(classFile.constantPoolLength);
			classFile.constantPool.read(in);
			
			ConstantPool.printConstanPoolInfo(classFile.constantPool);
			
			classFile.accessFlag = U2.read(in);
			int classIndex = U2.read(in);
			ConstantClass constantClass = (ConstantClass)classFile.constantPool.cpInfos[classIndex];
			ConstantUtf8 className = (ConstantUtf8)classFile.constantPool.cpInfos[constantClass.nameIndex];
			classFile.className = className.value;
			System.out.println("classname: "+classFile.className);
			
			int superIndex = U2.read(in);
			ConstantClass superClass = (ConstantClass)classFile.constantPool.cpInfos[superIndex];
			ConstantUtf8 superClassName = (ConstantUtf8)classFile.constantPool.cpInfos[superClass.nameIndex];
			classFile.superClassName = superClassName.value;
			System.out.println("superclassname :"+classFile.superClassName);
			
			classFile.interfaceCount = U2.read(in);
			classFile.interfaces = new String[classFile.interfaceCount];
			for(int i=0; i<classFile.interfaceCount; i++) {
				int interfaceIndex = U2.read(in);
				ConstantClass interfaceClass = (ConstantClass)classFile.constantPool.cpInfos[interfaceIndex];
				ConstantUtf8 interfaceClassName = (ConstantUtf8)classFile.constantPool.cpInfos[interfaceClass.nameIndex];
				classFile.interfaces[i] = interfaceClassName.value;
				System.out.println("interface: "+classFile.interfaces[i]);
			}
			
			classFile.fieldCount = U2.read(in);
			classFile.fileds = new MemberInfo[classFile.fieldCount];
			for(int i=0; i<classFile.fieldCount; i++) {
				classFile.fileds[i] = new MemberInfo(classFile.constantPool);
				classFile.fileds[i].read(in);
				System.out.print("field: "+((ConstantUtf8)classFile.constantPool.cpInfos[classFile.fileds[i].nameIndex]).value+", ");
				System.out.println("desc: "+((ConstantUtf8)classFile.constantPool.cpInfos[classFile.fileds[i].descriptorIndex]).value);
			}
			
			classFile.methodCount = U2.read(in);
			classFile.methods = new MemberInfo[classFile.methodCount];
			for(int i=0; i<classFile.methodCount; i++) {
				classFile.methods[i] = new MemberInfo(classFile.constantPool);
				classFile.methods[i].read(in);
				System.out.print("method: "+((ConstantUtf8)classFile.constantPool.cpInfos[classFile.methods[i].nameIndex]).value+"(),");
				System.out.print("desc: "+((ConstantUtf8)classFile.constantPool.cpInfos[classFile.methods[i].descriptorIndex]).value);
				for(int j=0; j<classFile.methods[i].attributeCount; j++) {
					if(classFile.methods[i].attributeInfos[j] instanceof CodeAttribute) {
						CodeAttribute codeAttribute = (CodeAttribute)classFile.methods[i].attributeInfos[j];
						for(int k=0; k<codeAttribute.codeLength; i++) {
							short code = codeAttribute.code[k];
							System.out.println(InstructionTable.getInstruction(code));
						}
					}
				}
			}
			
			
		} catch (IOException e) {
			
		}
	}

}
