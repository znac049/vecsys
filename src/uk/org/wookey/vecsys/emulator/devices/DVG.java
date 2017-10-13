package uk.org.wookey.vecsys.emulator.devices;

import java.io.IOException;

import uk.org.wookey.vecsys.emulator.Device;
import uk.org.wookey.vecsys.utils.Logger;

public class DVG extends Device {
	@SuppressWarnings("unused")
	private static Logger _log = new Logger("DVG");
	
	public static final int vgHalted = 0;
	public static final int startVG = 1;
	public static final int resetVG = 2;
	public static final int vgMem = 3;
	
	private DVGMemory vmem;
	
	public DVG() throws IOException {
		super("DVG");
		
		vmem = new DVGMemory();
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
