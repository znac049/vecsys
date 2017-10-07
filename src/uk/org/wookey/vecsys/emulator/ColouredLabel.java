package uk.org.wookey.vecsys.emulator;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;

public class ColouredLabel extends JLabel {
	private static final long serialVersionUID = 1L;
	private static final Font mono = new Font("Monospaced", Font.PLAIN, 16);
	
	public ColouredLabel(String text, Color colour) {
		super(text);
		setForeground(colour);
		setFont(mono);
	}
}
