package uk.org.wookey.vecsys.emulator;

public abstract class Device {
	protected String name;
	
	public Device(String devName) {
		name = devName;
	}
	
	public void setName(String devName) {
		name = devName;
	}
	
	public String getName() {
		return name;
	}
	
	public abstract int getByte(int addr);
	public abstract void setByte(int addr, int val) throws IllegalAccessException;
}
