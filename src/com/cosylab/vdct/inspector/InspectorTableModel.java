package com.cosylab.vdct.inspector;

import javax.swing.table.AbstractTableModel;

/**
 * Insert the type's description here.
 * Creation date: (6.1.2001 23:29:20)
 * @author: Matej Sekoranja
 */
public class InspectorTableModel extends javax.swing.table.AbstractTableModel {
	private InspectableProperty[] data = null;
	private Inspectable dataObject = null;
/**
 * InspactorTableModel constructor comment.
 */
public InspectorTableModel() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (14.11.1999 15:22:35)
 * @return java.lang.Class
 * @param column int
 */
public Class getColumnClass(int column) {
	return String.class;
}
/**
 * getColumnCount method comment.
 */
public int getColumnCount() {
	return 2;			// name & value
}
/**
 * Insert the method's description here.
 * Creation date: (10.1.2001 17:12:58)
 * @return com.cosylab.vdct.inspector.Inspectable
 */
public Inspectable getDataObject() {
	return dataObject;
}
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 22:17:39)
 * @return com.cosylab.vdct.inspector.InspectableProperty
 * @param row int
 */
public InspectableProperty getPropertyAt(int row) {
	return data[row];
}
/**
 * getRowCount method comment.
 */
public int getRowCount() {
	if (data!=null)
		return data.length;
	else
		return 0;
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int column) {
	if (column==0) return data[row].getName();
	else return data[row].getValue();
}
/**
 * Insert the method's description here.
 * Creation date: (6.1.2001 23:41:13)
 * @return boolean
 * @param rowIndex int
 * @param columnIndex int
 */
public boolean isCellEditable(int rowIndex, int columnIndex) {

	// disable editing in debug mode (!!!)
	if (com.cosylab.vdct.plugin.debug.PluginDebugManager.isDebugState())
		return false;
		
	if (columnIndex==1) return data[rowIndex].isEditable();
	else return false;
}
/**
 * Insert the method's description here.
 * Creation date: (10.1.2001 17:12:58)
 * @param object com.cosylab.vdct.inspector.Inspectable
 */
public void setDataObject(Inspectable object) {
	dataObject = object;
	if (object!=null)
		data = object.getProperties();
	else
		data = null;
	fireTableDataChanged();
}
/**
 * Sets the object value for the cell at <I>column</I> and
 * <I>row</I>.  <I>aValue</I> is the new value.  This method
 * will generate a tableChanged() notification.
 *
 * @param   aValue          the new value.  This can be null.
 * @param   row             the row whose value is to be looked up
 * @param   column          the column whose value is to be looked up
 * @return                  the value Object at the specified cell
 */

public void setValueAt(Object aValue, int row, int column) {
	data[row].setValue(aValue.toString());
	// generate notification
	fireTableCellUpdated(row, column);
}
/**
 * Insert the method's description here.
 * Creation date: (5.5.2001 15:12:05)
 * @param property com.cosylab.vdct.inspector.InspectableProperty
 */
public void updateProperty(InspectableProperty property) {
	for (int row=0; row < data.length; row++)
		if (data[row]==property)
			fireTableCellUpdated(row, 1);
}
/**
 * Insert the method's description here.
 * Creation date: (5.5.2001 15:06:07)
 * @param propertyName java.lang.String
 */
public void updateProperty(String propertyName) {
	for (int row=0; row < data.length; row++)
		if (data[row].getName().equals(propertyName))
			fireTableCellUpdated(row, 1);
}
}
