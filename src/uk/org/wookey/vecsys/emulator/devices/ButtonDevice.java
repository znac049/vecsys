package uk.org.wookey.vecsys.emulator.devices;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import uk.org.wookey.vecsys.emulator.Device;
import uk.org.wookey.vecsys.emulator.RoundButton;
import uk.org.wookey.vecsys.utils.Logger;

public class ButtonDevice extends Device {
	private static Logger _log = new Logger("ButtonDevice");
	
	private int onVal;
	private int offVal;
	private RoundButton button;
	private String name;
	private int boundKey;

	public ButtonDevice(String devName, int onVal, int offVal, int key) {
		super(devName + " button");
		
		button = new RoundButton(devName);
		
		this.onVal = onVal;
		this.offVal = offVal;
		
		name = devName;
		
		boundKey = key;
	}

	@Override
	public int getByte(int addr, int id) {
		int val = (button.isSelected()) ? onVal : offVal;
		
		_log.logInfo(String.format("%s button -> %d",  name, val));
		
		return val;
	}

	@Override
	public void setByte(int addr, int val, int id) throws IllegalAccessException {
		_log.logWarn(String.format("Write to read-only device %s button",  name));
	}
	
	public JComponent getComponent() {
		return button;
	}
	
	public void bindKey() {
		JRootPane root = ((JFrame) SwingUtilities.getRoot(button)).getRootPane();
		InputMap inputMap = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		inputMap.put(KeyStroke.getKeyStroke(boundKey, 0), "clickButton");
		
		root.getActionMap().put("clickButton", new AbstractAction() {
			public void actionPerformed(ActionEvent ae) {
				button.doClick();
				System.out.println("button clicked");
	        }
	    });
	}
}
