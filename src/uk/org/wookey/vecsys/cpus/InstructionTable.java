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
}
