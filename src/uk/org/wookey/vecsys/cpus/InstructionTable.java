package uk.org.wookey.vecsys.cpus;

public class InstructionTable {
	public MinimalInstruction instructions[];
	
	public InstructionTable() {
		instructions = new MinimalInstruction[256];
		
		int i;
		for (i=0; i<256; i++) {
			instructions[i] = null;
		}
	}
	
	public void add(MinimalInstruction instruction) throws Exception {
		if (instructions[instruction.opcode] != null) {
			throw new Exception(String.format("Duplicate opcode %02x for %s instruction", instruction.opcode, instruction.name));
		}
		
		instructions[instruction.opcode] = instruction; 
	}

	public MinimalInstruction get(int ir) {
		return instructions[ir];
	}
}
