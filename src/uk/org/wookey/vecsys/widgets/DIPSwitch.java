package uk.org.wookey.vecsys.widgets;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JPanel;

public class DIPSwitch extends JPanel {
	private ArrayList<SingleDIPSwitch> switches;
	
	private static final Color[] colours = {Color.black, new Color(0xa5, 0x2a, 0x2a), Color.red, Color.orange, Color.yellow,
			Color.green, Color.blue, new Color(0xee, 0x82, 0xee), new Color(0x20, 0x20, 0x20), Color.white};
	
	public DIPSwitch(int numSwitches) {
		super();
	
		switches = new ArrayList<SingleDIPSwitch>();
		for (int i=0; i<numSwitches; i++) {
			SingleDIPSwitch sw = new SingleDIPSwitch();
			
			sw.setForeground(colours[(numSwitches-i)%10]);
			add(sw);
		}
	}
}
