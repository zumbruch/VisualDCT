package com.cosylab.vdct.inspector;

import javax.swing.*;

/**
 * Insert the type's description here.
 * Creation date: (26.1.2001 21:00:33)
 * @author: Matej Sekoranja
 */
public class BorderlessComboBoxEditor extends JTextField implements ComboBoxEditor {
/**
 * BoerderlessComboBoxEditor constructor comment.
 */
public BorderlessComboBoxEditor() {
	setBorder(null);
	setColumns(9);
}
 /** Return the component that should be added to the tree hierarchy for
	* this editor
	*/
public java.awt.Component getEditorComponent() {
	return this;
}
 /** Return the edited item **/
public Object getItem() {
	return getText();
}
 /** Ask the editor to start editing and to select everything **/
public void selectAll() {
	selectAll();
	requestFocus();
}
 /** Set the item that should be edited. Cancel any editing if necessary **/
public void setItem(Object anObject) {
	if ( anObject != null )
		setText(anObject.toString());
	else
		setText("");
}
}
