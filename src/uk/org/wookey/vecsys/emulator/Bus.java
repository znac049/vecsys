package uk.org.wookey.vecsys.emulator;

import java.util.ArrayList;

import uk.org.wookey.vecsys.utils.Logger;

public class Bus {
	private static Logger _log = new Logger("Bus");
	
	private int busWidth;
	private int addressMask;
	private boolean overlapsAllowed;
	private int maxAddress;
	private int emptyValue;
	private boolean bigEndian;
	
	private ArrayList<MappedRange> ranges;
	
	public Bus(int width) throws RangeException {
		if ((width <= 0) || (width > 24)) {
			throw new RangeException(String.format("Bus width of %d not supported", width));
		}
		
		busWidth = width;
		maxAddress = (1<<busWidth)-1;
		addressMask = (1<<busWidth)-1;
		overlapsAllowed = false;
		emptyValue = 0xff;
		bigEndian = true;
		
		ranges = new ArrayList<MappedRange>();
	}
	
	public void setAddressMask(int mask) {
		addressMask = mask;
	}
	
	public int getAddressMask() {
		return addressMask;
	}
	
	private int mask(int addr) {
		return addr & addressMask;
	}
	
	private boolean validAddress(int addr) {
		return ((addr >= 0) || (addr <= maxAddress));
	}
	
	public void setBigEndian() {
		bigEndian = true;
	}
	
	public void setLittleEndian() {
		bigEndian = false;
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
	
	public void attach(int singleAddr, Device dev) throws RangeException {
		attach(new MappedRange(singleAddr, singleAddr, dev));
	}
	
	public void attach(int singleAddr, Device dev, int id) throws RangeException {
		attach(new MappedRange(singleAddr, singleAddr, dev, id));
	}
	
	public void attach(int start, int end, Device dev) throws RangeException {
		attach(new MappedRange(start, end, dev, -1));
	}
	
	public void attach(int start, int end, Device dev, int id) throws RangeException {
		attach(new MappedRange(start, end, dev, id));
	}
	
	public void attach(MappedRange range) throws RangeException {
		if ((!overlapsAllowed) && (overlaps(range))) {
			throw new RangeException("Attempt to attach an overlapping range");
		}
		
		// Keep the arraylist sorted in order o addresses, lo to high
		int i;
		int ind = -1;
		int end = range.getEndAddress();
		
		for (i=0; i<ranges.size(); i++)  {
			if ((ind == -1) && (end < ranges.get(i).getStartAddress())) {
				ind = i;
			}
		}
		
		if (ind != -1) {
			//_log.logInfo(String.format("Inserting %04x-%04x at %d", range.getStartAddress(), range.getEndAddress(), ind));
			ranges.add(ind, range);
		}
		else {
			//_log.logInfo(String.format("Inserting %04x-%04x at end", range.getStartAddress(), range.getEndAddress()));
			ranges.add(range);
		}
	}
	
	/*
	 * Find the device which handles a given address
	 */
	private MappedRange getRange(int addr) {
		if (!validAddress(addr)) {
			throw new ArrayIndexOutOfBoundsException(String.format("Bad address %04x", addr));
		}
		
		for (MappedRange range : ranges) {
			if (range.inRange(addr)) {
				return range;
			}
		}
		
		return null;
	}

	public int getByte(int addr) {
		addr = mask(addr);
		
		MappedRange range = getRange(addr);
		if (range != null) {
			Device dev = range.getDevice();
			
			addr = addr - range.getStartAddress();
		
			return dev.getByte(addr, range.getId()); 
		}
		else {
			_log.logWarn(String.format("Reading byte from address %04x with no handler", addr));
		}
		
		return emptyValue;
	}

	public void setByte(int addr, int val) {
		try {
			setByteX(addr, val);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setByteX(int addr, int val) throws IllegalAccessException {
		addr = mask(addr);
		
		MappedRange range = getRange(addr);
		if (range != null) {
			Device dev = range.getDevice();
			
			addr = addr - range.getStartAddress();

			dev.setByte(addr, val, range.getId());
		}
		else {
			_log.logWarn(String.format("Writing byte %02x to address %04x with no handler", val, addr));
		}
	}
	
	public int getWord(int addr) {
		int res = 0;
		
		if ((addr < 0) || (addr > maxAddress)) {
			throw new ArrayIndexOutOfBoundsException("MemoryDevice address out of bounds");
		}
		
		if (bigEndian) {
			res = (getByte(addr)<<8) | getByte(addr+1);
		}
		else {
			res = getByte(addr) | (getByte(addr+1)<<8);
		}
		
		return res;
	}

	public void setWord(int addr, int val) throws IllegalAccessException {
		if ((addr < 0) || (addr > maxAddress)) {
			throw new ArrayIndexOutOfBoundsException("MemoryDevice address out of bounds");
		}
		
		if (bigEndian) {
			setByte(addr, (val>>8));
			setByte(addr+1, val);
		}
		else {
			setByte(addr, val);
			setByte(addr+1, (val>>8));
		}
	}
	
	public int getEndAddress() {
		return maxAddress;
	}
	
	public void dump() {
		_log.logInfo("Memory Bus dump");
		_log.logInfo(String.format("    width=%d bits, mask=%06x. Max Address=%06x", busWidth, addressMask, maxAddress));
		_log.logInfo(String.format("    bigEndian=%b, can overlap=%b. Empty value=%02x", bigEndian, overlapsAllowed, emptyValue));
		
		for (MappedRange range : ranges) {
			int start = range.getStartAddress();
			int end = range.getEndAddress();
			
			if (start == end) {
				_log.logInfo(String.format("  %04x     : %-12s", start, range.getDevice().getName()));
			}
			else {
				_log.logInfo(String.format("  %04x-%04x: %-12s", start, end, range.getDevice().getName()));
			}
		}
		
		_log.logInfo("End of Memory bus dump");
	}
}
