package uk.org.wookey.vecsys.emulator;

import uk.org.wookey.vecsys.utils.Logger;

public class GameStatus {
	private static final Logger _log = new Logger("GameStatus");
	private static boolean running = false;
	
	public static boolean isRunning() {
		return running;
	}
	
	public static void setRunning(boolean newState) {
		running = newState;
		
		_log.logInfo(String.format("running -> %b", running));
	}
}
