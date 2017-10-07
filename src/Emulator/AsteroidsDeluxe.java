package Emulator;

import Utils.Logger;

public class AsteroidsDeluxe extends Emulator {
	private static Logger _log = new Logger("AD-Game");

	private Bus bus;
	
	public AsteroidsDeluxe() throws RangeException {
		bus = new Bus(16);
		bus.setAddressMask(0x7fff);
		
		_log.logInfo("Populating bus");
		
		MemoryDevice gameRom = new MemoryDevice(8192);
		gameRom.setWriteable(false);
		gameRom.setName("Game ROM");
		bus.attach(0x6000, 0x7fff, gameRom);
		
		MemoryDevice gameRam = new MemoryDevice(1024);
		gameRam.setName("Game RAM");
		bus.attach(0, 1024, gameRam);
		
		DVG dvg = new DVG();
		bus.attach(0x2001, dvg.getHaltedFlagDevice());
		bus.attach(0x3000, dvg.getStartVGDevice());
		bus.attach(0x3800, dvg.getResetVGDevice());
		bus.attach(0x4000, 0x57ff, dvg.getVectorMemory());
		
		bus.dump();
	}
}
