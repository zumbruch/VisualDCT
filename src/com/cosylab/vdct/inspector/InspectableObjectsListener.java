package com.cosylab.vdct.inspector;

/**
 * Insert the type's description here.
 * Creation date: (17.4.2001 17:12:55)
 * @author: Matej Sekoranja
 */
public interface InspectableObjectsListener {
/**
 * 
 * @param object com.cosylab.vdct.inspector.Inspectable
 */
void inspectableObjectAdded(Inspectable object);
/**
 * 
 * @param object com.cosylab.vdct.inspector.Inspectable
 */
void inspectableObjectRemoved(Inspectable object);
}
