package uk.org.wookey.vecsys.utils;

import java.awt.Container;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Prefs {
	public static Logger _logger = new Logger("Prefs");
	
	public static final String AppRoot = "uk/org/wookey/vecsys";
	public static final String DevRoot = AppRoot + "/devices";
	
	
	public static Preferences node(String name) {
		return Preferences.userRoot().node(name);
	}

	public static Preferences node(String root, String name) {  
		return Prefs.node(root + "/" + name);
	}	
}
