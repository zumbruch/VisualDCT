package com.cosylab.vdct.undo;

/**
 * Insert the type's description here.
 * Creation date: (3.5.2001 15:23:26)
 * @author: 
 */
public class CommentChangeAction extends ActionObject {
	private com.cosylab.vdct.vdb.CommentProperty commentProperty;
	private String oldValue;
	private String newValue;
	
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 15:30:47)
 * @param field com.cosylab.vdct.vdb.CommentProperty
 * @param oldValue java.lang.String
 * @param newValue java.lang.String
 */
public CommentChangeAction(com.cosylab.vdct.vdb.CommentProperty commentProperty, String oldValue, String newValue) {
	this.commentProperty=commentProperty;
	this.oldValue=oldValue;
	this.newValue=newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 15:50:49)
 * @return java.lang.String
 */
public java.lang.String getDescription() {
	return "Record comment value change ["+commentProperty.getName()+"](\""+oldValue+"\" to \""+newValue+"\")";
}
/**
 * This method was created in VisualAge.
 */
protected void redoAction() {
	commentProperty.setValue(newValue);
}
/**
 * This method was created in VisualAge.
 */
protected void undoAction() {
	commentProperty.setValue(oldValue);
}
}
