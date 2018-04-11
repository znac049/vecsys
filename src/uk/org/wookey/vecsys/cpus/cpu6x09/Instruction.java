package uk.org.wookey.vecsys.cpus.cpu6x09;

import uk.org.wookey.vecsys.cpus.MinimalInstruction;
import uk.org.wookey.vecsys.utils.Logger;

public class Instruction extends MinimalInstruction {
	private static Logger _log = new Logger("6x09 Instruction");

	public Mode mode;
	
	public enum Mode {
	       IMPLIED {
	            public String toString() {
	                return "Implied";
	            }
	       },
	       IMMBYTE {
	    	   public String toString() {
	    		   return "Immediate (byte)";
	    	   }
	       },
	       IMMWORD {
	    	   public String toString() {
	    		   return "Immediate (word)";
	    	   }	
	       },
	       DIRECT {
	    	   public String toString() {
	    		   return "Direct";
	    	   }
	       },
	       EXTENDED {
	    	   public String toString() {
	    		   return "Extended";
	    	   }
	       },
	       INDEXED {
	    	   public String toString() {
	    		   return "Indexed";
	    	   }
	       },
	       RELBYTE {
	    	   public String toString() {
	    		   return "Relative (byte offset)";
	    	   }
	       },
	       RELWORD {
	    	   public String toString() {
	    		   return "Relative (word offset)";
	    	   }
	       },
	       REGPOST {
	    	   public String toString() {
	    		   return "Reg post";
	    	   }
	       },
	       SYSPOST {
	    	   public String toString() {
	    		   return "Sys post";
	    	   }
	       },
	       USRPOST {
	    	   public String toString() {
	    		   return "Usr post";
	    	   }
	       },
	       IMMDIRECT {
	    	   public String toString() {
	    		   return "Immediate direct (*)";
	    	   }
	       },
	       IMMQUAD {
	    	   public String toString() {
	    		   return "Immediate quad (*)";
	    	   }
	       },
	       REGREG {
	    	   public String toString() {
	    		   return "Register to register";
	    	   }
	       },
	       SINGLEBIT {
	    	   public String toString() {
	    		   return "Single bit";
	    	   }
	       },
	       BLKMOVE {
	    	   public String toString() {
	    		   return "Block move";
	    	   }	
	       },
	       NUL {
	    	   public String toString() {
	    		   return "NULL";
	    	   }
	       }	
	}
	
	public Instruction(int opcode, String name, int size, Mode imp, int cycles) {
		super(opcode, name, size, cycles);
		this.mode = imp;
	}
	
	public int numModeBytes(Mode mode) {
		int nBytes = 0;
		
		_log.logInfo(String.format("Number of mode bytes for %s is %d", mode.toString(), nBytes));
		
		return nBytes;
	}
}
