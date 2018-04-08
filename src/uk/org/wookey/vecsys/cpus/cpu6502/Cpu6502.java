package uk.org.wookey.vecsys.cpus.cpu6502;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import uk.org.wookey.vecsys.cpus.Cpu;
import uk.org.wookey.vecsys.cpus.StatusPanel;
import uk.org.wookey.vecsys.cpus.cpu6502.Instruction.Mode;
import uk.org.wookey.vecsys.emulator.GBConstraints;
import uk.org.wookey.vecsys.emulator.TTLabel;
import uk.org.wookey.vecsys.utils.Logger;
import uk.org.wookey.vecsys.utils.VecUtils;

public class Cpu6502 extends Cpu {
	private static Logger _log = new Logger("6502");

    // Status register bits
    public static final int P_CARRY       = 0x01;
    public static final int P_ZERO        = 0x02;
    public static final int P_IRQ_DISABLE = 0x04;
    public static final int P_DECIMAL     = 0x08;
    public static final int P_BREAK       = 0x10;
    // Bit 5 is always '1'
    public static final int P_OVERFLOW    = 0x40;
    public static final int P_NEGATIVE    = 0x80;

    public static final int RESET_VEC = 0xfffc;
	private CpuState6502 state;
	
	private static final Instruction[] instructions = {
			// 00-0f
			new Instruction(0x00, "BRK",  1, Mode.IMP, 7),
			new Instruction(0x01, "ORA",  2, Mode.XIN, 6),
			new Instruction(0x02, "NOP",  2, Mode.NUL, 1),
			new Instruction(0x03, "NOP",  1, Mode.ZPR, 8),
			new Instruction(0x04, "TSB",  2, Mode.NUL, 3),
			new Instruction(0x05, "ORA",  2, Mode.ZPG, 3),
			new Instruction(0x06, "ASL",  2, Mode.NUL, 5),
			new Instruction(0x07, "RMB0", 2, Mode.ZPR, 5),
			new Instruction(0x08, "PHP",  1, Mode.IMP, 3),
			new Instruction(0x09, "ORA",  2, Mode.IMM, 2),
			new Instruction(0x0a, "ASL",  1, Mode.ACC, 2),
			new Instruction(0x0b, "NOP",  1, Mode.NUL, 2),
			new Instruction(0x0c, "TSB",  3, Mode.ABS, 4),
			new Instruction(0x0d, "ORA",  3, Mode.ABS, 4),
			new Instruction(0x0e, "ASL",  3, Mode.ABS, 6),
			new Instruction(0x0f, "BBR0", 3, Mode.ZPR, 6),
			// 10-1f
			new Instruction(0x10, "BPL",  2, Mode.REL, 2),
			new Instruction(0x11, "ORA",  2, Mode.INY, 5),
			new Instruction(0x12, "ORA",  2, Mode.ZPI, 1),
			new Instruction(0x13, "NOP",  1, Mode.NUL, 8),
			new Instruction(0x14, "TRB",  2, Mode.ZPG, 4),
			new Instruction(0x15, "ORA",  2, Mode.ZPX, 4),
			new Instruction(0x16, "ASL",  2, Mode.ZPX, 6),
			new Instruction(0x17, "RMB1", 2, Mode.ZPG, 6),
			new Instruction(0x18, "CLC",  1, Mode.IMP, 2),
			new Instruction(0x19, "ORA",  3, Mode.IMM, 4),
			new Instruction(0x1a, "INC",  1, Mode.ACC, 2),
			new Instruction(0x1b, "NOP",  1, Mode.NUL, 7),
			new Instruction(0x1c, "TRB",  3, Mode.ABS, 4),
			new Instruction(0x1d, "ORA",  3, Mode.ABX, 4),
			new Instruction(0x1e, "ASL",  3, Mode.ABX, 7),
			new Instruction(0x1f, "BBR1", 3, Mode.ZPR, 7),
			// 20-2f
			new Instruction(0x20, "JSR",  3, Mode.ABS, 6),
			new Instruction(0x21, "AND",  2, Mode.XIN, 6),
			new Instruction(0x22, "NOP",  2, Mode.NUL, 1),
			new Instruction(0x23, "NOP",  1, Mode.NUL, 8),
			new Instruction(0x24, "BIT",  2, Mode.ZPG, 3),
			new Instruction(0x25, "AND",  2, Mode.ZPG, 5),
			new Instruction(0x26, "ROL",  2, Mode.ZPG, 5),
			new Instruction(0x27, "RMB2", 2, Mode.ZPG, 5),
			new Instruction(0x28, "PLP",  1, Mode.IMP, 4),
			new Instruction(0x29, "AND",  2, Mode.IMM, 2),
			new Instruction(0x2a, "ROL",  1, Mode.ACC, 2),
			new Instruction(0x2b, "NOP",  1, Mode.NUL, 2),
			new Instruction(0x2c, "BIT",  3, Mode.ABS, 4),
			new Instruction(0x2d, "AND",  3, Mode.ABS, 4),
			new Instruction(0x2e, "ROL",  3, Mode.ABS, 6),
			new Instruction(0x2f, "BBR2", 3, Mode.ZPR, 6),
			// 30-3f
			new Instruction(0x30, "BMI",  2, Mode.REL, 2),
			new Instruction(0x31, "AND",  2, Mode.INY, 5),
			new Instruction(0x32, "AND",  2, Mode.ZPI, 1),
			new Instruction(0x33, "NOP",  1, Mode.NUL, 8),
			new Instruction(0x34, "BIT",  2, Mode.ZPX, 4),
			new Instruction(0x35, "AND",  2, Mode.ZPX, 4),
			new Instruction(0x36, "ROL",  2, Mode.ZPX, 6),
			new Instruction(0x37, "RMB3", 2, Mode.ZPG, 6),
			new Instruction(0x38, "SEC",  1, Mode.IMP, 2),
			new Instruction(0x39, "AND",  3, Mode.ABY, 4),
			new Instruction(0x3a, "DEC",  1, Mode.IMP, 2),
			new Instruction(0x3b, "NOP",  1, Mode.NUL, 7),
			new Instruction(0x3c, "BIT",  3, Mode.NUL, 4),
			new Instruction(0x3d, "AND",  3, Mode.ABX, 4),
			new Instruction(0x3e, "ROL",  3, Mode.ABX, 7),
			new Instruction(0x3f, "BBR3", 3, Mode.ZPR, 7),
			// 40-4f
			new Instruction(0x40, "RTI",  1, Mode.IMP, 6),
			new Instruction(0x41, "EOR",  2, Mode.XIN, 6),
			new Instruction(0x42, "NOP",  2, Mode.NUL, 1),
			new Instruction(0x43, "NOP",  1, Mode.NUL, 8),
			new Instruction(0x44, "NOP",  2, Mode.NUL, 3),
			new Instruction(0x45, "EOR",  2, Mode.ZPG, 3),
			new Instruction(0x46, "LSR",  2, Mode.ZPG, 5),
			new Instruction(0x47, "RMB4", 2, Mode.ZPG, 5),
			new Instruction(0x48, "PHA",  1, Mode.IMP, 3),
			new Instruction(0x49, "EOR",  2, Mode.IMM, 2),
			new Instruction(0x4a, "LSR",  1, Mode.ACC, 2),
			new Instruction(0x4b, "NOP",  1, Mode.NUL, 2),
			new Instruction(0x4c, "JMP",  3, Mode.ABS, 3),
			new Instruction(0x4d, "EOR",  3, Mode.ABS, 4),
			new Instruction(0x4e, "LSR",  3, Mode.ABS, 6),
			new Instruction(0x4f, "BBR4", 3, Mode.ZPR, 6),
			// 50-5f
			new Instruction(0x50, "BVC",  2, Mode.REL, 2),
			new Instruction(0x51, "EOR",  2, Mode.INY, 5),
			new Instruction(0x52, "EOR",  2, Mode.ZPI, 1),
			new Instruction(0x53, "NOP",  1, Mode.NUL, 8),
			new Instruction(0x54, "NOP",  2, Mode.NUL, 4),
			new Instruction(0x55, "EOR",  2, Mode.ZPX, 4),
			new Instruction(0x56, "LSR",  2, Mode.ZPX, 6),
			new Instruction(0x57, "RMB5", 2, Mode.ZPG, 6),
			new Instruction(0x58, "CLI",  1, Mode.IMP, 2),
			new Instruction(0x59, "EOR",  3, Mode.ABY, 4),
			new Instruction(0x5a, "PHY",  1, Mode.IMP, 2),
			new Instruction(0x5b, "NOP",  1, Mode.NUL, 7),
			new Instruction(0x5c, "NOP",  3, Mode.NUL, 4),
			new Instruction(0x5d, "EOR",  3, Mode.ABX, 4),
			new Instruction(0x5e, "LSR",  3, Mode.ABX, 7),
			new Instruction(0x5f, "BBR5", 3, Mode.ZPR, 7),
			// 60-6f
			new Instruction(0x60, "RTS",  1, Mode.IMP, 6),
			new Instruction(0x61, "ADC",  2, Mode.XIN, 6),
			new Instruction(0x62, "NOP",  2, Mode.NUL, 1),
			new Instruction(0x63, "NOP",  1, Mode.NUL, 8),
			new Instruction(0x64, "STZ",  2, Mode.ZPG, 3),
			new Instruction(0x65, "ADC",  2, Mode.ZPG, 3),
			new Instruction(0x66, "ROR",  2, Mode.ZPG, 5),
			new Instruction(0x67, "RMB6", 2, Mode.ZPG, 5),
			new Instruction(0x68, "PLA",  1, Mode.IMP, 4),
			new Instruction(0x69, "ADC",  2, Mode.IMM, 2),
			new Instruction(0x6a, "ROR",  1, Mode.ACC, 2),
			new Instruction(0x6b, "NOP",  1, Mode.NUL, 2),
			new Instruction(0x6c, "JMP",  3, Mode.IND, 5),
			new Instruction(0x6d, "ADC",  3, Mode.ABS, 4),
			new Instruction(0x6e, "ROR",  3, Mode.ABS, 6),
			new Instruction(0x6f, "BBR6", 3, Mode.ZPR, 6),
			// 70-7f
			new Instruction(0x70, "BVS",  2, Mode.REL, 2),
			new Instruction(0x71, "ADC",  2, Mode.INY, 5),
			new Instruction(0x72, "ADC",  2, Mode.ZPI, 1),
			new Instruction(0x73, "NOP",  1, Mode.NUL, 8),
			new Instruction(0x74, "STZ",  2, Mode.ZPX, 4),
			new Instruction(0x75, "ADC",  2, Mode.ZPX, 4),
			new Instruction(0x76, "ROR",  2, Mode.ZPX, 6),
			new Instruction(0x77, "RMB7", 2, Mode.ZPG, 6),
			new Instruction(0x78, "SEI",  1, Mode.IMP, 2),
			new Instruction(0x79, "ADC",  3, Mode.ABY, 4),
			new Instruction(0x7a, "PLY",  1, Mode.IMP, 2),
			new Instruction(0x7b, "NOP",  1, Mode.NUL, 7),
			new Instruction(0x7c, "JMP",  3, Mode.AIX, 4),
			new Instruction(0x7d, "ADC",  3, Mode.ABX, 4),
			new Instruction(0x7e, "ROR",  3, Mode.ABX, 7),
			new Instruction(0x7f, "BBR7", 3, Mode.ZPR, 7),
			// 80-8f
			new Instruction(0x80, "BRA",  2, Mode.REL, 2),
			new Instruction(0x81, "STA",  2, Mode.XIN, 6),
			new Instruction(0x82, "NOP",  2, Mode.NUL, 2),
			new Instruction(0x83, "NOP",  1, Mode.NUL, 6),
			new Instruction(0x84, "STY",  2, Mode.ZPG, 3),
			new Instruction(0x85, "STA",  2, Mode.ZPG, 3),
			new Instruction(0x86, "STX",  2, Mode.ZPG, 3),
			new Instruction(0x87, "SMB0", 2, Mode.ZPG, 3),
			new Instruction(0x88, "DEY",  1, Mode.IMP, 2),
			new Instruction(0x89, "BIT",  2, Mode.IMM, 2),
			new Instruction(0x8a, "TXA",  1, Mode.IMP, 2),
			new Instruction(0x8b, "NOP",  1, Mode.NUL, 2),
			new Instruction(0x8c, "STY",  3, Mode.ABS, 4),
			new Instruction(0x8d, "STA",  3, Mode.ABS, 4),
			new Instruction(0x8e, "STX",  3, Mode.ABS, 4),
			new Instruction(0x8f, "BBS0", 3, Mode.ZPR, 4),
			// 90-9f
			new Instruction(0x90, "BCC",  2, Mode.REL, 2),
			new Instruction(0x91, "STA",  2, Mode.INY, 6),
			new Instruction(0x92, "STA",  2, Mode.ZPI, 1),
			new Instruction(0x93, "NOP",  1, Mode.NUL, 6),
			new Instruction(0x94, "STY",  2, Mode.ZPX, 4),
			new Instruction(0x95, "STA",  2, Mode.ZPX, 4),
			new Instruction(0x96, "STX",  2, Mode.ZPY, 4),
			new Instruction(0x97, "SMB1", 2, Mode.ZPG, 4),
			new Instruction(0x98, "TYA",  1, Mode.IMP, 2),
			new Instruction(0x99, "STA",  3, Mode.ABY, 5),
			new Instruction(0x9a, "TXS",  1, Mode.IMP, 2),
			new Instruction(0x9b, "NOP",  1, Mode.NUL, 5),
			new Instruction(0x9c, "STZ",  3, Mode.ABS, 5),
			new Instruction(0x9d, "STA",  3, Mode.ABX, 5),
			new Instruction(0x9e, "STZ",  3, Mode.ABX, 5),
			new Instruction(0x9f, "BBS1", 3, Mode.ZPR, 5),
			// a0-af
			new Instruction(0xa0, "LDY",  2, Mode.IMM, 2),
			new Instruction(0xa1, "LDA",  2, Mode.XIN, 6),
			new Instruction(0xa2, "LDX",  2, Mode.IMM, 2),
			new Instruction(0xa3, "NOP",  1, Mode.NUL, 6),
			new Instruction(0xa4, "LDY",  2, Mode.ZPG, 3),
			new Instruction(0xa5, "LDA",  2, Mode.ZPG, 3),
			new Instruction(0xa6, "LDX",  2, Mode.ZPG, 3),
			new Instruction(0xa7, "SMB2", 2, Mode.ZPG, 3),
			new Instruction(0xa8, "TAY",  1, Mode.IMP, 2),
			new Instruction(0xa9, "LDA",  2, Mode.IMM, 2),
			new Instruction(0xaa, "TAX",  1, Mode.IMP, 2),
			new Instruction(0xab, "NOP",  1, Mode.NUL, 2),
			new Instruction(0xac, "LDY",  3, Mode.ABS, 4),
			new Instruction(0xad, "LDA",  3, Mode.ABS, 4),
			new Instruction(0xae, "LDX",  3, Mode.ABS, 4),
			new Instruction(0xaf, "BBS2", 3, Mode.ZPR, 4),
			// b0-bf
			new Instruction(0xb0, "BCS",  2, Mode.REL, 2),
			new Instruction(0xb1, "LDA",  2, Mode.INY, 5),
			new Instruction(0xb2, "LDA",  2, Mode.ZPI, 1),
			new Instruction(0xb3, "NOP",  1, Mode.NUL, 5),
			new Instruction(0xb4, "LDY",  2, Mode.ZPX, 4),
			new Instruction(0xb5, "LDA",  2, Mode.ZPX, 4),
			new Instruction(0xb6, "LDX",  2, Mode.ZPY, 4),
			new Instruction(0xb7, "SMB3", 2, Mode.ZPG, 4),
			new Instruction(0xb8, "CLV",  1, Mode.IMP, 2),
			new Instruction(0xb9, "LDA",  3, Mode.ABY, 4),
			new Instruction(0xba, "TSX",  1, Mode.IMP, 2),
			new Instruction(0xbb, "NOP",  1, Mode.NUL, 4),
			new Instruction(0xbc, "LDY",  3, Mode.ABX, 4),
			new Instruction(0xbd, "LDA",  3, Mode.ABX, 4),
			new Instruction(0xbe, "LDX",  3, Mode.ABY, 4),
			new Instruction(0xbf, "BBS3", 3, Mode.ZPR, 4),
			// c0-cf
			new Instruction(0xc0, "CPY",  2, Mode.IMM, 2),
			new Instruction(0xc1, "CMP",  2, Mode.XIN, 6),
			new Instruction(0xc2, "NOP",  2, Mode.NUL, 2),
			new Instruction(0xc3, "NOP",  1, Mode.NUL, 8),
			new Instruction(0xc4, "CPY",  2, Mode.ZPG, 3),
			new Instruction(0xc5, "CMP",  2, Mode.ZPG, 3),
			new Instruction(0xc6, "DEC",  2, Mode.ZPG, 5),
			new Instruction(0xc7, "SMB4", 2, Mode.ZPG, 5),
			new Instruction(0xc8, "INY",  1, Mode.IMP, 2),
			new Instruction(0xc9, "CMP",  2, Mode.IMM, 2),
			new Instruction(0xca, "DEX",  1, Mode.IMP, 2),
			new Instruction(0xcb, "NOP",  1, Mode.NUL, 2),
			new Instruction(0xcc, "CPY",  3, Mode.ABS, 4),
			new Instruction(0xcd, "CMP",  3, Mode.ABS, 4),
			new Instruction(0xce, "DEC",  3, Mode.ABS, 6),
			new Instruction(0xcf, "BBS4", 3, Mode.ZPR, 6),
			// d0-df
			new Instruction(0xd0, "BNE",  2, Mode.REL, 2),
			new Instruction(0xd1, "CMP",  2, Mode.INY, 5),
			new Instruction(0xd2, "CMP",  2, Mode.ZPI, 1),
			new Instruction(0xd3, "NOP",  1, Mode.NUL, 8),
			new Instruction(0xd4, "NOP",  2, Mode.NUL, 4),
			new Instruction(0xd5, "CMP",  2, Mode.ZPX, 4),
			new Instruction(0xd6, "DEC",  2, Mode.ZPX, 6),
			new Instruction(0xd7, "SMB5", 2, Mode.ZPG, 6),
			new Instruction(0xd8, "CLD",  1, Mode.IMP, 2),
			new Instruction(0xd9, "CMP",  3, Mode.ABY, 4),
			new Instruction(0xda, "PHX",  1, Mode.IMP, 2),
			new Instruction(0xdb, "NOP",  1, Mode.NUL, 7),
			new Instruction(0xdc, "NOP",  3, Mode.NUL, 4),
			new Instruction(0xdd, "CMP",  3, Mode.ABX, 4),
			new Instruction(0xde, "DEC",  3, Mode.ABX, 7),
			new Instruction(0xdf, "BBS5", 3, Mode.ZPR, 7),
			// e0-ef
			new Instruction(0xe0, "CPX",  2, Mode.IMM, 2),
			new Instruction(0xe1, "SBC",  2, Mode.XIN, 6),
			new Instruction(0xe2, "NOP",  2, Mode.NUL, 2),
			new Instruction(0xe3, "NOP",  1, Mode.NUL, 8),
			new Instruction(0xe4, "CPX",  2, Mode.ZPG, 3),
			new Instruction(0xe5, "SBC",  2, Mode.ZPG, 3),
			new Instruction(0xe6, "INC",  2, Mode.ZPG, 5),
			new Instruction(0xe7, "SMB6", 2, Mode.ZPG, 5),
			new Instruction(0xe8, "INX",  1, Mode.IMP, 2),
			new Instruction(0xe9, "SBC",  2, Mode.IMM, 2),
			new Instruction(0xea, "NOP",  1, Mode.IMP, 2),
			new Instruction(0xeb, "NOP",  1, Mode.NUL, 2),
			new Instruction(0xec, "CPX",  3, Mode.ABS, 4),
			new Instruction(0xed, "SBC",  3, Mode.ABS, 4),
			new Instruction(0xee, "INC",  3, Mode.ABS, 6),
			new Instruction(0xef, "BBS6", 3, Mode.ZPR, 6),
			// f0-ff
			new Instruction(0xf0, "BEQ",  2, Mode.REL, 2),
			new Instruction(0xf1, "SBC",  2, Mode.INY, 5),
			new Instruction(0xf2, "SBC",  2, Mode.ZPI, 1),
			new Instruction(0xf3, "NOP",  1, Mode.NUL, 8),
			new Instruction(0xf4, "NOP",  2, Mode.NUL, 4),
			new Instruction(0xf5, "SBC",  2, Mode.ZPX, 4),
			new Instruction(0xf6, "INC",  2, Mode.ZPX, 6),
			new Instruction(0xf7, "SMB7", 2, Mode.ZPG, 6),
			new Instruction(0xf8, "SED",  1, Mode.IMP, 2),
			new Instruction(0xf9, "SBC",  3, Mode.ABY, 4),
			new Instruction(0xfa, "PLX",  1, Mode.IMP, 2),
			new Instruction(0xfb, "NOP",  1, Mode.NUL, 7),
			new Instruction(0xfc, "NOP",  3, Mode.NUL, 4),
			new Instruction(0xfd, "SBC",  3, Mode.ABX, 4),
			new Instruction(0xfe, "INC",  3, Mode.ABX, 7),
			new Instruction(0xff, "BBS7", 3, Mode.ZPR, 7)
	};

