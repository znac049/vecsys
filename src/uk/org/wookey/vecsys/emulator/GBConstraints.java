package uk.org.wookey.vecsys.emulator;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import uk.org.wookey.vecsys.utils.Logger;

public class GBConstraints extends GridBagConstraints {
	private static Logger _log = new Logger("GBConstraints");

	public GBConstraints() {
		super();
		
		clear();
	}

	public void left() {
		if (gridx > 0) {
			gridx--;
		}
	}
	
	public void right() {
		gridx++;
		_log.logInfo(String.format("right() - gridx=%d, gridy=%d", gridx, gridy));
	}
	
	public void up() {
		if (gridy > 0) {
			gridy--;
		}
	}
	
	public void down() {
		gridy++;
	}
	
	public void nl() {
		gridx = 0;
		gridy++;
	}

	public void clear() {		
		gridx = 0;
		gridy = 0;
		
		gridwidth = 1;
		gridheight = 1;
		
		weightx = 0.5;
		weighty = 0.5;
		
		insets = new Insets(2, 2, 2, 2);
		
		fill = GridBagConstraints.BOTH;
		
		anchor = GridBagConstraints.PAGE_START;
	}
}
