package uk.org.wookey.vecsys.emulator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import javax.swing.JButton;

public class RoundButton extends JButton {
	private Shape shape;

	public RoundButton(String label) {
		super(label);

	    Dimension size = getPreferredSize();
	    size.width = size.height = Math.max(size.width, size.height);
	    setPreferredSize(size);

	    setContentAreaFilled(false);	
	}

	protected void paintComponent(Graphics g) {
		if (getModel().isArmed()) {
			g.setColor(Color.lightGray);
	    } else {
	    	g.setColor(getBackground());
	    }
		
	    g.fillOval(0, 0, getSize().width-1, getSize().height-1);

	    super.paintComponent(g);
	}

	protected void paintBorder(Graphics g) {
		g.setColor(getForeground());
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
}