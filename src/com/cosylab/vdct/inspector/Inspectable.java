package com.cosylab.vdct.inspector;

import javax.swing.*;

/**
 * Insert the type's description here.
 * Creation date: (10.1.2001 14:47:33)
 * @author: Matej Sekoranja
 */
public interface Inspectable {
/**
 * 
 * @return com.cosylab.vdct.inspector.InspectableProperty
 */
InspectableProperty getCommentProperty();
/**
 * Insert the method's description here.
 * Creation date: (10.1.2001 15:14:56)
 * @return javax.swing.Icon
 */
public Icon getIcon();
/**
 * Insert the method's description here.
 * Creation date: (10.1.2001 14:47:43)
 * @return java.lang.String
 */
public String getName();
/**
 * 
 * @return com.cosylab.vdct.inspector.InspectableProperty[]
 */
com.cosylab.vdct.inspector.InspectableProperty[] getProperties();
/**
 * Insert the method's description here.
 * Creation date: (10.1.2001 14:48:10)
 * @return java.lang.String
 */
public String toString();
}
