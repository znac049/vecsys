package Application;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class ApplicationWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private final static Logger _logger = new Logger("ApplicationWindow");
	private MainStatusBar statusBar;
	private TrayIcon trayIcon = null;
	
	public ApplicationWindow() {
		super("IC");
		
		if (SystemTray.isSupported()) {
			setupSystemTray();
		}
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		
		gbc.insets = new Insets(2, 2, 2, 2);
		
		gbc.fill = GridBagConstraints.BOTH;
		
		gbc.anchor = GridBagConstraints.PAGE_START;
		
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

		setLayout(new GridBagLayout());
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		MainMenuBar menu = new MainMenuBar();
		setJMenuBar(menu);
		
		gbc.gridy = 1;
		gbc.weighty = 1.0;
		
		statusBar = MainStatusBar.getMainStatusBar(); 
		JPanel outer = new JPanel();
		outer.setLayout(new BorderLayout());
		outer.add(statusBar, BorderLayout.EAST);
		outer.setBackground(new Color(0xd0, 0xd0, 0xd0));
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		//gbc.gridwidth = 3;
		add(outer, gbc);
		
		setVisible(true);
	}
	
	private void setupSystemTray() {
		SystemTray tray = SystemTray.getSystemTray();
		Image image = (new ImageIcon("images/bulb.gif", "Zzz")).getImage();
		trayIcon = new TrayIcon(image);
		
		trayIcon.setToolTip("DVG Tool");
		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	public TrayIcon getTrayIcon() {
		return trayIcon;
	}
}
