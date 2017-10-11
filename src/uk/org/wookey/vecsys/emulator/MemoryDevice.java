package uk.org.wookey.vecsys.emulator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import uk.org.wookey.vecsys.utils.Logger;

public class MemoryDevice extends Device {
	private static Logger _log = new Logger("MemoryDevice");
	
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
	
	public int loadFile(String name, int addr) throws IOException {
    	Path path = Paths.get(name);

    	return loadFile(path, addr);
	}
	
	public int loadFile(Path path, int addr) throws IOException {
		byte[] bytes = Files.readAllBytes(path);
		int i=0;
		
		try {
			for (i=0; i<bytes.length; i++) {
				setByte(addr+i, bytes[i], -1);
			}
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		_log.logInfo(String.format("Read %d bytes from %s", i, path.toString()));
		
		return i;
	}

	@Override
	public int getByte(int addr, int id) {
		if ((addr < 0) || (addr >= size)) {
			throw new ArrayIndexOutOfBoundsException("MemoryDevice address out of bounds");
		}
		
		return contents.get(addr).get();
	}

	@Override
	public void setByte(int addr, int val, int id) throws IllegalAccessException {
		if ((addr < 0) || (addr >= size)) {
			throw new ArrayIndexOutOfBoundsException("MemoryDevice address out of bounds");
		}
		
		if (readOnly) {
			throw new IllegalAccessException("Attempt to change read only memory");
		}
		
		contents.get(addr).set(val & 0xff);
	}
}
