package com.cosylab.vdct.db;

import com.cosylab.vdct.Console;

/**
 * This type was created in VisualAge.
 */
public class DBGParseException extends Exception {
/**
 * DBParseException constructor comment.
 * @param s java.lang.String
 */
public DBGParseException(String s, java.io.StreamTokenizer t, String fileName) {
	super(s);
	if (t.ttype == t.TT_WORD)
		if (t.sval!=null)
			Console.getInstance().print("\nError found in file '"+fileName+"', line "+t.lineno()+" near token '"+t.sval+"': ");
		else
			Console.getInstance().print("\nError found in file '"+fileName+"', line "+t.lineno()+": ");
	else
		Console.getInstance().print("\nError found in file '"+fileName+"', line "+t.lineno()+" near token '"+(int)t.nval+"': ");
}
}
