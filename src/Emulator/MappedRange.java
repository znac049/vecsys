package Emulator;

public class MappedRange {
	private int startAddress;
	private int endAddress;
	private Device device;
	
	public MappedRange(int start, int end, Device dev) {
		startAddress = start;
		endAddress = end;
		device = dev;
	}
	
	public boolean inRange(int addr) {
		return ((addr >= startAddress) && (addr <= endAddress));
	}
	
	public boolean overlaps(MappedRange target) {
		return inRange(target.startAddress) || inRange(target.endAddress);
	}
}