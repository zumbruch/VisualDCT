package com.cosylab.vdct.graphics.objects;

import java.util.*;

/**
 * Insert the type's description here.
 * Creation date: (21.12.2000 20:27:25)
 * @author: Matej Sekoranja
 */
public abstract class ContainerObject extends VisibleObject {
	protected boolean useHashtable = true;
	protected Hashtable subObjects = null;
	protected Vector subObjectsV = null;
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 20:40:08)
 * @param parent com.cosylab.vdct.graphics.objects.ContainerObject
 */
public ContainerObject(ContainerObject parent) {
	super(parent);
	subObjects = new Hashtable();			// key in not cass senstive
	subObjectsV = new Vector();				// to keep order
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 22:45:17)
 * @param parent com.cosylab.vdct.graphics.objects.ContainerObject
 * @param useHashtable boolean
 */
public ContainerObject(ContainerObject parent, boolean useHashtable) {
	super(parent);
	subObjectsV = new Vector();				// to keep order
	this.useHashtable=useHashtable;
	if (useHashtable)
		subObjects = new Hashtable();			// key in not cass senstive
}
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 20:30:04)
 * @param id java.lang.String
 * @param object com.cosylab.vdct.graphics.objects.VisibleObject
 */
public void addSubObject(String id, VisibleObject object) {
	if (useHashtable)
	{
		if (subObjects.containsKey(id.toUpperCase()))
		{
			com.cosylab.vdct.Console.getInstance().println("Object with name "+id+" already in this group -> will not be added.");
			return;
		}
		subObjects.put(id.toUpperCase(), object);
	}
	subObjectsV.addElement(object);

	if (object.getParent()==null) object.setParent(this);

	/*
	if (this instanceof Group)
		System.out.println("Added to group "+((Group)this).getAbsoluteName()+" object with id: "+id);
	*/	
	
}
/**
 * Insert the method's description here.
 * Creation date: (28.1.2001 12:04:45)
 */
public void clear() {
	if (useHashtable) subObjects.clear();
	subObjectsV.removeAllElements();
}
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 20:34:57)
 * @param id java.lang.String
 * @return boolean
 */
public boolean containsObject(String id) {
	if (useHashtable)
		return subObjects.containsKey(id.toUpperCase());
	else
		return false;
}
/**
 * Insert the method's description here.
 * Creation date: (28.1.2001 16:54:17)
 * @return java.lang.Object
 * @param id java.lang.String
 */
public Object getSubObject(String id) {
	if (useHashtable)
		return subObjects.get(id.toUpperCase());
	else
		return null;
}
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 20:35:48)
 * @return java.util.Hashtable
 */
public java.util.Hashtable getSubObjects() {
	return subObjects;
}
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 20:35:48)
 * @return java.util.Vector
 */
public java.util.Vector getSubObjectsV() {
	return subObjectsV;
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 22:49:30)
 * @param object java.lang.Object
 */
public void removeObject(Object object) throws Exception {
	if (!useHashtable) subObjectsV.removeElement(object);
	else {
		throw new Exception("ContainerObject uses Hashtable!");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 20:32:49)
 * @param id java.lang.String
 * @return java.lang.Object
 */
public Object removeObject(String id) {
	if (useHashtable) {
		Object object = subObjects.remove(id.toUpperCase());
		if (object!=null) subObjectsV.removeElement(object);

		/*
		if (object!=null)
			System.out.println("Removed: "+id+"["+object.toString()+"]");
		else
			System.out.println("Failed to remove: "+id);
		*/
			
		return object;
	}
	else 
		return null;
}
}
