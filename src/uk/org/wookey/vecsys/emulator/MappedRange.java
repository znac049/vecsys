package uk.org.wookey.vecsys.emulator;

public class MappedRange {
	private int startAddress;
	private int endAddress;
	private Device device;
	private int id;
	
	public MappedRange(int start, int end, Device dev) {
		this(start, end, dev, 0);
	}
	
	public MappedRange(int start, int end, Device dev, int newId) {
		startAddress = start;
		endAddress = end;
		device = dev;
		id = newId;
	}
	
	public int getStartAddress() {
		return startAddress;
	}
	
	public int getEndAddress() {
		return endAddress;
	}
	
	public boolean inRange(int addr) {
		return ((addr >= startAddress) && (addr <= endAddress));
	}
	
	public boolean overlaps(MappedRange target) {
		return inRange(target.startAddress) || inRange(target.endAddress);
	}

	public Device getDevice() {
		return device;
	}
	
	public int getId() {
		return id;
	}
}
