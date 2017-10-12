package uk.org.wookey.vecsys.emulator;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.loomcom.symon.CpuLoomcom;

import uk.org.wookey.vecsys.cpus.StatusPanel;
import uk.org.wookey.vecsys.emulator.devices.ButtonDevice;
import uk.org.wookey.vecsys.emulator.devices.CoinCounter;
import uk.org.wookey.vecsys.emulator.devices.CoinDoor;
import uk.org.wookey.vecsys.emulator.devices.DVG;
import uk.org.wookey.vecsys.emulator.devices.EaromDevice;
import uk.org.wookey.vecsys.emulator.devices.MemoryDevice;
import uk.org.wookey.vecsys.emulator.devices.PlayerButtons;
import uk.org.wookey.vecsys.emulator.devices.Pokey;
import uk.org.wookey.vecsys.emulator.devices.SwitchDevice;
import uk.org.wookey.vecsys.emulator.devices.ThreeKHz;
import uk.org.wookey.vecsys.emulator.devices.WatchDogDevice;
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
		bus.attach(0x2002, dvg.getHaltedFlagDevice());
		bus.attach(0x3000, dvg.getStartVGDevice());
		bus.attach(0x3800, dvg.getResetVGDevice());
		bus.attach(0x4000, 0x57ff, dvg.getVectorMemory());
		
		ButtonDevice leftButton = new ButtonDevice("Q", 0x80, 0, KeyEvent.VK_Q); 
		bus.attach(0x2407, leftButton);
		
		ButtonDevice rightButton = new ButtonDevice("D", 0x80, 0, KeyEvent.VK_D); 
		bus.attach(0x2406, rightButton);
		
		ButtonDevice shieldButton = new ButtonDevice("SP", 0x80, 0, KeyEvent.VK_SPACE); 
		bus.attach(0x2003, shieldButton);
		
		ButtonDevice fireButton = new ButtonDevice("K", 0x80, 0, KeyEvent.VK_K);
		bus.attach(0x2004, fireButton);
		
		ButtonDevice thrustButton = new ButtonDevice("P", 0x80, 0, KeyEvent.VK_P);
		bus.attach(0x2405, thrustButton);
		
		SwitchDevice selfTest = new SwitchDevice("SelfTest", 0x80, 0);
		bus.attach(0x2007, selfTest);
		
		Pokey pokey = new Pokey();
		bus.attach(0x2c00, 0x2c0f, pokey);
		
		WatchDogDevice watchdog = new WatchDogDevice();
		bus.attach(0x3400, watchdog);
		
		EaromDevice earom = new EaromDevice();
		bus.attach(0x2c40, 0x2c7f, earom, 0);
		bus.attach(0x3200, 0x323f, earom, 1);
		
		ThreeKHz threeK = new ThreeKHz();
		bus.attach(0x2001, threeK);
		
		PlayerButtons playerButtons = new PlayerButtons();
		bus.attach(0x2403, 0x2404, playerButtons, 0);
		bus.attach(0x3c00, 0x3c01, playerButtons, 1);
		
		CoinCounter coinCounters = new CoinCounter();
		bus.attach(0x3c05, 0x3c07, coinCounters);
		
		CoinDoor coinDoor = new CoinDoor();
		bus.attach(0x2400, 0x2401, coinDoor);
		
		cpu = new CpuLoomcom();
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
        final TitledBorder tb =
                BorderFactory.createTitledBorder("Configuration");

		configPanel.setBorder(tb);
		configPanel.setMinimumSize(new Dimension(300, 100));
		configPanel.setLayout(new GridBagLayout());

		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 0.0;
		configPanel.add(playerButtons.getPlayer1Button(), gbc);
		gbc.right();
		
		configPanel.add(playerButtons.getPlayer2Button(),  gbc);
		gbc.right();
		
		configPanel.add(selfTest.getComponent(), gbc);
		
		controlPanel = new JPanel();
		controlPanel.setMinimumSize(new Dimension(200, 50));
		controlPanel.setLayout(new GridBagLayout());
		
		gbc.reset();
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
		
		gbc.nl();
		gbc.right();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weightx = 0.0;
		gbc.gridwidth = 3;
		
		controlPanel.add(coinDoor.getPanel(), gbc);
	}

	@Override
	public JPanel getConfigPanel() {
		return configPanel;
	}

	@Override
	public JPanel getControlsPanel() {
		return controlPanel;
	}

	@Override
	public StatusPanel getStatusPanel() {
		return cpu.getStatusPanel();
	}

	@Override
	public void reset() {
		GameStatus.setRunning(false);
		cpu.reset();
	}
}
