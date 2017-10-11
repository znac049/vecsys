package uk.org.wookey.vecsys.emulator;

import uk.org.wookey.vecsys.utils.Logger;

public class Pokey extends Device {
	private static Logger _log = new Logger("POKEY");
	
	public Pokey() {
		super("POKEY");
	}

	@Override
	public int getByte(int addr, int id) {
		// TODO Auto-generated method stub
		_log.logInfo("Read register " + addr);
		return 0;
	}

	@Override
	public void setByte(int addr, int val, int id) throws IllegalAccessException {
		// TODO Auto-generated method stub
		_log.logInfo(String.format("Write byte %02x to register %d", val, addr));
	}

}
