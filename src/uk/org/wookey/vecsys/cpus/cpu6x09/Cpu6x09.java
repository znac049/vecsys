package uk.org.wookey.vecsys.cpus.cpu6x09;

import uk.org.wookey.vecsys.cpus.CPU;
import uk.org.wookey.vecsys.cpus.STATEPanel;
import uk.org.wookey.vecsys.utils.Logger;

public class Cpu6x09 extends CPU {
	private static Logger _log = new Logger("6x09");

	private CpuState state;
	
	public Cpu6x09() {
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
		return true;
	}

	@Override
	public STATEPanel getStatePanel() {
		return state.getStatePanel();
	}
}
