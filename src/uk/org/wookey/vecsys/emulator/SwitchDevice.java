package uk.org.wookey.vecsys.emulator;

import javax.swing.JComponent;
import javax.swing.JToggleButton;

import uk.org.wookey.vecsys.utils.Logger;

public class SwitchDevice extends Device {
	private static Logger _log = new Logger("SwitchDevice");
	
	private int onVal;
	private int offVal;
	private JToggleButton button;
	private String name;
	
	public SwitchDevice(String devName, int onVal, int offVal) {
		super(devName + " switch");
		
		button = new JToggleButton(devName);
		
		this.onVal = onVal;
		this.offVal = offVal;
		
		name = devName;
	}

	@Override
	public int getByte(int addr) {
		int val = (button.isSelected()) ? onVal : offVal;
		
		_log.logInfo(String.format("%s switch -> %d",  name, val));
		
		return val;
	}

	@Override
	public void setByte(int addr, int val) throws IllegalAccessException {
		_log.logWarn(String.format("Write to read-only device %s switch",  name));
	}
	
	public JComponent getComponent() {
		return button;
	}
}
