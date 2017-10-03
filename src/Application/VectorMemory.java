package Application;

import java.util.ArrayList;

public class VectorMemory {
	private final static int MEMORY_SIZE = 3*1024;
	
	private ArrayList<MemoryCell> cells;
	
	public VectorMemory() {
		cells = new ArrayList<MemoryCell>();
		
		for(int i=0; i<MEMORY_SIZE; i++) {
			cells.add(i, new MemoryCell());
		}
	}
	
	public int get(int addr) {
		if ((addr < 0) || (addr >= MEMORY_SIZE)) {
			return -1;
		}
		
		return cells.get(addr).get();
	}
	
	public void set(int addr, int val) {
		if ((addr >= 0) && (addr < MEMORY_SIZE)) {
			cells.get(addr).set(val);
		}
	}

	public void clear() {
		for (int i=0; i<MEMORY_SIZE; i++) {
			cells.get(i).clear();
		}
	}

	public int size() {
		return MEMORY_SIZE;
	}
}
