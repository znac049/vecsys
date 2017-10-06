package Emulator;

public class AsteroidsDeluxe extends Emulator {
	private Bus bus;
	
	public AsteroidsDeluxe() throws RangeException {
		bus = new Bus(16);
		bus.setAddressMask(0x7fff);
	}
}
