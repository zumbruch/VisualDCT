package com.cosylab.vdct.undo;

/**
 * Insert the type's description here.
 * Creation date: (3.5.2001 16:26:04)
 * @author: 
 */
public class CreateAction extends ActionObject {
	protected com.cosylab.vdct.graphics.objects.VisibleObject object;
	protected com.cosylab.vdct.graphics.objects.ContainerObject parent;
	protected int x, y;
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 16:27:58)
 * @param object com.cosylab.vdct.graphics.objects.VisibleObject
 * @param x int
 * @param y int
 */
public CreateAction(com.cosylab.vdct.graphics.objects.VisibleObject object) {
	this.object=object;
	this.x=object.getX(); this.y=object.getY();
	this.parent=object.getParent();
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 16:26:04)
 * @return java.lang.String
 */
public String getDescription() {
	return "Create ["+object+"]("+x+", "+y+")";
}
/**
 * This method was created in VisualAge.
 */
protected void redoAction() {
	parent.addSubObject(object.getHashID(), object);
	object.setDestroyed(false);
}
/**
 * This method was created in VisualAge.
 */
protected void undoAction() {
	parent.removeObject(object.getHashID());
}
}
