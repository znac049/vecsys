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
	
	private ArrayList<Integer> mem = new ArrayList<Integer>();
	
	private int scaleFactor;
	private int currentX;
	private int currentY;
	
	private int[] stack = new int[4];
	private int stackPtr;
	
	private int maxInstructions = 100;
	
	public VectorEngine(DataViewer viewer, SourceViewer source, VectorDisplay disp) {
		this.viewer = viewer;
		this.source = source;
		display = disp;
		
		scaleFactor = 0;
		currentX = 0;
		currentY = 0;
		
		stackPtr = 0;
		stack = new int[4];
		
		mem.clear();
	}
	
	public void set(byte[] bytes) {
		int i;
		
		mem.clear();
		
		for (i=0; i<bytes.length; i+= 2) {
			int wrd = (int) ((bytes[i] & 0xFF) | ((bytes[i+1] & 0xFF) << 8));
			mem.add(wrd);
		}
		
		viewer.set(mem);
	}
	
	private int scale(int base) {
		// @TODO: Do this!
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
				stack[stackPtr] = addr+1;
				stackPtr++;
				addr = dest;
			}
			break;
				
			case RTSL_OP:
			{
				int dest = op & 0x03ff;
				
				System.out.println(String.format("%04X: %04X      RTSL", addr, op));
				stackPtr--;
				addr = stack[stackPtr];
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
