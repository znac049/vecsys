package uk.org.wookey.vecsys.emulator;

public class DVG extends Device {
	private class VGHaltedDevice extends Device {
		public VGHaltedDevice() {
			super("VGHalted flag");
		}

		@Override
		public int getByte(int addr) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void setByte(int addr, int val) throws IllegalAccessException {
			// TODO Auto-generated method stub
			
		}
	}
	
	private class StartVGDevice extends Device {
		public StartVGDevice() {
			super("StartVG flag");
		}

		@Override
		public int getByte(int addr) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void setByte(int addr, int val) throws IllegalAccessException {
			// TODO Auto-generated method stub
			
		}
	}
	
	private class ResetVGDevice extends Device {
		public ResetVGDevice() {
			super("ResetVG flag");
		}

		@Override
		public int getByte(int addr) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void setByte(int addr, int val) throws IllegalAccessException {
			// TODO Auto-generated method stub
			
		}
	}
	
	private VGHaltedDevice haltedFlag;
	private StartVGDevice startVG;
	private ResetVGDevice resetVG;
	private DVGMemory vmem;
	
	public DVG() {
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
	public int getByte(int addr) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setByte(int addr, int val) throws IllegalAccessException {
		// TODO Auto-generated method stub
		
	}
}
