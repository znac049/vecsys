package uk.org.wookey.vecsys.emulator;

import java.io.IOException;

import uk.org.wookey.vecsys.utils.Logger;

public class DVG extends Device {
	private static Logger _log = new Logger("DVG");
	
	private boolean vgHalted;
	
	private class VGHaltedDevice extends Device {
		private final Logger _log = new Logger("HaltVG");
		
		public VGHaltedDevice() {
			super("VGHalted flag");
		}

		@Override
		public int getByte(int addr, int id) {
			return (vgHalted) ? 0x80 : 0;
		}

		@Override
		public void setByte(int addr, int val, int id) throws IllegalAccessException {
			_log.logError("Attempt to read from write-only device");
		}
	}
	
	private class StartVGDevice extends Device {
		private final Logger _log = new Logger("StartVG");
		
		public StartVGDevice() {
			super("StartVG flag");
		}

		@Override
		public int getByte(int addr, int id) {
			_log.logError("Attempt to read from a write only device");
			return 0;
		}

		@Override
		public void setByte(int addr, int val, int id) throws IllegalAccessException {
			// TODO Auto-generated method stub
			
		}
	}
	
	private class ResetVGDevice extends Device {
		private final Logger _log = new Logger("VGReset");
		
		public ResetVGDevice() {
			super("ResetVG flag");
		}

		@Override
		public int getByte(int addr, int id) {
			_log.logError("Attempt to read from a write only device");
			return 0;
		}

		@Override
		public void setByte(int addr, int val, int id) throws IllegalAccessException {
			// TODO Auto-generated method stub			
		}
	}
	
	private VGHaltedDevice haltedFlag;
	private StartVGDevice startVG;
	private ResetVGDevice resetVG;
	private DVGMemory vmem;
	
	public DVG() throws IOException {
		super("DVG");
		
		haltedFlag = new VGHaltedDevice();
		startVG = new StartVGDevice();
		resetVG = new ResetVGDevice();
		
		vmem = new DVGMemory();
	}
	
	public Device getHaltedFlagDevice() {
		return haltedFlag;
	}
	
	public Device getStartVGDevice() {
		return startVG;
	}

	public Device getResetVGDevice() {
		return resetVG;
	}
	
	public Device getVectorMemory() {
		return vmem;
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
