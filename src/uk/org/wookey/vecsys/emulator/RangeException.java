package uk.org.wookey.vecsys.emulator;

public class RangeException extends Exception {
	public RangeException(String msg) {
		super(msg);
	}
	
	public RangeException(Throwable cause) {
		super(cause);
	}
}
