package com.cosylab.vdct.vdb;

import com.cosylab.vdct.inspector.*;
/**
 * Insert the type's description here.
 * Creation date: (12.1.2001 22:40:34)
 * @author: 
 */
public class GUISeparator implements InspectableProperty {
	private String title;
/**
 * GUISeparator constructor comment.
 */
public GUISeparator(String title) {
	this.title=title;
}
/**
 * Insert the method's description here.
 * Creation date: (12.1.2001 22:40:34)
 * @return boolean
 */
public boolean allowsOtherValues() {
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 15:02:40)
 * @return java.lang.String
 */
public java.lang.String getHelp() {
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (12.1.2001 22:40:34)
 * @return java.lang.String
 */
public String getName() {
	return title;
}
/**
 * Insert the method's description here.
 * Creation date: (12.1.2001 22:40:34)
 * @return java.lang.String[]
 */
public java.lang.String[] getSelectableValues() {
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (12.1.2001 22:40:34)
 * @return java.lang.String
 */
public String getValue() {
	return title;
}
/**
 * Insert the method's description here.
 * Creation date: (12.1.2001 22:40:34)
 * @return boolean
 */
public boolean isEditable() {
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (12.1.2001 22:40:34)
 * @return boolean
 */
public boolean isSepatator() {
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (12.1.2001 22:40:34)
 * @param value java.lang.String
 */
public void setValue(String value) {}
}
