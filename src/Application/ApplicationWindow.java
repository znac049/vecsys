package Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;

import org.fife.ui.rtextarea.RTextScrollPane;

public class ApplicationWindow extends JFrame implements CaretListener, MouseListener, KeyListener {
	private static final long serialVersionUID = 1L;
	private final static Logger _logger = new Logger("ApplicationWindow");
	private MainStatusBar statusBar;
	private VectorEngine engine;
	
	private static final byte[] code = javax.xml.bind.DatatypeConverter.parseHexBinary("ffa30002ff97009000a200000090ff3300e0");

	private JFileChooser fileChooser;
	private DataViewer viewer;
	private SourceViewer source;
	private int currentAddress;
	
	private boolean displayActive;

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
		        int returnValue = fileChooser.showOpenDialog(null);
		        if (returnValue == JFileChooser.APPROVE_OPTION) {
		        	File selectedFile = fileChooser.getSelectedFile();
		        	System.out.println(selectedFile.getPath());
		        	Path path = Paths.get(selectedFile.getAbsolutePath());
		        	try {
		        		displayActive = false;
		        		engine.set(Files.readAllBytes(path), 0x00400);
		        		displayActive = true;
		        		engine.go(0);
		        	} catch (IOException e1) {
		        		// TODO Auto-generated catch block
		        		e1.printStackTrace();
		        	}
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

		String path = ".";
		try {
			path = new File(".").getCanonicalPath();
			System.out.println("PATH=" + path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		fileChooser = new JFileChooser(path);
		
		viewer = new DataViewer();
		viewer.addCaretListener(this);
		viewer.addKeyListener(this);
		viewer.addMouseListener(this); 
		RTextScrollPane vsp = new RTextScrollPane(viewer);
		cp.add(vsp, BorderLayout.LINE_START);
		
		source = new SourceViewer();
		source.setTabSize(8);
		RTextScrollPane sp = new RTextScrollPane(source);
		cp.add(sp, BorderLayout.CENTER);
		
		VectorDisplay disp = new VectorDisplay();		
		cp.add(disp, BorderLayout.LINE_END);
		
		statusBar = MainStatusBar.getMainStatusBar(); 
		cp.add(statusBar, BorderLayout.PAGE_END);
		setBackground(new Color(0x00, 0xd0, 0x00));
		
		currentAddress = -1;
		engine = new VectorEngine(viewer, source, disp);
		displayActive = false;
		engine.set(code, 0);
		displayActive = true;
		
		engine.go(0);
		
		setTitle("DVG Tool");
		pack();
		setLocationRelativeTo(null);
	}
	
	private void movementOccurred() {
		int line = viewer.getCaretLineNumber();
    	int addr = line * 4;
			
    	try {
			int start = viewer.getLineStartOffset(line);
	    	int end = viewer.getLineEndOffset(line);
	    	String text = viewer.getText(start, end-start);
	    	int caret = viewer.getCaretOffsetFromLineStart();
	    	
	    	System.out.println(String.format("Line: %s, start=%d, end=%d, text='%s', caret=%d", line, start, end, text, caret));
	    	
			if (caret > 0) {	
				for (int i=caret-1; i>=0; i--) {
					char ch = text.charAt(i);
					
					//System.out.print(String.format("c='%c' ", ch));
					if (ch == ',') {
						addr++;
					}
				}

				System.out.println(String.format("addr=%04X, ch=%c", addr, text.charAt(caret-1)));

		    	if (displayActive && (addr != currentAddress)) {
		    		currentAddress = addr;
	            	
	    	    	engine.disassemble(currentAddress);
	    	    	engine.go(currentAddress);
		    	}
			}
	    	
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		//System.out.println("Caret!");
		movementOccurred();		
	}	

	@Override
	public void mouseClicked(MouseEvent arg0) {
		System.out.println("Click!");;
		movementOccurred();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		System.out.println("Press!");
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		System.out.println("Key!");
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new ApplicationWindow().setVisible(true);
			}		
		});
	}
}
