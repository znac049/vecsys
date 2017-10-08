package uk.org.wookey.vecsys.emulator;

import uk.org.wookey.vecsys.cpus.Cpu;
import uk.org.wookey.vecsys.cpus.StatePanel;

public abstract class Emulator {
	protected Bus bus;
	protected Cpu cpu;
	
	public StatePanel getStatePanel() {
		return cpu.getStatePanel();
	}
	
	public void step() {
		cpu.step();
	}
}
