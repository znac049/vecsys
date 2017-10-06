package Emulator;

public interface ByteAccess {
	public int getByte(int addr);
	public void setByte(int addr, byte val);
}
