package Emulator;

public class Device {
	protected String name;
	
	public Device(String devName) {
		name = devName;
	}
	
	public void setName(String devName) {
		name = devName;
	}
	
	public String getName() {
		return name;
	}
}
