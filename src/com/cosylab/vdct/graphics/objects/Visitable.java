package com.cosylab.vdct.graphics.objects;

/**
 * Visitor pattern (interface elements must implement)
 * Creation date: (19.12.2000 18:27:22)
 * @author: Matej Sekoranja
 */
public interface Visitable {
/**
 * 
 * @param visitor com.cosylab.vdct.graphics.objects.Visitor
 */
void accept(Visitor visitor);
}
