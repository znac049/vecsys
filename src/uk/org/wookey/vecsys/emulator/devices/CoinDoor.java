package uk.org.wookey.vecsys.emulator.devices;

import uk.org.wookey.vecsys.emulator.Device;
import uk.org.wookey.vecsys.utils.Logger;
import uk.org.wookey.vecsys.widgets.CoinPanel;

public class CoinDoor extends Device {
	private static Logger _log = new Logger("AD-Game");

	private CoinPanel panel;
	
	public CoinDoor() {
		super("Coindoor");
	
		panel = new CoinPanel();
	}
	
	@Override
	public int getByte(int addr, int id) {
		_log.logInfo("Checking for coins");
		return panel.isActive(addr)?0x80:0;
	}

	@Override
	public void setByte(int addr, int val, int id) throws IllegalAccessException {
		// TODO Auto-generated method stub		
	}

	public CoinPanel getPanel() {
		return panel;
	}
}
