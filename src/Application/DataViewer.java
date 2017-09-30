package Application;

import java.util.ArrayList;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;

public class DataViewer extends RSyntaxTextArea {
	public DataViewer() {
		super(20, 40);
		
		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory)TokenMakerFactory.getDefaultInstance();
		//atmf.putMapping("text/DVG", "Application.DVGTokenMaker");
		//setSyntaxEditingStyle("text/DVG");
	}

	public void set(ArrayList<Integer> mem) {
		int i;
		int count = mem.size();

		setText("");
		
		for (i=0; i<count; i++) {
			if ((i % 4) == 0) {
				append(String.format("%04X: ", i));
			}
			
			append(String.format("$%04X", mem.get(i).intValue()));
			
			if ((i % 4) == 3) {
				append("\n");
			}
			else if (i < (count-1)) {
				append(", ");
			}
		}
	}
}
