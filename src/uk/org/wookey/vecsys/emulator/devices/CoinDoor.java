package uk.org.wookey.vecsys.emulator.devices;

import uk.org.wookey.vecsys.emulator.CoinPanel;
import uk.org.wookey.vecsys.emulator.Device;

public class CoinDoor extends Device {
	private CoinPanel panel;
	
	public CoinDoor() {
		super("Coindoor");
	
		panel = new CoinPanel();
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

	public CoinPanel getPanel() {
		return panel;
	}
}
