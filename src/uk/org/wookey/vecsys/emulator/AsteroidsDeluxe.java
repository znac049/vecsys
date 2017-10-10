package uk.org.wookey.vecsys.emulator;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JPanel;

import uk.org.wookey.vecsys.cpus.Cpu;
import uk.org.wookey.vecsys.cpus.cpu6502.Cpu6502;
import uk.org.wookey.vecsys.cpus.cpu6x09.Cpu6x09;
import uk.org.wookey.vecsys.utils.Logger;

public class AsteroidsDeluxe extends Emulator {
	private static Logger _log = new Logger("AD-Game");
	
	private JPanel controlPanel;
	private JPanel configPanel;

	public AsteroidsDeluxe() throws RangeException, IOException {
		bus = new Bus(16);
		bus.setAddressMask(0x7fff);
		bus.setLittleEndian();
		
		_log.logInfo("Populating bus");
		
		MemoryDevice gameRom = new MemoryDevice(8192);
		gameRom.loadFile("Code/ad-v3.bin", 0);
		gameRom.setWriteable(false);
		gameRom.setName("Game ROM");
		bus.attach(0x6000, 0x7fff, gameRom);
		
		MemoryDevice gameRam = new MemoryDevice(1024);
		gameRam.setName("Game RAM");
		bus.attach(0, 1024, gameRam);
		
		DVG dvg = new DVG();
		bus.attach(0x2001, dvg.getHaltedFlagDevice());
		bus.attach(0x3000, dvg.getStartVGDevice());
		bus.attach(0x3800, dvg.getResetVGDevice());
		bus.attach(0x4000, 0x57ff, dvg.getVectorMemory());
		
		ButtonDevice leftButton = new ButtonDevice("Q", 0x80, 0, KeyEvent.VK_Q); 
		bus.attach(0x2407, leftButton);
		
		ButtonDevice rightButton = new ButtonDevice("D", 0x80, 0, KeyEvent.VK_D); 
		bus.attach(0x2406, rightButton);
		
		ButtonDevice shieldButton = new ButtonDevice("<SPACE>", 0x80, 0, KeyEvent.VK_SPACE); 
		bus.attach(0x2003, shieldButton);
		
		ButtonDevice fireButton = new ButtonDevice("K", 0x80, 0, KeyEvent.VK_K);
		bus.attach(0x2004, fireButton);
		
		ButtonDevice thrustButton = new ButtonDevice("P", 0x80, 0, KeyEvent.VK_P);
		bus.attach(0x2405, thrustButton);
		
		SwitchDevice selfTest = new SwitchDevice("SelfTest", 0x80, 0);
		bus.attach(0x2007, selfTest);
		
		cpu = new Cpu6502();
		cpu.setBus(bus);
		cpu.reset();
		
		if (cpu.isBigEndian()) {
			bus.setBigEndian();
		}
		else {
			bus.setLittleEndian();
		}

		bus.dump();
		
		GBConstraints gbc = new GBConstraints();
		
		gbc.weightx = 0.0;
		
		configPanel = new JPanel();
		configPanel.setMinimumSize(new Dimension(300, 100));
		configPanel.setLayout(new GridBagLayout());

		configPanel.add(selfTest.getComponent(), gbc);
		
		controlPanel = new JPanel();
		controlPanel.setMinimumSize(new Dimension(200, 50));
		controlPanel.setLayout(new GridBagLayout());
		
		gbc.clear();
		controlPanel.add(leftButton.getComponent(),  gbc);
		gbc.right();
		
		controlPanel.add(rightButton.getComponent(),  gbc);
		gbc.right();
		gbc.right();
		
		controlPanel.add(fireButton.getComponent(), gbc);
		gbc.right();
		
		controlPanel.add(thrustButton.getComponent(), gbc);
		gbc.nl();
		
		gbc.gridx = 2;
		controlPanel.add(shieldButton.getComponent(), gbc);		
	}

	@Override
	public JPanel getConfigPanel() {
		return configPanel;
	}

	@Override
	public JPanel getControlsPanel() {
		return controlPanel;
	}
}
