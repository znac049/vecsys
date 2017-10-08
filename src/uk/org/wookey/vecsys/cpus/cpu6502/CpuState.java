package uk.org.wookey.vecsys.cpus.cpu6502;

import javax.swing.JPanel;

import com.loomcom.symon.Cpu6502;

import uk.org.wookey.vecsys.cpus.CPUState;
import uk.org.wookey.vecsys.cpus.STATEPanel;

public class CpuState extends CPUState {
	public int pc;
	public int ir;
	public int a;
	public int x;
	public int y;
	public int sp;
	
	// The flags that make up the status register
    public boolean carryFlag;
    public boolean negativeFlag;
    public boolean zeroFlag;
    public boolean irqDisableFlag;
    public boolean decimalModeFlag;
    public boolean breakFlag;
    public boolean overflowFlag;
    
	private StatePanel statePanel;
	
	public CpuState() {
		statePanel = new StatePanel();
		statePanel.rebuild(this);
	}
	
    public int getStatusFlag() {
        int sr = 0x20;
        
        if (carryFlag) {
            sr |= Cpu6502.P_CARRY;
        }
        
        if (zeroFlag) {
            sr |= Cpu6502.P_ZERO;
        }
        
        if (irqDisableFlag) {
            sr |= Cpu6502.P_IRQ_DISABLE;
        }
        
        if (decimalModeFlag) {
            sr |= Cpu6502.P_DECIMAL;
        }
        
        if (breakFlag) {
            sr |= Cpu6502.P_BREAK;
        }
        
        if (overflowFlag) {
            sr |= Cpu6502.P_OVERFLOW;
        }
        
        if (negativeFlag) {
            sr |= Cpu6502.P_NEGATIVE;
        }
        
        return sr;
    }

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public STATEPanel getStatePanel() {
		return statePanel;
	}
}
