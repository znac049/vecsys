package uk.org.wookey.vecsys.cpus.cpu6x09;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;

import javax.swing.JPanel;

import uk.org.wookey.vecsys.cpus.Cpu;
import uk.org.wookey.vecsys.cpus.InstructionTable;
import uk.org.wookey.vecsys.cpus.BaseStatusPanel;
import uk.org.wookey.vecsys.cpus.cpu6x09.Instruction.Mode;
import uk.org.wookey.vecsys.emulator.GBConstraints;
import uk.org.wookey.vecsys.emulator.TTLabel;
import uk.org.wookey.vecsys.utils.Logger;

public class Cpu6x09 extends Cpu {
	private static Logger _log = new Logger("6x09");

    public static final int RESET_VEC = 0xfffe;

	private CpuState state;
	private StatusPanel statusPanel;
	
	InstructionTable noPrefix;
	InstructionTable prefix10;
	InstructionTable prefix11;

	public class CpuState {
		public int pc;
		public int ir;
		
		public int a;
		public int b;
		
		public int e;
		public int f;
		
		public int x;
		public int y;
		
		public int dp;
		public int v;
		
		public int u;
		public int s;
		
		public int cc;
		
		public int instructionStartAddress;
		public int instructionLength;
		public int instructionBuff[];
		public Instruction instruction;

		public CpuState() {
			instructionBuff = new int[7];
			
			reset();
		}
		
		public void reset() {
			a = b = 0;
			e = f = 0;
			
			x = 0;
			y = 0;
			
			u = 0;
			s = 0;
			
			dp = 0;
			v = 0;
			cc = 0xa5;
		}
	}
	
	public class StatusPanel extends BaseStatusPanel {
		private static final long serialVersionUID = 1L;
		
		private TTLabel pcReg;
		private TTLabel sReg;
		private TTLabel uReg;
		private TTLabel dpReg;
		
		private TTLabel aReg;
		private TTLabel bReg;
		private TTLabel eReg;
		private TTLabel fReg;
		
		private TTLabel xReg;
		private TTLabel yReg;
		private TTLabel vReg;
		
		private TTLabel ccReg;
		
		private TTLabel ccStr;
		
		private TTLabel codeStr;
		
		private Color headingColour = Color.YELLOW;

		public StatusPanel() {
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
			gbc.right();
			
			add(new TTLabel("V", headingColour), gbc);
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
			
			vReg = new TTLabel("----");
			add(vReg, gbc);
			gbc.right();
			
			dpReg = new TTLabel("--");
			add(dpReg, gbc);
			gbc.nl();
			
			add(new TTLabel("A", headingColour), gbc);
			gbc.right();
			
			add(new TTLabel("B", headingColour), gbc);
			gbc.right();
			
			add(new TTLabel("E", headingColour), gbc);
			gbc.right();
			
			add(new TTLabel("F", headingColour), gbc);
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
			
			eReg = new TTLabel("--");
			add(eReg, gbc);
			gbc.right();
			
			fReg = new TTLabel("--");
			add(fReg, gbc);
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
				dpReg.setText(String.format("%02x",  state.dp));
				
				aReg.setText(String.format("%02x", state.a));
				bReg.setText(String.format("%02x", state.b));
			
				eReg.setText(String.format("%02x", state.a));
				fReg.setText(String.format("%02x", state.b));
			
				vReg.setText(String.format("%04x",  state.v));
				xReg.setText(String.format("%04x", state.x));
				yReg.setText(String.format("%04x", state.y));
			
				sReg.setText(String.format("%02x", state.s));
				uReg.setText(String.format("%02x", state.u));
			
				ccReg.setText(String.format("%02x", state.cc));
				ccStr.setText(ccString());

				codeStr.setText(codeString());
			}
		}
		
		private String codeString() {
			String res = String.format("%04x: ",  state.instructionStartAddress);
			
			for (int i=0; i<7; i++) {
				if (i < state.instructionLength) {
					res += String.format("%02x ",  state.instructionBuff[i]);
				}
				else {
					res += "   ";
				}
			}
			
			res += String.format("%-6s", state.instruction.toString());
			
			switch (state.instruction.mode) {
			case IMPLIED:
				// No operand
				break;

			case IMMBYTE:
				res += String.format("#$%02x", state.instructionBuff[state.instructionLength-1]);
				break;
				
			case IMMWORD:
				res += String.format("#$%02x%02x", state.instructionBuff[state.instructionLength-2], state.instructionBuff[state.instructionLength-1]);
				break;

			case IMMQUAD:
				res += String.format("#$%02x%02x%02x%02x", state.instructionBuff[state.instructionLength-4], state.instructionBuff[state.instructionLength-3], state.instructionBuff[state.instructionLength-2], state.instructionBuff[state.instructionLength-1]);
				break;

			case DIRECT:
				res += String.format("<$%02x", state.instructionBuff[state.instructionLength-1]);				
				break;

			case EXTENDED:
				res += String.format("$%02x%02x", state.instructionBuff[state.instructionLength-2], state.instructionBuff[state.instructionLength-1]);				
				break;

			case INDEXED:
				res += String.format("[$%02x%02x]", state.instructionBuff[state.instructionLength-2], state.instructionBuff[state.instructionLength-1]);				
				break;

			case SYSPOST:
				res += regList('U');
				break;
				
			case USRPOST:
				res += regList('S');
				break;

			case RELWORD:
				{
					int delta = (state.instructionBuff[state.instructionLength-2] << 8) | state.instructionBuff[state.instructionLength-1];
				
					if ((delta & 0x8000) != 0) {
						delta |= 0xffff0000;
					}
					res += String.format("delta %d",  delta);
				}
				break;
				
			case RELBYTE:
				{	
					int delta = state.instructionBuff[state.instructionLength-1];
				
					if ((delta & 0x80) != 0) {
						delta |= 0xffffff00;
					}
					res += String.format("delta %d",  delta);
				}
				break;

			case BLKMOVE:
				res += "blockmove ";
				break;
				
			case IMMDIRECT:
				res += "IMMDIRECT";
				break;
				
			case REGPOST:
				res += "REGPOST";
				break;
				
			case REGREG:
				res += "REGREG";
				break;
				
			case SINGLEBIT:
				res += "SINGLEBIT";
				break;
				
			default:
				res += "BADBADBAD";
				break;	
			}
			
			return res;
		}
		
