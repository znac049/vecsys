package uk.org.wookey.vecsys.emulator;

import java.io.IOException;

public class DVGMemory extends Device {
	private MemoryDevice vram;
	private MemoryDevice vrom;
	
	public DVGMemory() throws IOException {
		super("DVGMeory");
		
		vram = new MemoryDevice(2048);
		vram.setName("Vector RAM");
		
		vrom = new MemoryDevice(4096);
		vrom.setName("Vector ROM");
		vrom.loadFile("Code/ad-v3-vrom.bin", 0);
		vrom.setWriteable(false);
	}
	
	@Override
	public int getByte(int addr) {
		if (addr < 2048) {
			return vram.getByte(addr);
		}
		
		return vrom.getByte(addr-2048);
	}
	
	@Override
	public void setByte(int addr, int val) throws IllegalAccessException {
		if (addr < 2048) {
			vram.setByte(addr, val);
		}
		else {		
			vrom.setByte(addr-2048, val);
		}
	}
}
