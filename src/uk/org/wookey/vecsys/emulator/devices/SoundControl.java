package uk.org.wookey.vecsys.emulator.devices;

import uk.org.wookey.vecsys.emulator.Device;

public class SoundControl extends Device {
	public static final int explosion = 0;
	public static final int thrust = 1;
	
	public SoundControl() {
		super("Sound Control");
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
