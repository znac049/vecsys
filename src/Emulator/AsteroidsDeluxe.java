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
		
		MemoryDevice vectorRom = new MemoryDevice(4096);
		vectorRom.setWriteable(false);
		vectorRom.setName("Vector ROM");
		bus.attach(0x4800, 0x57ff, vectorRom);
		
		MemoryDevice gameRam = new MemoryDevice(1024);
		gameRam.setName("Game RAM");
		bus.attach(0, 1024, gameRam);
		
		MemoryDevice vectorRam = new MemoryDevice(2048);
		gameRam.setName("Game ROM");
		bus.attach(0x4000, 0x47ff, vectorRam);
		
		bus.dump();
	}
}
