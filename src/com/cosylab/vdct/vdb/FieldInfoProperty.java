package com.cosylab.vdct.vdb;

import com.cosylab.vdct.inspector.*;

/**
 * Insert the type's description here.
 * Creation date: (1.2.2001 22:49:33)
 * @author: Matej Sekoranja
 */
public class FieldInfoProperty implements InspectableProperty {
	private VDBFieldData field;
/**
 * DTYPInfoProperty constructor comment.
 */
public FieldInfoProperty(VDBFieldData field) {
	this.field=field;
}
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 22:49:33)
 * @return boolean
 */
public boolean allowsOtherValues() {
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 22:49:33)
 * @return java.lang.String
 */
public String getHelp() {
	return field.getHelp();
}
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 22:49:33)
 * @return java.lang.String
 */
public String getName() {
	return field.getName();
}
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 22:49:33)
 * @return java.lang.String[]
 */
public java.lang.String[] getSelectableValues() {
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 22:49:33)
 * @return java.lang.String
 */
public String getValue() {
/*	String value = field.getValue();
	if (value.equals(com.cosylab.vdct.Constants.NONE)) //!!! default value
		return field.getDbdData().getInit_value();
	else */
		return field.getValue();
}
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 22:49:33)
 * @return boolean
 */
public boolean isEditable() {
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 22:49:33)
 * @return boolean
 */
public boolean isSepatator() {
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 22:49:33)
 * @param value java.lang.String
 */
public void setValue(String value) {}
}
