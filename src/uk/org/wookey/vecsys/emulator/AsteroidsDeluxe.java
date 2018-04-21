package uk.org.wookey.vecsys.emulator;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.loomcom.symon.CpuLoomcom;

import uk.org.wookey.vecsys.cpus.BaseStatusPanel;
import uk.org.wookey.vecsys.cpus.cpu6502.Cpu6502;
import uk.org.wookey.vecsys.cpus.cpu6x09.Cpu6x09;
import uk.org.wookey.vecsys.cpus.simplecpu.SimpleCpu;
import uk.org.wookey.vecsys.emulator.devices.Button;
import uk.org.wookey.vecsys.emulator.devices.CoinCounter;
import uk.org.wookey.vecsys.emulator.devices.CoinDoor;
import uk.org.wookey.vecsys.emulator.devices.ControllerSelect;
import uk.org.wookey.vecsys.emulator.devices.DVG;
import uk.org.wookey.vecsys.emulator.devices.Earom;
import uk.org.wookey.vecsys.emulator.devices.MemoryDevice;
import uk.org.wookey.vecsys.emulator.devices.OptionSwitches;
import uk.org.wookey.vecsys.emulator.devices.PlayerButtons;
import uk.org.wookey.vecsys.emulator.devices.Pokey;
import uk.org.wookey.vecsys.emulator.devices.SlamButton;
import uk.org.wookey.vecsys.emulator.devices.SoundControl;
import uk.org.wookey.vecsys.emulator.devices.ToggleSwitch;
import uk.org.wookey.vecsys.emulator.devices.ThreeKHz;
import uk.org.wookey.vecsys.emulator.devices.WatchDog;
import uk.org.wookey.vecsys.utils.Logger;

public class AsteroidsDeluxe extends Emulator {
	private static Logger _log = new Logger("AD-Game");
	
	private JPanel controlPanel;
	private JPanel configPanel;
	
	private MemoryDevice gameRom;
	private MemoryDevice gameRam;
	private DVG dvg;
	private Button leftButton;
	private Button rightButton;
	private Button shieldButton;
	private Button fireButton;
	private Button thrustButton;
	private ToggleSwitch selfTest;
	private Pokey pokey;
	private WatchDog watchdog;
	private Earom earom;
	private PlayerButtons playerButtons;
	private ThreeKHz threeK;
	private CoinCounter coinCounters;
	private CoinDoor coinDoor;
	private OptionSwitches optionSwitches;
	private SoundControl soundControl;
	private ControllerSelect controllerSelect;
	private SlamButton slamSwitch;

	public AsteroidsDeluxe() throws RangeException, IOException {
		bus = new Bus(16);
		bus.setAddressMask(0x7fff);
		bus.setLittleEndian();
		
		_log.logInfo("Populating bus");
		
		createDevices();
		
		//cpu = new SimpleCpu();
		cpu = new Cpu6x09();
		cpu.setBus(bus);
		
		if (cpu.isBigEndian()) {
			bus.setBigEndian();
		}
		else {
			bus.setLittleEndian();
		}

		cpu.reset();

		bus.dump();
		
		buildConfigPanel();
		buildControlPanel();
		
		Thread runner = new Thread(new Runnable() {
			@Override
			public void run() {
				long before = System.nanoTime();
				
				while (true) {
					if ((System.nanoTime() - before) > 4000000) {
						cpu.interrupt(CpuLoomcom.NMI_INTERRUPT);
						before = System.nanoTime();
					}
					
					Thread.yield();
				}
			}
		});
		
		runner.start();
	}
	
	private void buildConfigPanel() {
		GBConstraints gbc = new GBConstraints();
		gbc.weightx = 0.5;
		
		configPanel = new JPanel();

		final TitledBorder tb = BorderFactory.createTitledBorder("Configuration");
		configPanel.setBorder(tb);
		configPanel.setMinimumSize(new Dimension(300, 100));
		configPanel.setLayout(new GridBagLayout());

		GridBagStrip row1 = new GridBagStrip();
		row1.append(selfTest.getWidget());
		row1.append(optionSwitches.getWidget());
		row1.append(slamSwitch.getWidget());
		row1.eol();

		configPanel.add(row1, gbc);
		gbc.nl();
				
		GridBagStrip row2 = new GridBagStrip();
		row2.append(coinCounters.getWidget(0));
		row2.append(coinCounters.getWidget(1));
		row2.append(coinCounters.getWidget(2));
		row2.eol();

		configPanel.add(row2, gbc);
	}
	
