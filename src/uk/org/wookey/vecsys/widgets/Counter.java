package uk.org.wookey.vecsys.widgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import uk.org.wookey.vecsys.emulator.GBConstraints;
import uk.org.wookey.vecsys.emulator.TTLabel;
import uk.org.wookey.vecsys.utils.Logger;

public class Counter extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final Logger _log = new Logger();
	
	private static final String[] digChars = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
	
	private int count;
	private int nDigits;
	private ArrayList<TTLabel> digits;
	
	private Preferences prefs;
	private String prefName;
	
	public Counter(String name, Preferences node, int nDigits) {
		super();
		
		this.nDigits = nDigits;
		prefs = node;
		prefName = name;
		
		setLayout(new GridBagLayout());
		setBackground(Color.darkGray);
		
		JPanel innerPanel = new JPanel();
		innerPanel.setBackground(Color.darkGray);
		
		final Border border = BorderFactory.createLineBorder(Color.white);
		innerPanel.setBorder(border);

		digits = new ArrayList<TTLabel>();
		for (int i=0; i<nDigits; i++) {
			digits.add(new TTLabel("-", Color.white));
		}

		for (int i=0; i<nDigits; i++) {
			innerPanel.add(digits.get(i));
		}
		
		GBConstraints gbc = new GBConstraints();
				
		gbc.insets = new Insets(20, 20, 20, 20);
		add(innerPanel, gbc);
		
		setCounter(prefs.getInt(prefName, 0));
	}

	private void setCounter(int count) {
		_log.logInfo(String.format("setCounter(%d)", count));
		_log.logInfo(prefs.toString());
		
		this.count = count;
		
		for (int i=nDigits-1; i>=0; i--) {
			int digit = count % 10;
			digits.get(i).setText(digChars[digit]);
			count = count / 10;
		}
		
		_log.logInfo(String.format("save counter %s: %d", prefName, count));
		prefs.putInt(prefName,  this.count);
	}
	
	public void increment() {
		setCounter(count+1);
	}
}
