package uk.org.wookey.vecsys.emulator.devices;

import java.io.IOException;

import uk.org.wookey.vecsys.emulator.Device;
import uk.org.wookey.vecsys.utils.Logger;
import uk.org.wookey.vecsys.widgets.VectorDisplay;

public class DVG extends Device {
	@SuppressWarnings("unused")
	private static Logger _log = new Logger("DVG");
	
	private final static int VCTR0_OP = 0x0;	
	private final static int VCTR1_OP = 0x1;	
	private final static int VCTR2_OP = 0x2;	
	private final static int VCTR3_OP = 0x3;	
	private final static int VCTR4_OP = 0x4;	
	private final static int VCTR5_OP = 0x5;	
	private final static int VCTR6_OP = 0x6;	
	private final static int VCTR7_OP = 0x7;	
	private final static int VCTR8_OP = 0x8;	
	private final static int VCTR9_OP = 0x9;	
	private final static int LABS_OP  = 0xa;
	private final static int HALT_OP  = 0xb;
	private final static int JSRL_OP  = 0xc;
	private final static int RTSL_OP  = 0xd;
	private final static int JMPL_OP  = 0xe;
	private final static int SVEC_OP  = 0xf;
	
	private final static int STACK_DEPTH = 4;
	
	public static final int vgHalted = 0;
	public static final int startVG = 1;
	public static final int resetVG = 2;
	public static final int vgMem = 3;

	private DVGMemory mem;
	private VectorDisplay display;

	private int scaleShiftRight;
	private int currentX;
	private int currentY;
	
	private int[] stack = new int[STACK_DEPTH];
	private int stackPtr = 0;
	
	private int maxInstructions = 100;
	

	public DVG() throws IOException {
		super("DVG");
		
		scaleShiftRight = 0;
		currentX = 0;
		currentY = 0;
		
		mem = new DVGMemory();
		display = new VectorDisplay();
	}
	
	@Override
	public int getByte(int addr, int id) {
		switch (id) {
			case vgHalted:
				_log.logInfo("Is the VG halted?");
				break;
				
			case startVG:
				_log.logError("Trying to read from the WO startVG address");
				break;
				
			case resetVG:
				_log.logError("Trying to read from the WO resetVG address");
				break;
				
			case vgMem:
				_log.logInfo(String.format("Read VRAM at address %04x", addr));
				return mem.getByte(addr, 0);
		}
		
		return 0;
	}

	@Override
	public void setByte(int addr, int val, int id) throws IllegalAccessException {
		switch (id) {
			case vgHalted:
				_log.logError("Trying to write to the RO VGHalted address");
				break;
			
			case startVG:
				_log.logInfo("Start the VG");
				break;
			
			case resetVG:
				_log.logInfo("Reset the VG");
				break;
			
			case vgMem:
				_log.logInfo(String.format("VRAM write %02x to %04x", val, addr));
				mem.setByte(addr, val, 0);
				break;
		}
	}
	
	private int twosComplement(int val, int bits) {
		int signMask = 1<<(bits-1);
		int valMask = signMask-1;
		int fullMask = (1<<bits)-1;
		
		System.out.println(String.format("XXXXXXXXXXXXXXXXXXXX twosComplement(%04x, %d) -> masks: sig=%04x, val=%04x, full=%04x", val, bits, signMask, valMask, fullMask));
		
		val = val & fullMask;
		System.out.println(String.format("XXXXXXXXXXXXXXXXXXXX Masked value=%04x", val));
		if ((val & signMask) != 0) {
			val =  0 - (val & valMask) + 1;
			System.out.println(String.format("XXXXXXXXXXXXXXXXXXXX Neg value=%d (%04x)", val, val));
		}
		else {
			val = val & valMask;
			System.out.println(String.format("XXXXXXXXXXXXXXXXXXXX Pos value=%d (%04x)", val, val));
		}
		
		System.out.println(String.format("XXXXXXXXXXXXXXXXXXXX -> %04x", val));
		
		return val;
	}
	
	private int decodeScaleFactor(int sf) {
		int rightShift = 0;
		
		sf = sf & 0x0f;
		if (sf >= 8) {
			// Division, so shift right
			rightShift = 16-sf;
		}
		else {
			// Multiplication, so shift left
			rightShift = -sf;
		}
		
		//System.out.println(String.format("decodeScaleFactor(%d) -> %d", sf, rightShift));
		
		return rightShift;
	}
	
	private boolean push(int addr) {
		if (stackPtr < STACK_DEPTH) {
			stack[stackPtr] = addr;
			stackPtr++;
			
			return true;
		}
		
		return false;
	}
	
