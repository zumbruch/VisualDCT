package com.cosylab.vdct.graphics.objects;

/**
 * Insert the type's description here.
 * Creation date: (19.12.2000 20:18:41)
 * @author: Matej Sekoranja
 */
public interface Linkable {
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 17:46:17)
 */
void disconnect(Linkable disconnector);
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 20:33:36)
 * @return java.lang.String
 */
String getID();
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 17:57:19)
 * @return java.lang.String
 */
String getLayerID();
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 17:51:40)
 * @return boolean
 */
boolean isConnectable();
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 17:52:22)
 * @return boolean
 */
boolean isDisconnected();
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 17:57:37)
 * @param id java.lang.String
 */
void setLayerID(String id);
}
