package com.cosylab.vdct.inspector;

import com.cosylab.vdct.dbd.*;
import com.cosylab.vdct.vdb.*;

/**
 * Insert the type's description here.
 * Creation date: (8.1.2001 17:43:35)
 * @author: Matej Sekoranja
 */
public interface InspectorInterface {
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 21:40:02)
 */
public void dispose();
/**
 * 
 * @return com.cosylab.vdct.inspector.Inspectable
 */
Inspectable getInspectedObject();
/**
 * 
 * @param object com.cosylab.vdct.inspector.Inspectable
 */
void inspectObject(Inspectable object);
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 21:48:50)
 * @return boolean
 */
public boolean isFrozen();
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 17:45:56)
 */
public void reinitialize();
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 15:18:35)
 * @param help java.lang.String
 */
void setHelp(String help);
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 17:48:51)
 * @param state boolean
 */
public void setVisible(boolean state);
/**
 * Insert the method's description here.
 * Creation date: (5.5.2001 15:13:26)
 */
void updateComment();
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 17:46:28)
 */
public void updateObjectList();
/**
 * 
 * @param property com.cosylab.vdct.inspector.InspectableProperty
 */
void updateProperty(InspectableProperty property);
}
