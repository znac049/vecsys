package uk.org.wookey.vecsys.utils;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.text.*;

public class Logger {
	private static JTextPane _log = null;
	private static PrintWriter _out = null;
	private String _logName;
	private int _logLevel = 1;
	private SimpleAttributeSet _labAttribs;
	private SimpleAttributeSet _msgAttribs;

	private SimpleAttributeSet _okAttribs;
	private SimpleAttributeSet _warnAttribs;
	private SimpleAttributeSet _errAttribs;
	
	private ArrayList<String[]> _msgBuffer;
	
	public Logger(JTextPane log)
	{
		_log = log;
		initialise("");
	}
	
	public Logger(String tag) {
		initialise(tag);
	}
	
	public Logger() {
		initialise(getCallerClassName(this.getClass()));
	}
	
	public Logger(int level) {
		initialise(getCallerClassName(this.getClass()));
		_logLevel = level;
	}
	
	public Logger(String tag, int level) {
		initialise(tag);
		_logLevel = level;
	}
	
	private void initialise(String tag) {
		if (tag.equals("")) {
			_logName = "";
		}
		else {
			_logName = "[" + tag + "]:";
		}
		
		if (_out == null) {
			try {
				_out = new PrintWriter("debug.txt");
			} catch (FileNotFoundException e) {
			}
		}
		
		_msgBuffer = new ArrayList<String[]>();
		
		_labAttribs = new SimpleAttributeSet();
		StyleConstants.setForeground(_labAttribs, Color.blue);

		_msgAttribs = new SimpleAttributeSet();
		StyleConstants.setForeground(_msgAttribs, Color.white);
		StyleConstants.setBold(_msgAttribs, false);

		_okAttribs = new SimpleAttributeSet();
		StyleConstants.setForeground(_okAttribs, Color.green);

		_warnAttribs = new SimpleAttributeSet();
		StyleConstants.setForeground(_warnAttribs, Color.orange);

		_errAttribs = new SimpleAttributeSet();
		StyleConstants.setForeground(_errAttribs, Color.red);
	}
	
	public synchronized void logMsg(String msg, SimpleAttributeSet labAttribs, SimpleAttributeSet msgAttribs) {
		if (_logLevel == 0) {
			return;
		}
		
		if (_out != null) {
			_out.println(msg);
			_out.flush();
		}
		
		if (_log != null) {
			if (_msgBuffer.size() > 0) {
				for (int i=0; i<_msgBuffer.size(); i++) {
					String bits[] = _msgBuffer.get(i);
					append(bits[0], labAttribs);
					append(bits[1], msgAttribs);
				}
				_msgBuffer.clear();
			}
			append(_logName, labAttribs);
			append(' ' + msg + '\n', msgAttribs);
		}
		else {
			// We don't have a text pane yet - buffer it for later display.
			String item[] = new String[2];
			item[0] = _logName;
			item[1] = msg;
			_msgBuffer.add(item);
			
			System.out.println(_logName + ' ' +msg);
		}
	}
	
	private void logMsg(String msg, SimpleAttributeSet attribs) {
		logMsg(msg, _labAttribs, attribs);
	}
	
	public synchronized void printBacktrace(String msg, Exception e, SimpleAttributeSet attribs) {
		StackTraceElement[] trace = e.getStackTrace();

		logMsg(msg, attribs);
		for (int i=0; i<trace.length; i++) {
			append(trace[i].toString() + '\n', attribs);
		}
	}
	
	public synchronized void printBacktrace(Exception e) {
		printBacktrace("Caught an exception:", e, _errAttribs);
	}
	
	protected void append(String msg, SimpleAttributeSet attributes) {
		Document doc = _log.getDocument();
		
		try {
			doc.insertString(doc.getLength(), msg, attributes);
			_log.setCaretPosition(doc.getLength());		
		} catch (BadLocationException e) {
			e.printStackTrace();
		}		
	}
	
	public synchronized void logMsg(String msg) {
		logMsg(msg, _labAttribs, _msgAttribs);
	}
	
	public synchronized void logInfo(String msg) {
		logMsg(msg);
	}
	
	public synchronized void logSuccess(String msg) {
		logMsg(msg, _okAttribs);
	}
	
	public synchronized void logWarn(String msg) {
		logMsg(msg, _warnAttribs);
	}
	
	public synchronized void logError(String msg) {
		logMsg(msg, _errAttribs);
	}
	
	public synchronized void logError(String msg, Exception e) {
		printBacktrace(msg, e, _errAttribs);
	}
	
    public static String getCallerClassName(final Class<?> clazz) {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        final String className = clazz.getName();
        boolean classFound = false;
        for (int i = 1; i < stackTrace.length; i++) {
            final StackTraceElement element = stackTrace[i];
            final String callerClassName = element.getClassName();
            // check if class name is the requested class
            if (callerClassName.equals(className)) classFound = true;
            else if (classFound) return callerClassName;
        }
        
        return "NoClass";
    }
}
