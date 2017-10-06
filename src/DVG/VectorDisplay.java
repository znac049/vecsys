package DVG;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class VectorDisplay extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final static int BORDER_SIZE = 16;
	private final static int INITIAL_SCREEN_WIDTH = 640 + BORDER_SIZE + BORDER_SIZE;
	private final static int INITIAL_SCREEN_HEIGHT = 480 + BORDER_SIZE + BORDER_SIZE;
	
	private Color[] greys;
	private boolean scaleAndCentre = false;
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
		super();
		
		setBackground(Color.black);
		
		setPreferredSize(new Dimension(INITIAL_SCREEN_WIDTH, INITIAL_SCREEN_HEIGHT));
		setMinimumSize(new Dimension(INITIAL_SCREEN_WIDTH, INITIAL_SCREEN_HEIGHT));
		
		greys = new Color[16];
		int i;
		int level = 0xff;
		
		minX = minY = 1023;
		maxX = maxY = 0;
		
		for (i=0; i<16; i++) {
			greys[i] = new Color(0, level, 0);
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
			
		
			lines.add(new Line(x1, y1, x2, y2, greys[intensity]));        
			System.out.println(String.format("addLine(%d, %d, %d, %d, %d)", x1, y1, x2, y2, intensity));
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
		int panelWidth = getWidth()-BORDER_SIZE*2;
		int panelHeight = getHeight()-BORDER_SIZE*2;
		int width;
		int height;
		float scaleX;
		float scaleY;
		
		System.out.println(String.format("Panel size=%dx%d", panelWidth, panelHeight));
		
		// Adjust the width and height so as to maintain a nice 4:3 ratio
		
		height = panelHeight;
		width = panelWidth;
		
		System.out.println(String.format("Adjusted size=%dx%d",  width, height));
		
	    super.paintComponent(g);

	    // Mess with the coordinate system and scaling to put the display in the center of the panel
		g.translate(BORDER_SIZE,  BORDER_SIZE+height);
		
		if (scaleAndCentre) {
			// If it's too much of a zoon, do something special
			if (deltaX < 200) {
				int diffX = 200-deltaX;
				
				minX = minX - (diffX/2);
				deltaX = 200;
			}
			
			if (deltaY < 200) {
				int diffY = 200-deltaY;
				
				minY = minY - (diffY/2);
				deltaY = 200;
			}
			
			scaleX = ((float) width) / ((float) Integer.max(deltaX, 100));
			scaleY = ((float) height) / ((float) Integer.max(deltaY, 100));
		}
		else {
			scaleX = ((float) width) / ((float) 1024);
			scaleY = ((float) height) / ((float) 1024);
		}
		
		System.out.println(String.format("Display area=%dx%d, scale set to %f,%f, deltas=%d,%d", width, height, scaleX, scaleY, deltaX, deltaY));
		
		Graphics2D g2 = (Graphics2D) g;		
		g2.scale(scaleX, -scaleY);
		g2.setStroke(new BasicStroke((float) 0.5));
		
		g.setColor(Color.cyan);
		g.drawLine(0, 0, 1023, 0);
		g.drawLine(1023, 0, 1023, 1023);
		g.drawLine(1023, 1023, 0, 1023);
		g.drawLine(0, 1023, 0, 0);
		
        if (scaleAndCentre) {
        	for (Line line : lines) {
        		g.setColor(line.color);
        		g.drawLine(line.x1-minX, line.y1-minY, line.x2-minX, line.y2-minY);
        	}
        }
        else {
        	for (Line line : lines) {
        		g.setColor(line.color);
        		g.drawLine(line.x1, line.y1, line.x2, line.y2);
        	}
        }
	    
	    //System.out.println(String.format("min=%d,%d - max=%d,%d", minX, minY, maxX, maxY));
	}
}

