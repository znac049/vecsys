package uk.org.wookey.vecsys.cpus;

public class MinimalInstruction {
	public int opcode;
	public String name;
	public int cycles;
	public int size;
	
	public MinimalInstruction(int opcode, String name, int size, int cycles) {
		this.opcode = opcode;
		this.name = name;
		this.cycles = cycles;
		this.size = size;
	}
	
	public String toString() {
		return name;
	}
}
