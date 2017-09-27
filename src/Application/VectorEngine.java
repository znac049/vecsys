package Application;

import java.util.ArrayList;

public class VectorEngine {
	private DataViewer viewer;
	private SourceViewer source;
	private ArrayList<Integer> mem = new ArrayList<Integer>();
	
	public VectorEngine(DataViewer viewer, SourceViewer source) {
		this.viewer = viewer;
		this.source = source;
		
		mem.clear();
		
		viewer.clear();
		source.clear();
	}
	
	public void set(byte[] bytes) {
		int i;
		
		mem.clear();
		
		for (i=0; i<bytes.length; i+= 2) {
			int wrd = (int) ((bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8));
			mem.add(wrd);
		}
	}
}
