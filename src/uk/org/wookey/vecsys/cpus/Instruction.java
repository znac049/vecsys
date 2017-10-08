package uk.org.wookey.vecsys.cpus;

public class Instruction {
	public static int opcode;
	public String name;
	public int cycles;
	public int size;
	private int mode;
	
	public Instruction(int opcode, String name, int cycles, int size, int mode) {
		this.opcode = opcode;
		this.name = name;
		this.cycles = cycles;
		this.size = size;
		this.mode = mode;
	}
}
