package com.cosylab.vdct.db;

/**
 * This type was created in VisualAge.
 */
public class DBLinkData {
	protected String fieldName;
	protected String targetID;
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 21:12:09)
 * @param fieldName java.lang.String
 * @param targetID java.lang.String
 */
public DBLinkData(String fieldName, String targetID) {
	this.fieldName = fieldName;
	this.targetID = targetID;
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 21:12:17)
 * @return java.lang.String
 */
public java.lang.String getFieldName() {
	return fieldName;
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 21:12:17)
 * @return java.lang.String
 */
public java.lang.String getTargetID() {
	return targetID;
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 21:12:17)
 * @param newFieldName java.lang.String
 */
public void setFieldName(java.lang.String newFieldName) {
	fieldName = newFieldName;
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 21:12:17)
 * @param newTargetID java.lang.String
 */
public void setTargetID(java.lang.String newTargetID) {
	targetID = newTargetID;
}
}