    private class Cpu6502StatusPanel extends StatusPanel {
    	private TTLabel pcReg;
    	private TTLabel aReg;
    	private TTLabel xReg;
    	private TTLabel yReg;
    	private TTLabel sReg;
    	private TTLabel spReg;
    	private TTLabel flagStr;
    	
    	private TTLabel codeStr;
    	
    	public Cpu6502StatusPanel() {
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
    		
    		registers.add(new TTLabel("A", headingColour), gbc);
    		gbc.right();
    		
    		registers.add(new TTLabel("X", headingColour), gbc);
    		gbc.right();
    		
    		registers.add(new TTLabel("Y", headingColour), gbc);
    		gbc.right();
    		
    		registers.add(new TTLabel("SR", headingColour), gbc);
    		gbc.right();
    		
    		registers.add(new TTLabel("SP", headingColour), gbc);
    		gbc.right();
    		
    		registers.add(new TTLabel("NV-BDIZC", headingColour), gbc);
    		
    		gbc.nl();
    		
    		pcReg = new TTLabel("----");
    		registers.add(pcReg, gbc);
    		gbc.right();
    		
    		aReg = new TTLabel("--");
    		registers.add(aReg, gbc);
    		gbc.right();
    		
    		xReg = new TTLabel("--");
    		registers.add(xReg, gbc);
    		gbc.right();
    		
    		yReg = new TTLabel("--");
    		registers.add(yReg, gbc);
    		gbc.right();
    		
    		sReg = new TTLabel("--");
    		registers.add(sReg, gbc);
    		gbc.right();
    		
    		spReg = new TTLabel("---");
    		registers.add(spReg, gbc);
    		gbc.right();
    		
    		flagStr = new TTLabel("--------");
    		registers.add(flagStr, gbc);
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
    		
    		codeStr = new TTLabel("????");
    		codePanel.add(codeStr, BorderLayout.WEST);
    		
    		add(codePanel, gbc);
    	}
    	
