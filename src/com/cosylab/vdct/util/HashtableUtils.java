package com.cosylab.vdct.util;

import java.util.*;

/**
 * This type was created in VisualAge.
 */
public class HashtableUtils {
/**
 * This method was created in VisualAge.
 * @param hash Hashtable
 * @param obj java.lang.Object[]
 */
public static Object[] getArray(Hashtable hash) {

	int len = hash.size(); 
	Object[] objs = new Object[len];
	
	Enumeration e = hash.elements();
	for (int i=1; e.hasMoreElements(); i++)
		objs[len-i]=e.nextElement();

	return objs;
}
}
