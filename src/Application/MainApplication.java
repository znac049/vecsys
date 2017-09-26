package Application;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class MainApplication {
	private static ApplicationWindow mainWindow;
	private final Logger _logger = new Logger("MainApplication");

	public static ApplicationWindow getAppWindow() {
		return mainWindow;
	}
	
	public MainApplication() {
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		    	_logger.logInfo("L&F: " + info.getName());
		    	
		        if ("Metal".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		}
		
		//mainWindow.revalidate();
		//mainWindow.repaint();
	}


}
