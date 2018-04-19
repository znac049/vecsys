package uk.org.wookey.vecsys.emulator;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class GridBagStrip extends JPanel {
	private static final long serialVersionUID = 1L;
	private GBConstraints gbc;
	
	public GridBagStrip() {
		super();
		
		setLayout(new GridBagLayout());
		
		gbc = new GBConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		append(new JLabel(""));
		gbc.fill = GridBagConstraints.NONE;
	}
	
	public void append(Container container) {
		add(container, gbc);
		gbc.gridx++;
	}
	
	public void eol() {
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(new JLabel(""), gbc);
	}
}
