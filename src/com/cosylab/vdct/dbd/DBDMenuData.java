package com.cosylab.vdct.dbd;

import java.util.*;

/**
 * This type was created in VisualAge.
 */
public class DBDMenuData {
	protected String name;
	protected Hashtable choices;
/**
 * MenuData constructor comment.
 */
public DBDMenuData() {
	choices = new Hashtable();
}
/**
 * This method was created in VisualAge.
 * @param choice_name java.lang.String
 * @param choice_value java.lang.String
 */
public void addMenuChoice(String choice_name, String choice_value) {
	if (!choices.containsKey(choice_name))
		choices.put(choice_name, choice_value);
}
/**
 * This method was created in VisualAge.
 * @param value java.lang.String
 */
public boolean containsValue(String value) {
	String val;
	Enumeration e = choices.elements();
	while (e.hasMoreElements()) {
		val = e.nextElement().toString();
		if (val.equalsIgnoreCase(value)) return true;
	}
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:26:16)
 * @return java.util.Hashtable
 */
public java.util.Hashtable getChoices() {
	return choices;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:26:16)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:26:16)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	name = newName;
}
}
