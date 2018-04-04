package uk.org.wookey.vecsys.cpus.cpu6x09;

import uk.org.wookey.vecsys.cpus.Cpu;
import uk.org.wookey.vecsys.cpus.StatusPanel;
import uk.org.wookey.vecsys.utils.Logger;

public class Cpu6x09 extends Cpu {
	private static Logger _log = new Logger("6x09");

	private CpuState6x09 state;
	private StatePanel6x09 statePanel;
	
	public Cpu6x09() {
		state = new CpuState6x09();
		statePanel = new StatePanel6x09();
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isBigEndian() {
		return true;
	}

	@Override
	public void step() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void go() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StatusPanel getStatusPanel() {
		return statePanel;
	}

	@Override
	public void interrupt(int interruptId) {
		// TODO Auto-generated method stub
		
	}
}
