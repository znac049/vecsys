package Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.TrayIcon;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ApplicationWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private final static Logger _logger = new Logger("ApplicationWindow");
	private MainStatusBar statusBar;
	private TrayIcon trayIcon = null;
	
	private static final byte[] code = javax.xml.bind.DatatypeConverter.parseHexBinary("ffa30002ff97009000a200000090ff3300e0");
	
	public ApplicationWindow() {
		super("DVG");
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		int xSize = tk.getScreenSize().width;
		int ySize = tk.getScreenSize().height;
		
		if (xSize > 1360) {
			xSize = 1360;
		}
		
		if (ySize > 768) {
			ySize = 768;
		}
		
		setSize(xSize, ySize);
		setLocation((tk.getScreenSize().width - xSize) / 2, (tk.getScreenSize().height - ySize) / 2);

		setLayout(new BorderLayout());
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		MainMenuBar menu = new MainMenuBar();
		setJMenuBar(menu);
		
		VectorDisplay disp = new VectorDisplay();		
		add(disp, BorderLayout.CENTER);
		
		statusBar = MainStatusBar.getMainStatusBar(); 
		add(statusBar, BorderLayout.PAGE_END);
		setBackground(new Color(0xd0, 0xd0, 0xd0));

		setVisible(true);
	}
	
}
