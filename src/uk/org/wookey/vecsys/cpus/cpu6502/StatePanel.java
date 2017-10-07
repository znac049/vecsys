package uk.org.wookey.vecsys.cpus.cpu6502;

import java.awt.Color;

import uk.org.wookey.vecsys.cpus.STATEPanel;
import uk.org.wookey.vecsys.emulator.ColouredLabel;
import uk.org.wookey.vecsys.emulator.GBConstraints;

public class StatePanel extends STATEPanel {
	private static final long serialVersionUID = 1L;

	public StatePanel() {
		super();
		
		GBConstraints gbc = new GBConstraints(7,2);
		
		add(new ColouredLabel("PC", Color.BLUE), gbc);
		gbc.right();
		
		add(new ColouredLabel("A", Color.BLUE), gbc);
		gbc.right();
		
		add(new ColouredLabel("X", Color.BLUE), gbc);
		gbc.right();
		
		add(new ColouredLabel("Y", Color.BLUE), gbc);
		gbc.right();
		
		add(new ColouredLabel("SR", Color.BLUE), gbc);
		gbc.right();
		
		add(new ColouredLabel("SP", Color.BLUE), gbc);
		gbc.right();
		
		add(new ColouredLabel("NV-BDIZC", Color.BLUE), gbc);
	}
}
