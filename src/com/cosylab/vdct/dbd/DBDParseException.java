package com.cosylab.vdct.dbd;

import com.cosylab.vdct.Console;

/**
 * This type was created in VisualAge.
 */
public class DBDParseException extends Exception {
/**
 * ParseException constructor comment.
 * @param s java.lang.String
 */
public DBDParseException(String s, java.io.StreamTokenizer t, String fileName) {
	super(s);
	Console.getInstance().print("\nError found in file '"+fileName+"', line "+t.lineno()+": ");
}
}
