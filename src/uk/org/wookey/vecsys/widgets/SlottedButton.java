package uk.org.wookey.vecsys.widgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JButton;

public class SlottedButton extends JButton {
	private static final long serialVersionUID = 1L;
	
	private static final Color darkRed = new Color(0xb0, 0, 0);
	
	private static final int WIDTH = 48;
	private static final int HEIGHT = 48;
	private static final int SLOT_WIDTH = 8;
	private static final int SLOT_OFFSET = (WIDTH - SLOT_WIDTH) / 2;
	
	//private Shape shape;

	public SlottedButton(String label) {
		super();

	    Dimension size = new Dimension(WIDTH, HEIGHT);
	    
	    setBackground(darkRed);

	    setMinimumSize(size);
	    setMaximumSize(size);
	    setPreferredSize(size);

	    setContentAreaFilled(false);	
	}

	protected void paintComponent(Graphics g) {
		if (getModel().isArmed()) {
			g.setColor(Color.lightGray);
	    } else {
	    	g.setColor(getBackground());
	    }

		g.fillRect(0,  0,  WIDTH-1,  HEIGHT-1);
		g.setColor(Color.BLACK);
		g.fillRect(SLOT_OFFSET, 3, SLOT_WIDTH, HEIGHT-5);

	    super.paintComponent(g);
	}

	protected void paintBorder(Graphics g) {
		g.setColor(getForeground());
		g.drawRect(0,  0, WIDTH-1, HEIGHT-1);
	}

	// Hit detection.
	//public boolean contains(int x, int y) {
	//	// If the button has changed size, 
	//	// make a new shape object.
	//    if (shape == null || !shape.getBounds().equals(getBounds())) {
	//    	shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
	//    }
	//    
	//    return shape.contains(x, y);
	//}
}