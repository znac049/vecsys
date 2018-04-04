package uk.org.wookey.vecsys.widgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import uk.org.wookey.vecsys.emulator.GBConstraints;
import uk.org.wookey.vecsys.utils.Logger;

public class CoinPanel extends JPanel {
	private static final Logger _log = new Logger();
	
	private SlottedButton left;
	private SlottedButton centre;
	private SlottedButton right;
	
	public CoinPanel() {
		super();
		
		setLayout(new GridBagLayout());
		
		GBConstraints gbc = new GBConstraints();
		gbc.weightx = 0.0;
		
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
		boolean res = false;
		
		switch (slot) {
			case 0:
				res = left.isSelected();
				break;
				
			case 1:
				res = centre.isSelected();
				break;
				
			case 2:
				res = right.isSelected();
				break;
		}
		
		_log.logInfo(String.format("CoinPanel.isActive(%d, args) -> %b", slot, res));
		
		return res;
	}
}
