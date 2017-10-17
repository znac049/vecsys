package uk.org.wookey.vecsys.emulator.devices;

import uk.org.wookey.vecsys.emulator.Device;
import uk.org.wookey.vecsys.widgets.RoundButton;

public class SlamButton extends Device {
	public SlamButton() {
		super("Slam");
		
		RoundButton slam = new RoundButton("Slam");
		components.add(slam);
	}

	@Override
	public int getByte(int addr, int id) {
		RoundButton sw = (RoundButton) components.get(0);
		
		return sw.isSelected() ? 0 : 0x80;
	}

	@Override
	public void setByte(int addr, int val, int id) throws IllegalAccessException {
		// TODO Auto-generated method stub	
	}

}
