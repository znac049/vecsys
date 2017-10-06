package DVG;


import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rtextarea.RTextScrollPane;

public class SourceViewer extends RSyntaxTextArea {
	public SourceViewer() {
		super(20, 60);
		
		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory)TokenMakerFactory.getDefaultInstance();
		atmf.putMapping("text/DVG", "Application.DVGTokenMaker");
		setSyntaxEditingStyle("text/DVG");
		//RTextScrollPane sp = new RTextScrollPane(this);
	}

	public void clear() {
	}
}
