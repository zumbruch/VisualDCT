package com.cosylab.vdct.db;

/**
 * This type was created in VisualAge.
 */
public class DBFieldData extends DBComment {
	protected String name;	
	protected String value;
	protected boolean template_def = false;
	
	protected java.awt.Color color = java.awt.Color.black;
	protected boolean rotated;

	private static final String nullString = "";
	protected String description = nullString;

	protected boolean hasAdditionalData = false;
/**
 * FieldData constructor comment.
 */
public DBFieldData(String name, String value) {
	this.name=name;
	this.value=value;
}
/**
 * FieldData constructor comment.
 */
public DBFieldData(String name, String value, java.awt.Color color, boolean rotated, String description) {
	this.name=name;
	this.value=value;
	this.color=color;
	this.rotated=rotated;
	this.description=this.description;
	this.hasAdditionalData=true;
}
/**
 * Insert the method's description here.
 * Creation date: (24.4.2001 15:54:01)
 * @return java.awt.Color
 */
public java.awt.Color getColor() {
	return color;
}
/**
 * Insert the method's description here.
 * Creation date: (24.4.2001 15:54:01)
 * @return java.lang.String
 */
public java.lang.String getDescription() {
	return description;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:12:01)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:12:01)
 * @return java.lang.String
 */
public java.lang.String getValue() {
	return value;
}
/**
 * Insert the method's description here.
 * Creation date: (24.4.2001 18:39:06)
 * @return boolean
 */
public boolean isHasAdditionalData() {
	return hasAdditionalData;
}
/**
 * Insert the method's description here.
 * Creation date: (24.4.2001 15:54:01)
 * @return boolean
 */
public boolean isRotated() {
	return rotated;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:12:01)
 * @return boolean
 */
public boolean isTemplate_def() {
	return template_def;
}
/**
 * Insert the method's description here.
 * Creation date: (24.4.2001 15:54:01)
 * @param newColor java.awt.Color
 */
public void setColor(java.awt.Color newColor) {
	this.hasAdditionalData=true;
	color = newColor;
}
/**
 * Insert the method's description here.
 * Creation date: (24.4.2001 15:54:01)
 * @param newDescription java.lang.String
 */
public void setDescription(java.lang.String newDescription) {
	this.hasAdditionalData=true;
	description = newDescription;
}
/**
 * Insert the method's description here.
 * Creation date: (24.4.2001 18:39:06)
 * @param newHasAdditionalData boolean
 */
public void setHasAdditionalData(boolean newHasAdditionalData) {
	hasAdditionalData = newHasAdditionalData;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:12:01)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	name = newName;
}
/**
 * Insert the method's description here.
 * Creation date: (24.4.2001 15:54:01)
 * @param newRotated boolean
 */
public void setRotated(boolean newRotated) {
	this.hasAdditionalData=true;
	rotated = newRotated;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:12:01)
 * @param newTemplate_def boolean
 */
public void setTemplate_def(boolean newTemplate_def) {
	template_def = newTemplate_def;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:12:01)
 * @param newValue java.lang.String
 */
public void setValue(java.lang.String newValue) {
	value = newValue;
}
}
