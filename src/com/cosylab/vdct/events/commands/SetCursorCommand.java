package com.cosylab.vdct.events.commands;

import com.cosylab.vdct.events.Command;
import java.awt.Cursor;
import javax.swing.JComponent;

/**
 * Insert the type's description here.
 * Creation date: (21.12.2000 22:42:23)
 * @author: Matej Sekoranja
 */
public class SetCursorCommand extends Command {
	private JComponent component;
	private Cursor cursor;
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 22:43:26)
 * @param component javax.swing.JComponent
 */
public SetCursorCommand(JComponent component) {
	this.component=component;
}
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 22:42:23)
 */
public void execute() {
	component.setCursor(cursor);
}
/**
 * Insert the method's description here.
 * Creation date: (25.12.2000 16:51:44)
 * @param cursor java.awt.Cursor
 */
public void setCursor(Cursor cursor) {
	this.cursor=cursor;
}
}
