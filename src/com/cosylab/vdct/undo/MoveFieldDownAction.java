package com.cosylab.vdct.undo;

/**
 * Insert the type's description here.
 * Creation date: (5.5.2001 12:11:51)
 * @author: 
 */
public class MoveFieldDownAction extends MoveFieldUpAction {
/**
 * MoveFieldDownAction constructor comment.
 * @param field com.cosylab.vdct.graphics.objects.Field
 */
public MoveFieldDownAction(com.cosylab.vdct.graphics.objects.Field field) {
	super(field);
}
/**
 * Insert the method's description here.
 * Creation date: (5.5.2001 12:07:47)
 * @return java.lang.String
 */
public String getDescription() {
	return "Move field down ["+field+"]";
;
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
