package uk.org.wookey.vecsys.emulator;

public class MemoryCell {
	private int val;
	
	public void set(int newVal) {
		val = newVal;
	}
	
	public int get() {
		return val;
	}
}
