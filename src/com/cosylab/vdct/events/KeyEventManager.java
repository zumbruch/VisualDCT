package com.cosylab.vdct.events;

import javax.swing.JComponent;
import java.awt.event.KeyListener;

/**
 * Insert the type's description here.
 * Creation date: (18.12.2000 16:23:24)
 * @author: Matej Sekoranja
 */
public class KeyEventManager extends EventManager {
	protected static KeyEventManager instance = null;
/**
 * Insert the method's description here.
 * Creation date: (18.12.2000 15:30:35)
 * @return com.cosylab.vdct.events.KeyEventManager
 */
public static KeyEventManager getInstance() {
	if (instance==null)
		instance = new KeyEventManager();
	return instance;
}
/**
 * Insert the method's description here.
 * Creation date: (18.12.2000 16:12:53)
 * @param id java.lang.String
 * @param component javax.swing.JComponent
 */
public void registerSubscreiber(String id, JComponent component) {
	super.registerSubscreiber(id, component);
}
/**
 * Insert the method's description here.
 * Creation date: (18.12.2000 16:25:04)
 * @param subscriberID java.lang.String
 * @param listener java.awt.KeyListener
 */
public void subscribe(String subscriberID, KeyListener listener) {
	JComponent comp = (JComponent)getSubscreiber().get(subscriberID);
	if (comp!=null)
		comp.addKeyListener(listener);
}
/**
 * Insert the method's description here.
 * Creation date: (18.12.2000 16:12:53)
 * @param id java.lang.String
 * @param component javax.swing.JComponent
 */
 
public void unregisterSubscreiber(String id, JComponent component) {
	super.unregisterSubscreiber(id, component);
}
/**
 * Insert the method's description here.
 * Creation date: (18.12.2000 16:25:04)
 * @param subscriberID java.lang.String
 * @param listener java.awt.KeyListener
 */
public void unsubscribe(String subscriberID, KeyListener listener) {
	JComponent comp = (JComponent)getSubscreiber().get(subscriberID);
	if (comp!=null)
		comp.removeKeyListener(listener);
}
}
