package uk.org.wookey.vecsys.cpus;

import uk.org.wookey.vecsys.emulator.Bus;

public abstract class Cpu {
	protected Bus bus;
	
	public void setBus(Bus bus) {
		this.bus = bus;
	}
	
	public abstract void reset();
	public abstract boolean isBigEndian();
	public abstract void step();
	public abstract void go();
	public abstract void stop();
	public abstract StatusPanel getStatusPanel();

	public void step(int numSteps) {
		for (int i=0; i<numSteps; i++) {
			step();
		}
	}
}
