package Emulator;

public interface WordAccess {
	public int getWord(int addr);
	public void setWord(int addr, int val) throws IllegalAccessException;
}
