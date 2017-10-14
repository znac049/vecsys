package uk.org.wookey.vecsys.emulator.devices;

import uk.org.wookey.vecsys.emulator.Device;
import uk.org.wookey.vecsys.widgets.DIPSwitch;

public class OptionSwitches extends Device {	
	public OptionSwitches() {
		super("Options DIPs");
		
		components.add(new DIPSwitch(8));
	}
	
	@Override
	public int getByte(int addr, int id) {
		DIPSwitch dip = (DIPSwitch) getWidget();
		int shift = (3 - (addr & 0x03)) * 2;
		
		return (dip.getValue() >> shift) & 0x03;
	}

	@Override
	public void setByte(int addr, int val, int id) throws IllegalAccessException {
		// TODO Auto-generated method stub	
	}
}
