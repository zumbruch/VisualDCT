package com.cosylab.vdct.graphics.objects;

/**
 * Insert the type's description here.
 * Creation date: (29.1.2001 17:50:12)
 * @author: Matej Sekoranja
 */
public interface InLink extends Linkable {
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 17:55:10)
 * @return int
 */
int getInX();
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 17:55:21)
 * @return int
 */
int getInY();
/**
 * 
 * @return com.cosylab.vdct.graphics.objects.OutLink
 */
OutLink getOutput();
/**
 * 
 * @param output com.cosylab.vdct.graphics.objects.OutLink
 * @param prevOutput com.cosylab.vdct.graphics.objects.OutLink
 */
void setOutput(OutLink output, OutLink prevOutput);
}
