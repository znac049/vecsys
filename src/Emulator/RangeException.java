package Emulator;

public class RangeException extends Exception {
	public RangeException(String msg) {
		super(msg);
	}
	
	public RangeException(Throwable cause) {
		super(cause);
	}
}
