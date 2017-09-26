package Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class ApplicationWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private final static Logger _logger = new Logger("ApplicationWindow");
	private MainStatusBar statusBar;
	
	private static final byte[] code = javax.xml.bind.DatatypeConverter.parseHexBinary("ffa30002ff97009000a200000090ff3300e0");
	
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
		cp.add(source, BorderLayout.CENTER);
		
		VectorDisplay disp = new VectorDisplay();		
		cp.add(disp, BorderLayout.CENTER);
		
		statusBar = MainStatusBar.getMainStatusBar(); 
		cp.add(statusBar, BorderLayout.PAGE_END);
		setBackground(new Color(0x00, 0xd0, 0x00));

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
