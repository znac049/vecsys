package uk.org.wookey.vecsys.emulator;

import java.awt.Container;
import java.awt.Event;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import DVG.AboutWindow;
import uk.org.wookey.vecsys.utils.Logger;

public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final Logger _logger = new Logger("MainWindow");

	public class MainMenuBar extends JMenuBar implements ActionListener {
		private static final long serialVersionUID = 1L;
		private static final String FILE_TEXT = "File";
		private static final String FILE_OPEN_TEXT = "Open";
		private static final String EXIT_TEXT = "Exit";
		private static final String HELP_TEXT = "Help";
		private static final String ABOUT_TEXT = "About";
		
		private final Logger _logger = new Logger("MainMenu");
		
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
			}
			else if (cmd.equalsIgnoreCase(ABOUT_TEXT)) {
				JDialog about = new AboutWindow();
				about.setVisible(true);
			}
		}
	}

	public MainWindow(Emulator emulator) {
		super("VecSys");
		
		_logger.logMsg("And we're off!");
		
		Container cp = getContentPane();
		GBConstraints gbc = new GBConstraints();

		cp.setLayout(new GridBagLayout());
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		MainMenuBar menu = new MainMenuBar();
		setJMenuBar(menu);
		
		cp.add(emulator.getStatePanel(), gbc);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
