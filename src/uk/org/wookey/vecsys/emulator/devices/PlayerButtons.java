package uk.org.wookey.vecsys.emulator.devices;

import javax.swing.JPanel;

import uk.org.wookey.vecsys.emulator.Device;
import uk.org.wookey.vecsys.utils.Logger;
import uk.org.wookey.vecsys.widgets.LEDButton;

public class PlayerButtons extends Device {
	private static Logger _log = new Logger("PlayerButtons/LEDs");

	public static final int buttons = 0;
	public static final int leds = 1;
	
	private LEDButton p1;
	private LEDButton p2;
	
	public PlayerButtons() {
		super("Player Buttons/LEDs");

		JPanel buttonPanel = new JPanel();
		
		p1 = new LEDButton("Player1");
		buttonPanel.add(p1);
		
		p2 = new LEDButton("Player2");
		buttonPanel.add(p2);
		
		components.add(buttonPanel);
	}
	
	@Override
	public int getByte(int addr, int id) {
		if (id == buttons) {
			if (addr == 0) {
				return (p1.isSelected())?0x80:0;
			}
			else if (addr == 1) {
				return (p2.isSelected())?0x80:0;
			}
		}
		
		_log.logWarn(String.format("Reading from write only Player %d LED latch.", id+1));
		
		return 0;
	}

	@Override
	public void setByte(int addr, int val, int id) throws IllegalAccessException {
		if (id == leds) {
			if (addr == 0) {
				p1.setLED(val == 0);
			}
			else if (addr == 1) {
				p2.setLED(val == 0);
			}
		}
		else {
			_log.logWarn(String.format("Write to read-only player buttons +%d", addr));
		}
	}
}
