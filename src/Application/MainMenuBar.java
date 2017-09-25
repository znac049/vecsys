package Application;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class MainMenuBar extends JMenuBar implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final String FILE_TEXT = "File";
	private static final String EXIT_TEXT = "Exit";
	private static final String TOOLS_TEXT = "Tools";
	private static final String WORLDS_TEXT = "Worlds";
	private static final String HIGHLIGHTS_TEXT = "Highlights";
	private static final String SETTINGS_TEXT = "Settings";
	private static final String MACROS_TEXT = "Macros";
	private static final String KEYMAP_TEXT = "Key Map";
	private static final String LOGSESSION_TEXT = "Log Session";
	private static final String HELP_TEXT = "Help";
	private static final String ABOUT_TEXT = "About";
	
	private Logger _logger = new Logger("MainMenu");
	private JTabbedPane _tabs = null;

	public MainMenuBar() {
		JMenu fileMenu = new JMenu(FILE_TEXT);
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
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
	
	public void addTabs(JTabbedPane tabs) {
		_tabs = tabs;
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		
		_logger.logInfo("Main menu click: '" + cmd + "'");
		
		if (cmd.equalsIgnoreCase(EXIT_TEXT)) {
			System.exit(0);
		}
		else if (cmd.equalsIgnoreCase(ABOUT_TEXT)) {
			JDialog about = new AboutWindow();
			about.setVisible(true);
		}
	}
}
