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
		switch (id) {
			case vgHalted:
				_log.logInfo("Is the VG halted?");
				break;
				
			case startVG:
				_log.logError("Trying to read from the WO startVG address");
				break;
				
			case resetVG:
				_log.logError("Trying to read from the WO resetVG address");
				break;
				
			case vgMem:
				_log.logInfo(String.format("Read VRAM at address %04x", addr));
				return vmem.getByte(addr, 0);
		}
		
		return 0;
	}

	@Override
	public void setByte(int addr, int val, int id) throws IllegalAccessException {
		switch (id) {
			case vgHalted:
				_log.logError("Trying to write to the RO VGHalted address");
				break;
			
			case startVG:
				_log.logInfo("Start the VG");
				break;
			
			case resetVG:
				_log.logInfo("Reset the VG");
				break;
			
			case vgMem:
				_log.logInfo(String.format("VRAM write %02x to %04x", val, addr));
				vmem.setByte(addr, val, 0);
				break;
		}
	}
}
