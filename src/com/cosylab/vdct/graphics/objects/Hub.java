package com.cosylab.vdct.graphics.objects;

/**
 * Insert the type's description here.
 * Creation date: (29.1.2001 17:58:17)
 * @author: Matej Sekoranja
 */
public interface Hub {
/**
 * 
 * @param link com.cosylab.vdct.graphics.objects.Linkable
 */
void addLink(Linkable link);
/**
 * 
 * @param link com.cosylab.vdct.graphics.objects.Linkable
 */
void removeLink(Linkable link);
}
