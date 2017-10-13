package uk.org.wookey.vecsys.widgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import javax.swing.JButton;

import uk.org.wookey.vecsys.utils.Logger;

public class LEDButton extends JButton {
	private static final Logger _log = new Logger("LEDButton");
	private Shape shape;
	private boolean ledActive;

	public LEDButton(String label) {
		super();

		ledActive = false;
		
	    Dimension size = new Dimension(30, 30);
	    setMinimumSize(size);
	    setMaximumSize(size);
	    setPreferredSize(size);

	    setContentAreaFilled(false);	
	}

	protected void paintComponent(Graphics g) {
		if (getModel().isArmed()) {
			if (ledActive) {
				g.setColor(Color.PINK);
			}
			else {
				g.setColor(Color.lightGray);
			}
	    } else {
	    	if (ledActive) {
	    		g.setColor(Color.RED);
	    	}
	    	else {
	    		g.setColor(getBackground());
	    	}
	    }
		
	    g.fillOval(5, 5, getSize().width-11, getSize().height-11);

	    super.paintComponent(g);
	}

	protected void paintBorder(Graphics g) {
		g.setColor(getForeground());
	    g.drawOval(5, 5, getSize().width-11, getSize().height-11);
	    g.drawOval(0, 0, getSize().width-1, getSize().height-1);
	}

	// Hit detection.
	public boolean contains(int x, int y) {
		// If the button has changed size, 
		// make a new shape object.
	    if (shape == null || !shape.getBounds().equals(getBounds())) {
	    	shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
	    }
	    
	    return shape.contains(x, y);
	}
	
	public void setLED(boolean on) {
		ledActive = on;
		
		_log.logInfo("LED " + (on?"ON":"OFF"));
		
		repaint();
	}
}