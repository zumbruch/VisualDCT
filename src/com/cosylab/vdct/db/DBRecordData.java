package com.cosylab.vdct.db;

import java.util.*;

/**
 * This type was created in VisualAge.
 */

public class DBRecordData extends DBComment {
	protected String record_type;
	protected String name;
	protected Hashtable fields = null;
	protected Vector fieldsV = null;

	protected int x = -1;			// used for layout
	protected int y = -1;
	protected java.awt.Color color = java.awt.Color.black;
	protected boolean rotated = false;
	protected String description = null;

/**
 * RecordData constructor comment.
 */
public DBRecordData() {
	fields = new Hashtable();
	fieldsV = new Vector();
}
/**
 * This method was created in VisualAge.
 * @param fd VisualDCTPackage.FieldData
 */
public void addField(DBFieldData fd) {
	if (!fields.containsKey(fd.getName())) {
		fields.put(fd.getName(), fd);
		fieldsV.addElement(fd);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 17:22:37)
 * @return java.awt.Color
 */
public java.awt.Color getColor() {
	return color;
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 17:22:37)
 * @return java.lang.String
 */
public java.lang.String getDescription() {
	return description;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:23:46)
 * @return java.util.Hashtable
 */
public Hashtable getFields() {
	return fields;
}
/**
 * Returs ordered (as read) list
 * Creation date: (6.1.2001 20:39:27)
 * @return java.util.Vector
 */
public Vector getFieldsV() {
	return fieldsV;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:23:46)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:23:46)
 * @return java.lang.String
 */
public java.lang.String getRecord_type() {
	return record_type;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:23:46)
 * @return int
 */
public int getX() {
	return x;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:23:46)
 * @return int
 */
public int getY() {
	return y;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:23:46)
 * @return boolean
 */
public boolean isRotated() {
	return rotated;
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 17:22:37)
 * @param newColor java.awt.Color
 */
public void setColor(java.awt.Color newColor) {
	color = newColor;
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 17:22:37)
 * @param newDescriprion java.lang.String
 */
public void setDescription(java.lang.String newDescription) {
	description = newDescription;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:23:46)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	name = newName;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:23:46)
 * @param newRecord_type java.lang.String
 */
public void setRecord_type(java.lang.String newRecord_type) {
	record_type = newRecord_type;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:23:46)
 * @param newRotated boolean
 */
public void setRotated(boolean newRotated) {
	rotated = newRotated;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:23:46)
 * @param newX int
 */
public void setX(int newX) {
	x = newX;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:23:46)
 * @param newY int
 */
public void setY(int newY) {
	y = newY;
}
}
