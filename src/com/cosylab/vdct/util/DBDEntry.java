/*
 * Created on Jul 30, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cosylab.vdct.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cosylab.vdct.Console;

/**
 * @author ilist
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DBDEntry {
	static File baseDir;
	
	String value;
	boolean savesToFile = true;
	
	public DBDEntry(String value) {
		this.value = value;
	}
	
	/**
	 * @return
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param string
	 */
	public void setValue(String string) {
		value = string;
	}

	public File getFile() {
		File f = new File( matchAndReplace(value) );
		
		// if not absolute, make relatove to DB file
		if (!f.isAbsolute()) f = new File(baseDir, value);
		
		try {
			return f.getCanonicalFile();
		} catch (IOException ioe) {
			System.out.println("Failed to cannonize '"+f.toString()+"'. Exception: ");
			ioe.printStackTrace();
		}
		return f.getAbsoluteFile();
	}
	
	public String toString() {
		return getFile().toString();
	}
	/**
	 * @return
	 */
	public boolean getSavesToFile() {
		return savesToFile;
	}

	/**
	 * @param b
	 */
	public void setSavesToFile(boolean b) {
		savesToFile = b;
	}

	public static String matchAndReplace(String value) {
		if (value==null || value.indexOf('$')<0) return value;
		
		Pattern macrop = Pattern.compile("\\$\\(([^\\$]+)\\)");
		
		Matcher macro = macrop.matcher(value);
		StringBuffer result=new StringBuffer();
		while (macro.find()) {
			String macron=macro.group(1);
			
			String replacement1 = getProperties().getProperty(macron);
			if (replacement1 == null) {
				macro.appendReplacement(result, macron.replaceAll("\\$","\\\\\\$"));
				continue;
			}
			
			
			getProperties().remove(macron);  // to avoid infinite loop
			String replacement2 = matchAndReplace(replacement1);
			getProperties().setProperty(macron, replacement2); // to speedup lookups
			
			macro.appendReplacement(result, replacement2.replaceAll("\\$","\\\\\\$"));
		}
		
		macro.appendTail(result);
		
		return result.toString();
	}

	/**
	 * @return
	 */
	public static File getBaseDir() {
		return baseDir;
	}

	/**
	 * @param string
	 */
	public static void setBaseDir(File file) {
		baseDir = file;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof DBDEntry)) return false;
		DBDEntry entry = (DBDEntry)obj;
		return entry.getFile().equals(getFile());
	}

	private static Properties properties = null;

	public static Properties getProperties() {
		if (properties==null) {
			properties = new Properties(System.getProperties());
			
			// imoport from epics release file
			File f = new File(properties.getProperty("EPICS_BASE")+"/configure/RELEASE");
			if (f.canRead()) {
				LineNumberReader r = null;
				try {
					r = new LineNumberReader(new FileReader(f));
					String line;
					while ((line=r.readLine())!=null) {
						if (line.matches("[^#=].*=.*")) {
							String[] strs=line.split("=",2);
							if (strs.length==2) properties.put(strs[0].trim(),strs[1].trim());
						}	
					}
				} catch (FileNotFoundException e) {
					//can't happen
					e.printStackTrace();
				} catch (IOException e) {
					Console.getInstance().println();
					Console.getInstance().println("o) Error while reading EPICS release file '"+f.getAbsolutePath()+"'.");
				} finally {
					try {
						if (r!=null) r.close();
					} catch (IOException e) {
					}
				}
			}
		}
		
		return properties;
	}
}
