package uk.org.wookey.vecsys.cpus.cpu6502;

import uk.org.wookey.vecsys.cpus.cpu6502.Cpu6502.Mode;

public class Instruction {
	public static int opcode;
	public String name;
	public int cycles;
	public int size;
	public Mode mode;
	
	public Instruction(int opcode, String name, int size, Mode imp, int cycles) {
		this.opcode = opcode;
		this.name = name;
		this.cycles = cycles;
		this.size = size;
		this.mode = imp;
	}
	
	public String toString() {
		return name;
	}
}
