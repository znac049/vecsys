package uk.org.wookey.vecsys.cpus;

public abstract class CPUState {
	public CPUState() {
		reset();
	}
	
	public abstract void reset();
	public abstract STATEPanel getStatePanel();
}
