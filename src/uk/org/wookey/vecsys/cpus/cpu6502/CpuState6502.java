package uk.org.wookey.vecsys.cpus.cpu6502;

import javax.swing.JPanel;

import com.loomcom.symon.CpuLoomcom;

import uk.org.wookey.vecsys.cpus.CpuState;
import uk.org.wookey.vecsys.cpus.StatePanel;

public class CpuState6502 extends CpuState {
	public int pc;
	public int a;
	public int x;
	public int y;
	public int sp;

	public int ir;
    public int[] args = new int[2];
	
	public int nextIr;
    public int[] nextArgs = new int[2];
	
	// The flags that make up the status register
    public boolean carryFlag;
    public boolean negativeFlag;
    public boolean zeroFlag;
    public boolean irqDisableFlag;
    public boolean decimalModeFlag;
    public boolean breakFlag;
    public boolean overflowFlag;
    
	private StatePanel6502 statePanel;
	
	public CpuState6502() {
		statePanel = new StatePanel6502();
		statePanel.rebuild(this);
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

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StatePanel getStatePanel() {
		return statePanel;
	}
}
