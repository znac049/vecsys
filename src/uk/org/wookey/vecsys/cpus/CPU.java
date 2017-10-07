package uk.org.wookey.vecsys.cpus;

import uk.org.wookey.vecsys.emulator.Bus;

public abstract class CPU {
	protected Bus bus;
	
	public void setBus(Bus bus) {
		this.bus = bus;
	}
	
	public abstract void reset();
	public abstract boolean isBigEndian();
	
	public abstract void step(int numSteps);
	public void step() {
		step(1);
	}

	public abstract STATEPanel getStatePanel();
}