		@Override
		public void update() {
			if (isEnabled()) {
				pcReg.setText(String.format("%04X", state.pc));
				aReg.setText(String.format("%02X", state.a));
				xReg.setText(String.format("%02X", state.x));
				yReg.setText(String.format("%02X", state.y));
				sReg.setText(String.format("%02X", state.getStatusFlag()));
				spReg.setText(String.format("1%02X", state.sp));
				flagStr.setText(VecUtils.binaryString(state.getStatusFlag(), 8));
			
				codeStr.setText(getCodeString());
			}
		}
		
		private String getCodeString() {
			String src = String.format("%04X: %02x", state.pc, state.nextIr);
			
			for (int i=1; i<2; i++) {
				src += (i<state.nextInstruction.size)?String.format(" %02X", state.nextArgs[i-1]):"   ";
			}
			
			return src + " " + state.nextInstruction.toString();
		
		}
    }
    
    Cpu6502StatusPanel statusPanel;

	public Cpu6502() {
		state = new CpuState6502();
		statusPanel = new Cpu6502StatusPanel();
	}
	
	@Override
	public void reset() {
		state.pc = bus.getWord(RESET_VEC);
		
		state.a = 0;
		state.x = 0;
		state.y = 0;
		state.sp = 0xff;

		state.carryFlag = false;
        state.zeroFlag = false;
        state.irqDisableFlag = false;
        state.decimalModeFlag = false;
        state.breakFlag = false;
        state.overflowFlag = false;
        state.negativeFlag = false;

        fetchNextInstruction();		
 	}
	
	public Instruction fetchNextInstruction() {
		state.nextIr = bus.getByte(state.pc);
		
		state.nextInstruction = instructions[state.nextIr];
		
		for (int i=1; i<state.nextInstruction.size; i++) {
			state.nextArgs[i-1] = bus.getByte(state.pc+i);
		}
		
		_log.logInfo(String.format("Instruction at $%04x: %s %s", state.pc, state.nextInstruction.toString(), state.decodeNextInstruction()));
		
		return state.nextInstruction;
	}

	@Override
	public boolean isBigEndian() {
		return false;
	}

	@Override
	public void step() {
		Instruction inst = fetchNextInstruction();
		
		state.pc += inst.size;
	}
	
	@Override
	public void go() {
		// TODO Auto-generated method stub	
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub	
	}

	@Override
	public StatusPanel getStatusPanel() {
		// TODO Auto-generated method stub
		return statusPanel;
	}

	@Override
	public void interrupt(int interruptId) {
		// TODO Auto-generated method stub
		
	}
}
