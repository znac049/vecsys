package Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Event;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rtextarea.RTextScrollPane;

public class ApplicationWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private final static Logger _logger = new Logger("ApplicationWindow");
	private MainStatusBar statusBar;
	private VectorEngine engine;
	
	private static final byte[] code = javax.xml.bind.DatatypeConverter.parseHexBinary("ffa30002ff97009000a200000090ff3300e0");

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
		          engine.set(code);
		        }
		 
			}
			else if (cmd.equalsIgnoreCase(ABOUT_TEXT)) {
				JDialog about = new AboutWindow();
				about.setVisible(true);
			}
		}
	}

	public ApplicationWindow() {
		super("DVG");
		
		_logger.logMsg("And we're off!");
		
		Container cp = getContentPane();

		setLayout(new BorderLayout());
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		MainMenuBar menu = new MainMenuBar();
		setJMenuBar(menu);
		
		DataViewer viewer = new DataViewer();
		cp.add(viewer, BorderLayout.LINE_START);
		
		SourceViewer source = new SourceViewer();
		source.addMouseListener(new MouseAdapter() { 
            public void mouseClicked(MouseEvent me) { 
            	System.out.println("Clicked!");
            	System.out.println(me);
            	
            	int line;
            	
				try {
					line = source.getLineOfOffset(source.getCaretPosition());
	            	System.out.println("Line: " + line);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
           } 
		});
		RTextScrollPane sp = new RTextScrollPane(source);
		cp.add(sp, BorderLayout.CENTER);
		
		VectorDisplay disp = new VectorDisplay();		
		cp.add(disp, BorderLayout.LINE_END);
		
		statusBar = MainStatusBar.getMainStatusBar(); 
		cp.add(statusBar, BorderLayout.PAGE_END);
		setBackground(new Color(0x00, 0xd0, 0x00));
		
		engine = new VectorEngine(viewer, source, disp);
		engine.set(code);
		engine.display(0);

		setTitle("DVG Tool");
		pack();
		setLocationRelativeTo(null);
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new ApplicationWindow().setVisible(true);
			}	
		});

	}	
}
