package com.cosylab.vdct.inspector;

import javax.swing.*;
import javax.swing.border.*;
/**
 * Insert the type's description here.
 * Creation date: (10.1.2001 15:10:15)
 * @author: Matej Sekoranja
 */
public class InspectorListCellRenderer extends JLabel implements ListCellRenderer {
	private static String noObject = "(No object selected)";
/**
 * InspectorListCellRenderer constructor comment.
 */
public InspectorListCellRenderer() {
	setOpaque(true);
}
	/**
	 * Return a component that has been configured to display the specified
	 * value. That component's <code>paint</code> method is then called to
	 * "render" the cell.  If it is necessary to compute the dimensions
	 * of a list because the list cells do not have a fixed size, this method
	 * is called to generate a component on which <code>getPreferredSize</code>
	 * can be invoked.
	 *
	 * @param list The JList we're painting.
	 * @param value The value returned by list.getModel().getElementAt(index).
	 * @param index The cells index.
	 * @param isSelected True if the specified cell was selected.
	 * @param cellHasFocus True if the specified cell has the focus.
	 * @return A component whose paint() method will render the specified value.
	 *
	 * @see JList
	 * @see ListSelectionModel
	 * @see ListModel
	 */
public java.awt.Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		
	setComponentOrientation(list.getComponentOrientation());
		
	if (isSelected) {
	    setBackground(list.getSelectionBackground());
	    setForeground(list.getSelectionForeground());
	}
	else {
	    setBackground(list.getBackground());
	    setForeground(list.getForeground());
	}

	if (value==null)
		setText(noObject);
	else
		setText(value.toString());

	if (value instanceof Inspectable)
	{
		Inspectable idval = (Inspectable)value;
		setIcon(idval.getIcon());
	}
	else
		setIcon(null);

	setEnabled(list.isEnabled());
	setFont(list.getFont());

	return this;
}
}
