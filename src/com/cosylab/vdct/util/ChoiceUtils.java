package com.cosylab.vdct.util;

/**
 * Insert the type's description here.
 * Creation date: (5.11.1999 14:51:56)
 * @author: 
 */
public class ChoiceUtils {
/**
 * Insert the method's description here.
 * Creation date: (5.11.1999 14:52:48)
 * @return boolean
 * @param ch java.awt.Choice
 * @param item java.lang.String
 */
public static boolean containsItem(java.awt.Choice ch, String item) {
	boolean found = false;

	for (int i=0; (i<ch.getItemCount()) && !found; i++)
		if (ch.getItem(i).equals(item)) found=true;
	
	return found;
}
}
