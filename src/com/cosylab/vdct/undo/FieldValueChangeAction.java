package com.cosylab.vdct.undo;

/**
 * Insert the type's description here.
 * Creation date: (3.5.2001 15:23:26)
 * @author: 
 */
public class FieldValueChangeAction extends ActionObject {
	private com.cosylab.vdct.vdb.VDBFieldData field;
	private String oldValue;
	private String newValue;
	
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 15:30:47)
 * @param field com.cosylab.vdct.vdb.VDBFieldData
 * @param oldValue java.lang.String
 * @param newValue java.lang.String
 */
public FieldValueChangeAction(com.cosylab.vdct.vdb.VDBFieldData field, String oldValue, String newValue) {
	this.field=field;
	this.oldValue=oldValue;
	this.newValue=newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 15:50:49)
 * @return java.lang.String
 */
public java.lang.String getDescription() {
	return "Field value change ["+field.getFullName()+"](\""+oldValue+"\" to \""+newValue+"\")";
}
/**
 * This method was created in VisualAge.
 */
protected void redoAction() {
	field.setValue(newValue);
}
/**
 * This method was created in VisualAge.
 */
protected void undoAction() {
	field.setValue(oldValue);
}
}
