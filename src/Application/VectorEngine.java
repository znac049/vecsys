package Application;

import java.util.ArrayList;

public class VectorEngine {
	private DataViewer viewer;
	private SourceViewer source;
	private VectorDisplay display;
	
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
	
	private VectorMemory mem = new VectorMemory();
	
	private int scaleShiftRight;
	private int currentX;
	private int currentY;
	
	private int[] stack = new int[STACK_DEPTH];
	private int stackPtr = 0;
	
	private int maxInstructions = 100;
	
	public VectorEngine(DataViewer viewer, SourceViewer source, VectorDisplay disp) {
		this.viewer = viewer;
		this.source = source;
		display = disp;
		
		scaleShiftRight = 0;
		currentX = 0;
		currentY = 0;
		
		mem.clear();
	}
	
	private int twosComplement(int val, int bits) {
		int signMask = 1<<bits;
		int valMask = signMask-1;
		
		//System.out.println(String.format("twosComplement(%04x, %d) -> masks: %04x, %04x", val, bits, signMask, valMask));
		
		if ((val & signMask) != 0) {
			val = (~(val & valMask))+1;
		}
		else {
			val = val & valMask;
		}
		
		//System.out.println(String.format("-> %04x", val));
		
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
	
	public void set(byte[] bytes, int addr) {
		int i;
		
		System.out.println(String.format("Loading %d bytes into vector memory at $%04X", bytes.length, addr));
		for (i=0; i<bytes.length; i+= 2) {
			int wrd = (int) ((bytes[i] & 0xFF) | ((bytes[i+1] & 0xFF) << 8));
			mem.set(addr++, wrd);
		}
		
		System.out.println("Refreshing viewer");
		viewer.set(mem);
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
				//System.out.println(String.format("%04X: %04X      RTSL", addr, op));
				addr = pop();
				if (addr < 0) {
					System.out.println("Stack underflow. Halting.");
					halted = true;
				}
			}
			break;
				
			case LABS_OP:
			// Move the beam to an absolute position and set the global scale factor
			{
				int wordTwo = mem.get(addr+1);

				x = twosComplement(wordTwo, 12);
				y = twosComplement(wordOne, 12);
				
				currentX = x;
				currentY = y;
				
				scaleShiftRight = decodeScaleFactor(wordTwo>>12);
				
				System.out.println(String.format("%04X: %04X %04X LABS, (%d,%d),%d", addr, wordOne, wordTwo, x, y, wordTwo>>12));
				addr += 2;
			}
			break;
				
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

	public void disassemble(int addr) {
		int endAddr = mem.size();

		source.setText(String.format("\torg\t$%04X\n\n", addr));
		
		while ((addr >= 0) && (addr < endAddr)) {
			int wordOne = mem.get(addr);
			int opcode = wordOne >> 12;
			
			source.append(String.format("%04X:", addr));

			switch (opcode) {
			case HALT_OP:
				source.append("\thalt\n");
				return;
				
			case JMPL_OP:
			{
				int dest = wordOne & 0x0fff;
				source.append(String.format("\tjmpl\t$%04X\n", dest));
				return;
			}
				
			case JSRL_OP:
			{
				int dest = wordOne & 0x0fff;
				source.append(String.format("\tjsrl\t$%04X\n", dest));
				addr++;
			}
			break;
				
			case RTSL_OP:
				source.append("\trtsl\n");
				return;
				
			case LABS_OP:
			// Move the beam to an absolute position and set the global scale factor
			{
				int wordTwo = mem.get(addr+1);

				int x = twosComplement(wordTwo, 12);
				int y = twosComplement(wordOne, 12);
				
				source.append(String.format("\tlabs\t(%d,%d),%d\n", x, y, wordTwo>>12));
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
				
				int y = wordOne & 0x03ff;
				if ((wordOne & 0x0400) != 0) {
					y = -y;
				}
				
				int x = wordTwo & 0x03ff;
				if ((wordTwo & 0x0400) != 0) {
					x = -x;
				}

				int z = wordTwo >> 12;
		
				opShift = (opShift + scaleShiftRight) & 0x0f;
				
				source.append(String.format("\tvctr%d\t(%d,%d),%d\n", opcode, x, y, z));
				addr += 2;
			}
			break;
			
			case SVEC_OP:
			{
				int y = (wordOne & 0x0300);
				if ((wordOne & 0x0400) != 0) {
					y = -y;
				}
				
				int x = (wordOne & 0x0003) << 8;
				if ((wordOne & 0x0004) != 0) {
					x = -x;
				}
				
				int z = (wordOne >> 4) & 0x000f;
				
				int sf = ((wordOne >> 11) & 0x0001) | ((wordOne >> 2) & 0x0002);
				int opShift = 7 - sf;
				
				source.append(String.format("\tsvec\t(%d,%d),%d,@%d\n", x, y, z, sf));
				addr++;
			}
			break;
				
			default:
				addr++;
				break;
			}
		} 
	} 
}
