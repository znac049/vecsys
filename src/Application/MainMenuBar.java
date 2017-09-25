package Application;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;

public class MainMenuBar extends JMenuBar implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final String FILE_TEXT = "File";
	private static final String FILE_OPEN_TEXT = "Open";
	private static final String EXIT_TEXT = "Exit";
	private static final String HELP_TEXT = "Help";
	private static final String ABOUT_TEXT = "About";
	
	private Logger _logger = new Logger("MainMenu");

	public MainMenuBar() {
		JMenu fileMenu = new JMenu(FILE_TEXT);
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		JMenuItem openItem = new JMenuItem(FILE_OPEN_TEXT);
		openItem.setMnemonic(KeyEvent.VK_O);
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
		openItem.addActionListener(this);
		fileMenu.add(openItem);

		JMenuItem exitItem = new JMenuItem(EXIT_TEXT);
		exitItem.setMnemonic(KeyEvent.VK_X);
		exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK));
		exitItem.addActionListener(this);
		fileMenu.add(exitItem);
		add(fileMenu);
				
		JMenu helpMenu = new JMenu(HELP_TEXT);
		helpMenu.setMnemonic(KeyEvent.VK_H);

		JMenuItem aboutItem = new JMenuItem(ABOUT_TEXT);
		aboutItem.addActionListener(this);
		helpMenu.add(aboutItem);
		
		add(helpMenu);
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		
		_logger.logInfo("Main menu click: '" + cmd + "'");
		
		if (cmd.equalsIgnoreCase(EXIT_TEXT)) {
			System.exit(0);
		}
		else if (cmd.equalsIgnoreCase(FILE_OPEN_TEXT)) {
	        JFileChooser fileChooser = new JFileChooser();
	        int returnValue = fileChooser.showOpenDialog(null);
	        if (returnValue == JFileChooser.APPROVE_OPTION) {
	          File selectedFile = fileChooser.getSelectedFile();
	          System.out.println(selectedFile.getName());
	        }
	 
		}
		else if (cmd.equalsIgnoreCase(ABOUT_TEXT)) {
			JDialog about = new AboutWindow();
			about.setVisible(true);
		}
	}
}
