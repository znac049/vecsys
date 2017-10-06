package Emulator;

import java.util.ArrayList;

import Utils.Logger;

public class Bus implements ByteAccess, WordAccess {
	private static Logger _log = new Logger("Bus");
	
	private int busWidth;
	private int addressMask;
	private boolean overlapsAllowed;
	
	private ArrayList<MappedRange> ranges;
	
	public Bus(int width) throws RangeException {
		if ((width <= 0) || (width > 29)) {
			throw new RangeException(String.format("Bus width of %d not supported", width));
		}
		
		busWidth = width;		
		addressMask = (1<<width)-1;
		overlapsAllowed = false;
		
		ranges = new ArrayList<MappedRange>();
	}
	
	public void setAddressMask(int mask) {
		addressMask = mask;
	}
	
	public int getAddressMask() {
		return addressMask;
	}
	
	public void setOverlapping(boolean canOverlap) {
		overlapsAllowed = canOverlap;
		
		/*
		 * If we're disabling overlaps, we'd better check that no existing ranges
		 * overlap and log a warning at the very least.
		 */
		if (!canOverlap) {
			
		}
	}
	
	private boolean overlaps(MappedRange r) {
		for (MappedRange range : ranges) {
			if (r.overlaps(range)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void attach(int start, int end, Device dev) throws RangeException {
		attach(new MappedRange(start, end, dev));
	}
	
	public void attach(MappedRange range) throws RangeException {
		if ((!overlapsAllowed) && (overlaps(range))) {
			throw new RangeException("Attempt to attach an overlapping range");
		}
		
		// Keep the arraylist sorted in order o addresses, lo to high
		int i;
		int prevInd = -1;
		int end = range.getEndAddress();
		
		for (i=0; i<ranges.size(); i++)  {
			if (end < ranges.get(i).getStartAddress()) {
				prevInd = i;
			}
		}
		
		if (prevInd != -1) {
			_log.logInfo(String.format("Inserting at %d", prevInd));
			ranges.add(prevInd, range);
		}
		else {
			_log.logInfo("Inserting at end");
			ranges.add(range);
		}
	}
	
	public void dump() {
		_log.logInfo("Memory Bus dump");
		
		for (MappedRange range : ranges) {
			_log.logInfo(String.format("  %04x-%04x", range.getStartAddress(), range.getEndAddress()));
		}
		
		_log.logInfo("End of Memory bus dump");
	}

	@Override
	public int getWord(int addr) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setWord(int addr, int val) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getByte(int addr) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setByte(int addr, int val) {
		// TODO Auto-generated method stub
		
	}
}
