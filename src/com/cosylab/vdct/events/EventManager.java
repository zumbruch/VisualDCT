package com.cosylab.vdct.events;

import java.util.*;

/**
 * Insert the type's description here.
 * Creation date: (18.12.2000 15:26:40)
 * @author: Matej Sekoranja
 */
public abstract class EventManager {
	protected Hashtable subscreiber;

/**
 * CommandManager constructor comment.
 */
protected EventManager() {
	subscreiber = new Hashtable();
}
/**
 * Insert the method's description here.
 * Creation date: (18.12.2000 15:32:53)
 */
public void clear() {
	subscreiber.clear();
}
/**
 * Insert the method's description here.
 * Creation date: (18.12.2000 16:31:58)
 * @return java.util.Hashtable
 */
public java.util.Hashtable getSubscreiber() {
	return subscreiber;
}
/**
 * Insert the method's description here.
 * Creation date: (18.12.2000 16:12:53)
 * @param id java.lang.String
 * @param subscreiber java.lang.Object
 */
public void registerSubscreiber(String id, Object newSubscreiber) {
	if (subscreiber.containsKey(id))
		throw new IllegalArgumentException("Error: subscreiber with id '"+id+"' already exists...");
	else subscreiber.put(id, newSubscreiber);
}
/**
 * Insert the method's description here.
 * Creation date: (18.12.2000 16:12:53)
 * @param id java.lang.String
 * @param subscreiber java.lang.Object
 */
public void unregisterSubscreiber(String id, Object newSubscreiber) {
	subscreiber.remove(id);
}
}
