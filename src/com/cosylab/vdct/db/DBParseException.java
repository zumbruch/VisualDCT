package com.cosylab.vdct.db;

import com.cosylab.vdct.Console;

/**
 * This type was created in VisualAge.
 */
 
public class DBParseException extends Exception {
/**
 * DBParseException constructor comment.
 * @param s java.lang.String
 */
public DBParseException(String s, java.io.StreamTokenizer t, String fileName) {
	super(s);
	Console.getInstance().print("\nError found in file '"+fileName+"', line "+t.lineno()+": ");
}
}
