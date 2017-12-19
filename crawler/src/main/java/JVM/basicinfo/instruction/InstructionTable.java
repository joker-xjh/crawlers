package JVM.basicinfo.instruction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InstructionTable {
	
	private static Map<Integer, String> instructions;
	
	public static String getInstruction(int bytecode) {
		if(instructions == null)
			buildMap();
		return instructions.get(bytecode);
	}
	
	private static void buildMap() {
		instructions = new HashMap<>();
		File file = new File("src/main/java/JVM/ins.txt");
		try (FileReader fileReader = new FileReader(file);
				BufferedReader reader = new BufferedReader(fileReader)){
			int i = 0;
			String line;
			Integer ins =  null;
			while((line = reader.readLine()) != null) {
				if((i & 1) == 0)
					ins = Integer.parseInt(line.substring(2, 4), 16);
				else
					instructions.put(ins, line);
				i++;
			}
		} catch (IOException e) {
			
		}
	}

}
