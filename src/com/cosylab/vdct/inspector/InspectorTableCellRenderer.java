package com.cosylab.vdct.inspector;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;

/**
 * Insert the type's description here.
 * Creation date: (7.1.2001 11:03:22)
 * @author: Matej Sekoranja
 */
public class InspectorTableCellRenderer extends DefaultTableCellRenderer {
	private Color bgColor;
	private Color fgColor;
	private Color sectionbgColor = Color.black;
	private Color sectionfgColor = Color.white;
	private InspectorTableModel tableModel;
/**
 * InspectorTableCellRenderer constructor comment.
 */
public InspectorTableCellRenderer(JTable table, InspectorTableModel tableModel) {
	super();
	this.tableModel=tableModel;
	bgColor = table.getBackground();
	fgColor = table.getForeground();
	sectionbgColor = table.getGridColor();
	sectionfgColor = Color.white;
	setFont(table.getFont());
	setBorder(noFocusBorder);
}
/**
 * Insert the method's description here.
 * Creation date: (7.1.2001 11:08:56)
 * @return java.awt.Component
 * @param table javax.swing.JTable
 * @param value java.lang.Object
 * @param isSelected boolean
 * @param hasFocus boolean
 * @param row int
 * @param column int
 */
public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	String str = value.toString();
	if (tableModel.getPropertyAt(row).isSepatator()) {
		super.setHorizontalAlignment(JLabel.CENTER);
		super.setBackground(sectionbgColor);
		super.setForeground(sectionfgColor);
	}
	else {
		super.setHorizontalAlignment(JLabel.LEFT);
		super.setBackground(bgColor);
		super.setForeground(fgColor);
	}
	setValue(str); 
	return this;
}
}
