package uk.org.wookey.vecsys.emulator;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import uk.org.wookey.vecsys.utils.Logger;

public class GBConstraints extends GridBagConstraints {
	private static Logger _log = new Logger("GBConstraints");

	public GBConstraints(int width, int height) {
		super();
		
		gridx = 0;
		gridy = 0;
		
		gridwidth = width;
		gridheight = height;
		
		weightx = 1.0;
		weighty = 0.0;
		
		insets = new Insets(2, 2, 2, 2);
		
		fill = GridBagConstraints.BOTH;
		
		anchor = GridBagConstraints.PAGE_START;
	}

	public GBConstraints() {
		this(1, 1);
	}
	
	public void left() {
		if (gridx > 0) {
			gridx--;
		}
	}
	
	public void right() {
		if (gridx < (gridwidth-1)) {
			gridx++;
			_log.logInfo("right()");
		}
		else {
			_log.logError("Bump x!");
		}
	}
}
