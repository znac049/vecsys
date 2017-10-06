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
	
	public void attach(MappedRange range) {
		
	}
	
	public void dump() {
		_log.logInfo("Memory Bus dump");
		
		for (MappedRange range : ranges) {
			
		}
	}

	@Override
	public int getWord(int addr) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setWord(int addr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getByte(int addr) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setByte(int addr, byte val) {
		// TODO Auto-generated method stub
		
	}
}
