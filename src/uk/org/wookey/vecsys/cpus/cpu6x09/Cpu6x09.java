package uk.org.wookey.vecsys.cpus.cpu6x09;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;

import javax.swing.JPanel;

import com.sun.xml.internal.ws.util.xml.XMLReaderComposite.State;

import uk.org.wookey.vecsys.cpus.Cpu;
import uk.org.wookey.vecsys.cpus.InstructionTable;
import uk.org.wookey.vecsys.cpus.BaseStatusPanel;
import uk.org.wookey.vecsys.cpus.cpu6x09.Instruction.Mode;
import uk.org.wookey.vecsys.emulator.GBConstraints;
import uk.org.wookey.vecsys.emulator.RangeException;
import uk.org.wookey.vecsys.emulator.TTLabel;
import uk.org.wookey.vecsys.utils.Logger;

public class Cpu6x09 extends Cpu {
	private static Logger _log = new Logger("6x09");

    public static final int RESET_VEC = 0xfffe;
    public static final int SWI_VEC = 0xfffa;
    public static final int SWI2_VEC = 0xfff4;

	private CpuState state;
	private StatusPanel statusPanel;
	
	InstructionTable noPrefix;
	InstructionTable prefix10;
	InstructionTable prefix11;

	public class CpuState {
		private static final int CC_E = 0x80;
		private static final int CC_F = 0x40;
		private static final int CC_H = 0x20;
		private static final int CC_I = 0x10;
		private static final int CC_N = 0x08;
		private static final int CC_Z = 0x04;
		private static final int CC_V = 0x02;
		private static final int CC_C = 0x01;
		
		public int pc;
		
		public int ir;
		public int irByteCount;
		
		public int pb;
		public int pbByteCount;
		
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
		
		public boolean nativeMode;
		
		public int startAddress;
		public int instByteCount;
		public int instBuff[];
		public Instruction instruction;

		public CpuState() {
			instBuff = new int[7];
			
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
			cc = 0x50;
			
			nativeMode = false;
		}
		
		public int getD() {
			return (a << 8) | b;
		}
		
		public void setD(int val) {
			state.b = (val & 0xff);
			state.a = (val >> 8) & 0xff;
		}
		
		public int getW() {
			return (e<<8) | f;
		}

		public void setW(int val) {
			state.f = (val & 0xff);
			state.e = (val >> 8) & 0xff;
		}
		
		public void setStdFlags(int val, int negMask) {
			if ((val & negMask) != 0) {
				state.cc |= CC_N;
			}
			else {
				state.cc &= ~CC_N;
			}
			
			if (val == 0) {
				state.cc |= CC_Z;
			}
			else {
				state.cc &= ~CC_Z;
			}
		}
		
		public void setStdFlags(int val, boolean isByte) {
			setStdFlags(val, isByte?0x80:0x8000);
		}
		
		public void setZ(boolean zFlag) {
			setZ(zFlag?1:0);
		}

		public void setZ(int val) {
			if (val != 0) {
				state.cc |= CpuState.CC_Z;
			}
			else {
				state.cc &= ~CpuState.CC_Z;
			}
		}
		
		public int getZ() {
			return ((state.cc & CpuState.CC_Z) == 0)?0:1;
		}
		
		public void setN(boolean nFlag) {
			setN(nFlag?1:0);
		}

		public void setN(int val) {
			if (val != 0) {
				state.cc |= CpuState.CC_N;
			}
			else {
				state.cc &= ~CpuState.CC_N;
			}
		}
		
		public int getN() {
			return ((state.cc & CpuState.CC_N) == 0)?0:1;
		}
		
		public void setV(boolean vFlag) {
			setV(vFlag?1:0);
		}

		public void setV(int val) {
			if (val != 0) {
				state.cc |= CpuState.CC_V;
			}
			else {
				state.cc &= ~CpuState.CC_V;
			}
		}
		
		public int getV() {
			return ((state.cc & CpuState.CC_V) == 0)?0:1;
		}
		
		public void setC(boolean cFlag) {
			setC(cFlag?1:0);
		}

		public void setC(int val) {
			if (val != 0) {
				state.cc |= CpuState.CC_C;
			}
			else {
				state.cc &= ~CpuState.CC_C;
			}
		}

		public int getC() {
			return ((state.cc & CpuState.CC_C) == 0)?0:1;
		}
		
		public void setE(boolean eFlag) {
			setE(eFlag?1:0);
		}

