package uk.org.wookey.vecsys.widgets;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.swing.JPanel;
import uk.org.wookey.vecsys.utils.Logger;
import uk.org.wookey.vecsys.utils.VecUtils;

public class DIPSwitch extends JPanel implements MouseListener {
	private static final long serialVersionUID = 1L;

	private static Logger _log = new Logger("SingleDIPSwitch");
	
	private ArrayList<SingleDIPSwitch> switches;
	private int val;
	private boolean leftToRight;
	private Preferences prefs;
	private String key;
	
	private static final Color[] colours = {Color.black, new Color(0xa5, 0x2a, 0x2a), Color.red, Color.orange, Color.yellow,
			Color.green, Color.blue, new Color(0xee, 0x82, 0xee), new Color(0x80, 0x80, 0x80), Color.white};
	
	
	public DIPSwitch(int numSwitches, Preferences prefs, String key) {
		this(numSwitches, prefs, key, true);
	}
	
	public DIPSwitch(int numSwitches, Preferences prefs, String key, boolean lToR) {
		super();
		
		leftToRight = lToR;
		this.prefs = prefs;
		this.key = key;
		
		switches = new ArrayList<SingleDIPSwitch>();
		for (int i=0; i<numSwitches; i++) {
			SingleDIPSwitch sw = new SingleDIPSwitch();
			
			if (leftToRight) {
				sw.setForeground(colours[(i+1)%10]);
			}
			else {
				sw.setForeground(colours[(numSwitches-i)%10]);
			}
			
			add(sw);
			switches.add(sw);
			sw.addMouseListener(this);
		}
		
		val = prefs.getInt(key,  0xff);
	}
	
	public int getValue() {
		return val;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		val = 0;
		
		int maxIndex = switches.size()-1;
		
		if (leftToRight) {
			for (int i=0; i<=maxIndex; i++) {
				val = val << 1;
				
				if (switches.get(maxIndex-i).isSelected()) {
					val = val | 1;
				}
			}
		}
		else {
			for (int i=0; i<=maxIndex; i++) {
				if (switches.get(maxIndex-i).isSelected()) {
					val = val | (1<<i);
				}
			}
		}
		
		//_log.logInfo(String.format("DIP Switches changed to %02x", val));
		prefs.putInt(key, val);
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
}