	private void buildControlPanel() {
		GBConstraints gbc = new GBConstraints();
		
		controlPanel = new JPanel();
		final TitledBorder tb = BorderFactory.createTitledBorder("Controls");
		controlPanel.setBorder(tb);
		controlPanel.setLayout(new GridBagLayout());

		// Top row - player 1 and 2 buttons/leds
		GridBagStrip row1 = new GridBagStrip();
		row1.append(playerButtons.getWidget());
		row1.eol();

		controlPanel.add(row1, gbc);
		gbc.nl();
		
		// 2nd row - 4 game buttons
		GridBagStrip row2 = new GridBagStrip();
		row2.append(leftButton.getWidget());
		row2.append(rightButton.getWidget());
		row2.append(fireButton.getWidget());
		row2.append(thrustButton.getWidget());
		row2.eol();
		
		controlPanel.add(row2, gbc);
		gbc.nl();
		
		// 3rd row - shields button
		GridBagStrip row3 = new GridBagStrip();
		row3.append(shieldButton.getWidget());
		row3.eol();

		controlPanel.add(row3, gbc);
		gbc.nl();

		// Bottom row - coin door
		GridBagStrip row4 = new GridBagStrip();
		row4.append(coinDoor.getWidget());
		row4.eol();
		
		controlPanel.add(row4, gbc);
	}
	
	private void createDevices() throws IOException, RangeException {
		gameRom = new MemoryDevice(8192);
		gameRom.loadFile("Code/ad-v3.bin", 0);
		//gameRom.loadFile("Code/test-noram.bin", 0);
		gameRom.setWriteable(false);
		gameRom.setName("Game ROM");
		bus.attach(0x6000, 0x7fff, gameRom);
		
		
		gameRam = new MemoryDevice(1024);
		gameRam.setName("Game RAM");
		bus.attach(0, 0x03ff, gameRam);
		
		
		dvg = new DVG();
		bus.attach(0x2002, dvg, DVG.vgHalted);
		bus.attach(0x3000, dvg, DVG.startVG);
		bus.attach(0x3800, dvg, DVG.resetVG);
		bus.attach(0x4000, 0x57ff, dvg, DVG.vgMem);
		
		
		leftButton = new Button("Q", 0, 0x80, KeyEvent.VK_Q); 
		bus.attach(0x2407, leftButton);
		
		
		rightButton = new Button("D", 0, 0x80, KeyEvent.VK_D); 
		bus.attach(0x2406, rightButton);
		
		
		shieldButton = new Button("SP", 0, 0x80, KeyEvent.VK_SPACE); 
		bus.attach(0x2003, shieldButton);
		
		
		fireButton = new Button("K", 0, 0x80, KeyEvent.VK_K);
		bus.attach(0x2004, fireButton);
		
		
		thrustButton = new Button("P", 0, 0x80, KeyEvent.VK_P);
		bus.attach(0x2405, thrustButton);
		
		
		selfTest = new ToggleSwitch("SelfTest", 0, 0x80);
		bus.attach(0x2007, selfTest);
		
		
		pokey = new Pokey();
		bus.attach(0x2c00, 0x2c0f, pokey);
		
		
		watchdog = new WatchDog();
		bus.attach(0x3400, watchdog);
		
		
		earom = new Earom();
		bus.attach(0x2c40, 0x2c7f, earom, Earom.read);
		bus.attach(0x3200, 0x323f, earom, Earom.latch);
		bus.attach(0x3a00, earom, Earom.control);
		
		
		threeK = new ThreeKHz();
		bus.attach(0x2001, threeK);
		
		
		playerButtons = new PlayerButtons();
		bus.attach(0x2403, 0x2404, playerButtons, PlayerButtons.buttons);
		bus.attach(0x3c00, 0x3c01, playerButtons, PlayerButtons.leds);
		
		
		coinCounters = new CoinCounter("AD");
		bus.attach(0x3c05, 0x3c07, coinCounters);
		
		
		coinDoor = new CoinDoor();
		bus.attach(0x2400, 0x2402, coinDoor);
		
		
		optionSwitches = new OptionSwitches("AD");
		bus.attach(0x2800, 0x2803, optionSwitches);	
		
		
		soundControl = new SoundControl();
		bus.attach(0x3600, soundControl, SoundControl.explosion);
		bus.attach(0x3c03, soundControl, SoundControl.thrust);
		
		
		controllerSelect = new ControllerSelect();
		bus.attach(0x3c04, controllerSelect);
		
		
		slamSwitch = new SlamButton();
		bus.attach(0x2006, slamSwitch);
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
	public BaseStatusPanel getStatusPanel() {
		return cpu.getStatusPanel();
	}

	@Override
	public void reset() {
		GameStatus.setRunning(false);
		cpu.reset();
	}
}
