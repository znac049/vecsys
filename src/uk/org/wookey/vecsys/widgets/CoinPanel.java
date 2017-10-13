package uk.org.wookey.vecsys.widgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import uk.org.wookey.vecsys.emulator.GBConstraints;

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
	
	public boolean isActive(int slot) {
		switch (slot) {
			case 0:
				return left.isSelected();
				
			case 1:
				return centre.isSelected();
				
			case 2:
				return right.isSelected();
		}
		
		return false;
	}
}
