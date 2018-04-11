package uk.org.wookey.vecsys.cpus.simplecpu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import uk.org.wookey.vecsys.cpus.Cpu;
import uk.org.wookey.vecsys.cpus.CpuState;
import uk.org.wookey.vecsys.cpus.BaseStatusPanel;
import uk.org.wookey.vecsys.emulator.GBConstraints;
import uk.org.wookey.vecsys.emulator.TTLabel;
import uk.org.wookey.vecsys.utils.Logger;

public class SimpleCpu extends Cpu {
	private static Logger _log = new Logger("SimpleCPU");

	private TTLabel pcReg;
	private TTLabel bReg;
	private TTLabel codeStr;

	private static final Instruction[] instructions = {
		// 00-0f
		new Instruction(0x00, "NOP",  1, 7),
		new Instruction(0x01, "INCB",  2, 6),
		new Instruction(0x02, "DECB",  2, 1)
	};

	private class SimpleCpuState extends CpuState {
		public int b;
		
		public SimpleCpuState() {
			b = 42;
		}
	}
	
	private class SimpleStatusPanel extends BaseStatusPanel {
		public SimpleStatusPanel() {
    		super();
    		setLayout(new GridBagLayout());
    		
    		Color headingColour = Color.YELLOW;
    		
    		JPanel registers = new JPanel();
    		registers.setLayout(new GridBagLayout());
    		registers.setBackground(Color.DARK_GRAY);
    		
    		GBConstraints gbc = new GBConstraints();
    		gbc.anchor = GridBagConstraints.CENTER;
    		gbc.fill = GridBagConstraints.NONE;
    		
    		registers.add(new TTLabel("PC", headingColour), gbc);
    		gbc.right();
    		
    		registers.add(new TTLabel("B", headingColour), gbc);
    		gbc.nl();
    		
    		pcReg = new TTLabel("----");
    		registers.add(pcReg, gbc);
    		gbc.right();
    		
    		bReg = new TTLabel("--");
    		registers.add(bReg, gbc);
    		gbc.nl();
    		
    		gbc.reset();
    		add(registers, gbc);
    		gbc.nl();
    		
    		JLabel spacer = new JLabel("X");
    		spacer.setMinimumSize(new Dimension(spacer.getMinimumSize().width, 50));
    		add(spacer, gbc);
    		gbc.nl();
    		
    		JPanel codePanel = new JPanel();
    		codePanel.setLayout(new BorderLayout());
    		
    		codePanel.setBackground(Color.DARK_GRAY);
    		
    		codeStr = new TTLabel("Wha?????");
    		codePanel.add(codeStr, BorderLayout.WEST);
    		
    		add(codePanel, gbc);			
		}
		
		@Override
		public void update() {
			if (isEnabled()) {
				pcReg.setText(String.format("%04x",  state.pc));
				bReg.setText(String.format("%02x", state.b));
			
				codeStr.setText(String.format("%02x", state.ir));
			}
		}
	}
	
	private SimpleCpuState state;
	private SimpleStatusPanel statusPanel;
	
	public SimpleCpu() {
		state = new SimpleCpuState();
		statusPanel = new SimpleStatusPanel();
	}
	
	@Override
	public void reset() {
		state.pc = 0x6000;
		statusPanel.update();
	}

	@Override
	public boolean isBigEndian() {
		return false;
	}

	public void fetchNextInstruction() {
		state.ir = bus.getByte(state.pc);
		state.pc++;
		
		_log.logInfo(String.format("Instruction at $%04x: %02x", state.pc, state.ir));
	}

	@Override
	public void step() {
		fetchNextInstruction();
		
		if (state.ir == 0x00) {
			// NOP
		}
		else if ((state.ir & 0x03) == 1) {
			// INCB
			state.b = (state.b + 1) & 0xff;
		}
		else if ((state.ir & 0x03) == 2) {
			// DECB
			state.b = (state.b - 1) & 0xff;
		}
		else if ((state.ir & 0x03) == 3) {
			// CLRB
			state.b = 0;
		}
		else {
			// anything else
		}
	}

	@Override
	public void go() {
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void interrupt(int interruptId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BaseStatusPanel getStatusPanel() {
		return statusPanel;
	}

}
