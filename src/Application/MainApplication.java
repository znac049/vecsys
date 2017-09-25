package Application;

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
		    // If Nimbus is not available, you can set the GUI to another look and feel.
		}
		
		mainWindow = new ApplicationWindow();
		
		// Timers
		(new Thread(TimerProcess.getTimerProcess())).start();
		
		TimedEvent ev = new TimedEvent();
		ev.setRepeat(5000, 5);
		//TimerProcess.queueTimerEvent(ev);
		
		ev = new TimedEvent();
		ev.setRepeat(3000);
		//TimerProcess.queueTimerEvent(ev);

		ev = new TimedEvent();
		ev.setRepeat(17000);		
		//TimerProcess.queueTimerEvent(ev);
		
		mainWindow.revalidate();
		mainWindow.repaint();
	}

	public static void main(String[] args) {
		new MainApplication();
	}
}
