package com.cosylab.vdct.graphics;

import java.awt.Graphics;

/**
 * Decorator (using Decorator pattern)
 * Creation date: (10.12.2000 11:02:55)
 * @author: Matej Sekoranja
 */
public abstract class Decorator implements VisualComponent {
	private VisualComponent component;
/**
 * Insert the method's description here.
 * Creation date: (10.12.2000 13:25:58)
 */
public Decorator() {}
/**
 * ComponentManager constructor comment.
 */
public Decorator(VisualComponent component) {
	this.component=component;
}
/**
 * Default implementation
 * Creation date: (10.12.2000 11:25:20)
 */
public void draw(Graphics g) {
	component.draw(g);
}
/**
 * Insert the method's description here.
 * Creation date: (10.12.2000 13:02:11)
 * @return com.cosylab.vdct.graphics.VisualComponent
 */
public VisualComponent getComponent() {
	return component;
}
/**
 * Insert the method's description here.
 * Creation date: (11.12.2000 16:23:31)
 */
public int getComponentHeight() {
	if (component==null) return 0;
	else return component.getComponentHeight();
}
/**
 * Insert the method's description here.
 * Creation date: (11.12.2000 16:23:02)
 * @return int
 */
public int getComponentWidth() {
	if (component==null) return 0;
	else return component.getComponentWidth();
}
/**
 * Default implementation
 * Creation date: (10.12.2000 11:26:54)
 */
public void resize(int x0, int y0, int width, int height) {
	component.resize(x0, y0, width, height);
}
/**
 * Insert the method's description here.
 * Creation date: (10.12.2000 13:02:11)
 * @param newComponent com.cosylab.vdct.graphics.VisualComponent
 */
public void setComponent(VisualComponent newComponent) {
	component = newComponent;
}
}
