package uk.org.wookey.vecsys.emulator;

import java.awt.Component;

import javax.swing.JPanel;

import uk.org.wookey.vecsys.cpus.Cpu;
import uk.org.wookey.vecsys.cpus.StatusPanel;

public abstract class Emulator {
	protected Bus bus;
	protected Cpu cpu;
	public Component getControlsPanel;

	public abstract JPanel getConfigPanel();
	public abstract JPanel getControlsPanel();	
	public abstract StatusPanel getStatusPanel();
	
	public void step() {
		cpu.step();
	}
}
