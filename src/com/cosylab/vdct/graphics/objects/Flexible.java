package com.cosylab.vdct.graphics.objects;

/**
 * Insert the type's description here.
 * Creation date: (4.2.2001 21:54:45)
 * @author: Matej Sekoranja
 */
public interface Flexible {
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 21:59:44)
 * @param group java.lang.String
 */
boolean copyToGroup(String group);
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 10:15:48)
 * @return java.lang.String
 */
String getFlexibleName();
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 22:00:01)
 * @param group java.lang.String
 */
boolean moveToGroup(String group);
/**
 * Insert the method's description here.
 * Creation date: (2.5.2001 23:23:17)
 * @param newName java.lang.String
 */
boolean rename(String newName);
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 10:15:48)
 * @return java.lang.String
 */
String toString();
}