	private int pop() {
		if (stackPtr <= 0) {
			return -1;
		}
		
		stackPtr--;
		return stack[stackPtr];
	}
	
	public void go(int addr) {
		int endAddr = mem.size();
		int instructions = 0;
		boolean halted = false;
		int x;
		int y;
		
		display.clearScreen();
		
		stackPtr = 0;
		currentX = currentY = 511;
		
		while ((!halted) && (addr >= 0) && (addr < endAddr)) {
			int wordOne = mem.get(addr);
			int opcode = wordOne >> 12;
		
			switch (opcode) {
			case HALT_OP:
				//System.out.println(String.format("%04X: %04X HALT", addr, op));
				System.out.println("Halting.");
				halted = true;
				break;
				
			case JMPL_OP:
			{
				int dest = wordOne & 0x0fff;
				
				//System.out.println(String.format("%04X: %04X      JMPL $%04X", addr, op, dest));
				addr = dest;
			}
			break;
				
			case JSRL_OP:
			{
				int dest = wordOne & 0x0fff;
				
				//System.out.println(String.format("%04X: %04X      JSRL $%04X", addr, op, dest));
				if (!push(addr+1)) {
					System.out.println("Stack overflow. Halting.");
					halted = true;
				}
				else {
					addr = dest;
				}
			}
			break;
				
			case RTSL_OP:
			{
				System.out.println(String.format("%04X: %04X      RTSL", addr, wordOne));
				addr = pop();
				if (addr < 0) {
					System.out.println("Stack underflow. Halting.");
					halted = true;
				}
				
				System.out.println(String.format("Return address: $%04X", addr));
			}
			break;
				
			case LABS_OP:
			// Move the beam to an absolute position and set the global scale factor
			{
				int wordTwo = mem.get(addr+1);

				x = twosComplement(wordTwo, 11);
				y = twosComplement(wordOne, 11);
				
				currentX = x;
				currentY = y;
				
				scaleShiftRight = decodeScaleFactor(wordTwo>>12);
				
				System.out.println(String.format("%04X: %04X %04X LABS, (%d,%d),%d", addr, wordOne, wordTwo, x, y, wordTwo>>12));
				addr += 2;
			}
			break;
				
			case VCTR0_OP:
			case VCTR1_OP:
			case VCTR2_OP:
			case VCTR3_OP:
			case VCTR4_OP:
			case VCTR5_OP:
			case VCTR6_OP:
			case VCTR7_OP:
			case VCTR8_OP:
			case VCTR9_OP:
			// Draw a vector.
			{
				int wordTwo = mem.get(addr+1);
				int opShift = 9 - opcode;
				
				y = wordOne & 0x03ff;
				if ((wordOne & 0x0400) != 0) {
					y = -y;
				}
				
				x = wordTwo & 0x03ff;
				if ((wordTwo & 0x0400) != 0) {
					x = -x;
				}

				int z = wordTwo >> 12;
		
				opShift = (opShift + scaleShiftRight) & 0x0f;
				
				System.out.println(String.format("%04X: %04X %04X VCTR%d, (%d,%d),%d", addr, wordOne, wordTwo, opcode, x, y, z));
				
				x = x >> opShift;
				y = y >> opShift;
				
				display.addLine(currentX, currentY, currentX + x, currentY + y, z);

				currentX += x;
				currentY += y;

				addr += 2;
			}
			break;
			
			case SVEC_OP:
			{
				y = (wordOne & 0x0300);
				if ((wordOne & 0x0400) != 0) {
					y = -y;
				}
				
				x = (wordOne & 0x0003) << 8;
				if ((wordOne & 0x0004) != 0) {
					x = -x;
				}
				
				int z = (wordOne >> 4) & 0x000f;
				
				int sf = ((wordOne >> 11) & 0x0001) | ((wordOne >> 2) & 0x0002);
				int opShift = 7 - sf;
				
				System.out.println(String.format("%04X: %04X      SVEC (%d,%d),%d,@%d", addr, wordOne, x, y, z, sf));

				opShift = (opShift + scaleShiftRight) & 0x0f;
				x = x >> opShift;
				y = y >> opShift;
				
				display.addLine(currentX, currentY, currentX + x, currentY + y, z);

				currentX += x;
				currentY += y;

				addr++;
			}
			break;
				
			default:
				System.out.println(String.format("Bad OP: %04X", wordOne));
				System.out.println("Halting.");
				halted = true;
			}
			
			instructions++;
			if (instructions > maxInstructions) {
				System.out.println(String.format("Max instructions (%d) executed. Halting.", maxInstructions));
				halted = true;
			}
		}
	}
}
