package uk.org.wookey.vecsys.emulator.devices;

import java.util.prefs.Preferences;

import uk.org.wookey.vecsys.emulator.Device;
import uk.org.wookey.vecsys.widgets.Counter;

public class CoinCounter extends Device {
	public static final int leftCounter = 0;
	public static final int centreCounter = 1;
	public static final int rightCounter = 2;
	
	public CoinCounter(String gameName) {
		super("Coin Counters");
		
		Preferences prefs = Preferences.userRoot().node(gameName);
		
		Counter left = new Counter("leftCounter", prefs, 6);
		Counter centre = new Counter("centerCounter", prefs, 6);
		Counter right = new Counter("rightCounter", prefs, 6);
		
		components.add(left);
		components.add(centre);
		components.add(right);
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
}
