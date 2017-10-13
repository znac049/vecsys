package uk.org.wookey.vecsys.emulator;

import java.util.ArrayList;

import javax.swing.JComponent;

public abstract class Device {
	protected String name;
	protected ArrayList<JComponent> components;
	
	public Device(String devName) {
		name = devName;
		
		components = new ArrayList<JComponent>();
	}
	
	public void setName(String devName) {
		name = devName;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean hasGUIComponents() {
		return true;
	}
	
	public JComponent getComponent() {
		return getComponent(0);
	}
	
	public JComponent getComponent(int componentNumber) {
		if (componentNumber < components.size()) {
			return components.get(componentNumber);
		}
		
		return null;
	}
	
	public abstract int getByte(int addr, int id);
	public abstract void setByte(int addr, int val, int id) throws IllegalAccessException;
}
