package uk.org.wookey.vecsys.cpus.cpu6502;

import uk.org.wookey.vecsys.cpus.CPU;
import uk.org.wookey.vecsys.cpus.STATEPanel;
import uk.org.wookey.vecsys.utils.Logger;

public class Cpu6502 extends CPU {
	private static Logger _log = new Logger("6502");

    // Status register bits
    public static final int P_CARRY       = 0x01;
    public static final int P_ZERO        = 0x02;
    public static final int P_IRQ_DISABLE = 0x04;
    public static final int P_DECIMAL     = 0x08;
    public static final int P_BREAK       = 0x10;
    // Bit 5 is always '1'
    public static final int P_OVERFLOW    = 0x40;
    public static final int P_NEGATIVE    = 0x80;

	private CpuState state;
	
	public Cpu6502() {
		state = new CpuState();
	}
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void step(int numSteps) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isBigEndian() {
		return false;
	}

	@Override
	public STATEPanel getStatePanel() {
		return state.getStatePanel();
	}
}
