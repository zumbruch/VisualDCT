package com.cosylab.vdct.undo;

/**
 * Insert the type's description here.
 * Creation date: (4.5.2001 11:37:23)
 * @author: 
 */
public class MoveToGroupAction extends ActionObject {
	private com.cosylab.vdct.graphics.objects.Flexible object;
	private String oldGroup;
	private String newGroup;
/**
 * Insert the method's description here.
 * Creation date: (4.5.2001 11:40:19)
 * @param object com.cosylab.vdct.graphics.objects.Flexible
 * @param oldGroup java.lang.String
 * @param newGroup java.lang.String
 */
public MoveToGroupAction(com.cosylab.vdct.graphics.objects.Flexible object, String oldGroup, String newGroup) {
	this.object=object;
	this.oldGroup=oldGroup;
	this.newGroup=newGroup;
}
/**
 * Insert the method's description here.
 * Creation date: (4.5.2001 11:37:23)
 * @return java.lang.String
 */
public String getDescription() {
	return "Move To Group ["+object.getFlexibleName()+"](\""+oldGroup+"\" to \""+newGroup+"\")";
}
/**
 * This method was created in VisualAge.
 */
protected void redoAction() {
	object.moveToGroup(newGroup);
}
/**
 * This method was created in VisualAge.
 */
protected void undoAction() {
	object.moveToGroup(oldGroup);
}
}
