package uk.org.wookey.vecsys.emulator.devices;

import uk.org.wookey.vecsys.emulator.Device;

public class Earom extends Device {
	public static final int read = 0;
	public static final int latch = 1;
	public static final int control = 2;
	
	public Earom() {
		super("EAROM");
	}
	
	@Override
	public int getByte(int addr, int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setByte(int addr, int val, int id) throws IllegalAccessException {
		// TODO Auto-generated method stub
	}
}
