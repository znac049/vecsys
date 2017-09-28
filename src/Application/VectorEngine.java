package Application;

import java.util.ArrayList;

public class VectorEngine {
	private DataViewer viewer;
	private SourceViewer source;
	private VectorDisplay display;
	
	private final static int VCTR9_OP = 0x0000;	
	private final static int VCTR8_OP = 0x1000;	
	private final static int VCTR7_OP = 0x2000;	
	private final static int VCTR6_OP = 0x3000;	
	private final static int VCTR5_OP = 0x4000;	
	private final static int VCTR4_OP = 0x5000;	
	private final static int VCTR3_OP = 0x6000;	
	private final static int VCTR2_OP = 0x7000;	
	private final static int VCTR1_OP = 0x8000;	
	private final static int VCTR0_OP = 0x9000;	
	private final static int LABS_OP  = 0xa000;
	private final static int HALT_OP  = 0xb000;
	private final static int JSRL_OP  = 0xc000;
	private final static int RTSL_OP  = 0xd000;
	private final static int JMPL_OP  = 0xe000;
	private final static int SVEC_OP  = 0xf000;
	
	private final static int STACK_DEPTH = 4;
	
	private ArrayList<Integer> mem = new ArrayList<Integer>();
	
	private int scaleFactor;
	private int currentX;
	private int currentY;
	
	private int[] stack = new int[STACK_DEPTH];
	private int stackPtr = 0;
	
	private int maxInstructions = 100;
	
	public VectorEngine(DataViewer viewer, SourceViewer source, VectorDisplay disp) {
		this.viewer = viewer;
		this.source = source;
		display = disp;
		
		scaleFactor = 0;
		currentX = 0;
		currentY = 0;
		
		mem.clear();
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
	
	public void set(byte[] bytes) {
		int i;
		
		mem.clear();
		
		System.out.println(String.format("Loading %d bytes into vector memory", bytes.length));
		for (i=0; i<bytes.length; i+= 2) {
			int wrd = (int) ((bytes[i] & 0xFF) | ((bytes[i+1] & 0xFF) << 8));
			mem.add(wrd);
		}
		
		System.out.println("Refreshing viewer");
		viewer.set(mem);
	}
	
	private int scale(int base) {
		// @TODO: Do this!
		if (scaleFactor <= 8) {
			base = base << scaleFactor;
		}
		else {
			int shift = 0x08 - (scaleFactor & 0x07);
			base = base >> shift;
		}
		
		return base;
	}
	
	public void display(int addr) {
		int endAddr = mem.size();
		int instructions = 0;
		boolean halted = false;
		
		display.clearScreen();
		
		while ((!halted) && (addr >= 0) && (addr < endAddr)) {
			int op = mem.get(addr).intValue();
			
			switch (op & 0xf000) {
			case HALT_OP:
				System.out.println(String.format("%04X: %04X HALT", addr, op));
				System.out.println("Halting.");
				halted = true;
				break;
				
			case JMPL_OP:
			{
				int dest = op & 0x03ff;
				
				System.out.println(String.format("%04X: %04X      JMPL $%04X", addr, op, dest));
				addr = dest;
			}
			break;
				
			case JSRL_OP:
			{
				int dest = op & 0x03ff;
				
				System.out.println(String.format("%04X: %04X      JSRL $%04X", addr, op, dest));
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
				System.out.println(String.format("%04X: %04X      RTSL", addr, op));
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
				int y = op & 0x03ff;
				
				if ((op & 0x0400) != 0) {
					y = -y;
				}
				
				int op2 = mem.get(addr+1).intValue();
				int x = op2 & 0x03ff;
				
				scaleFactor = (op2 & 0xf000) >> 12;
				
				if ((op2 & 0x0400) != 0) {
					x = -x;
				}
				
				currentX = scale(x);
				currentY = scale(y);
				
				System.out.println(String.format("%04X: %04X %04X LABS, sf=%d, dx=%d, dy=%d", addr, op, op2, scaleFactor, currentX, currentY));
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
				int y = op & 0x03ff;
				int num = 9 - ((op & 0xf000) >> 12);
				
				if ((op & 0x0400) != 0) {
					y = -y;
				}
				
				int op2 = mem.get(addr+1).intValue();
				int x = op2 & 0x03ff;
				
				int intensity = (op2 & 0xf000) >> 12;
				
				if ((op2 & 0x0400) != 0) {
					x = -x;
				}

				x = x >> num;
				y = y >> num;
				
				display.addLine(currentX, currentY, currentX + x, currentY + y, intensity);
				currentX += x;
				currentY += y;
				
				System.out.println(String.format("%04X: %04X %04X VCTR%d, int=%d, dx=%d, dy=%d", addr, op, op2, num, intensity, x, y));
				addr += 2;
			}
			break;
			
			case SVEC_OP:
			{
				int y = op & 0x0003;
				
				if ((op & 0x0004) != 0) {
					y = -y;
				}
				
				int x = (op & 0300) >> 8;
				
				if ((op & 0x0400) != 0) {
					x = -x;
				}
				
				int intensity = (op & 0x00f0) >> 4;
				int scale = ((op >> 11) & 0x0001) | ((op >> 2) & 0x0002);
				int shift = 7 - scale;
				
				x = x << shift;
				y = y << shift;
				System.out.println(String.format("%04X: %04X      SVEC int=%d, dx=%d, dy=%d", addr, op, intensity, x, y));
				addr++;
			}
			break;
				
			default:
				System.out.println(String.format("Bad OP: %04X", op));
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
