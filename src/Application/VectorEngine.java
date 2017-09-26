package Application;

public class VectorEngine {
	private DataViewer viewer;
	private SourceViewer source;
	
	public VectorEngine(DataViewer viewer, SourceViewer source) {
		this.viewer = viewer;
		this.source = source;
		
		viewer.clear();
		source.clear();
	}
}
