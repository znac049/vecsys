package Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rtextarea.RTextScrollPane;

public class ApplicationWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private final static Logger _logger = new Logger("ApplicationWindow");
	private MainStatusBar statusBar;
	
	private static final byte[] code = javax.xml.bind.DatatypeConverter.parseHexBinary("ffa30002ff97009000a200000090ff3300e0");
	
	public ApplicationWindow() {
		super("DVG");
		
		_logger.logMsg("And we're off!");
		
		Container cp = getContentPane();

		setLayout(new BorderLayout());
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		MainMenuBar menu = new MainMenuBar();
		setJMenuBar(menu);
		
		DataViewer viewer = new DataViewer();
		cp.add(viewer, BorderLayout.LINE_START);
		
		SourceViewer source = new SourceViewer();
		source.addMouseListener(new MouseAdapter() { 
            public void mouseClicked(MouseEvent me) { 
            	System.out.println("Clicked!");
            	System.out.println(me);
            	
            	int line;
            	
				try {
					line = source.getLineOfOffset(source.getCaretPosition());
	            	System.out.println("Line: " + line);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
           } 
		});
		RTextScrollPane sp = new RTextScrollPane(source);
		cp.add(sp, BorderLayout.CENTER);
		
		VectorDisplay disp = new VectorDisplay();		
		cp.add(disp, BorderLayout.LINE_END);
		
		statusBar = MainStatusBar.getMainStatusBar(); 
		cp.add(statusBar, BorderLayout.PAGE_END);
		setBackground(new Color(0x00, 0xd0, 0x00));

		setTitle("DVG Tool");
		pack();
		setLocationRelativeTo(null);
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new ApplicationWindow().setVisible(true);
			}	
		});

	}	
}
