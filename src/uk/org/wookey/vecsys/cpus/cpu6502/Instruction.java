package uk.org.wookey.vecsys.cpus.cpu6502;

import uk.org.wookey.vecsys.cpus.MinimalInstruction;

public class Instruction extends MinimalInstruction {
	public Mode mode;

    public enum Mode {
        ACC {
            public String toString() {
                return "Accumulator";
            }
        },
        AIX {
            public String toString() {
                return "Absolute, X-Indexed Indirect";
            }
        },
        ABS {
            public String toString() {
                return "Absolute";
            }
        },

        ABX {
            public String toString() {
                return "Absolute, X-indexed";
            }
        },

        ABY {
            public String toString() {
                return "Absolute, Y-indexed";
            }
        },

        IMM {
            public String toString() {
                return "Immediate";
            }
        },

        IMP {
            public String toString() {
                return "Implied";
            }
        },

        IND {
            public String toString() {
                return "Indirect";
            }
        },

        XIN {
            public String toString() {
                return "X-indexed Indirect";
            }
        },

        INY {
            public String toString() {
                return "Indirect, Y-indexed";
            }
        },

        REL {
            public String toString() {
                return "Relative";
            }
        },

        ZPG {
            public String toString() {
                return "Zero Page";
            }
        },

        ZPR {
            public String toString() {
                return "Zero Page, Relative";
            }
        },

        ZPX {
            public String toString() {
                return "Zero Page, X-indexed";
            }
        },

        ZPY {
            public String toString() {
                return "Zero Page, Y-indexed";
            }
        },

        ZPI {
            public String toString() {
                return "Zero Page Indirect";
            }
        },

        NUL {
            public String toString() {
                return "NULL";
            }
        }
    }

	public Instruction(int opcode, String name, int size, Mode direct, int cycles) {
		super(opcode, name, size, cycles);
		this.mode = direct;
	}
}
