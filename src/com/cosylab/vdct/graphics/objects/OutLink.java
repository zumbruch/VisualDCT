package com.cosylab.vdct.graphics.objects;

/**
 * Insert the type's description here.
 * Creation date: (29.1.2001 17:50:31)
 * @author: Matej Sekoranja
 */
public interface OutLink extends Linkable {
/**
 * 
 * @return com.cosylab.vdct.graphics.objects.InLink
 */
InLink getInput();
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 17:53:09)
 * @return int
 */
int getOutX();
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 17:53:18)
 * @return int
 */
int getOutY();
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 14:46:40)
 * @return int
 */
int getQueueCount();
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 17:53:59)
 */
void setInput(InLink input);
}
