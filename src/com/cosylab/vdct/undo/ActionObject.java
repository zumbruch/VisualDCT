package com.cosylab.vdct.undo;

/**
 * This type was created in VisualAge.
 */
public abstract class ActionObject {
	//protected boolean done = true;
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 15:50:42)
 * @return java.lang.String
 */
public abstract String getDescription();
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isDone() {
	//return done; 
	return false;
}
/**
 * This method was created in VisualAge.
 */
public void redo() {
	//if (!done) {
		redoAction();
	/*	done=true;
	}*/
}
/**
 * This method was created in VisualAge.
 */
protected abstract void redoAction();
/**
 * This method was created in VisualAge.
 */
public void undo() {
	//if (done) {
		undoAction();
	/*	done=false;
	}*/
}
/**
 * This method was created in VisualAge.
 */
protected abstract void undoAction();
}
