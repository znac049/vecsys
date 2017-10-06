package Emulator;

public class MemoryCell {
	private int val;
	
	public void set(int newVal) {
		val = newVal;
	}
	
	public int get() {
		return val;
	}
}
