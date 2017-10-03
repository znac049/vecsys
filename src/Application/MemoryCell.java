package Application;

public class MemoryCell {
	private int wordVal;
	private int readCount;
	private int writeCount;
	
	public MemoryCell() {
		clear();		
	}
	
	public void clear() {
		wordVal = 0;
		
		resetCounts();
	}
	
	public void resetCounts() {
		wordVal = 0;
		
		readCount = writeCount = 0;
	}
	
	public void set(int val) {
		wordVal = val;
		writeCount++;
	}
	
	public int get() {
		readCount++;
		return wordVal;
	}
}
