package com.cosylab.vdct.undo;

/**
 * Insert the type's description here.
 * Creation date: (3.5.2001 20:42:25)
 * @author: 
 */
public interface ComposedActionInterface {
/**
 * 
 * @param action com.cosylab.vdct.undo.ActionObject
 */
void addAction(ActionObject action);
/**
 * 
 * @param action com.cosylab.vdct.undo.ActionObject
 */
void removeAction(ActionObject action);
}
