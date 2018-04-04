package uk.org.wookey.vecsys.widgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

import uk.org.wookey.vecsys.emulator.NullMouseListener;

public class SlottedButton extends JButton {
	private static final long serialVersionUID = 1L;
	
	private static final Color darkRed = new Color(0xb0, 0, 0);
	
	private static final int WIDTH = 48;
	private static final int HEIGHT = 48;
	private static final int SLOT_WIDTH = 8;
	private static final int SLOT_OFFSET = (WIDTH - SLOT_WIDTH) / 2;
	
	private long mouseClickTime;
	private boolean clickActive;
	
	//private Shape shape;

	public SlottedButton(String label) {
		super();

	    Dimension size = new Dimension(WIDTH, HEIGHT);
	    
	    clickActive = false;
	    mouseClickTime = 0;
	    
	    setBackground(darkRed);

	    setMinimumSize(size);
	    setMaximumSize(size);
	    setPreferredSize(size);

	    setContentAreaFilled(false);
	    
	    addMouseListener(new NullMouseListener() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				mouseClickTime = System.currentTimeMillis() + 500;
				clickActive = true;
			}
	    });
	}
	
	public boolean isActive() {
		if (clickActive && mouseClickTime < System.currentTimeMillis()) {
			clickActive = false;
			repaint();
		}
		return false;
	}

	protected void paintComponent(Graphics g) {
		//if (getModel().isArmed()) {
		if (clickActive) {
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
}