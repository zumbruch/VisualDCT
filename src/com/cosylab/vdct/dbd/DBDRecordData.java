package com.cosylab.vdct.dbd;

import java.util.*;

/**
 * This type was created in VisualAge.
 */

public class DBDRecordData {
	protected String name;
	protected Hashtable fields = null;
	protected Vector fieldsV = null;
/**
 * RecordData constructor comment.
 */
public DBDRecordData() {
	fields = new Hashtable();
	fieldsV = new Vector();
}
/**
 * This method was created in VisualAge.
 * @param fd VisualDCTPackage.FieldData
 */
public void addField(DBDFieldData fd) {
	if (!fields.containsKey(fd.name)) {
		fields.put(fd.name, fd);
		fieldsV.addElement(fd);
	}
}
/**
 * This method was created in VisualAge.
 * @return com.cosylab.vdct.dbd.DBDFieldData
 * @param fieldName java.lang.String
 */
public DBDFieldData getDBDFieldData(String fieldName) {
	return (DBDFieldData)(fields.get(fieldName));
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:28:10)
 * @return java.util.Hashtable
 */
public java.util.Hashtable getFields() {
	return fields;
}
/**
 * Insert the method's description here.
 * Creation date: (6.1.2001 20:42:28)
 * @return java.util.Vector
 */
public java.util.Vector getFieldsV() {
	return fieldsV;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:28:10)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:28:10)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	name = newName;
}
}
