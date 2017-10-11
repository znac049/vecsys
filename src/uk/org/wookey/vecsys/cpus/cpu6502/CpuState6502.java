package uk.org.wookey.vecsys.cpus.cpu6502;

import javax.swing.JPanel;

import com.loomcom.symon.CpuLoomcom;

public class CpuState6502 {
	public int pc;
	public int a;
	public int x;
	public int y;
	public int sp;

	public int ir;
    public int[] args = new int[2];
	
	public int nextIr;
    public int[] nextArgs = new int[2];
    public Instruction nextInstruction;
	
	// The flags that make up the status register
    public boolean carryFlag;
    public boolean negativeFlag;
    public boolean zeroFlag;
    public boolean irqDisableFlag;
    public boolean decimalModeFlag;
    public boolean breakFlag;
    public boolean overflowFlag;
    
	public CpuState6502() {
	}
	
    public int getStatusFlag() {
        int sr = 0x20;
        
        if (carryFlag) {
            sr |= CpuLoomcom.P_CARRY;
        }
        
        if (zeroFlag) {
            sr |= CpuLoomcom.P_ZERO;
        }
        
        if (irqDisableFlag) {
            sr |= CpuLoomcom.P_IRQ_DISABLE;
        }
        
        if (decimalModeFlag) {
            sr |= CpuLoomcom.P_DECIMAL;
        }
        
        if (breakFlag) {
            sr |= CpuLoomcom.P_BREAK;
        }
        
        if (overflowFlag) {
            sr |= CpuLoomcom.P_OVERFLOW;
        }
        
        if (negativeFlag) {
            sr |= CpuLoomcom.P_NEGATIVE;
        }
        
        return sr;
    }
	public String decodeNextInstruction() {
		switch (nextInstruction.mode) {
		case ACC:
			return "A";
			
		case AIX:
			return "???AIX";
			
		case ABS:
			return String.format("$%02x%02x", nextArgs[1], nextArgs[0]);
			
		case ABX:
			return String.format("$%02x%02x,X", nextArgs[1], nextArgs[0]);
			
		case ABY:
			return String.format("$%02x%02x,Y", nextArgs[1], nextArgs[0]);
			
		case IMM:
			return String.format("#$%02x", nextArgs[0]);
			
		case IMP:
			return "";
			
		case IND:
			return String.format("($%02x%02x)", nextArgs[1], nextArgs[0]);
			
		case XIN:
			return String.format("($%02x,X)", nextArgs[0]);
			
		case INY:
			return String.format("($%02x),Y", nextArgs[0]);
			
		case REL:
			int target;
			
			if ((nextArgs[0] & 0x80) != 0) {
				target = pc + 2 - ((~nextArgs[0] & 0xff)+1);
			}
			else {
				target = pc + 2 + nextArgs[0];
			}
			return String.format("$%04x", target);
			
		case ZPG:
			return String.format("$%02x", nextArgs[0]);
			
		case ZPR:
			return "???ZPR";
			
		case ZPX:
			return String.format("$%02x,X", nextArgs[0]);
			
		case ZPY:
			return String.format("$%02x,Y", nextArgs[0]);
			
		case ZPI:
			break;
			
		case NUL:
			return "???NUL";
			
		default:
			return "???";
		}
		
		return "";
	}
}
