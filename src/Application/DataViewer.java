package Application;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class DataViewer extends JPanel {
	public DataViewer() {
		super();
		
		setLayout(new GridLayout(0, 3));
		
		setPreferredSize(new Dimension(100, 100));
		setMinimumSize(new Dimension(100, 100));
		
		clear();
		
		setVisible(true);
	}

	private void clear() {
		removeAllContent();
	}
	
	private void removeAllContent() {
		Component[] componentList = getComponents();

		//Loop through the components
		for(Component c : componentList) {
			remove(c);
		}
	}
	
	public void set(ArrayList<Integer> mem) {
		int i;
		int count = mem.size();
		
		removeAllContent();
		clear();
		
		for (i=0; i<count; i++) {
			if ((i & 1) == 0) {
				add(new JLabel(String.format("%04X", i)));
			}
			
			add(new JLabel(String.format("%04X", mem.get(i).intValue())));
		}
	}
}
