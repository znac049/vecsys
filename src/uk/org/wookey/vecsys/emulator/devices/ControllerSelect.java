package uk.org.wookey.vecsys.emulator.devices;

import uk.org.wookey.vecsys.emulator.Device;
import uk.org.wookey.vecsys.utils.Logger;

public class ControllerSelect extends Device {
	private static Logger _log = new Logger("Controller Select");

	public ControllerSelect() {
		super("Controller (p1/2) select");
	}
	
	@Override
	public int getByte(int addr, int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setByte(int addr, int val, int id) throws IllegalAccessException {
		_log.logInfo(String.format("Write %d to Controller Select", val));	
	}
}
