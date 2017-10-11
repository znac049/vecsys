package uk.org.wookey.vecsys.emulator;

import java.awt.Container;
import java.awt.Event;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;

import DVG.AboutWindow;
import javafx.scene.control.ToggleButton;
import uk.org.wookey.vecsys.utils.Logger;

public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final Logger _log = new Logger("MainWindow");
	
	private JButton stepButton;
	private JButton goButton;
	private JButton stopButton;
	private Emulator emulator;

	public class MainMenuBar extends JMenuBar implements ActionListener {
		private static final long serialVersionUID = 1L;
		private static final String FILE_TEXT = "File";
		private static final String FILE_OPEN_TEXT = "Open";
		private static final String GAME_TEXT = "Game";
		private static final String GAME_RESET_TEXT = "Reset";
		private static final String EXIT_TEXT = "Exit";
		private static final String HELP_TEXT = "Help";
		private static final String ABOUT_TEXT = "About";
		
		private final Logger _log = new Logger("MainMenu");
		
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
			
			JMenu gameMenu = new JMenu(GAME_TEXT);
			
			JMenuItem resetItem = new JMenuItem(GAME_RESET_TEXT);
			resetItem.setMnemonic(KeyEvent.VK_R);
			resetItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK));
			resetItem.addMouseListener(new NullMouseListener() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					reset();
				}
			});
			gameMenu.add(resetItem);
			
			add(gameMenu);
					
			JMenu helpMenu = new JMenu(HELP_TEXT);
			helpMenu.setMnemonic(KeyEvent.VK_H);

			JMenuItem aboutItem = new JMenuItem(ABOUT_TEXT);
			aboutItem.addActionListener(this);
			helpMenu.add(aboutItem);
			
			add(helpMenu);
		}
		
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			
			//_log.logInfo("Main menu click: '" + cmd + "'");
			
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
		
		this.emulator = emulator;
		GameStatus.setRunning(false);
		
		Container cp = getContentPane();
		GBConstraints gbc = new GBConstraints();

		cp.setLayout(new GridBagLayout());
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		MainMenuBar menu = new MainMenuBar();
		setJMenuBar(menu);
		
		gbc.gridwidth = 3;
		cp.add(emulator.getStatusPanel(), gbc);
		gbc.nl();
		gbc.gridwidth = 1;
		
		stepButton = new JButton("Step");
		cp.add(stepButton, gbc);
		stepButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				//_log.logInfo("Step cliclked");
				emulator.step();
			}
		});
		
		gbc.right();
		goButton = new JButton("Go");
		cp.add(goButton,  gbc);
		goButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				//_log.logInfo("GO cliclked");
				stepButton.setEnabled(false);
				goButton.setEnabled(false);
				stopButton.setEnabled(true);
				GameStatus.setRunning(true);
				emulator.getStatusPanel().turnOff();
			}
		});
		
		gbc.right();
		stopButton = new JButton("Stop");
		cp.add(stopButton,  gbc);
		stopButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				//_log.logInfo("STOP cliclked");
				GameStatus.setRunning(false);
				emulator.getStatusPanel().turnOn();
				stepButton.setEnabled(true);
				goButton.setEnabled(true);
				stopButton.setEnabled(false);
				emulator.getStatusPanel().update();
			}
		});
		stopButton.setEnabled(false);
		
		
		gbc.nl();
		gbc.gridwidth = 3;
		cp.add(emulator.getConfigPanel(), gbc);
		
		gbc.nl();
		cp.add(emulator.getControlsPanel(), gbc);
		
		Thread runner = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (GameStatus.isRunning()) {
						emulator.step();
					}
					
					Thread.yield();
				}
			}
		});
		
		runner.start();
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void reset() {
		emulator.reset();
		stopButton.setEnabled(false);
		goButton.setEnabled(true);
		stepButton.setEnabled(true);
	}
}
