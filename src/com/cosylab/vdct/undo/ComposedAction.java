package com.cosylab.vdct.undo;

/**
 * Insert the type's description here.
 * Creation date: (3.5.2001 20:34:53)
 * @author: 
 */
public class ComposedAction extends ActionObject implements ComposedActionInterface {
	protected java.util.Vector actions;
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 20:35:51)
 */
public ComposedAction() {
	actions = new java.util.Vector();
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 20:36:35)
 * @param action com.cosylab.vdct.undo.ActionObject
 */
public void addAction(ActionObject action) {
	actions.addElement(action);
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 20:34:53)
 * @return java.lang.String
 */
public String getDescription() {
	return "Composed Action ["+actions.size()+" actions]";
}
/**
 * This method was created in VisualAge.
 */
protected void redoAction() {
	java.util.Enumeration e = actions.elements();
	while (e.hasMoreElements())
		((ActionObject)e.nextElement()).redoAction();
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 20:36:50)
 * @param action com.cosylab.vdct.undo.ActionObject
 */
public void removeAction(ActionObject action) {
	actions.removeElement(action);
}
/**
 * This method was created in VisualAge.
 */
protected void undoAction() {

	Object[] objs = new Object[actions.size()];
	actions.copyInto(objs);
	for (int i=objs.length-1; i>=0; i--)
		((ActionObject)objs[i]).undoAction();
}
}
