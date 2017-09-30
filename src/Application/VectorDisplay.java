package Application;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class VectorDisplay extends JComponent{
	private static final long serialVersionUID = 1L;
	
	private final static int SCREEN_MAX_WIDTH = 512;
	private final static int SCREEN_MAX_HEIGHT = 512;
	
	private Color[] greys;
	private boolean scaleAndCentre = true;
	private int minX, minY, maxX, maxY;

	private static class Line {
	    final int x1; 
	    final int y1;
	    final int x2;
	    final int y2;   
	    final Color color;
	
	    public Line(int x1, int y1, int x2, int y2, Color color) {
	        this.x1 = x1;
	        this.y1 = y1;
	        this.x2 = x2;
	        this.y2 = y2;
	        this.color = color;
	    }               
	}
	
	private final LinkedList<Line> lines = new LinkedList<Line>();
	
	public VectorDisplay() {
		setPreferredSize(new Dimension(SCREEN_MAX_WIDTH, SCREEN_MAX_HEIGHT));
		setMinimumSize(new Dimension(SCREEN_MAX_WIDTH, SCREEN_MAX_HEIGHT));
		
		greys = new Color[16];
		int i;
		int level = 0xff;
		
		minX = minY = 1023;
		maxX = maxY = 0;
		
		for (i=0; i<16; i++) {
			greys[i] = new Color(level, 0, 0);
			level = level - 0x0f;
		}
	}
	
	public void addLine(int x1, int y1, int x2, int y2, int intensity) {
		if (intensity > 0) {
			minX = (x1<minX)?x1:minX;
			minX = (x2<minX)?x2:minX;
			
			minY = (y1<minY)?y1:minY;
			minY = (y2<minY)?y2:minY;
			
			maxX = (x1>maxX)?x1:maxX;
			maxX = (x2>maxX)?x2:maxX;
			
			maxY = (y1>maxY)?y1:maxY;
			maxY = (y2>maxY)?y2:maxY;
			
			x1 = x1/2;
			y1 = y1/2;
			x2 = x2/2;
			y2 = y2/2;
		
		
			lines.add(new Line(x1, y1, x2, y2, greys[intensity]));        
			//System.out.println(String.format("addLine(%d, %d, %d, %d, %d)", x1, y1, x2, y2, intensity));
			repaint();
		}
	}
	
	public void clearScreen() {
	    lines.clear();
		
	    minX = minY = 1023;
		maxX = maxY = 0;

		repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    for (Line line : lines) {
	        g.setColor(line.color);
	        g.drawLine(line.x1, line.y1, line.x2, line.y2);
	    }
	    
	    System.out.println(String.format("min=%d,%d - max=%d,%d", minX, minY, maxX, maxY));
	}
}

