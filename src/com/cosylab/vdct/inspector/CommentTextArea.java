package com.cosylab.vdct.inspector;

import javax.swing.*;
import java.awt.event.FocusListener;

/**
 * Insert the type's description here.
 * Creation date: (26.1.2001 15:10:46)
 * @author: Matej Sekoranja
 */
public class CommentTextArea extends JTextArea implements java.awt.event.FocusListener {
	InspectableProperty property = null;
/**
 * CommentTextArea constructor comment.
 */
public CommentTextArea() {
	addFocusListener(this);
}
	/**
	 * Invoked when a component gains the keyboard focus.
	 */
public void focusGained(java.awt.event.FocusEvent e) {
	InspectorManager.getInstance().getActiveInspector().setHelp(property.getHelp());
}
	/**
	 * Invoked when a component loses the keyboard focus.
	 */
public void focusLost(java.awt.event.FocusEvent e) {
	property.setValue(getText());
	InspectorManager.getInstance().getActiveInspector().setHelp("");
}
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 15:11:22)
 * @param newProperty com.cosylab.vdct.inspector.InspectableProperty
 */
public void setProperty(InspectableProperty newProperty) {
	if ((newProperty==null) && (property!=null)) {
		property.setValue(getText());
		setText("");
		setEnabled(false);
	}

	property=newProperty;
	if (property==null) return;

	if (!isEnabled()) 
		setEnabled(true);
	if (isEditable()!=property.isEditable())
		setEditable(property.isEditable());

	setText(property.getValue());
}
}
