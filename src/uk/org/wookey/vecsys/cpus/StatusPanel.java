package uk.org.wookey.vecsys.cpus;

import java.awt.Color;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

public abstract class StatusPanel extends JPanel {
	public StatusPanel() {
		super();
		
		setLayout(new GridBagLayout());
		setBackground(Color.LIGHT_GRAY);
	}
	
	public abstract void update();

	public void turnOff() {
		setEnabled(false);
	}

	public void turnOn() {
		setEnabled(true);
	}
}
