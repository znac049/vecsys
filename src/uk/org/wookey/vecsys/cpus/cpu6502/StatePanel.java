package uk.org.wookey.vecsys.cpus.cpu6502;

import java.awt.Color;
import java.awt.GridBagConstraints;

import uk.org.wookey.vecsys.cpus.STATEPanel;
import uk.org.wookey.vecsys.emulator.TTLabel;
import uk.org.wookey.vecsys.emulator.GBConstraints;

public class StatePanel extends STATEPanel {
	private static final long serialVersionUID = 1L;
	
	private TTLabel pcReg;
	private TTLabel aReg;
	private TTLabel xReg;
	private TTLabel yReg;
	private TTLabel sReg;
	private TTLabel spReg;
	private TTLabel flagStr;

	public StatePanel() {
		super();
		
		GBConstraints gbc = new GBConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.NONE;
		
		add(new TTLabel("PC", headingColour), gbc);
		gbc.right();
		
		add(new TTLabel("A", headingColour), gbc);
		gbc.right();
		
		add(new TTLabel("X", headingColour), gbc);
		gbc.right();
		
		add(new TTLabel("Y", headingColour), gbc);
		gbc.right();
		
		add(new TTLabel("SR", headingColour), gbc);
		gbc.right();
		
		add(new TTLabel("SP", headingColour), gbc);
		gbc.right();
		
		add(new TTLabel("NV-BDIZC", headingColour), gbc);
		
		gbc.nl();
		
		pcReg = new TTLabel("----");
		add(pcReg, gbc);
		gbc.right();
		
		aReg = new TTLabel("--");
		add(aReg, gbc);
		gbc.right();
		
		xReg = new TTLabel("--");
		add(xReg, gbc);
		gbc.right();
		
		yReg = new TTLabel("--");
		add(yReg, gbc);
		gbc.right();
		
		sReg = new TTLabel("--");
		add(sReg, gbc);
		gbc.right();
		
		spReg = new TTLabel("---");
		add(spReg, gbc);
		gbc.right();
		
		flagStr = new TTLabel("--------");
		add(flagStr, gbc);
	}
	
	public void rebuild(CpuState state) {
		pcReg.setText(String.format("%04x", state.pc));
		aReg.setText(String.format("%02x", state.a));
		xReg.setText(String.format("%02x", state.x));
		yReg.setText(String.format("%02x", state.y));
		sReg.setText(String.format("%02x", state.getStatusFlag()));
		spReg.setText(String.format("1%02x", state.sp));
		//flagStr.setText(String.format("%08b", state.getStatusFlag()));
	}
}
