package com.cosylab.vdct.graphics;

import java.awt.event.*;
import java.awt.Graphics;
import javax.swing.event.*;
import com.cosylab.vdct.events.*;
import com.cosylab.vdct.events.commands.*;

/**
 * Insert the type's description here.
 * Creation date: (10.12.2000 12:31:58)
 * @author: Matej Sekoranja
 */
 
public class WorkspacePanel extends PanelDecorator implements ComponentListener {
/**
 * VisualAge support
 * Creation date: (10.12.2000 12:34:33)
 */
public WorkspacePanel() {
	initialize();
}
/**
 * WorkspacePanel constructor comment.
 * @param component com.cosylab.vdct.graphics.VisualComponent
 */
public WorkspacePanel(VisualComponent component) {
	super(component);
}
	/**
	 * Invoked when the component has been made invisible.
	 */
public void componentHidden(ComponentEvent e) {}
	/**
	 * Invoked when the component's position changes.
	 */
public void componentMoved(ComponentEvent e) {}
	/**
	 * Invoked when the component's size changes.
	 */
public void componentResized(ComponentEvent e) {
	getComponent().resize(0, 0, getWidth(), getHeight());
}
	/**
	 * Invoked when the component has been made visible.
	 */
public void componentShown(ComponentEvent e) {
}
/**
 * Insert the method's description here.
 * Creation date: (11.12.2000 15:44:25)
 */
protected void initialize() {
	addComponentListener(this);
	MouseEventManager.getInstance().registerSubscreiber("WorkspacePanel", this);
	CommandManager.getInstance().addCommand("RepaintWorkspace", new RepaintCommand(this));
	CommandManager.getInstance().addCommand("NullCommand", new NullCommand(this));
}
/**
 * Insert the method's description here.
 * Creation date: (10.12.2000 14:19:55)
 * @param g java.awt.Graphics
 */

protected void paintComponent(Graphics g) {
	getComponent().draw(g);
}
}
