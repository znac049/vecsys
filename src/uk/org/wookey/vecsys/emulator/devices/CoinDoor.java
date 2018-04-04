package uk.org.wookey.vecsys.emulator.devices;

import com.loomcom.symon.CpuLoomcom;

import uk.org.wookey.vecsys.emulator.Device;
import uk.org.wookey.vecsys.utils.Logger;
import uk.org.wookey.vecsys.widgets.CoinPanel;

public class CoinDoor extends Device {
	private static Logger _log = new Logger();

	private CoinPanel panel;
	
	public CoinDoor() {
		super("Coindoor");
	
		panel = new CoinPanel();
		components.add(panel);
		
		Thread runner = new Thread(new Runnable() {
			@Override
			public void run() {
				long endTime = System.currentTimeMillis() + 50;
				
				while (true) {
					if (System.currentTimeMillis() > endTime) {
						panel.isActive(0);
						panel.isActive(1);
						panel.isActive(2);
						
						endTime = System.nanoTime() + 50;
					}
					
					Thread.yield();
				}
			}
		});
		
		runner.start();

	}
	
	@Override
	public int getByte(int addr, int id) {
		//_log.logInfo(String.format("Checking for coins @%d", addr));
		return panel.isActive(addr)?0x80:0;
	}

	@Override
	public void setByte(int addr, int val, int id) throws IllegalAccessException {
		// TODO Auto-generated method stub		
	}
}
