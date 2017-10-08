package uk.org.wookey.vecsys.cpus;

import java.awt.Color;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

public abstract class StatePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	protected final static Color headingColour = new Color(152, 244, 66);
	
	public StatePanel() {
		setLayout(new GridBagLayout());
		setBackground(Color.DARK_GRAY);
	}
	
	public abstract void rebuild(CpuState state);
}