		private String regList(char stack) {
			int op = state.instructionBuff[state.instructionLength-1];
			String res = "";
			boolean first = true;
			
			if ((op & 0x80) != 0) {
				if (!first) {
					res += ",";
					first = false;
				}
				
				res += "PC";
			}
			
			if ((op & 0x40) != 0) {
				if (!first) {
					res += ",";
					first = false;
				}
				
				res += stack;
			}
			
			if ((op & 0x20) != 0) {
				if (!first) {
					res += ",";
					first = false;
				}
				
				res += "Y";
			}
			
			if ((op & 0x10) != 0) {
				if (!first) {
					res += ",";
					first = false;
				}
				
				res += "X";
			}
			
			if ((op & 0x08) != 0) {
				if (!first) {
					res += ",";
					first = false;
				}
				
				res += "DP";
			}
			
			if ((op & 0x04) != 0) {
				if (!first) {
					res += ",";
					first = false;
				}
				
				res += "B";
			}
			
			if ((op & 0x02) != 0) {
				if (!first) {
					res += ",";
					first = false;
				}
				
				res += "A";
			}
			
			if ((op & 0x01) != 0) {
				if (!first) {
					res += ",";
				}
				
				res += "CC";
			}
			
			return res;
		}
		
		private String ccString() {
			int cc = state.cc;
			
			String res = ((cc & 0x80) != 0)?"E":"-";
			
			res += ((cc & 0x40) != 0)?"F":"-";
			res += ((cc & 0x20) != 0)?"H":"-";
			res += ((cc & 0x10) != 0)?"I":"-";
			res += ((cc & 0x08) != 0)?"N":"-";
			res += ((cc & 0x04) != 0)?"Z":"-";
			res += ((cc & 0x02) != 0)?"V":"-";
			res += ((cc & 0x01) != 0)?"C":"-";
			
			return res;
		}
	}

	public Cpu6x09() {
		noPrefix = new InstructionTable();
		prefix10 = new InstructionTable();
		prefix11 = new InstructionTable();
		
		buildInstructionTables();
		
		state = new CpuState();
		statusPanel = new StatusPanel();
	}
	
	@Override
	public void reset() {
		state.reset();
		state.pc = bus.getWord(RESET_VEC);
		
		_log.logInfo(String.format("Read RESET vector @%04x -> %04x",  RESET_VEC, state.pc));
		
		fetchNextInstruction();
		fetchOperand();
		
		statusPanel.update();
	}

	private int fetchByte() {
		int b = bus.getByte(state.pc);
		
		state.instructionBuff[state.instructionLength] = b;
		state.instructionLength++;
		
		state.pc++;
		
		return b;
	}
	
	public void fetchNextInstruction() {
		state.instructionStartAddress = state.pc;
		state.instructionLength = 0;
		
		state.ir = fetchByte();
		
		if (state.ir == 0x10) {
			state.ir = fetchByte();
			state.instruction = (Instruction) prefix10.get(state.ir);
		}
		else if (state.ir == 0x11) {
			state.ir = fetchByte();
			state.instruction = (Instruction) prefix11.get(state.ir);
		}
		else {
			state.instruction = (Instruction) noPrefix.get(state.ir);
		}
		
		
		_log.logInfo(String.format("Instruction at $%04x: %02x, %d bytes", state.instructionStartAddress, state.ir, state.instructionLength));
	}
	
	public void fetchOperand() {
		if (state.instruction != null) {
			switch (state.instruction.mode) {
			case BLKMOVE:
			case IMPLIED:
			case INDEXED:
			case REGPOST:
			case REGREG:
			case SINGLEBIT:
			case IMMDIRECT:
				break;

			case DIRECT:
			case IMMBYTE:
			case RELBYTE:
			case SYSPOST:
			case USRPOST:
				fetchByte();
				break;

			case IMMWORD:
			case RELWORD:
			case EXTENDED:
				fetchByte();
				fetchByte();
				break;
				
			case IMMQUAD:
				fetchByte();
				fetchByte();
				fetchByte();
				fetchByte();
				break;

			default:
				break;		
			}
		}		
	}

	@Override
	public boolean isBigEndian() {
		return true;
	}

	@Override
	public void step() {
		executeInstruction();
		
		fetchNextInstruction();
		fetchOperand();
		
		if (statusPanel.isEnabled()) {
			statusPanel.update();
		}
	}

	private void executeInstruction() {
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
	public BaseStatusPanel getStatusPanel() {
		return statusPanel;
	}

	@Override
	public void interrupt(int interruptId) {
		// TODO Auto-generated method stub
		
	}

	private void buildInstructionTables() {
		try {
			noPrefix.add(new Instruction(0x00, "NEG",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x01, "OIM",  0, Mode.IMMDIRECT, 0));
			noPrefix.add(new Instruction(0x02, "AIM",  0, Mode.IMMDIRECT, 0));
			noPrefix.add(new Instruction(0x03, "COM",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x04, "LSR",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x05, "EIM",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x06, "ROR",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x07, "ASR",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x08, "ASL",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x09, "ROL",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x0a, "DEC",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x0b, "TIM",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x0c, "INC",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x0d, "TST",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x0e, "JMP",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x0f, "CLR",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x12, "NOP",  0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x13, "SYNC", 0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x14, "SEXW", 0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x16, "LBRA", 0, Mode.RELWORD, 0));
			noPrefix.add(new Instruction(0x17, "LBSR", 0, Mode.RELWORD, 0));
			noPrefix.add(new Instruction(0x19, "DAA",  0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x1a, "ORCC", 0, Mode.IMMBYTE, 0));
			noPrefix.add(new Instruction(0x1c, "ANDCC", 0, Mode.IMMBYTE, 0));
			noPrefix.add(new Instruction(0x1d, "SEX",  0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x1e, "EXG",  0, Mode.REGPOST, 0));
			noPrefix.add(new Instruction(0x1f, "TFR",  0, Mode.REGPOST, 0));
			noPrefix.add(new Instruction(0x20, "BRA",  0, Mode.RELBYTE, 0));
			noPrefix.add(new Instruction(0x21, "BRN",  0, Mode.RELBYTE, 0));
			noPrefix.add(new Instruction(0x22, "BHI",  0, Mode.RELBYTE, 0));
			noPrefix.add(new Instruction(0x23, "BLS",  0, Mode.RELBYTE, 0));
			noPrefix.add(new Instruction(0x24, "BCC",  0, Mode.RELBYTE, 0));
			noPrefix.add(new Instruction(0x25, "BCS",  0, Mode.RELBYTE, 0));
			noPrefix.add(new Instruction(0x26, "BNE",  0, Mode.RELBYTE, 0));
			noPrefix.add(new Instruction(0x27, "BEQ",  0, Mode.RELBYTE, 0));
			noPrefix.add(new Instruction(0x28, "BVC",  0, Mode.RELBYTE, 0));
			noPrefix.add(new Instruction(0x29, "BVS",  0, Mode.RELBYTE, 0));
			noPrefix.add(new Instruction(0x2a, "BPL",  0, Mode.RELBYTE, 0));
			noPrefix.add(new Instruction(0x2b, "BMI",  0, Mode.RELBYTE, 0));
			noPrefix.add(new Instruction(0x2c, "BGE",  0, Mode.RELBYTE, 0));
			noPrefix.add(new Instruction(0x2d, "BLT",  0, Mode.RELBYTE, 0));
			noPrefix.add(new Instruction(0x2e, "BGT",  0, Mode.RELBYTE, 0));
			noPrefix.add(new Instruction(0x2f, "BLE",  0, Mode.RELBYTE, 0));
			noPrefix.add(new Instruction(0x30, "LEAX", 0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0x31, "LEAY", 0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0x32, "LEAS", 0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0x33, "LEAU", 0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0x34, "PSHS", 0, Mode.SYSPOST, 0));
			noPrefix.add(new Instruction(0x35, "PULS", 0, Mode.SYSPOST, 0));
			noPrefix.add(new Instruction(0x36, "PSHU", 0, Mode.USRPOST, 0));
			noPrefix.add(new Instruction(0x37, "PULU", 0, Mode.USRPOST, 0));
			noPrefix.add(new Instruction(0x39, "RTS",  0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x3a, "ABX",  0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x3b, "RTI",  0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x3c, "CWAI", 0, Mode.IMMBYTE, 0));
			noPrefix.add(new Instruction(0x3d, "MUL",  0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x3e, "RESET", 0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x3f, "SWI",  0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x40, "NEGA", 0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x43, "COMA", 0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x44, "LSRA", 0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x46, "RORA", 0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x47, "ASRA", 0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x48, "ASLA", 0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x49, "ROLA", 0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x4a, "DECA", 0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x4c, "INCA", 0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x4d, "TSTA", 0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x4f, "CLRA", 0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x50, "NEGB", 0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x53, "COMB", 0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x54, "LSRB", 0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x56, "RORB", 0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x57, "ASRB", 0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x58, "ASLB", 0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x59, "ROLB", 0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x5a, "DECB", 0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x5c, "INCB", 0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x5d, "TSTB", 0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x5f, "CLRB", 0, Mode.IMPLIED, 0));
			noPrefix.add(new Instruction(0x60, "NEG",  0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0x61, "OIM",  0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0x62, "AIM",  0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0x63, "COM",  0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0x64, "LSR",  0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0x65, "EIM",  0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0x66, "ROR",  0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0x67, "ASR",  0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0x68, "ASL",  0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0x69, "ROL",  0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0x6a, "DEC",  0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0x6b, "TIM",  0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0x6c, "INC",  0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0x6d, "TST",  0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0x6e, "JMP",  0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0x6f, "CLR",  0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0x70, "NEG",  0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0x71, "OIM",  0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0x72, "AIM",  0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0x73, "COM",  0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0x74, "LSR",  0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0x75, "EIM",  0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0x76, "ROR",  0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0x77, "ASR",  0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0x78, "ASL",  0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0x79, "ROL",  0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0x7a, "DEC",  0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0x7b, "TIM",  0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0x7c, "INC",  0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0x7d, "TST",  0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0x7e, "JMP",  0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0x7f, "CLR",  0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0x80, "SUBA", 0, Mode.IMMBYTE, 0));
			noPrefix.add(new Instruction(0x81, "CMPA", 0, Mode.IMMBYTE, 0));
			noPrefix.add(new Instruction(0x82, "SBCA", 0, Mode.IMMBYTE, 0));
			noPrefix.add(new Instruction(0x83, "SUBD", 0, Mode.IMMWORD, 0));
			noPrefix.add(new Instruction(0x84, "ANDA", 0, Mode.IMMBYTE, 0));
			noPrefix.add(new Instruction(0x85, "BITA", 0, Mode.IMMBYTE, 0));
			noPrefix.add(new Instruction(0x86, "LDA",  0, Mode.IMMBYTE, 0));
			noPrefix.add(new Instruction(0x88, "EORA", 0, Mode.IMMBYTE, 0));
			noPrefix.add(new Instruction(0x89, "ADCA", 0, Mode.IMMBYTE, 0));
			noPrefix.add(new Instruction(0x8a, "ORA",  0, Mode.IMMBYTE, 0));
			noPrefix.add(new Instruction(0x8b, "ADDA", 0, Mode.IMMBYTE, 0));
			noPrefix.add(new Instruction(0x8c, "CMPX", 0, Mode.IMMWORD, 0));
			noPrefix.add(new Instruction(0x8d, "BSR",  0, Mode.RELBYTE, 0));
			noPrefix.add(new Instruction(0x8e, "LDX",  0, Mode.IMMWORD, 0));
			noPrefix.add(new Instruction(0x90, "SUBA", 0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x91, "CMPA", 0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x92, "SBCA", 0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x93, "SUBD", 0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x94, "ANDA", 0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x95, "BITA", 0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x96, "LDA",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x97, "STA",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x98, "EORA", 0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x99, "ADCA", 0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x9a, "ORA",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x9b, "ADDA", 0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x9c, "CMPX", 0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x9d, "JSR",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x9e, "LDX",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x9f, "STX",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0xa0, "SUBA", 0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xa1, "CMPA", 0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xa2, "SBCA", 0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xa3, "SUBD", 0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xa4, "ANDA", 0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xa5, "BITA", 0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xa6, "LDA",  0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xa7, "STA",  0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xa8, "EORA", 0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xa9, "ADCA", 0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xaa, "ORA",  0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xab, "ADDA", 0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xac, "CMPX", 0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xad, "JSR",  0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xae, "LDX",  0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xaf, "STX",  0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xb0, "SUBA", 0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xb1, "CMPA", 0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xb2, "SBCA", 0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xb3, "SUBD", 0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xb4, "ANDA", 0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xb5, "BITA", 0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xb6, "LDA",  0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xb7, "STA",  0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xb8, "EORA", 0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xb9, "ADCA", 0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xba, "ORA",  0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xbb, "ADDA", 0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xbc, "CMPX", 0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xbd, "JSR",  0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xbe, "LDX",  0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xbf, "STX",  0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xc0, "SUBB", 0, Mode.IMMBYTE, 0));
			noPrefix.add(new Instruction(0xc1, "CMPB", 0, Mode.IMMBYTE, 0));
			noPrefix.add(new Instruction(0xc2, "SBCB", 0, Mode.IMMBYTE, 0));
			noPrefix.add(new Instruction(0xc3, "ADDD", 0, Mode.IMMWORD, 0));
			noPrefix.add(new Instruction(0xc4, "ANDB", 0, Mode.IMMBYTE, 0));
			noPrefix.add(new Instruction(0xc5, "BITB", 0, Mode.IMMBYTE, 0));
			noPrefix.add(new Instruction(0xc6, "LDB",  0, Mode.IMMBYTE, 0));
			noPrefix.add(new Instruction(0xc8, "EORB", 0, Mode.IMMBYTE, 0));
			noPrefix.add(new Instruction(0xc9, "ADCB", 0, Mode.IMMBYTE, 0));
			noPrefix.add(new Instruction(0xca, "ORB",  0, Mode.IMMBYTE, 0));
			noPrefix.add(new Instruction(0xcb, "ADDB", 0, Mode.IMMBYTE, 0));
			noPrefix.add(new Instruction(0xcc, "LDD",  0, Mode.IMMWORD, 0));
			noPrefix.add(new Instruction(0xcd, "LDQ",  0, Mode.IMMQUAD, 0));
			noPrefix.add(new Instruction(0xce, "LDU",  0, Mode.IMMWORD, 0));
			noPrefix.add(new Instruction(0xd0, "SUBB", 0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0xd1, "CMPB", 0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0xd2, "SBCB", 0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0xd3, "ADDD", 0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0xd4, "ANDB", 0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0xd5, "BITB", 0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0xd6, "LDB",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0xd7, "STB",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0xd8, "EORB", 0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0xd9, "ADCB", 0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0xda, "ORB",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0xdb, "ADDB", 0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0xdc, "LDD",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0xdd, "STD",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0xde, "LDU",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0xdf, "STU",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0xe0, "SUBB", 0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xe1, "CMPB", 0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xe2, "SBCB", 0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xe3, "ADDD", 0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xe4, "ANDB", 0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xe5, "BITB", 0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xe6, "LDB",  0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xe7, "STB",  0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xe8, "EORB", 0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xe9, "ADCB", 0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xea, "ORB",  0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xeb, "ADDB", 0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xec, "LDD",  0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xed, "STD",  0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xee, "LDU",  0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xef, "STU",  0, Mode.INDEXED, 0));
			noPrefix.add(new Instruction(0xf0, "SUBB", 0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xf1, "CMPB", 0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xf2, "SBCB", 0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xf3, "ADDD", 0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xf4, "ANDB", 0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xf5, "BITB", 0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xf6, "LDB",  0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xf7, "STB",  0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xf8, "EORB", 0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xf9, "ADCB", 0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xfa, "ORB",  0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xfb, "ADDB", 0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xfc, "LDD",  0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xfd, "STD",  0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xfe, "LDU",  0, Mode.EXTENDED, 0));
			noPrefix.add(new Instruction(0xff, "STU",  0, Mode.EXTENDED, 0));


			prefix10.add(new Instruction(0x21, "LBRN", 0, Mode.RELWORD, 0));
			prefix10.add(new Instruction(0x22, "LBHI", 0, Mode.RELWORD, 0));
			prefix10.add(new Instruction(0x23, "LBLS", 0, Mode.RELWORD, 0));
			prefix10.add(new Instruction(0x24, "LBCC", 0, Mode.RELWORD, 0));
			prefix10.add(new Instruction(0x25, "LBCS", 0, Mode.RELWORD, 0));
			prefix10.add(new Instruction(0x26, "LBNE", 0, Mode.RELWORD, 0));
			prefix10.add(new Instruction(0x27, "LBEQ", 0, Mode.RELWORD, 0));
			prefix10.add(new Instruction(0x28, "LBVC", 0, Mode.RELWORD, 0));
			prefix10.add(new Instruction(0x29, "LBVS", 0, Mode.RELWORD, 0));
			prefix10.add(new Instruction(0x2a, "LBPL", 0, Mode.RELWORD, 0));
			prefix10.add(new Instruction(0x2b, "LBMI", 0, Mode.RELWORD, 0));
			prefix10.add(new Instruction(0x2c, "LBGE", 0, Mode.RELWORD, 0));
			prefix10.add(new Instruction(0x2d, "LBLT", 0, Mode.RELWORD, 0));
			prefix10.add(new Instruction(0x2e, "LBGT", 0, Mode.RELWORD, 0));
			prefix10.add(new Instruction(0x2f, "LBLE", 0, Mode.RELWORD, 0));
			prefix10.add(new Instruction(0x30, "ADDR", 0, Mode.REGREG, 0));
			prefix10.add(new Instruction(0x31, "ADCR", 0, Mode.REGREG, 0));
			prefix10.add(new Instruction(0x32, "SUBR", 0, Mode.REGREG, 0));
			prefix10.add(new Instruction(0x33, "SBCR", 0, Mode.REGREG, 0));
			prefix10.add(new Instruction(0x34, "ANDR", 0, Mode.REGREG, 0));
			prefix10.add(new Instruction(0x35, "ORR",  0, Mode.REGREG, 0));
			prefix10.add(new Instruction(0x36, "EORR", 0, Mode.REGREG, 0));
			prefix10.add(new Instruction(0x37, "CMPR", 0, Mode.REGREG, 0));
			prefix10.add(new Instruction(0x38, "PSHSW", 0, Mode.IMPLIED, 0));
			prefix10.add(new Instruction(0x39, "PULSW", 0, Mode.IMPLIED, 0));
			prefix10.add(new Instruction(0x3a, "PSHUW", 0, Mode.IMPLIED, 0));
			prefix10.add(new Instruction(0x3b, "PULUW", 0, Mode.IMPLIED, 0));
			prefix10.add(new Instruction(0x3f, "SWI2", 0, Mode.IMPLIED, 0));
			prefix10.add(new Instruction(0x40, "NEGD", 0, Mode.IMPLIED, 0));
			prefix10.add(new Instruction(0x43, "COMD", 0, Mode.IMPLIED, 0));
			prefix10.add(new Instruction(0x44, "LSRD", 0, Mode.IMPLIED, 0));
			prefix10.add(new Instruction(0x46, "RORD", 0, Mode.IMPLIED, 0));
			prefix10.add(new Instruction(0x47, "ASRD", 0, Mode.IMPLIED, 0));
			prefix10.add(new Instruction(0x48, "ASLD", 0, Mode.IMPLIED, 0));
			prefix10.add(new Instruction(0x49, "ROLD", 0, Mode.IMPLIED, 0));
			prefix10.add(new Instruction(0x4a, "DECD", 0, Mode.IMPLIED, 0));
			prefix10.add(new Instruction(0x4c, "INCD", 0, Mode.IMPLIED, 0));
			prefix10.add(new Instruction(0x4d, "TSTD", 0, Mode.IMPLIED, 0));
			prefix10.add(new Instruction(0x4f, "CLRD", 0, Mode.IMPLIED, 0));
			prefix10.add(new Instruction(0x53, "COMW", 0, Mode.IMPLIED, 0));
			prefix10.add(new Instruction(0x54, "LSRW", 0, Mode.IMPLIED, 0));
			prefix10.add(new Instruction(0x56, "RORW", 0, Mode.IMPLIED, 0));
			prefix10.add(new Instruction(0x59, "ROLW", 0, Mode.IMPLIED, 0));
			prefix10.add(new Instruction(0x5a, "DECW", 0, Mode.IMPLIED, 0));
			prefix10.add(new Instruction(0x5c, "INCW", 0, Mode.IMPLIED, 0));
			prefix10.add(new Instruction(0x5d, "TSTW", 0, Mode.IMPLIED, 0));
			prefix10.add(new Instruction(0x5f, "CLRW", 0, Mode.IMPLIED, 0));
			prefix10.add(new Instruction(0x80, "SUBW", 0, Mode.IMMWORD, 0));
			prefix10.add(new Instruction(0x81, "CMPW", 0, Mode.IMMWORD, 0));
			prefix10.add(new Instruction(0x82, "SBCD", 0, Mode.IMMWORD, 0));
			prefix10.add(new Instruction(0x83, "CMPD", 0, Mode.IMMWORD, 0));
			prefix10.add(new Instruction(0x84, "ANDD", 0, Mode.IMMWORD, 0));
			prefix10.add(new Instruction(0x85, "BITD", 0, Mode.IMMWORD, 0));
			prefix10.add(new Instruction(0x86, "LDW",  0, Mode.IMMWORD, 0));
			prefix10.add(new Instruction(0x88, "EORD", 0, Mode.IMMWORD, 0));
			prefix10.add(new Instruction(0x89, "ADCD", 0, Mode.IMMWORD, 0));
			prefix10.add(new Instruction(0x8a, "ORD",  0, Mode.IMMWORD, 0));
			prefix10.add(new Instruction(0x8b, "ADDW", 0, Mode.IMMWORD, 0));
			prefix10.add(new Instruction(0x8c, "CMPY", 0, Mode.IMMWORD, 0));
			prefix10.add(new Instruction(0x8e, "LDY",  0, Mode.IMMWORD, 0));
			prefix10.add(new Instruction(0x90, "SUBW", 0, Mode.DIRECT, 0));
			prefix10.add(new Instruction(0x91, "CMPW", 0, Mode.DIRECT, 0));
			prefix10.add(new Instruction(0x92, "SBCD", 0, Mode.DIRECT, 0));
			prefix10.add(new Instruction(0x93, "CMPD", 0, Mode.DIRECT, 0));
			prefix10.add(new Instruction(0x94, "ANDD", 0, Mode.DIRECT, 0));
			prefix10.add(new Instruction(0x95, "BITD", 0, Mode.DIRECT, 0));
			prefix10.add(new Instruction(0x96, "LDW",  0, Mode.DIRECT, 0));
			prefix10.add(new Instruction(0x97, "STW",  0, Mode.DIRECT, 0));
			prefix10.add(new Instruction(0x98, "EORD", 0, Mode.DIRECT, 0));
			prefix10.add(new Instruction(0x99, "ADCD", 0, Mode.DIRECT, 0));
			prefix10.add(new Instruction(0x9a, "ORD",  0, Mode.DIRECT, 0));
			prefix10.add(new Instruction(0x9b, "ADDW", 0, Mode.DIRECT, 0));
			prefix10.add(new Instruction(0x9c, "CMPY", 0, Mode.DIRECT, 0));
			prefix10.add(new Instruction(0x9e, "LDY",  0, Mode.DIRECT, 0));
			prefix10.add(new Instruction(0x9f, "STY",  0, Mode.DIRECT, 0));
			prefix10.add(new Instruction(0xa0, "SUBW", 0, Mode.INDEXED, 0));
			prefix10.add(new Instruction(0xa1, "CMPW", 0, Mode.INDEXED, 0));
			prefix10.add(new Instruction(0xa2, "SBCD", 0, Mode.INDEXED, 0));
			prefix10.add(new Instruction(0xa3, "CMPD", 0, Mode.INDEXED, 0));
			prefix10.add(new Instruction(0xa4, "ANDD", 0, Mode.INDEXED, 0));
			prefix10.add(new Instruction(0xa5, "BITD", 0, Mode.INDEXED, 0));
			prefix10.add(new Instruction(0xa6, "LDW",  0, Mode.INDEXED, 0));
			prefix10.add(new Instruction(0xa7, "STW",  0, Mode.INDEXED, 0));
			prefix10.add(new Instruction(0xa8, "EORD", 0, Mode.INDEXED, 0));
			prefix10.add(new Instruction(0xa9, "ADCD", 0, Mode.INDEXED, 0));
			prefix10.add(new Instruction(0xaa, "ORD",  0, Mode.INDEXED, 0));
			prefix10.add(new Instruction(0xab, "ADDW", 0, Mode.INDEXED, 0));
			prefix10.add(new Instruction(0xac, "CMPY", 0, Mode.INDEXED, 0));
			prefix10.add(new Instruction(0xae, "LDY",  0, Mode.INDEXED, 0));
			prefix10.add(new Instruction(0xaf, "STY",  0, Mode.INDEXED, 0));
			prefix10.add(new Instruction(0xb0, "SUBW", 0, Mode.EXTENDED, 0));
			prefix10.add(new Instruction(0xb1, "CMPW", 0, Mode.EXTENDED, 0));
			prefix10.add(new Instruction(0xb2, "SBCD", 0, Mode.EXTENDED, 0));
			prefix10.add(new Instruction(0xb3, "CMPD", 0, Mode.EXTENDED, 0));
			prefix10.add(new Instruction(0xb4, "ANDD", 0, Mode.EXTENDED, 0));
			prefix10.add(new Instruction(0xb5, "BITD", 0, Mode.EXTENDED, 0));
			prefix10.add(new Instruction(0xb6, "LDW",  0, Mode.EXTENDED, 0));
			prefix10.add(new Instruction(0xb7, "STW",  0, Mode.EXTENDED, 0));
			prefix10.add(new Instruction(0xb8, "EORD", 0, Mode.EXTENDED, 0));
			prefix10.add(new Instruction(0xb9, "ADCD", 0, Mode.EXTENDED, 0));
			prefix10.add(new Instruction(0xba, "ORD",  0, Mode.EXTENDED, 0));
			prefix10.add(new Instruction(0xbb, "ADDW", 0, Mode.EXTENDED, 0));
			prefix10.add(new Instruction(0xbc, "CMPY", 0, Mode.EXTENDED, 0));
			prefix10.add(new Instruction(0xbe, "LDY",  0, Mode.EXTENDED, 0));
			prefix10.add(new Instruction(0xbf, "STY",  0, Mode.EXTENDED, 0));
			prefix10.add(new Instruction(0xce, "LDS",  0, Mode.IMMWORD, 0));
			prefix10.add(new Instruction(0xdc, "LDQ",  0, Mode.DIRECT, 0));
			prefix10.add(new Instruction(0xdd, "STQ",  0, Mode.DIRECT, 0));
			prefix10.add(new Instruction(0xde, "LDS",  0, Mode.DIRECT, 0));
			prefix10.add(new Instruction(0xdf, "STS",  0, Mode.DIRECT, 0));
			prefix10.add(new Instruction(0xec, "LDQ",  0, Mode.INDEXED, 0));
			prefix10.add(new Instruction(0xed, "STQ",  0, Mode.INDEXED, 0));
			prefix10.add(new Instruction(0xee, "LDS",  0, Mode.INDEXED, 0));
			prefix10.add(new Instruction(0xef, "STS",  0, Mode.INDEXED, 0));
			prefix10.add(new Instruction(0xfc, "LDQ",  0, Mode.EXTENDED, 0));
			prefix10.add(new Instruction(0xfd, "STQ",  0, Mode.EXTENDED, 0));
			prefix10.add(new Instruction(0xfe, "LDS",  0, Mode.EXTENDED, 0));
			prefix10.add(new Instruction(0xff, "STS",  0, Mode.EXTENDED, 0));


			prefix11.add(new Instruction(0x30, "BAND", 0, Mode.SINGLEBIT, 0));
			prefix11.add(new Instruction(0x31, "BIAND", 0, Mode.SINGLEBIT, 0));
			prefix11.add(new Instruction(0x32, "BOR",  0, Mode.SINGLEBIT, 0));
			prefix11.add(new Instruction(0x33, "BIOR", 0, Mode.SINGLEBIT, 0));
			prefix11.add(new Instruction(0x34, "BEOR", 0, Mode.SINGLEBIT, 0));
			prefix11.add(new Instruction(0x35, "BIEOR", 0, Mode.SINGLEBIT, 0));
			prefix11.add(new Instruction(0x36, "LDBT", 0, Mode.SINGLEBIT, 0));
			prefix11.add(new Instruction(0x37, "STBT", 0, Mode.SINGLEBIT, 0));
			prefix11.add(new Instruction(0x38, "TFM",  0, Mode.BLKMOVE, 0));
			prefix11.add(new Instruction(0x39, "TFM",  0, Mode.BLKMOVE, 0));
			prefix11.add(new Instruction(0x3a, "TFM",  0, Mode.BLKMOVE, 0));
			prefix11.add(new Instruction(0x3b, "TFM",  0, Mode.BLKMOVE, 0));
			prefix11.add(new Instruction(0x3c, "BITMD", 0, Mode.IMMBYTE, 0));
			prefix11.add(new Instruction(0x3d, "LDMD", 0, Mode.IMMBYTE, 0));
			prefix11.add(new Instruction(0x3f, "SWI3", 0, Mode.IMPLIED, 0));
			prefix11.add(new Instruction(0x43, "COME", 0, Mode.IMPLIED, 0));
			prefix11.add(new Instruction(0x4a, "DECE", 0, Mode.IMPLIED, 0));
			prefix11.add(new Instruction(0x4c, "INCE", 0, Mode.IMPLIED, 0));
			prefix11.add(new Instruction(0x4d, "TSTE", 0, Mode.IMPLIED, 0));
			prefix11.add(new Instruction(0x4f, "CLRE", 0, Mode.IMPLIED, 0));
			prefix11.add(new Instruction(0x53, "COMF", 0, Mode.IMPLIED, 0));
			prefix11.add(new Instruction(0x5a, "DECF", 0, Mode.IMPLIED, 0));
			prefix11.add(new Instruction(0x5c, "INCF", 0, Mode.IMPLIED, 0));
			prefix11.add(new Instruction(0x5d, "TSTF", 0, Mode.IMPLIED, 0));
			prefix11.add(new Instruction(0x5f, "CLRF", 0, Mode.IMPLIED, 0));
			prefix11.add(new Instruction(0x80, "SUBE", 0, Mode.IMMBYTE, 0));
			prefix11.add(new Instruction(0x81, "CMPE", 0, Mode.IMMBYTE, 0));
			prefix11.add(new Instruction(0x83, "CMPU", 0, Mode.IMMWORD, 0));
			prefix11.add(new Instruction(0x86, "LDE",  0, Mode.IMMBYTE, 0));
			prefix11.add(new Instruction(0x8b, "ADDE", 0, Mode.IMMBYTE, 0));
			prefix11.add(new Instruction(0x8c, "CMPS", 0, Mode.IMMWORD, 0));
			prefix11.add(new Instruction(0x8d, "DIVD", 0, Mode.IMMBYTE, 0));
			prefix11.add(new Instruction(0x8e, "DIVQ", 0, Mode.IMMWORD, 0));
			prefix11.add(new Instruction(0x8f, "MULD", 0, Mode.IMMWORD, 0));
			prefix11.add(new Instruction(0x90, "SUBE", 0, Mode.DIRECT, 0));
			prefix11.add(new Instruction(0x91, "CMPE", 0, Mode.DIRECT, 0));
			prefix11.add(new Instruction(0x93, "CMPU", 0, Mode.DIRECT, 0));
			prefix11.add(new Instruction(0x96, "LDE",  0, Mode.DIRECT, 0));
			prefix11.add(new Instruction(0x97, "STE",  0, Mode.DIRECT, 0));
			prefix11.add(new Instruction(0x9b, "ADDE", 0, Mode.DIRECT, 0));
			prefix11.add(new Instruction(0x9c, "CMPS", 0, Mode.DIRECT, 0));
			prefix11.add(new Instruction(0x9d, "DIVD", 0, Mode.DIRECT, 0));
			prefix11.add(new Instruction(0x9e, "DIVQ", 0, Mode.DIRECT, 0));
			prefix11.add(new Instruction(0x9f, "MULD", 0, Mode.DIRECT, 0));
			prefix11.add(new Instruction(0xa0, "SUBE", 0, Mode.INDEXED, 0));
			prefix11.add(new Instruction(0xa1, "CMPE", 0, Mode.INDEXED, 0));
			prefix11.add(new Instruction(0xa3, "CMPU", 0, Mode.INDEXED, 0));
			prefix11.add(new Instruction(0xa6, "LDE",  0, Mode.INDEXED, 0));
			prefix11.add(new Instruction(0xa7, "STE",  0, Mode.INDEXED, 0));
			prefix11.add(new Instruction(0xab, "ADDE", 0, Mode.INDEXED, 0));
			prefix11.add(new Instruction(0xac, "CMPS", 0, Mode.INDEXED, 0));
			prefix11.add(new Instruction(0xad, "DIVD", 0, Mode.INDEXED, 0));
			prefix11.add(new Instruction(0xae, "DIVQ", 0, Mode.INDEXED, 0));
			prefix11.add(new Instruction(0xaf, "MULD", 0, Mode.INDEXED, 0));
			prefix11.add(new Instruction(0xb0, "SUBE", 0, Mode.EXTENDED, 0));
			prefix11.add(new Instruction(0xb1, "CMPE", 0, Mode.EXTENDED, 0));
			prefix11.add(new Instruction(0xb3, "CMPU", 0, Mode.EXTENDED, 0));
			prefix11.add(new Instruction(0xb6, "LDE",  0, Mode.EXTENDED, 0));
			prefix11.add(new Instruction(0xb7, "STE",  0, Mode.EXTENDED, 0));
			prefix11.add(new Instruction(0xbb, "ADDE", 0, Mode.EXTENDED, 0));
			prefix11.add(new Instruction(0xbc, "CMPS", 0, Mode.EXTENDED, 0));
			prefix11.add(new Instruction(0xbd, "DIVD", 0, Mode.EXTENDED, 0));
			prefix11.add(new Instruction(0xbe, "DIVQ", 0, Mode.EXTENDED, 0));
			prefix11.add(new Instruction(0xbf, "MULD", 0, Mode.EXTENDED, 0));
			prefix11.add(new Instruction(0xc0, "SUBF", 0, Mode.IMMBYTE, 0));
			prefix11.add(new Instruction(0xc1, "CMPF", 0, Mode.IMMBYTE, 0));
			prefix11.add(new Instruction(0xc6, "LDF",  0, Mode.IMMBYTE, 0));
			prefix11.add(new Instruction(0xcb, "ADDF", 0, Mode.IMMBYTE, 0));
			prefix11.add(new Instruction(0xd0, "SUBF", 0, Mode.DIRECT, 0));
			prefix11.add(new Instruction(0xd1, "CMPF", 0, Mode.DIRECT, 0));
			prefix11.add(new Instruction(0xd6, "LDF",  0, Mode.DIRECT, 0));
			prefix11.add(new Instruction(0xd7, "STF",  0, Mode.DIRECT, 0));
			prefix11.add(new Instruction(0xdb, "ADDF", 0, Mode.DIRECT, 0));
			prefix11.add(new Instruction(0xe0, "SUBF", 0, Mode.INDEXED, 0));
			prefix11.add(new Instruction(0xe1, "CMPF", 0, Mode.INDEXED, 0));
			prefix11.add(new Instruction(0xe6, "LDF",  0, Mode.INDEXED, 0));
			prefix11.add(new Instruction(0xe7, "STF",  0, Mode.INDEXED, 0));
			prefix11.add(new Instruction(0xf0, "SUBF", 0, Mode.EXTENDED, 0));
			prefix11.add(new Instruction(0xf1, "CMPF", 0, Mode.EXTENDED, 0));
			prefix11.add(new Instruction(0xf6, "LDF",  0, Mode.EXTENDED, 0));
			prefix11.add(new Instruction(0xf7, "STF",  0, Mode.EXTENDED, 0));
			prefix11.add(new Instruction(0xfb, "ADDF", 0, Mode.EXTENDED, 0));
		} catch (Exception e) {
			_log.logError("Fundamental screwup defining 6x09 instructions", e);
		}		
	}
}
