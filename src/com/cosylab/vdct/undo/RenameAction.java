package com.cosylab.vdct.undo;

/**
 * Insert the type's description here.
 * Creation date: (4.5.2001 11:37:23)
 * @author: 
 */
public class RenameAction extends ActionObject {
	private com.cosylab.vdct.graphics.objects.Flexible object;
	private String oldName;
	private String newName;
/**
 * Insert the method's description here.
 * Creation date: (4.5.2001 11:40:19)
 * @param object com.cosylab.vdct.graphics.objects.Flexible
 * @param oldName java.lang.String
 * @param newName java.lang.String
 */
public RenameAction(com.cosylab.vdct.graphics.objects.Flexible object, String oldName, String newName) {
	this.object=object;
	this.oldName=oldName;
	this.newName=newName;
}
/**
 * Insert the method's description here.
 * Creation date: (4.5.2001 11:37:23)
 * @return java.lang.String
 */
public String getDescription() {
	return "Rename ["+object.getFlexibleName()+"](\""+oldName+"\" to \""+newName+"\")";
}
/**
 * This method was created in VisualAge.
 */
protected void redoAction() {
	object.rename(newName);
}
/**
 * This method was created in VisualAge.
 */
protected void undoAction() {
	object.rename(oldName);
}
}
