package uk.org.wookey.vecsys.emulator.devices;

import java.io.IOException;

import uk.org.wookey.vecsys.emulator.Device;

public class DVGMemory extends Device {
	private MemoryDevice vram;
	private MemoryDevice vrom;
	
	private final static int VRAM_SIZE_BYTES = 2048;
	private final static int VROM_SIZE_BYTES = 4096;
	private final static int VMEM_SIZE_BYTES = VRAM_SIZE_BYTES+VROM_SIZE_BYTES;
	private final static int VRAM_SIZE_WORDS = VRAM_SIZE_BYTES/2;
	private final static int VROM_SIZE_WORDS = VROM_SIZE_BYTES/2;
	private final static int VMEM_SIZE_WORDS = VMEM_SIZE_BYTES/2;
	
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
	public int getByte(int addr, int id) {
		if (addr < 2048) {
			return vram.getByte(addr, id);
		}
		
		return vrom.getByte(addr-2048, id);
	}
	
	@Override
	public void setByte(int addr, int val, int id) throws IllegalAccessException {
		if (addr < 2048) {
			vram.setByte(addr, val, id);
		}
		else {		
			vrom.setByte(addr-2048, val, id);
		}
	}
	
	public int get(int wideAddr) {
		if ((wideAddr < 0) || (wideAddr >= VMEM_SIZE_WORDS)) {
			return -1;
		}

		int byteAddr = wideAddr << 1;
		
		if (wideAddr < VRAM_SIZE_WORDS) {
			return (vram.getByte(byteAddr, 0) | vram.getByte(byteAddr+1, 0)<<8);
		}
		else {
			byteAddr = byteAddr - VRAM_SIZE_BYTES;
			
			return (vrom.getByte(byteAddr, 0) | vrom.getByte(byteAddr+1, 0)<<8);
		}
	}
	
	public int size() {
		return VMEM_SIZE_WORDS;
	}
}
