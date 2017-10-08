package uk.org.wookey.vecsys.cpus.cpu6502;

import uk.org.wookey.vecsys.cpus.Cpu;
import uk.org.wookey.vecsys.cpus.Instruction;
import uk.org.wookey.vecsys.cpus.StatePanel;
import uk.org.wookey.vecsys.utils.Logger;

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
			new Instruction(0x00, "BRK",  0, 1, 0),
			new Instruction(0x01, "ORA",  0, 1, 0),
			new Instruction(0x02, "NOP",  0, 1, 0),
			new Instruction(0x03, "NOP",  0, 1, 0),
			new Instruction(0x04, "TSB",  0, 1, 0),
			new Instruction(0x05, "ORA",  0, 1, 0),
			new Instruction(0x06, "ASL",  0, 1, 0),
			new Instruction(0x07, "RMB0", 0, 1, 0),
			new Instruction(0x08, "PHP",  0, 1, 0),
			new Instruction(0x09, "ORA",  0, 1, 0),
			new Instruction(0x0a, "ASL",  0, 1, 0),
			new Instruction(0x0b, "NOP",  0, 1, 0),
			new Instruction(0x0c, "TSB",  0, 1, 0),
			new Instruction(0x0d, "ORA",  0, 1, 0),
			new Instruction(0x0e, "ASL",  0, 1, 0),
			new Instruction(0x0f, "BBR0", 0, 1, 0),
			// 10-1f
			new Instruction(0x10, "BPL",  0, 1, 0),
			new Instruction(0x11, "ORA",  0, 1, 0),
			new Instruction(0x12, "ORA",  0, 1, 0),
			new Instruction(0x13, "NOP",  0, 1, 0),
			new Instruction(0x14, "TRB",  0, 1, 0),
			new Instruction(0x15, "ORA",  0, 1, 0),
			new Instruction(0x16, "ASL",  0, 1, 0),
			new Instruction(0x17, "RMB1", 0, 1, 0),
			new Instruction(0x18, "CLC",  0, 1, 0),
			new Instruction(0x19, "ORA",  0, 1, 0),
			new Instruction(0x1a, "INC",  0, 1, 0),
			new Instruction(0x1b, "NOP",  0, 1, 0),
			new Instruction(0x1c, "TRB",  0, 1, 0),
			new Instruction(0x1d, "ORA",  0, 1, 0),
			new Instruction(0x1e, "ASL",  0, 1, 0),
			new Instruction(0x1f, "BBR1", 0, 1, 0),
			// 20-2f
			new Instruction(0x20, "JSR",  0, 1, 0),
			new Instruction(0x21, "AND",  0, 1, 0),
			new Instruction(0x22, "NOP",  0, 1, 0),
			new Instruction(0x23, "NOP",  0, 1, 0),
			new Instruction(0x24, "BIT",  0, 1, 0),
			new Instruction(0x25, "AND",  0, 1, 0),
			new Instruction(0x26, "ROL",  0, 1, 0),
			new Instruction(0x27, "RMB2", 0, 1, 0),
			new Instruction(0x28, "PLP",  0, 1, 0),
			new Instruction(0x29, "AND",  0, 1, 0),
			new Instruction(0x2a, "ROL",  0, 1, 0),
			new Instruction(0x2b, "NOP",  0, 1, 0),
			new Instruction(0x2c, "BIT",  0, 1, 0),
			new Instruction(0x2d, "AND",  0, 1, 0),
			new Instruction(0x2e, "ROL",  0, 1, 0),
			new Instruction(0x2f, "BBR2", 0, 1, 0),
			// 30-3f
			new Instruction(0x30, "BMI",  0, 1, 0),
			new Instruction(0x31, "AND",  0, 1, 0),
			new Instruction(0x32, "AND",  0, 1, 0),
			new Instruction(0x33, "NOP",  0, 1, 0),
			new Instruction(0x34, "BIT",  0, 1, 0),
			new Instruction(0x35, "AND",  0, 1, 0),
			new Instruction(0x36, "ROL",  0, 1, 0),
			new Instruction(0x37, "RMB3", 0, 1, 0),
			new Instruction(0x38, "SEC",  0, 1, 0),
			new Instruction(0x39, "AND",  0, 1, 0),
			new Instruction(0x3a, "DEC",  0, 1, 0),
			new Instruction(0x3b, "NOP",  0, 1, 0),
			new Instruction(0x3c, "BIT",  0, 1, 0),
			new Instruction(0x3d, "AND",  0, 1, 0),
			new Instruction(0x3e, "ROL",  0, 1, 0),
			new Instruction(0x3f, "BBR3", 0, 1, 0),
			// 40-4f
			new Instruction(0x40, "RTI",  0, 1, 0),
			new Instruction(0x41, "EOR",  0, 1, 0),
			new Instruction(0x42, "NOP",  0, 1, 0),
			new Instruction(0x43, "NOP",  0, 1, 0),
			new Instruction(0x44, "NOP",  0, 1, 0),
			new Instruction(0x45, "EOR",  0, 1, 0),
			new Instruction(0x46, "LSR",  0, 1, 0),
			new Instruction(0x47, "RMB4", 0, 1, 0),
			new Instruction(0x48, "PHA",  0, 1, 0),
			new Instruction(0x49, "EOR",  0, 1, 0),
			new Instruction(0x4a, "LSR",  0, 1, 0),
			new Instruction(0x4b, "NOP",  0, 1, 0),
			new Instruction(0x4c, "JMP",  0, 1, 0),
			new Instruction(0x4d, "EOR",  0, 1, 0),
			new Instruction(0x4e, "LSR",  0, 1, 0),
			new Instruction(0x4f, "BBR4", 0, 1, 0),
			// 50-5f
			new Instruction(0x50, "BVC",  0, 1, 0),
			new Instruction(0x51, "EOR",  0, 1, 0),
			new Instruction(0x52, "EOR",  0, 1, 0),
			new Instruction(0x53, "NOP",  0, 1, 0),
			new Instruction(0x54, "NOP",  0, 1, 0),
			new Instruction(0x55, "EOR",  0, 1, 0),
			new Instruction(0x56, "LSR",  0, 1, 0),
			new Instruction(0x57, "RMB5", 0, 1, 0),
			new Instruction(0x58, "CLI",  0, 1, 0),
			new Instruction(0x59, "EOR",  0, 1, 0),
			new Instruction(0x5a, "PHY",  0, 1, 0),
			new Instruction(0x5b, "NOP",  0, 1, 0),
			new Instruction(0x5c, "NOP",  0, 1, 0),
			new Instruction(0x5d, "EOR",  0, 1, 0),
			new Instruction(0x5e, "LSR",  0, 1, 0),
			new Instruction(0x5f, "BBR5", 0, 1, 0),
			// 60-6f
			new Instruction(0x60, "RTS",  0, 1, 0),
			new Instruction(0x61, "ADC",  0, 1, 0),
			new Instruction(0x62, "NOP",  0, 1, 0),
			new Instruction(0x63, "NOP",  0, 1, 0),
			new Instruction(0x64, "STZ",  0, 1, 0),
			new Instruction(0x65, "ADC",  0, 1, 0),
			new Instruction(0x66, "ROR",  0, 1, 0),
			new Instruction(0x67, "RMB6", 0, 1, 0),
			new Instruction(0x68, "PLA",  0, 1, 0),
			new Instruction(0x69, "ADC",  0, 1, 0),
			new Instruction(0x6a, "ROR",  0, 1, 0),
			new Instruction(0x6b, "NOP",  0, 1, 0),
			new Instruction(0x6c, "JMP",  0, 1, 0),
			new Instruction(0x6d, "ADC",  0, 1, 0),
			new Instruction(0x6e, "ROR",  0, 1, 0),
			new Instruction(0x6f, "BBR6", 0, 1, 0),
			// 70-7f
			new Instruction(0x70, "BVS",  0, 1, 0),
			new Instruction(0x71, "ADC",  0, 1, 0),
			new Instruction(0x72, "ADC",  0, 1, 0),
			new Instruction(0x73, "NOP",  0, 1, 0),
			new Instruction(0x74, "STZ",  0, 1, 0),
			new Instruction(0x75, "ADC",  0, 1, 0),
			new Instruction(0x76, "ROR",  0, 1, 0),
			new Instruction(0x77, "RMB7", 0, 1, 0),
			new Instruction(0x78, "SEI",  0, 1, 0),
			new Instruction(0x79, "ADC",  0, 1, 0),
			new Instruction(0x7a, "PLY",  0, 1, 0),
			new Instruction(0x7b, "NOP",  0, 1, 0),
			new Instruction(0x7c, "JMP",  0, 1, 0),
			new Instruction(0x7d, "ADC",  0, 1, 0),
			new Instruction(0x7e, "ROR",  0, 1, 0),
			new Instruction(0x7f, "BBR7", 0, 1, 0),
			// 80-8f
			new Instruction(0x80, "BRA",  0, 1, 0),
			new Instruction(0x81, "STA",  0, 1, 0),
			new Instruction(0x82, "NOP",  0, 1, 0),
			new Instruction(0x83, "NOP",  0, 1, 0),
			new Instruction(0x84, "STY",  0, 1, 0),
			new Instruction(0x85, "STA",  0, 1, 0),
			new Instruction(0x86, "STX",  0, 1, 0),
			new Instruction(0x87, "SMB0", 0, 1, 0),
			new Instruction(0x88, "DEY",  0, 1, 0),
			new Instruction(0x89, "BIT",  0, 1, 0),
			new Instruction(0x8a, "TXA",  0, 1, 0),
			new Instruction(0x8b, "NOP",  0, 1, 0),
			new Instruction(0x8c, "STY",  0, 1, 0),
			new Instruction(0x8d, "STA",  0, 1, 0),
			new Instruction(0x8e, "STX",  0, 1, 0),
			new Instruction(0x8f, "BBS0", 0, 1, 0),
			// 90-9f
			new Instruction(0x90, "BCC",  0, 1, 0),
			new Instruction(0x91, "STA",  0, 1, 0),
			new Instruction(0x92, "STA",  0, 1, 0),
			new Instruction(0x93, "NOP",  0, 1, 0),
			new Instruction(0x94, "STY",  0, 1, 0),
			new Instruction(0x95, "STA",  0, 1, 0),
			new Instruction(0x96, "STX",  0, 1, 0),
			new Instruction(0x97, "SMB1", 0, 1, 0),
			new Instruction(0x98, "TYA",  0, 1, 0),
			new Instruction(0x99, "STA",  0, 1, 0),
			new Instruction(0x9a, "TXS",  0, 1, 0),
			new Instruction(0x9b, "NOP",  0, 1, 0),
			new Instruction(0x9c, "STZ",  0, 1, 0),
			new Instruction(0x9d, "STA",  0, 1, 0),
			new Instruction(0x9e, "STZ",  0, 1, 0),
			new Instruction(0x9f, "BBS1", 0, 1, 0),
			// a0-af
			new Instruction(0xa0, "LDY",  0, 1, 0),
			new Instruction(0xa1, "LDA",  0, 1, 0),
			new Instruction(0xa2, "LDX",  0, 1, 0),
			new Instruction(0xa3, "NOP",  0, 1, 0),
			new Instruction(0xa4, "LDY",  0, 1, 0),
			new Instruction(0xa5, "LDA",  0, 1, 0),
			new Instruction(0xa6, "LDX",  0, 1, 0),
			new Instruction(0xa7, "SMB2", 0, 1, 0),
			new Instruction(0xa8, "TAY",  0, 1, 0),
			new Instruction(0xa9, "LDA",  0, 1, 0),
			new Instruction(0xaa, "TAX",  0, 1, 0),
			new Instruction(0xab, "NOP",  0, 1, 0),
			new Instruction(0xac, "LDY",  0, 1, 0),
			new Instruction(0xad, "LDA",  0, 1, 0),
			new Instruction(0xae, "LDX",  0, 1, 0),
			new Instruction(0xaf, "BBS2", 0, 1, 0),
			// b0-bf
			new Instruction(0xb0, "BCS",  0, 1, 0),
			new Instruction(0xb1, "LDA",  0, 1, 0),
			new Instruction(0xb2, "LDA",  0, 1, 0),
			new Instruction(0xb3, "NOP",  0, 1, 0),
			new Instruction(0xb4, "LDY",  0, 1, 0),
			new Instruction(0xb5, "LDA",  0, 1, 0),
			new Instruction(0xb6, "LDX",  0, 1, 0),
			new Instruction(0xb7, "SMB3", 0, 1, 0),
			new Instruction(0xb8, "CLV",  0, 1, 0),
			new Instruction(0xb9, "LDA",  0, 1, 0),
			new Instruction(0xba, "TSX",  0, 1, 0),
			new Instruction(0xbb, "NOP",  0, 1, 0),
			new Instruction(0xbc, "LDY",  0, 1, 0),
			new Instruction(0xbd, "LDA",  0, 1, 0),
			new Instruction(0xbe, "LDX",  0, 1, 0),
			new Instruction(0xbf, "BBS3", 0, 1, 0),
			// c0-cf
			new Instruction(0xc0, "CPY",  0, 1, 0),
			new Instruction(0xc1, "CMP",  0, 1, 0),
			new Instruction(0xc2, "NOP",  0, 1, 0),
			new Instruction(0xc3, "NOP",  0, 1, 0),
			new Instruction(0xc4, "CPY",  0, 1, 0),
			new Instruction(0xc5, "CMP",  0, 1, 0),
			new Instruction(0xc6, "DEC",  0, 1, 0),
			new Instruction(0xc7, "SMB4", 0, 1, 0),
			new Instruction(0xc8, "INY",  0, 1, 0),
			new Instruction(0xc9, "CMP",  0, 1, 0),
			new Instruction(0xca, "DEX",  0, 1, 0),
			new Instruction(0xcb, "NOP",  0, 1, 0),
			new Instruction(0xcc, "CPY",  0, 1, 0),
			new Instruction(0xcd, "CMP",  0, 1, 0),
			new Instruction(0xce, "DEC",  0, 1, 0),
			new Instruction(0xcf, "BBS4", 0, 1, 0),
			// d0-df
			new Instruction(0xd0, "BNE",  0, 1, 0),
			new Instruction(0xd1, "CMP",  0, 1, 0),
			new Instruction(0xd2, "CMP",  0, 1, 0),
			new Instruction(0xd3, "NOP",  0, 1, 0),
			new Instruction(0xd4, "NOP",  0, 1, 0),
			new Instruction(0xd5, "CMP",  0, 1, 0),
			new Instruction(0xd6, "DEC",  0, 1, 0),
			new Instruction(0xd7, "SMB5", 0, 1, 0),
			new Instruction(0xd8, "CLD",  0, 1, 0),
			new Instruction(0xd9, "CMP",  0, 1, 0),
			new Instruction(0xda, "PHX",  0, 1, 0),
			new Instruction(0xdb, "NOP",  0, 1, 0),
			new Instruction(0xdc, "NOP",  0, 1, 0),
			new Instruction(0xdd, "CMP",  0, 1, 0),
			new Instruction(0xde, "DEC",  0, 1, 0),
			new Instruction(0xdf, "BBS5", 0, 1, 0),
			// e0-ef
			new Instruction(0xe0, "CPX",  0, 1, 0),
			new Instruction(0xe1, "SBC",  0, 1, 0),
			new Instruction(0xe2, "NOP",  0, 1, 0),
			new Instruction(0xe3, "NOP",  0, 1, 0),
			new Instruction(0xe4, "CPX",  0, 1, 0),
			new Instruction(0xe5, "SBC",  0, 1, 0),
			new Instruction(0xe6, "INC",  0, 1, 0),
			new Instruction(0xe7, "SMB6", 0, 1, 0),
			new Instruction(0xe8, "INX",  0, 1, 0),
			new Instruction(0xe9, "SBC",  0, 1, 0),
			new Instruction(0xea, "NOP",  0, 1, 0),
			new Instruction(0xeb, "NOP",  0, 1, 0),
			new Instruction(0xec, "CPX",  0, 1, 0),
			new Instruction(0xed, "SBC",  0, 1, 0),
			new Instruction(0xee, "INC",  0, 1, 0),
			new Instruction(0xef, "BBS6", 0, 1, 0),
			// f0-ff
			new Instruction(0xf0, "BEQ",  0, 1, 0),
			new Instruction(0xf1, "SBC",  0, 1, 0),
			new Instruction(0xf2, "SBC",  0, 1, 0),
			new Instruction(0xf3, "NOP",  0, 1, 0),
			new Instruction(0xf4, "NOP",  0, 1, 0),
			new Instruction(0xf5, "SBC",  0, 1, 0),
			new Instruction(0xf6, "INC",  0, 1, 0),
			new Instruction(0xf7, "SMB7", 0, 1, 0),
			new Instruction(0xf8, "SED",  0, 1, 0),
			new Instruction(0xf9, "SBC",  0, 1, 0),
			new Instruction(0xfa, "PLX",  0, 1, 0),
			new Instruction(0xfb, "NOP",  0, 1, 0),
			new Instruction(0xfc, "NOP",  0, 1, 0),
			new Instruction(0xfd, "SBC",  0, 1, 0),
			new Instruction(0xfe, "INC",  0, 1, 0),
			new Instruction(0xff, "BBS7", 0, 1, 0)
	};
	
	public Cpu6502() {
		state = new CpuState6502();
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

		
		state.getStatePanel().rebuild(state);
	}

	@Override
	public boolean isBigEndian() {
		return false;
	}

	@Override
	public StatePanel getStatePanel() {
		return state.getStatePanel();
	}

	@Override
	public void step() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void go() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}
}
