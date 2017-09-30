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
	
	private final static int SCREEN_SIZE = 512;
	
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
		setPreferredSize(new Dimension(SCREEN_SIZE, SCREEN_SIZE));
		setMinimumSize(new Dimension(SCREEN_SIZE, SCREEN_SIZE));
		
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
			
			//x1 = x1/2;
			//y1 = y1/2;
			//x2 = x2/2;
			//y2 = y2/2;
		
		
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
		int deltaX = maxX-minX;
		int deltaY = maxY-minY;
		int range = (deltaX>deltaY)?deltaX:deltaY;
		float scale = (float)SCREEN_SIZE / (float)range;
		
		System.out.println(String.format("range=%d, scale=%f", range, scale));
		
	    super.paintComponent(g);
	    for (Line line : lines) {
	        g.setColor(line.color);
	        
	        int x1 = line.x1;
	        int y1 = line.y1;
	        int x2 = line.x2;
	        int y2 = line.y2;
	        
	        if (scaleAndCentre) {
	        	float fx1 = (float)(x1-minX) * scale;
	        	float fy1 = (float)(y1-minY) * scale;
	        	float fx2 = (float)(x2-minX) * scale;
	        	float fy2 = (float)(y2-minY) * scale;
	        	
	        	x1 = (int)fx1;
	        	y1 = (int)fy1;
	        	
	        	x2 = (int)fx2;
	        	y2 = (int)fy2;
	        }
	        g.drawLine(x1, y1, x2, y2);
	    }
	    
	    System.out.println(String.format("min=%d,%d - max=%d,%d", minX, minY, maxX, maxY));
	}
}

