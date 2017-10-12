package uk.org.wookey.vecsys.emulator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class CoinPanel extends JPanel {
	private SlottedButton left;
	private SlottedButton centre;
	private SlottedButton right;
	
	public CoinPanel() {
		super();
		
		setLayout(new GridBagLayout());
		
		GBConstraints gbc = new GBConstraints();
		gbc.weightx = 0.5;
		
		setBorder(BorderFactory.createLineBorder(Color.black));
		
		left = new SlottedButton("Left");
		add(left, gbc);
		gbc.right();
		
		centre = new SlottedButton("Centre");
		add(centre, gbc);
		gbc.right();
		
		right = new SlottedButton("Right");
		add(right, gbc);
	}
}
