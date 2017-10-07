package Emulator;

public class DVG extends Device {
	private class VGHaltedDevice extends Device {
		public VGHaltedDevice() {
			super("VGHalted flag");
		}
	}
	
	private class StartVGDevice extends Device {
		public StartVGDevice() {
			super("StartVG flag");
		}
	}
	
	private class ResetVGDevice extends Device {
		public ResetVGDevice() {
			super("ResetVG flag");
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
}
