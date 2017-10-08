package uk.org.wookey.vecsys.cpus.cpu6x09;

import javax.swing.JPanel;

import uk.org.wookey.vecsys.cpus.CPUState;
import uk.org.wookey.vecsys.cpus.STATEPanel;

public class CpuState extends CPUState {
	private StatePanel statePanel;
	
	public CpuState() {
		statePanel = new StatePanel();
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
