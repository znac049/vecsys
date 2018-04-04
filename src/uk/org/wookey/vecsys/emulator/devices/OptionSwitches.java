package uk.org.wookey.vecsys.emulator.devices;

import java.util.prefs.Preferences;

import uk.org.wookey.vecsys.emulator.Device;
import uk.org.wookey.vecsys.widgets.DIPSwitch;

public class OptionSwitches extends Device {	
	public OptionSwitches(String gameName) {
		super("Options DIPs");
		
		Preferences prefs = Preferences.userRoot().node(gameName);
		
		components.add(new DIPSwitch(8, prefs, "options1"));
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
