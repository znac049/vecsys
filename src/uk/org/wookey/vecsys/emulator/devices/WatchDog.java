package uk.org.wookey.vecsys.emulator.devices;

import uk.org.wookey.vecsys.emulator.Device;

public class WatchDog extends Device{
	public WatchDog() {
		super("WatchDog");
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
