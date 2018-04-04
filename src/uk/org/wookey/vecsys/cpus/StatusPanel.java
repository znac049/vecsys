package uk.org.wookey.vecsys.cpus;

import java.awt.Color;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

public abstract class StatusPanel extends JPanel {
	protected boolean enabled;
	
	public StatusPanel() {
		super();
		
		setLayout(new GridBagLayout());
		setBackground(Color.LIGHT_GRAY);
		enabled = true;
	}
	
	public abstract void update();

	public boolean isEnabled() {
		return enabled;
	}
	
	public void turnOff() {
		enabled = false;
		setEnabled(enabled);
	}

	public void turnOn() {
		enabled = true;
		setEnabled(enabled);
	}
}
