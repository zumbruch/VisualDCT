package com.cosylab.vdct.graphics;

import java.awt.Graphics;

/**
 * Interface class of ComponentDecorator
 * Creation date: (10.12.2000 11:34:30)
 * @author: Matej Sekoranja
 */

public interface VisualComponent {
/**
 * Insert the method's description here.
 * Creation date: (10.12.2000 11:25:20)
 */
public void draw(Graphics g);
/**
 * Insert the method's description here.
 * Creation date: (11.12.2000 16:23:31)
 */
public int getComponentHeight();
/**
 * Insert the method's description here.
 * Creation date: (11.12.2000 16:23:02)
 * @return int
 */
public int getComponentWidth();
/**
 * Insert the method's description here.
 * Creation date: (10.12.2000 11:26:54)
 */
public void resize(int x0, int y0, int width, int height);
}
