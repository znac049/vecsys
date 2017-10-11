package com.loomcom.symon;

import java.awt.Component;
import java.awt.GridBagConstraints;

import uk.org.wookey.vecsys.cpus.AbstractStatePanel;
import uk.org.wookey.vecsys.cpus.CpuState;
import uk.org.wookey.vecsys.emulator.GBConstraints;
import uk.org.wookey.vecsys.emulator.TTLabel;
import uk.org.wookey.vecsys.utils.VecUtils;

/**
 * A compact, struct-like representation of CPU state.
 */
public class CpuStateLoomcom extends CpuState {
    /**
     * Accumulator
     */
    public int a;

    /**
     * X index regsiter
     */
    public int x;

    /**
     * Y index register
     */
    public int y;

    /**
     * Stack Pointer
     */
    public int sp;

    /**
     * Program Counter
     */
    public int pc;

    /**
     * Last Loaded Instruction Register
     */
    public int ir;

    /**
     * Peek-Ahead to next IR
     */
    public int nextIr;
    public int[] args = new int[2];
    public int[] nextArgs = new int[2];
    public int instSize;
    public boolean opTrap;
    public boolean irqAsserted;
    public boolean nmiAsserted;
    public int lastPc;

    /* Status Flag Register bits */
    public boolean carryFlag;
    public boolean negativeFlag;
    public boolean zeroFlag;
    public boolean irqDisableFlag;
    public boolean decimalModeFlag;
    public boolean breakFlag;
    public boolean overflowFlag;
    public long stepCounter = 0L;

    public class StatePanel extends AbstractStatePanel {
    	private TTLabel pcReg;
    	private TTLabel aReg;
    	private TTLabel xReg;
    	private TTLabel yReg;
    	private TTLabel sReg;
    	private TTLabel spReg;
    	private TTLabel flagStr;

    	public StatePanel() {
    		super();
    		
    		GBConstraints gbc = new GBConstraints();
    		gbc.anchor = GridBagConstraints.CENTER;
    		gbc.fill = GridBagConstraints.NONE;
    		
    		add(new TTLabel("PC", headingColour), gbc);
    		gbc.right();
    		
    		add(new TTLabel("A", headingColour), gbc);
    		gbc.right();
    		
    		add(new TTLabel("X", headingColour), gbc);
    		gbc.right();
    		
    		add(new TTLabel("Y", headingColour), gbc);
    		gbc.right();
    		
    		add(new TTLabel("SR", headingColour), gbc);
    		gbc.right();
    		
    		add(new TTLabel("SP", headingColour), gbc);
    		gbc.right();
    		
    		add(new TTLabel("NV-BDIZC", headingColour), gbc);
    		
    		gbc.nl();
    		
    		pcReg = new TTLabel("----");
    		add(pcReg, gbc);
    		gbc.right();
    		
    		aReg = new TTLabel("--");
    		add(aReg, gbc);
    		gbc.right();
    		
    		xReg = new TTLabel("--");
    		add(xReg, gbc);
    		gbc.right();
    		
    		yReg = new TTLabel("--");
    		add(yReg, gbc);
    		gbc.right();
    		
    		sReg = new TTLabel("--");
    		add(sReg, gbc);
    		gbc.right();
    		
    		spReg = new TTLabel("---");
    		add(spReg, gbc);
    		gbc.right();
    		
    		flagStr = new TTLabel("--------");
    		add(flagStr, gbc);
    	}

		@Override
		public void redraw(CpuState state) {
			CpuStateLoomcom st = (CpuStateLoomcom)state;
			
			pcReg.setText(String.format("%04x", st.pc));
			aReg.setText(String.format("%02x", st.a));
			xReg.setText(String.format("%02x", st.x));
			yReg.setText(String.format("%02x", st.y));
			sReg.setText(String.format("%02x", st.getStatusFlag()));
			spReg.setText(String.format("1%02x", st.sp));
			flagStr.setText(VecUtils.binaryString(st.getStatusFlag(), 8));
			
		}
    }
    
    StatePanel statePanel;
    
    public CpuStateLoomcom() {        
		statePanel = new StatePanel();
    }

    /**
     * Snapshot a copy of the CpuState.
     *
     * (This is a copy constructor rather than an implementation of <code>Cloneable</code>
     * based on Josh Bloch's recommendation)
     *
     * @param s The CpuState to copy.
     */
    public CpuStateLoomcom(CpuStateLoomcom s) {
        this.a = s.a;
        this.x = s.x;
        this.y = s.y;
        this.sp = s.sp;
        this.pc = s.pc;
        this.ir = s.ir;
        this.nextIr = s.nextIr;
        this.lastPc = s.lastPc;
        this.args[0] = s.args[0];
        this.args[1] = s.args[1];
        this.nextArgs[0] = s.nextArgs[0];
        this.nextArgs[1] = s.nextArgs[1];
        this.instSize = s.instSize;
        this.opTrap = s.opTrap;
        this.irqAsserted = s.irqAsserted;
        this.carryFlag = s.carryFlag;
        this.negativeFlag = s.negativeFlag;
        this.zeroFlag = s.zeroFlag;
        this.irqDisableFlag = s.irqDisableFlag;
        this.decimalModeFlag = s.decimalModeFlag;
        this.breakFlag = s.breakFlag;
        this.overflowFlag = s.overflowFlag;
        this.stepCounter = s.stepCounter;
    }

    /**
     * Returns a string formatted for the trace log.
     *
     * @return a string formatted for the trace log.
     */
    public String toTraceEvent() {
        String opcode = CpuLoomcom.disassembleOp(ir, args);
        return String.format("%s %-13s A:%02x X:%02x Y:%02x F:%02x S:1%02x %s\n", 
        		getInstructionByteStatus(), opcode, a, x, y, getStatusFlag(), sp, getProcessorStatusString());
    }

    /**
     * @return The value of the Process Status Register, as a byte.
     */
    public int getStatusFlag() {
        int status = 0x20;
        if (carryFlag) {
            status |= CpuLoomcom.P_CARRY;
        }
        if (zeroFlag) {
            status |= CpuLoomcom.P_ZERO;
        }
        if (irqDisableFlag) {
            status |= CpuLoomcom.P_IRQ_DISABLE;
        }
        if (decimalModeFlag) {
            status |= CpuLoomcom.P_DECIMAL;
        }
        if (breakFlag) {
            status |= CpuLoomcom.P_BREAK;
        }
        if (overflowFlag) {
            status |= CpuLoomcom.P_OVERFLOW;
        }
        if (negativeFlag) {
            status |= CpuLoomcom.P_NEGATIVE;
        }
        return status;
    }

    public String getInstructionByteStatus() {
        switch (CpuLoomcom.instructionSizes[ir]) {
            case 0:
            case 1:
            	return String.format("%04x  %02x     ", lastPc, ir);

            case 2:
            	return String.format("%04x  %02x %02x  ", lastPc, ir, args[0]);
            
            case 3:
            	return String.format("%04x  %02x %02x %02x", lastPc, ir, args[0], args[1]);
            default:
                return null;
        }
    }

    /**
     * @return A string representing the current status register state.
     */
    public String getProcessorStatusString() {
        return "[" + (negativeFlag ? 'N' : '.') +
                (overflowFlag ? 'V' : '.') +
                "-" +
                (breakFlag ? 'B' : '.') +
                (decimalModeFlag ? 'D' : '.') +
                (irqDisableFlag ? 'I' : '.') +
                (zeroFlag ? 'Z' : '.') +
                (carryFlag ? 'C' : '.') +
                "]";
    }

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AbstractStatePanel getStatePanel() {
		return statePanel;
	}
}
