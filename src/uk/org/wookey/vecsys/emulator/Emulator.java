package uk.org.wookey.vecsys.emulator;

import uk.org.wookey.vecsys.cpus.CPU;
import uk.org.wookey.vecsys.cpus.STATEPanel;

public abstract class Emulator {
	protected Bus bus;
	protected CPU cpu;
	
	public STATEPanel getStatePanel() {
		return cpu.getStatePanel();
	}
}
