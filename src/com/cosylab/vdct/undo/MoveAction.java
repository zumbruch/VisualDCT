package com.cosylab.vdct.undo;

import com.cosylab.vdct.graphics.objects.*;

/**
 * This type was created in VisualAge.
 */
public class MoveAction extends ActionObject {
	private Movable object;
	private int dx, dy;
/**
 * This method was created in VisualAge.
 * @param object Movable 
 * @param dx int
 * @param dy int
 */
public MoveAction(Movable object, int dx, int dy) {
	this.object=object;
	this.dx=dx; this.dy=dy;
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 15:53:42)
 * @return java.lang.String
 */
public java.lang.String getDescription() {
	return "Move ["+object+"]("+dx+", "+dy+")";
}
/**
 * redoAction method comment.
 */
protected void redoAction() {
	object.move(dx, dy);
}
/**
 * undoAction method comment.
 */
protected void undoAction() {
	object.move(-dx, -dy);
}
}
