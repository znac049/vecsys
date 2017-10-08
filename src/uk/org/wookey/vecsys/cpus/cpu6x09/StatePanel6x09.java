package uk.org.wookey.vecsys.cpus.cpu6x09;

import java.awt.Color;
import java.awt.GridBagConstraints;

import uk.org.wookey.vecsys.cpus.CpuState;
import uk.org.wookey.vecsys.cpus.StatePanel;
import uk.org.wookey.vecsys.emulator.TTLabel;
import uk.org.wookey.vecsys.emulator.GBConstraints;

public class StatePanel6x09 extends StatePanel {
	private static final long serialVersionUID = 1L;
	
	private TTLabel pcReg;
	private TTLabel sReg;
	private TTLabel uReg;
	private TTLabel dpReg;
	
	private TTLabel aReg;
	private TTLabel bReg;
	private TTLabel xReg;
	private TTLabel yReg;
	private TTLabel ccReg;
	
	private TTLabel ccStr;

	public StatePanel6x09() {
		super();
		
		GBConstraints gbc = new GBConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.NONE;
		
		add(new TTLabel("PC", headingColour), gbc);
		gbc.right();
		
		add(new TTLabel("S", headingColour), gbc);
		gbc.right();
		
		add(new TTLabel("U", headingColour), gbc);
		gbc.right();
		
		add(new TTLabel("DP", headingColour), gbc);
		gbc.nl();
		
		pcReg = new TTLabel("----");
		add(pcReg, gbc);
		gbc.right();
		
		sReg = new TTLabel("----");
		add(sReg, gbc);
		gbc.right();
		
		uReg = new TTLabel("----");
		add(uReg, gbc);
		gbc.right();
		
		dpReg = new TTLabel("--");
		add(dpReg, gbc);
		gbc.nl();
		
		add(new TTLabel("A", headingColour), gbc);
		gbc.right();
		
		add(new TTLabel("B", headingColour), gbc);
		gbc.right();
		
		add(new TTLabel("X", headingColour), gbc);
		gbc.right();
		
		add(new TTLabel("Y", headingColour), gbc);
		gbc.right();
		
		add(new TTLabel("CC", headingColour), gbc);
		gbc.right();
		
		add(new TTLabel("EFHINZVC", headingColour), gbc);
		
		gbc.nl();
		
		aReg = new TTLabel("--");
		add(aReg, gbc);
		gbc.right();
		
		bReg = new TTLabel("--");
		add(bReg, gbc);
		gbc.right();
		
		xReg = new TTLabel("----");
		add(xReg, gbc);
		gbc.right();
		
		yReg = new TTLabel("----");
		add(yReg, gbc);
		gbc.right();
		
		ccReg = new TTLabel("--");
		add(ccReg, gbc);
		gbc.right();
		
		ccStr = new TTLabel("--------");
		add(ccStr, gbc);
	}
	
	public void update(CpuState6x09 state) {
		
	}

	@Override
	public void rebuild(CpuState state) {
		// TODO Auto-generated method stub
		
	}
}