		public void setE(int val) {
			if (val != 0) {
				state.cc |= CpuState.CC_E;
			}
			else {
				state.cc &= ~CpuState.CC_E;
			}
		}

		public int getE() {
			return ((state.cc & CpuState.CC_E) == 0)?0:1;
		}
		
		public void setF(boolean fFlag) {
			setF(fFlag?1:0);
		}

		public void setF(int val) {
			if (val != 0) {
				state.cc |= CpuState.CC_F;
			}
			else {
				state.cc &= ~CpuState.CC_F;
			}
		}

		public int getf() {
			return ((state.cc & CpuState.CC_I) == 0)?0:1;
		}
		
		public void setI(boolean iFlag) {
			setC(iFlag?1:0);
		}

		public void setI(int val) {
			if (val != 0) {
				state.cc |= CpuState.CC_I;
			}
			else {
				state.cc &= ~CpuState.CC_I;
			}
		}

		public int getI() {
			return ((state.cc & CpuState.CC_I) == 0)?0:1;
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
		
		private Color headingColour = new Color(0, 0, 196);

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
			
			add(new TTLabel("V", headingColour), gbc);
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
    		
    		codeStr = new TTLabel("Wha?????", Color.GREEN);
    	
    		codePanel.add(codeStr, BorderLayout.WEST);
    		
    		gbc.gridwidth = 10;
    		gbc.weightx = 1.0;
    		gbc.fill = GridBagConstraints.HORIZONTAL;
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
			
				vReg.setText(String.format("%02x",  state.v));
				xReg.setText(String.format("%04x", state.x));
				yReg.setText(String.format("%04x", state.y));
			
				sReg.setText(String.format("%04x", state.s));
				uReg.setText(String.format("%04x", state.u));
			
				ccReg.setText(String.format("%02x", state.cc));
				ccStr.setText(ccString());

				codeStr.setText(codeString());
			}
		}
		
