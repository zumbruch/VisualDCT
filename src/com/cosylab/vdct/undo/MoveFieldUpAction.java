package com.cosylab.vdct.undo;

/**
 * Insert the type's description here.
 * Creation date: (5.5.2001 12:07:47)
 * @author: 
 */
public class MoveFieldUpAction extends ActionObject {
	protected com.cosylab.vdct.graphics.objects.Field field;
	protected com.cosylab.vdct.graphics.objects.Record record;
/**
 * Insert the method's description here.
 * Creation date: (5.5.2001 12:08:29)
 * @param field com.cosylab.vdct.graphics.objects.Field
 */
public MoveFieldUpAction(com.cosylab.vdct.graphics.objects.Field field) {
	this.field=field;
	this.record = (com.cosylab.vdct.graphics.objects.Record)field.getParent();
}
/**
 * Insert the method's description here.
 * Creation date: (5.5.2001 12:07:47)
 * @return java.lang.String
 */
public String getDescription() {
	return "Move field up ["+field+"]";
;
}
/**
 * This method was created in VisualAge.
 */
protected void redoAction() {
	record.moveFieldUp(field);
}
/**
 * This method was created in VisualAge.
 */
protected void undoAction() {
	record.moveFieldDown(field);
}
}
