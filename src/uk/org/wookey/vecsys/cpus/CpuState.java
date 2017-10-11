package uk.org.wookey.vecsys.cpus;

public abstract class CpuState {
	public CpuState() {
		reset();
	}
	
	public abstract void reset();
	public abstract AbstractStatePanel getStatePanel();
}
