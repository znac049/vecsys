package uk.org.wookey.vecsys.widgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JToggleButton;

import uk.org.wookey.vecsys.utils.Logger;

public class SingleDIPSwitch extends JToggleButton {
	private static Logger _log = new Logger("SingleDIPSwitch");
	
	public static final int DEFAULT_WIDTH = 16;
	public static final int DEFAULT_HEIGHT = 40;
	
	public SingleDIPSwitch() {
		super();
		
		setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		setForeground(Color.black);
		setBackground(Color.lightGray);
	}
	
	protected void paintComponent(Graphics g) {
		Dimension size = getSize();
		int slotX = size.width/4;
		int slotWidth = size.width/2;
		int slotY = size.height/10;
		int slotHeight = slotY*8;

		g.setColor(getBackground());
		g.fillRect(0, 0, size.width-1, size.height-1);
		
		g.setColor(getForeground());
		//g.setColor(Color.blue);
		if (this.isSelected()) {
			g.fillRect(slotX,  slotY,  slotWidth, slotHeight/2);
	    } else {
	    	g.fillRect(slotX,  slotY*4,  slotWidth,  slotHeight/2);
	    }

		g.setColor(Color.black);
		g.drawRect(slotX,  slotY,  slotWidth,  slotHeight);
	}

	protected void paintBorder(Graphics g) {
		g.setColor(getForeground());
		Dimension size = getSize();
		
		g.drawRect(0,  0,  size.width-1,  size.height-1);
	}
}
