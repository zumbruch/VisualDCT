package com.cosylab.vdct.graphics.objects;

/**
 * Insert the type's description here.
 * Creation date: (25.12.2000 14:08:34)
 * @author: Matej Sekoranja
 */
public interface Movable {
/**
 * Insert the method's description here.
 * Creation date: (27.12.2000 13:08:09)
 * @return boolean
 * @param dx int
 * @param dy int
 */
public boolean checkMove(int dx, int dy);
/**
 * Insert the method's description here.
 * Creation date: (25.12.2000 14:13:14)
 * @return boolean
 * @param dx int
 * @param dy int
 */
public boolean move(int dx, int dy);
}
