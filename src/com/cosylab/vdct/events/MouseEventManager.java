package com.cosylab.vdct.events;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

/**
 * Insert the type's description here.
 * Creation date: (18.12.2000 16:23:24)
 * @author: Matej Sekoranja
 */
public class MouseEventManager extends EventManager {
	protected static MouseEventManager instance = null;
/**
 * Insert the method's description here.
 * Creation date: (18.12.2000 15:30:35)
 * @return com.cosylab.vdct.events.MouseEventManager
 */
public static MouseEventManager getInstance() {
	if (instance==null)
		instance = new MouseEventManager();
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
 * @param listener javax.swing.event.MouseInputListener
 */
public void subscribe(String subscriberID, MouseInputListener listener) {
	JComponent comp = (JComponent)getSubscreiber().get(subscriberID);
	if (comp!=null) {
		comp.addMouseListener(listener);
		comp.addMouseMotionListener(listener);
	}
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
 * @param listener javax.swing.event.MouseInputListener
 */
public void unsubscribe(String subscriberID, MouseInputListener listener) {
	JComponent comp = (JComponent)getSubscreiber().get(subscriberID);
	if (comp!=null) {
		comp.removeMouseListener(listener);
		comp.removeMouseMotionListener(listener);
	}
}
}
