package uk.org.wookey.vecsys.utils;

public class VecUtils {
	public static String binaryString(int val, int places) {
		String res = "";
		int mask = 1<<(places-1);
		
		while (mask != 0) {
			if ((val & mask) != 0) {
				res += '1';
			}
			else {
				res += '0';
			}
			
			mask = mask >> 1;
		}
		
		return res;
	}
}
