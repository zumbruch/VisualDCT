package com.cosylab.vdct.undo;

/**
 * Insert the type's description here.
 * Creation date: (3.5.2001 20:32:44)
 * @author: 
 */
public class DeleteAction extends CreateAction {
/**
 * DeleteAction constructor comment.
 * @param object com.cosylab.vdct.graphics.objects.VisibleObject
 */
public DeleteAction(com.cosylab.vdct.graphics.objects.VisibleObject object) {
	super(object);
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 16:26:04)
 * @return java.lang.String
 */
public String getDescription() {
	return "Delete ["+object+"]("+x+", "+y+")";
}
/**
 * This method was created in VisualAge.
 */
protected void redoAction() {
	super.undoAction();
}
/**
 * This method was created in VisualAge.
 */
protected void undoAction() {
	super.redoAction();
}
}
