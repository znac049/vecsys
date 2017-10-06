package Application;

import javax.swing.SwingUtilities;

import DVG.ApplicationWindow;
import Emulator.AsteroidsDeluxe;
import Emulator.RangeException;

public class Application {
	public Application() {
		try {
			AsteroidsDeluxe ad = new AsteroidsDeluxe();
		} catch (RangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Application();
			}		
		});
	}

}
