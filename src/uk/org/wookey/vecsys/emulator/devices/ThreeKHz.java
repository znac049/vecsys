package uk.org.wookey.vecsys.emulator.devices;

import uk.org.wookey.vecsys.emulator.Device;
import uk.org.wookey.vecsys.emulator.GameStatus;
import uk.org.wookey.vecsys.utils.Logger;

public class ThreeKHz extends Device {
	private static final Logger _log = new Logger("3KHz Clock");
	
	private static int clock = 0x80;
	private int ticks;
	
	public ThreeKHz() {
		super("3KHz");
		ticks = 0;
		
		Thread runner = new Thread(new Runnable() {
			@Override
			public void run() {
				long before = System.nanoTime();
				
				while (true) {
					if ((System.nanoTime() - before) > 333000) {
						clock = (clock == 0)?0x80:0;
						before = System.nanoTime();
						//tick();
					}
					
					Thread.yield();
				}
			}
		});
		
		runner.start();
	}
	
	@Override
	public int getByte(int addr, int id) {
		_log.logInfo("-> " + clock);
		
		return clock;
	}

	@Override
	public void setByte(int addr, int val, int id) throws IllegalAccessException {
		_log.logWarn("Write to read only 3KHz clock");
	}
	
	private void tick() {
		ticks++;
		if (ticks >= 3000) {
			ticks = 0;
			_log.logInfo("Heartbeat!");
		}
	}
}
