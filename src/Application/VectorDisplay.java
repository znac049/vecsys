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
		
		setBackground(Color.black);
		
		addLine(0, SCREEN_MAX_HEIGHT-1, SCREEN_MAX_WIDTH-1, 0);
	}
	
	public void addLine(int x1, int y1, int x2, int y2) {
	    addLine(x1, y1, x2, y2, Color.white);
	}
	
	public void addLine(int x1, int y1, int x2, int y2, Color color) {
	    lines.add(new Line(x1, y1, x2, y2, color));        
	    repaint();
	}
	
	public void clearLines() {
	    lines.clear();
	    repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    for (Line line : lines) {
	        g.setColor(line.color);
	        g.drawLine(line.x1, line.y1, line.x2, line.y2);
	    }
	}
}

