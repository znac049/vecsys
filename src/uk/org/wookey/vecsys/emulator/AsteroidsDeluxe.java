package uk.org.wookey.vecsys.emulator;

import java.io.IOException;

import uk.org.wookey.vecsys.cpus.Cpu;
import uk.org.wookey.vecsys.cpus.cpu6502.Cpu6502;
import uk.org.wookey.vecsys.cpus.cpu6x09.Cpu6x09;
import uk.org.wookey.vecsys.utils.Logger;

public class AsteroidsDeluxe extends Emulator {
	private static Logger _log = new Logger("AD-Game");

	public AsteroidsDeluxe() throws RangeException, IOException {
		bus = new Bus(16);
		bus.setAddressMask(0x7fff);
		bus.setLittleEndian();
		
		_log.logInfo("Populating bus");
		
		MemoryDevice gameRom = new MemoryDevice(8192);
		gameRom.loadFile("Code/ad-v3.bin", 0);
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
		
		cpu = new Cpu6502();
		cpu.setBus(bus);
		cpu.reset();
		cpu.step();
		
		if (cpu.isBigEndian()) {
			bus.setBigEndian();
		}
		else {
			bus.setLittleEndian();
		}

		bus.dump();
	}
}
