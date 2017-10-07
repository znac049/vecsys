package uk.org.wookey.vecsys.application;

import java.io.IOException;

import javax.swing.SwingUtilities;

import DVG.ApplicationWindow;
import uk.org.wookey.vecsys.emulator.AsteroidsDeluxe;
import uk.org.wookey.vecsys.emulator.Emulator;
import uk.org.wookey.vecsys.emulator.MainWindow;
import uk.org.wookey.vecsys.emulator.RangeException;

public class Application {
	public Application() {
		Emulator ad;
		
		try {
			ad = new AsteroidsDeluxe();
			MainWindow mainWindow = new MainWindow(ad);
		} catch (RangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
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
