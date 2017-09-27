package Application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rtextarea.RTextScrollPane;

public class SourceViewer extends JPanel {
	public SourceViewer() {
		super();
		
		setLayout(new BorderLayout());
		
		RSyntaxTextArea textArea = new RSyntaxTextArea(20, 60);
		
		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory)TokenMakerFactory.getDefaultInstance();
		atmf.putMapping("text/DVG", "Application.DVGTokenMaker");
		textArea.setSyntaxEditingStyle("text/DVG");
		RTextScrollPane sp = new RTextScrollPane(textArea);
		add(sp);
	}

	public void clear() {
		// TODO Auto-generated method stub
		
	}
}
