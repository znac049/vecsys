package Application;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class DataViewer extends JPanel {
	public DataViewer() {
		super();
		
		setLayout(new GridLayout(0, 3));
		
		setPreferredSize(new Dimension(100, 100));
		setMinimumSize(new Dimension(100, 100));
		
		setVisible(true);
	}

	public void clear() {
		removeAllContent();
		
		add(new JLabel("0000:"));
	}
	
	private void removeAllContent() {
		Component[] componentList = getComponents();

		//Loop through the components
		for(Component c : componentList) {
			remove(c);
		}
	}
	
	public void set(byte[] bytes) {
		removeAllContent();
		
		add(new JLabel("0000:"));
	}
}
