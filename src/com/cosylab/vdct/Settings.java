package com.cosylab.vdct;

import java.io.*;
import java.util.*;

/**
 * Insert the type's description here.
 * Creation date: (4.2.2001 11:30:32)
 * @author: Matej Sekoranja
 */
public class Settings {
	private static final String SETTINGS_HEADER = "VisualDCT v"+Version.VERSION+" settings file";
	private static Settings instance = null;
	private static String defaultDir = "./";
	private File file = null;
	private Properties properties;

	// caching
	private boolean snapToGrid = true;
	private boolean showGrid = true;
	private boolean navigator = true;
/**
 * Settings constructor comment.
 */
protected Settings(String fileName) {
	fileName = getDefaultDir()+fileName;
	file = new File(fileName);

	int pos = file.getAbsolutePath().lastIndexOf(File.separatorChar);
	if (pos>0)
	{
		String configDir = file.getAbsolutePath().substring(0, pos);
		File dir = new File(configDir);
		if (!dir.exists())
			dir.mkdirs();
	}
	
	properties = new Properties();
	if (!load()) {
		setProperty("SnapToGrid", String.valueOf(snapToGrid));
		setProperty("ShowGrid", String.valueOf(showGrid));
		setProperty("GridSize", String.valueOf(Constants.GRID_SIZE));
		setProperty("Toolbar", String.valueOf(true));
		setProperty("Statusbar", String.valueOf(true));
		setProperty("Navigator", String.valueOf(navigator));
		//setProperty("GroupSeparator", String.valueOf(Constants.GROUP_SEPARATOR));
		Console.getInstance().println("o) No settings file loaded. Using defaults...");
	}
	else {
		
		String val = getProperty("SnapToGrid");
		if (val!=null)
			snapToGrid = val.toLowerCase().equals("true");
		
		val = getProperty("ShowGrid");
		if (val!=null)
			showGrid = val.toLowerCase().equals("true");

		val = getProperty("Navigator");
		if (val!=null)
			navigator = val.toLowerCase().equals("true");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2001 11:08:15)
 * @return java.lang.String
 */
public static java.lang.String getDefaultDir() {
	return defaultDir;
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 11:37:11)
 * @return java.io.File
 */
public java.io.File getFile() {
	return file;
}
/**
 * Insert the method's description here.
 * Creation date: (27.4.2001 18:48:59)
 * @return int
 */
public int getGridSize() {
	String size = getProperty("GridSize");
	if (size!=null)
		return Integer.parseInt(size);
	else
		return Constants.GRID_SIZE;
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 11:31:11)
 * @return com.cosylab.vdct.Settings
 */
public static Settings getInstance() {
	if (instance==null) instance = new Settings(Constants.CONFIG_DIR+Constants.SETTINGS_FILE);
	return instance;
}
/**
 * Insert the method's description here.
 * Creation date: (27.4.2001 18:50:50)
 * @return boolean
 */
public boolean getNavigator() {
/*	String snap = getProperty("Navigator");
	if (snap!=null)
		return snap.toLowerCase().equals("true");
	else
		return false;*/
	return navigator;
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 12:00:51)
 * @return java.lang.String
 * @param key java.lang.String
 */
public String getProperty(String key) {
	return properties.getProperty(key);
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 12:02:12)
 * @return java.lang.String
 * @param key java.lang.String
 * @param defaultValue java.lang.String
 */
public String getProperty(String key, String defaultValue) {
	return properties.getProperty(key, defaultValue);
	
}
/**
 * Insert the method's description here.
 * Creation date: (27.4.2001 18:50:50)
 * @return boolean
 */
public boolean getShowGrid() {
/*	String show = getProperty("ShowGrid");
	if (show!=null)
		return show.toLowerCase().equals("true");
	else
		return false;*/
	return showGrid;
}
/**
 * Insert the method's description here.
 * Creation date: (27.4.2001 18:50:50)
 * @return boolean
 */
public boolean getSnapToGrid() {
/*	String snap = getProperty("SnapToGrid");
	if (snap!=null)
		return snap.toLowerCase().equals("true");
	else
		return false;*/
	return snapToGrid;
}
/**
 * Insert the method's description here.
 * Creation date: (27.4.2001 18:50:50)
 * @return boolean
 */
public boolean getStatusbar() {
	String show = getProperty("Statusbar");
	if (show!=null)
		return show.toLowerCase().equals("true");
	else
		return false;
}
/**
 * Insert the method's description here.
 * Creation date: (27.4.2001 18:50:50)
 * @return boolean
 */
public boolean getToolbar() {
	String show = getProperty("Toolbar");
	if (show!=null)
		return show.toLowerCase().equals("true");
	else
		return false;
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 11:32:25)
 * @return boolean
 */
public boolean load() {
	if ((file!=null) && file.exists()) {
		try {
			properties.load(new BufferedInputStream(new FileInputStream(file)));
		} catch (Exception e) {
			Console.getInstance().println("o) Failed to load settings file:");
			Console.getInstance().println(e.toString());
			return false;
		}

		// set constants
		String separator = properties.getProperty("GroupSeparator");
		if (separator!=null && separator.length()>0)
			Constants.GROUP_SEPARATOR = separator.charAt(0);
		
		return true;	
	}
	else
		return false;
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 11:32:36)
 * @return boolean
 */
public boolean save() {
	if (file!=null) {
		try {

			properties.setProperty("GroupSeparator", String.valueOf(Constants.GROUP_SEPARATOR));
			
			properties.store(new BufferedOutputStream(
								new FileOutputStream(file)),
							 SETTINGS_HEADER);
		} catch (Exception e) {
			Console.getInstance().println("o) Failed to save settings file:");
			Console.getInstance().println(e.toString());
			return false;
		}
		return true;	
	}
	else
		return false;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2001 11:08:15)
 * @param newDefaultDir java.lang.String
 */
public static void setDefaultDir(java.lang.String newDefaultDir) {
	defaultDir = newDefaultDir;
	if (defaultDir.charAt(defaultDir.length()-1)!=File.separatorChar)
		defaultDir += File.separatorChar;
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 11:37:11)
 * @param newFile java.io.File
 */
public void setFile(java.io.File newFile) {
	file = newFile;
}
/**
 * Insert the method's description here.
 * Creation date: (27.4.2001 18:48:29)
 * @param size int
 */
public void setGridSize(int size) {
	setProperty("GridSize", String.valueOf(size));
}
/**
 * Insert the method's description here.
 * Creation date: (27.4.2001 18:46:17)
 * @param state boolean
 */
public void setNavigator(boolean state) {
	navigator = state;
	setProperty("Navigator", String.valueOf(state));
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 11:59:58)
 * @param key java.lang.String
 * @param value java.lang.String
 */
public void setProperty(String key, String value) {
	properties.setProperty(key, value);
}
/**
 * Insert the method's description here.
 * Creation date: (27.4.2001 18:46:17)
 * @param state boolean
 */
public void setShowGrid(boolean state) {
	showGrid = state;
	setProperty("ShowGrid", String.valueOf(state));
}
/**
 * Insert the method's description here.
 * Creation date: (27.4.2001 18:46:17)
 * @param state boolean
 */
public void setSnapToGrid(boolean state) {
	snapToGrid = state;
	setProperty("SnapToGrid", String.valueOf(state));
}
/**
 * Insert the method's description here.
 * Creation date: (27.4.2001 18:46:17)
 * @param state boolean
 */
public void setStatusbar(boolean state) {
	setProperty("Statusbar", String.valueOf(state));
}
/**
 * Insert the method's description here.
 * Creation date: (27.4.2001 18:46:17)
 * @param state boolean
 */
public void setToolbar(boolean state) {
	setProperty("Toolbar", String.valueOf(state));
}
}
