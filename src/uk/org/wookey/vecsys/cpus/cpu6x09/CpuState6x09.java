package uk.org.wookey.vecsys.cpus.cpu6x09;

import javax.swing.JPanel;

import uk.org.wookey.vecsys.cpus.CpuState;
import uk.org.wookey.vecsys.cpus.AbstractStatePanel;

public class CpuState6x09 extends CpuState {
	private StatePanel6x09 statePanel;
	
	public CpuState6x09() {
		statePanel = new StatePanel6x09();
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
