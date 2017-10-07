package uk.org.wookey.vecsys.emulator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class MemoryDevice extends Device {
	private boolean readOnly;
	private int size;
	private ArrayList<MemoryCell> contents;
	
	public MemoryDevice(int memSize) {
		super("Memory");
		
		size = memSize;
		readOnly = false;
		contents = new ArrayList<MemoryCell>();
		for (int i=0; i<size; i++) {
			contents.add(new MemoryCell());
		}
	}
	
	public void setWriteable(boolean canWrite) {
		readOnly = !canWrite;
	}
	
	public boolean isWriteable() {
		return !readOnly;
	}
	
	public int loadFile(Path path, int addr) throws IOException {
		byte[] bytes = Files.readAllBytes(path);
		int i=0;
		
		try {
			for (i=0; i<bytes.length; i++) {
				setByte(addr+i, bytes[i]);
			}
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return i;
	}

	@Override
	public int getByte(int addr) {
		if ((addr < 0) || (addr >= size)) {
			throw new ArrayIndexOutOfBoundsException("MemoryDevice address out of bounds");
		}
		
		return contents.get(addr).get();
	}

	@Override
	public void setByte(int addr, int val) throws IllegalAccessException {
		if ((addr < 0) || (addr >= size)) {
			throw new ArrayIndexOutOfBoundsException("MemoryDevice address out of bounds");
		}
		
		if (readOnly) {
			throw new IllegalAccessException("Attempt to change read only memory");
		}
		
		contents.get(addr).set(val & 0xff);
	}
}