		private String codeString() {
			String res = String.format("%04x: ",  state.startAddress);
			
			for (int i=0; i<7; i++) {
				if (i < state.instByteCount) {
					res += String.format("%02x ",  state.instBuff[i]);
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
				res += String.format("#$%02x", state.pb);
				break;
				
			case IMMWORD:
				res += String.format("#$%04x", state.pb);
				break;

			case IMMQUAD:
				res += String.format("#$%02x%02x%02x%02x", state.instBuff[state.instByteCount-4], state.instBuff[state.instByteCount-3], state.instBuff[state.instByteCount-2], state.instBuff[state.instByteCount-1]);
				break;

			case DIRECT:
				res += String.format("<$%02x", state.pb);				
				break;

			case EXTENDED:
				res += String.format("$%04x", state.pb);				
				break;

			case INDEXED:
				{
					int mode = state.instBuff[state.irByteCount];
					final String regNames[] = {"X","Y","U","S"}; 
					
					if ((mode & 0x80) == 0) {
						// 5-bit offset
						int reg = (mode >> 5) & 0x03;
						mode &= 0x1f;
						
						// sign extend...
						if ((mode & 0x10) != 0) {
							mode |= 0xfffffff0;
						}
						
						res += String.format("%d,%s", mode, regNames[reg]);
					}
					else {
						int reg = (mode >> 5) & 0x03;
						
						switch (mode & 0x1f) {
						case 0x00:
							res += String.format(",%s+", regNames[reg]);
							break;
							
						case 0x01:
							res += String.format(",%s++", regNames[reg]);
							break;
							
						case 0x02:
							res += String.format(",-%s", regNames[reg]);
							break;
							
						case 0x03:
							res += String.format(",--%s", regNames[reg]);
							break;
							
						case 0x04:
							res += String.format(",%s", regNames[reg]);
							break;
							
						case 0x05:
							res += String.format("B,%s", regNames[reg]);
							break;
							
						case 0x06:
							res += String.format("A,%s", regNames[reg]);
							break;
							
						case 0x07:
							res += String.format("E,%s", regNames[reg]);
							break;
							
						case 0x08:
							res += String.format("%d,%s", sexByte(state.pb & 0xff), regNames[reg]);
							break;
							
						case 0x09:
							res += String.format("%d,%s", sexWord(state.pb & 0xffff), regNames[reg]);
							break;
							
						case 0x0a:
							res += String.format("F,%s", regNames[reg]);
							break;
							
						case 0x0b:
							res += String.format("D,%s", regNames[reg]);
							break;
							
						case 0x0c:
							res += String.format("%d,PCR", sexByte(state.pb & 0xff));
							break;
							
						case 0x0d:
							res += String.format("%d,PCR", sexWord(state.pb & 0xffff));
							break;
							
						case 0x0e:
							res += String.format("W,%s", regNames[reg]);
							break;
							
						case 0x11:
							res += String.format("[,%s++]", regNames[reg]);
							break;
							
						case 0x13:
							res += String.format("[,--%s]", regNames[reg]);
							break;
							
						case 0x14:
							res += String.format("[,%s]", regNames[reg]);
							break;
							
						case 0x15:
							res += String.format("[B,%s]", regNames[reg]);
							break;
							
						case 0x16:
							res += String.format("[A,%s]", regNames[reg]);
							break;
							
						case 0x17:
							res += String.format("[E,%s]", regNames[reg]);
							break;
							
						case 0x18:
							res += String.format("[%d,%s]", sexByte(state.pb & 0xff), regNames[reg]);
							break;
							
						case 0x19:
							res += String.format("[%d,%s]", sexWord(state.pb & 0xffff), regNames[reg]);
							break;
							
						case 0x1a:
							res += String.format("[F,%s]", regNames[reg]);
							break;
							
						case 0x1b:
							res += String.format("[D,%s]", regNames[reg]);
							break;
							
						case 0x1c:
							res += String.format("[%d,PCR]", sexByte(state.pb & 0xff));
							break;
							
						case 0x1d:
							res += String.format("[%d,PCR]", sexWord(state.pb & 0xffff));
							break;
							
						case 0x1e:
							res += String.format("[W,%s]", regNames[reg]);
							break;
							
						case 0x1f:
							res += String.format("[%d]", sexWord(state.pb & 0xffff));
							break;
							
						}
					}
					
				}
				break;

			case SYSPOST:
				res += regList('U');
				break;
				
			case USRPOST:
				res += regList('S');
				break;

			case RELWORD:
				{
					int delta = sexWord(state.pb & 0xffff);
				
					res += String.format("$%04x",  state.pc + delta);
				}
				break;
				
			case RELBYTE:
				{	
					int delta = sexByte(state.pb & 0xff);
				
					res += String.format("$%04x",  state.pc + delta);
				}
				break;

			case BLKMOVE:
				res += "blockmove ";
				break;
				
			case IMMDIRECT:
				res += "IMMDIRECT";
				break;
				
			case REGPOST:
				{
					final String regNames[] = {"D", "X","Y","U","S","PC","W","V","A","B","CC","DP","??","??","E","F"};
					int r1 = (state.pb>>4) & 0x0f;
					int r2 = state.pb & 0x0f;
					
					res += String.format("%s,%s", regNames[r1], regNames[r2]);
				}
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
			int op = state.instBuff[state.instByteCount-1];
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
		
		state.instBuff[state.instByteCount] = b;
		state.instByteCount++;
		
		state.pc++;
		
		return b;
	}
	
	private int fetchIRByte() {
		int b = fetchByte();
		
		state.ir = ((state.ir << 8) | b) & 0xffff;
		state.irByteCount++;
		
		return b;
	}
	
	private int fetchPostByte() {
		int b = fetchByte();
		
		state.pb = (state.pb << 8) | b;
		state.pbByteCount++;
		
		return b;
	}
	
	public void fetchNextInstruction() {
		state.startAddress = state.pc;
		state.instByteCount = 0;
		state.ir = state.irByteCount = 0;
		state.pb = state.pbByteCount = 0;
		
		fetchIRByte();
		
		if (state.ir == 0x10) {
			fetchIRByte();
			state.instruction = (Instruction) prefix10.get(state.ir);
		}
		else if (state.ir == 0x11) {
			fetchIRByte();
			state.instruction = (Instruction) prefix11.get(state.ir);
		}
		else {
			state.instruction = (Instruction) noPrefix.get(state.ir);
		}		
		
		//_log.logInfo(String.format("Instruction at $%04x: %04x, %d bytes", state.startAddress, state.ir, state.irByteCount));
	}
	
	public void fetchOperand() {
		if (state.instruction != null) {
			switch (state.instruction.mode) {
			case BLKMOVE:
			case IMPLIED:
			case REGREG:
			case SINGLEBIT:
				break;

			case DIRECT:
			case IMMBYTE:
			case IMMDIRECT:
			case RELBYTE:
			case SYSPOST:
			case USRPOST:
			case REGPOST:
				fetchPostByte();
				break;

			case IMMWORD:
			case RELWORD:
			case EXTENDED:
				fetchPostByte();
				fetchPostByte();
				break;
				
			case IMMQUAD:
				fetchPostByte();
				fetchPostByte();
				fetchPostByte();
				fetchPostByte();
				break;

			case INDEXED:
				{
					int postByte = fetchPostByte();
					
					//_log.logInfo(String.format("Indexed - work out how many post bytes; first: %02x", postByte));
					
					// top bit clear is a simple 5bit offset
					if ((postByte & 0x80) == 0) {
						//_log.logInfo("Simple 5-bit offset");
					}
					else {
						// Inspect the bottom five bits to see what's going on
						int indexMode = postByte & 0x1f;
						
						switch (indexMode) {
						case 0x00:
						case 0x01:
						case 0x02:
						case 0x03:
						case 0x04:
						case 0x05:
						case 0x06:
						case 0x07:
						case 0x0a:
						case 0x0b:
						case 0x0e:
						case 0x11:
						case 0x13:
						case 0x14:
						case 0x15:
						case 0x16:
						case 0x17:
						case 0x1a:
						case 0x1b:
						case 0x1e:
							break;
							
						case 0x08:
						case 0x0c:
						case 0x18:
						case 0x1c:
							fetchPostByte();
							break;
							
						case 0x09:
						case 0x0d:
						case 0x19:
						case 0x1d:
						case 0x1f:
							fetchPostByte();
							fetchPostByte();
							break;							
						}
					}
				}
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

	private int getReg(int rr) {
		switch (rr) {
		case 0:
			return state.getD();
			
		case 1:
			return state.x;
			
		case 2:
			return state.y;
			
		case 3:
			return state.u;
			
		case 4:
			return state.s;
			
		case 5:
			return state.pc;
			
		case 6:
			return state.getW();
			
		case 7:
			return state.v;
			
		case 8:
			return state.a;
			
		case 9:
			return state.b;
			
		case 10:
			return state.cc;
			
		case 11:
			return state.dp;
			
		case 14:
			return state.e;
			
		case 15:
			return state.f;
		}
		
		return 0;
	}

	private void setReg(int rr, int val) {
		switch (rr) {
		case 0:
			state.setD(val);
			break;
			
		case 1:
			state.x = val;
			break;
			
		case 2:
			state.y = val;
			break;
			
		case 3:
			state.u = val;
			break;
			
		case 4:
			state.s = val;
			break;
			
		case 5:
			state.pc = val;
			break;
			
		case 6:
			state.setW(val);
			break;
			
		case 7:
			state.v = val;
			break;
			
		case 8:
			state.a = val;
			break;
			
		case 9:
			state.b = val;
			break;
			
		case 10:
			state.cc = val;
			break;
			
		case 11:
			state.dp = val;
			break;
			
		case 14:
			state.e = val;
			break;
			
		case 15:
			state.f = val;
			break;
		}
	}
	
	private int calcEA() {
		int postByte = state.instBuff[state.irByteCount];
		int reg = ((postByte & 0x60) >> 5) + 1;
		int regVal = getReg(reg);
		int ea = 0;
		
		//_log.logInfo(String.format("calcEA: postByte=%02x, reg=%d, regVal=%04x\n", postByte, reg, regVal));
		
		// top bit clear is a simple 5bit offset
		if ((state.pbByteCount == 1) && ((postByte & 0x80) == 0)) {
			ea = regVal + sexVal(postByte & 0x1f, 5);
		}
		else {
			// Inspect the bottom five bits to see what's going on
			int indexMode = postByte & 0x1f;
			
			switch (indexMode) {
			case 0x00:	// ,r+
				ea = regVal+1;
				setReg(reg, ea);
				break;
				
			case 0x01:	// ,r++
				ea = regVal+2;
				setReg(reg, ea);
				break;
				
			case 0x02:	// ,-r
				ea = regVal-1;
				setReg(reg, ea);
				break;
				
			case 0x03:	// ,--r
				ea = regVal-2;
				setReg(reg, ea);
				break;
				
			case 0x04:	// ,r
				ea = regVal;
				break;
				
			case 0x05:	// B,r
				ea = regVal + sexByte(state.b);
				break;
				
			case 0x06:	// A,r
				ea = regVal + sexByte(state.a);
				break;
				
			case 0x07:	// E,r
				ea = regVal + sexByte(state.e);
				break;
				
			case 0x0a:	// F,r
				ea = regVal + sexByte(state.f);
				break;
				
			case 0x0b:	// D,r
				ea = regVal + sexWord(state.getD());
				break;
				
			case 0x0e:	// W,r
				ea = regVal + sexWord(state.getW());
				break;
				
			case 0x11:	// [,r++]
				ea = bus.getWord(regVal);
				setReg(reg, regVal+2);
				break;
				
			case 0x13:	// [,--r]
				regVal = regVal - 2;
				ea = bus.getWord(regVal);
				setReg(reg, regVal);
				break;
				
			case 0x14:	// [,r]
				ea = bus.getWord(regVal);
				break;
				
			case 0x15:	// [B,r]
				ea = bus.getWord(regVal+sexByte(state.b));
				break;
				
			case 0x16:	// [A,r]
				ea = bus.getWord(regVal+sexByte(state.a));
				break;
				
			case 0x17:	// [E,r]
				ea = bus.getWord(regVal+sexByte(state.e));
				break;
				
			case 0x1a:	// [F,r]
				ea = bus.getWord(regVal+sexByte(state.f));
				break;
				
			case 0x1b:	// [D,r]
				ea = bus.getWord(regVal+sexWord(state.getD()));
				break;
				
			case 0x1e:	// [W,r]
				ea = bus.getWord(regVal+sexWord(state.getW()));
				break;
				
			case 0x08:	// n8,r
				ea = regVal + sexByte(state.pb & 0xff);
				break;
				
			case 0x0c:	// n8,PCR
				ea = state.pc + sexByte(state.pb & 0xff);
				break;
				
			case 0x18:	// [n8,r]
				ea = bus.getWord(regVal + sexByte(state.pb & 0xff));
				break;
				
			case 0x1c:	// [n8,PCR]
				ea = bus.getWord(state.pc + sexByte(state.pb & 0xff));
				break;
				
			case 0x09:	// n16,r
				ea = regVal + sexWord(state.pb & 0xffff);
				break;
				
			case 0x0d:	// n16,PCR
				ea = state.pc + sexWord(state.pb & 0xffff);
				break;
				
			case 0x19:	// [n16,r]
				ea = bus.getWord(regVal + sexWord(state.pb & 0xffff));
				break;
				
			case 0x1d:	// [n16,PCR]
				ea = bus.getWord(state.pc + sexWord(state.pb & 0xffff));
				break;
				
			case 0x1f:	// [n16]
				ea = bus.getWord(state.pb & 0xffff);
				break;							
			}
		}
		
		return ea;
	}
	
	private int sexVal(int b, int bits) {
		int mask = 1 << (bits-1);
		int negBits = -mask;
		
		if ((b & mask) != 0) {
			b |= negBits;
		}
		
		return b;
	}
	
	private int sexByte(int b) {
		return sexVal(b & 0xff, 8);
	}
	
	private int sexWord(int w) {
		return sexVal(w & 0xffff, 16);
	}
	
	private int negInst(int val, boolean isByte) {
		int negMask = isByte?0x80:0x8000;
		int mask = isByte?0xff:0xffff;

		// Special case - $80 (-128) won't fit into 8 bits when negated; similarly for 16 bits
		if ((val & negMask) != 0) {
			state.cc |= CpuState.CC_V;
		}
		else {
			state.cc &= ~CpuState.CC_V;
		}

		// Special case - negating zero will yield 0
		if (val == 0) {
			state.cc &= ~CpuState.CC_C;
		}
		else {
			state.cc |= CpuState.CC_C;
		}
		
		val = (isByte?sexByte(val):sexWord(val));
		val = (-val) & mask;
		state.setStdFlags(val, negMask);
		
		return val;
	}
	
	private void comInst(int addr, boolean isByte) {
		int val = isByte?bus.getByte(addr):bus.getWord(addr);
		
		val = ~val;
		
		state.setStdFlags(val, isByte);
		state.cc &= ~CpuState.CC_V;
		
		bus.setByte(addr, val);
	}
	
	private int subInst(int val, int subval, boolean isByte) {
		int mask = isByte?0xff:0xffff;
		
		val = sexWord(val) - sexWord(subval);
		
		int tmp = val & mask;
		state.setStdFlags(tmp, isByte);
		state.setV(val != tmp);
		// TODO: Set carry flag if necessary
		
		return tmp;
	}
	
	private void lsrInst(int addr, boolean isByte) {
		int val = bus.getByte(addr);
		
		state.setC(val & 0x01);
		val = val >> 1;
		state.setStdFlags(val, isByte);
		state.setV(0);
		
		bus.setByte(addr, val);
	}
	
	private int lslInst(int val, boolean isByte) {
		int msbMask = isByte?0xff:0xffff;
		int msb = val & msbMask;
		
		state.setC(msb);
		
		val = val << 1;
		state.setStdFlags(val, isByte);

		// deal with the V flag (sign has changed)
		state.setV(msb != (val & msbMask));
		return val;
	}
	
	private int ldInst(int val, boolean isByte) {
		// CC flags
		state.setStdFlags(val, isByte);
		state.setV(0);
		
		return val;
	}
	
	private void stInst(int addr, int val, boolean isByte) {
		if (isByte) {
			bus.setByte(addr, val);
		}
		else {
			try {
				bus.setWord(addr, val);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// CC flags
		state.setStdFlags(val, isByte);
		state.setV(0);
	}
	
	private int eorInst(int val1, int val2, boolean isByte) {
		val1 = val1 ^ val2;
		
		// CC flags
		state.setStdFlags(val1, isByte);
		state.cc &= ~CpuState.CC_V;
		
		return val1;
	}

	private void branchIfInst(int offset, boolean doBranch, boolean isByte) {
		int sexed = isByte?sexByte(offset):sexWord(offset);
		
		if (doBranch) {
			state.pc = state.pc + sexed;
		}
	}

	private void rtsInst() {
		state.pc = bus.getWord(state.s);
		state.s = (state.s + 2) & 0xffff;
	}
	
	private void rtiInst() {
		int sp = state.s;
		
		state.cc = bus.getByte(sp++);
		
		if (state.getE() == 1) {
			if (state.nativeMode) {
				state.s = pulInst(state.s, true, 0xff, true);				
			}
			else {
				state.s = pulInst(state.s, true, 0xff, false);
			}
		}
		else {
			state.s = pulInst(state.s, true, 0x80, false);
		}
	}
	
	private void exgInst() {
		int r1 = (state.pb >> 4) & 0x0f;
		int r2 = state.pb & 0x0f;
		
		int regVal = getReg(r1);
		
		setReg(r1, getReg(r2));
		setReg(r2, regVal);
	}
	
	private void tfrInst() {
		int r1 = (state.pb >> 4) & 0x0f;
		int r2 = state.pb & 0x0f;
		
		setReg(r2, getReg(r1));
	}
	
	private void bsrInst(int target) {
		state.s = state.s - 2;
		try {
			bus.setWord(state.s,  state.pc);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		state.pc = target;
	}
	
	private void clrInst(int addr, boolean isByte) {
		bus.setByte(addr, 0);
		state.setStdFlags(0, true);
		state.setV(0);
		state.setC(0);		
	}
	
	private void addInst(int addr, int amount, boolean isByte) {
		int val = bus.getByte(addr) + (isByte?sexByte(amount):sexWord(amount));
		int mask = isByte?0xff:0xffff;
		
		bus.setByte(addr, val);
		state.setStdFlags(val, true);
		state.setV((val & mask) != val);
	}
	
	private int pushWord(int sp, int val) {
		sp = sp - 2;
		try {
			bus.setWord(sp,  val);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sp;
	}
	
	private int pushByte(int sp, int val) {
		sp = sp - 1;
		bus.setByte(sp,  val);
		
		return sp;
	}
	
	private void pushAll() {
		state.s = pushInst(state.s, state.u, 0xff, state.nativeMode);
	}
	
	private int pushInst(int sp, int su, int regs) {
		return pushInst(sp, su, regs, false);
	}
	
	private int pushInst(int sp, int su, int regs, boolean isNative) {
		if ((regs & 0x80) != 0) {
			sp = pushWord(sp, state.pc);
		}
		
		if ((regs & 0x40) != 0) {
			sp = pushWord(sp, su);
		}
		
		if ((regs & 0x20) != 0) {
			sp = pushWord(sp, state.y);
		}
		
		if ((regs & 0x10) != 0) {
			sp = pushWord(sp, state.x);
		}
		
		if ((regs & 0x08) != 0) {
			sp = pushByte(sp, state.dp);
		}
		
		if (isNative) {
			sp = pushByte(sp, state.f);
			sp = pushByte(sp, state.e);
		}
		
		if ((regs & 0x04) != 0) {
			sp = pushByte(sp, state.b);
		}
		
		if ((regs & 0x02) != 0) {
			sp = pushByte(sp, state.a);
		}
		
		if ((regs & 0x01) != 0) {
			sp = pushByte(sp, state.cc);
		}
		
		return sp;
	}
	
	private int pulInst(int sp, boolean systemStack, int regs, boolean isNative) {
		if ((regs & 0x01) != 0) {
			state.cc = bus.getByte(sp++);
		}
		
		if ((regs & 0x02) != 0) {
			state.a = bus.getByte(sp++);
		}
		
		if ((regs & 0x04) != 0) {
			state.b = bus.getByte(sp++);
		}
		
		if (isNative) {
			state.e = bus.getByte(sp++);
			state.f = bus.getByte(sp++);
		}
		
		if ((regs & 0x08) != 0) {
			state.dp = bus.getByte(sp++);
		}
		
		if ((regs & 0x10) != 0) {
			state.x = bus.getWord(sp);
			sp += 2;
		}
		
		if ((regs & 0x20) != 0) {
			state.y = bus.getWord(sp);
			sp += 2;
		}
		
		if ((regs & 0x40) != 0) {
			if (systemStack) {
				state.u = bus.getWord(sp);
			}
			else {
				state.s = bus.getWord(sp);
			}
			sp += 2;
		}
		
		if ((regs & 0x80) != 0) {
			state.pc = bus.getWord(sp);
			sp += 2;
		}
		
		return sp;
	}
	
	private void executeInstruction() {
		switch (state.ir) {
			case 0x00:		// neg	<$xx
			{
				int addr = (state.dp<<8) | state.pb;
				
				bus.setByte(addr, negInst(bus.getByte(addr), true));
			}
			break;
				
			case 0x03:		// com <$xx
				comInst((state.dp<<8) | state.pb, true);
				break;
				
			case 0x04:		// lsr <$xx
				lsrInst((state.dp<<8) | state.pb, true);
				break;
				
			case 0x0a:		// dec <$xx
				addInst((state.dp<<8) | state.pb, -1, true);
				break;
				
			case 0x0c:		// inc <$xx
				addInst((state.dp<<8) | state.pb, 1, true);
				break;
				
			case 0x0e:		// jmp <$xx
				state.pc = (state.dp<<8) | state.pb;
				break;
				
			case 0x0f:		// clr <$xx
				clrInst((state.dp<<8) | state.pb, true);
				break;
				
			case 0x12:		// nop
				break;
				
			case 0x14:		// sexw
				if ((state.f & 0x80) != 0) {
					state.e = 0xff;
				}
				else {
					state.e = 0;
				}
				break;
				
			case 0x16:		// lbra
				state.pc = state.pc + sexWord(state.pb);
				break;
				
			case 0x17:		// lbsr
				bsrInst(state.pb);
				break;
				
			case 0x1a:		// orcc
				state.cc |= state.pb;
				break;
				
			case 0x1c:		// andcc
				state.cc &= state.pb;
				break;
				
			case 0x1d:		// sex
				if ((state.b & 0x80) != 0) {
					state.a = 0xff;
				}
				else {
					state.a = 0;
				}
				break;
				
			case 0x1e:		// exg r,r
				exgInst();
				break;
				
			case 0x1f:		// tfr r,r
				tfrInst();
				break;
				
			case 0x20:		// bra
				state.pc = state.pc + sexByte(state.pb);
				break;
				
			case 0x21:		// brn
				// effectively a nop
				break;
				
			case 0x22:		// bhi
				branchIfInst(state.pb, (state.cc & (CpuState.CC_C | CpuState.CC_Z)) == 0, true);
				break;
				
			case 0x23:		// bls
				branchIfInst(state.pb, (state.cc & (CpuState.CC_C | CpuState.CC_Z)) != 0, true);
				break;
				
			case 0x24:		// bcc
				branchIfInst(state.pb, (state.cc & CpuState.CC_C) == 0, true);
				break;
				
			case 0x25:		// bcs
				branchIfInst(state.pb, (state.cc & CpuState.CC_C) != 0, true);
				break;
				
			case 0x26:		// bne
				branchIfInst(state.pb, (state.cc & CpuState.CC_Z) == 0, true);
				break;
				
			case 0x27:		// beq
				branchIfInst(state.pb, (state.cc & CpuState.CC_Z) == 0, true);
				break;
				
			case 0x28:		// bvc
				branchIfInst(state.pb, (state.cc & CpuState.CC_V) == 0, true);
				break;
				
			case 0x29:		// bvs
				branchIfInst(state.pb, (state.cc & CpuState.CC_V) != 0, true);
				break;
				
			case 0x2a:		// bpl
				branchIfInst(state.pb, (state.cc & CpuState.CC_N) == 0, true);
				break;
				
			case 0x2b:		// bmi
				branchIfInst(state.pb, (state.cc & CpuState.CC_N) != 0, true);
				break;
				
			case 0x2c:		// bge
				branchIfInst(state.pb, (state.getN() == state.getV()), true);
				break;
				
			case 0x2d:		// blt
				branchIfInst(state.pb, (state.getN() != state.getV()), true);
				break;
				
			case 0x2e:		// bgt
				branchIfInst(state.pb, (state.getN() == state.getV()) && (state.getV() == 0), true);
				break;
				
			case 0x2f:		// ble
				branchIfInst(state.pb, (state.getN() != state.getV()) || (state.getZ() == 0), true);
				break;
				
			case 0x30:		// leax
				state.x = calcEA();				
				state.setZ(state.x == 0);
				break;
				
			case 0x31:		// leay
				state.y = calcEA();				
				state.setZ(state.y == 0);
				break;
				
			case 0x32:		// leas
				state.s = calcEA();				
				break;
				
			case 0x33:		// leau
				state.u = calcEA();				
				break;
				
			case 0x34:		// pshs
				state.s = pushInst(state.s, state.u, state.pb);
				break;
				
			case 0x35:		// puls
				state.s = pulInst(state.s, true, state.pb, false);
				break;
				
			case 0x36:		// pshu
				state.u = pushInst(state.u, state.s, state.pb, false);
				break;
				
			case 0x37:		// pulu
				state.u = pulInst(state.u, false, state.pb, false);
				break;
				
			case 0x39:		// rts
				rtsInst();
				break;
				
			case 0x3f:		// swi
				state.setE(1);
				pushAll();
				state.setF(1);
				state.setI(1);
				state.pc = bus.getWord(SWI_VEC);
				break;
				
			case 0x40:		// nega
				state.a = negInst(state.a, true);
				break;
				
			case 0x50:		// negb
				state.b = negInst(state.b, true);
				break;
				
			case 0x58:		// lslb
				state.b = lslInst(state.b, true);
				break;
				
			case 0x60:		// neg indexed
				{
					int addr = calcEA();
				
					bus.setByte(addr, negInst(bus.getByte(addr), true));
				}
				break;
				
			case 0x63:		// com indexed
				comInst(calcEA(), true);
				break;
				
			case 0x64:		// lsr indexed
				lsrInst(calcEA(), true);
				break;
				
			case 0x6a:		// dec indexed
				addInst(calcEA(), -1, true);
				break;
				
			case 0x6c:		// inc indexed
				addInst(calcEA(), 1, true);
				break;
				
			case 0x6e:		// jmp indexed
				state.pc = calcEA();
				break;
				
			case 0x86:		// lda #
				state.a = ldInst(state.pb, true);
				break;
				
			case 0x8c:		// cmpx #
				subInst(state.x, bus.getWord(state.pb), false);
				break;
				
			case 0x8d:		// bsr
				bsrInst(state.pb);
				break;
				
			case 0x8e:		// ldx	#
				state.x = ldInst(state.pb, false);
				break;
				
			case 0xa8:		// eora indexed
				state.a = eorInst(state.a, bus.getByte(calcEA()), true);
				break;
				
			case 0xb7:		// sta $xxxx
				stInst(state.pb, state.a, true);
				break;
				
			case 0xc6:		// ldb #
				state.b = ldInst(state.pb, true);
				break;
				
			case 0xe7:		// stb indexed
				stInst(calcEA(), state.b, true);
				break;
				
			case 0xf7:		// stb $xxxx
				stInst(state.pb, state.b, true);
				break;
				
			case 0x1026:	// lbne
				branchIfInst(state.pb, (state.cc & CpuState.CC_Z) == 0, true);
				break;
				
			case 0x1027:	// lbeq
				branchIfInst(state.pb, (state.cc & CpuState.CC_Z) != 0, true);
				break;
				
			case 0x108e:	// ldy	#
				state.y = ldInst(state.pb, false);
				break;
				
			case 0x10ce:	// lds #
				state.s = ldInst(state.pb, false);
				break;
				
			default:
				_log.logWarn(String.format("Unimplemented instruction $%04x at $%04x", state.ir, state.startAddress));
				break;
		}
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
			noPrefix.add(new Instruction(0x05, "EIM",  0, Mode.IMMDIRECT, 0));
			noPrefix.add(new Instruction(0x06, "ROR",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x07, "ASR",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x08, "ASL",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x09, "ROL",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x0a, "DEC",  0, Mode.DIRECT, 0));
			noPrefix.add(new Instruction(0x0b, "TIM",  0, Mode.IMMDIRECT, 0));
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
