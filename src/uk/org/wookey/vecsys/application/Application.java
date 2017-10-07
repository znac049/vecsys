package uk.org.wookey.vecsys.application;

import javax.swing.SwingUtilities;

import DVG.ApplicationWindow;
import uk.org.wookey.vecsys.emulator.AsteroidsDeluxe;
import uk.org.wookey.vecsys.emulator.RangeException;

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
