package com.cosylab.vdct.vdb;

import com.cosylab.vdct.dbd.*;
import com.cosylab.vdct.DataProvider;
import com.cosylab.vdct.Console;
import com.cosylab.vdct.inspector.InspectableProperty;
import com.cosylab.vdct.graphics.objects.Debuggable;
import java.util.*;

/**
 * This type was created in VisualAge.
 */
public class VDBFieldData implements InspectableProperty, Debuggable {
	protected int type;
	protected int GUI_type;
	protected String name;
	protected String value;
	protected String init_value;
	protected String comment;
	protected boolean template_def = false;
	protected DBDFieldData dbdData;
	protected VDBRecordData record = null;

	private static final String debugDefault = "###";
	protected String debugValue = debugDefault;

/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:47:04)
 * @return boolean
 */
public boolean allowsOtherValues() {
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 18:11:46)
 * @return java.lang.String
 */
public java.lang.String getComment() {
	return comment;
}
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 22:01:51)
 * @return com.cosylab.vdct.dbd.DBDFieldData
 */
public com.cosylab.vdct.dbd.DBDFieldData getDbdData() {
	return dbdData;
}
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 12:11:29)
 * @return java.lang.String
 */
public String getFullName() {
	if (getRecord()==null)
		return "(undefined)"+com.cosylab.vdct.Constants.FIELD_SEPARATOR+getName();
	else
		return getRecord().getName()+com.cosylab.vdct.Constants.FIELD_SEPARATOR+getName();
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 18:11:46)
 * @return int
 */
public int getGUI_type() {
	return GUI_type;
}
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 15:03:07)
 * @return java.lang.String
 */
public java.lang.String getHelp() {
	return dbdData.getPrompt_value();
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 18:11:46)
 * @return java.lang.String
 */
public java.lang.String getInit_value() {
	return init_value;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 18:11:46)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (12.1.2001 20:01:39)
 * @return com.cosylab.vdct.vdb.VDBRecordData
 */
public VDBRecordData getRecord() {
	return record;
}
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:47:04)
 * @return java.lang.String[]
 */
public java.lang.String[] getSelectableValues() {
	if ((dbdData.getField_type() == DBDConstants.DBF_MENU) ||
		(dbdData.getField_type() == DBDConstants.DBF_DEVICE)) {

			DBDData dbd = DataProvider.getInstance().getDbdDB();
			Vector values = new Vector();

			if (dbdData.getField_type() == DBDConstants.DBF_MENU) {
					DBDMenuData md = dbd.getDBDMenuData(dbdData.getMenu_name());
					if (md!=null) {
						Enumeration e = md.getChoices().elements();
						values.addElement(com.cosylab.vdct.Constants.NONE);
						while (e.hasMoreElements())
							values.addElement(e.nextElement().toString());
					}					
					else Console.getInstance().println("Menu '"+dbdData.getMenu_name()+"' not found...");
			}
			else if (dbdData.getField_type() == DBDConstants.DBF_DEVICE) {
				
					Enumeration e = dbd.getDevices().elements();
					DBDDeviceData dev;
					values.addElement(com.cosylab.vdct.Constants.NONE);
					while (e.hasMoreElements()) {
						dev = (DBDDeviceData)(e.nextElement());
						if (record.getType().equals(dev.getRecord_type()))
							values.addElement(dev.getChoice_string());
					}
			}

			if (values.size()==0) return null;

			String choices[] = new String[values.size()];
			values.copyInto(choices);
			// !!!
			new com.cosylab.vdct.util.StringQuickSort().sort(choices);
			return choices;
			
	}
	else return null;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 18:11:46)
 * @return int
 */
public int getType() {
	return type;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 18:11:46)
 * @return java.lang.String
 */
public java.lang.String getValue() {
	if (!com.cosylab.vdct.plugin.debug.PluginDebugManager.isDebugState())
		return value;
	else
		return debugValue;
}
/**
 * Insert the method's description here.
 * Creation date: (27.1.2001 16:08:45)
 * @return boolean
 */
public boolean hasDefaultValue() {
	// !!! is this true, also for MENU, DEVICE
	if (value.equals(com.cosylab.vdct.Constants.NONE) && dbdData.getInit_value().equals("")) return true;
	else if (!value.equals(dbdData.getInit_value()))
		return false;
	else 
		return true;

}
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:47:04)
 * @return boolean
 */
public boolean isEditable() {
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:47:04)
 * @return boolean
 */
public boolean isSepatator() {
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 18:11:46)
 * @return boolean
 */
public boolean isTemplate_def() {
	return template_def;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 18:11:46)
 * @param newComment java.lang.String
 */
public void setComment(java.lang.String newComment) {
	comment = newComment;
}
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 22:01:51)
 * @param newDbdData com.cosylab.vdct.dbd.DBDFieldData
 */
public void setDbdData(com.cosylab.vdct.dbd.DBDFieldData newDbdData) {
	dbdData = newDbdData;
}
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 19:13:20)
 * @param value java.lang.String
 */
public void setDebugValue(String newValue)
{
	if (com.cosylab.vdct.plugin.debug.PluginDebugManager.isDebugState())
	{
		debugValue = newValue; 
		if (record!=null) record.fieldValueChanged(this);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 18:11:46)
 * @param newGUI_type int
 */
public void setGUI_type(int newGUI_type) {
	GUI_type = newGUI_type;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 18:11:46)
 * @param newInit_value java.lang.String
 */
public void setInit_value(java.lang.String newInit_value) {
	init_value = newInit_value;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 18:11:46)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	name = newName;
}
/**
 * Insert the method's description here.
 * Creation date: (12.1.2001 20:01:39)
 * @param newRecord com.cosylab.vdct.vdb.VDBRecordData
 */
public void setRecord(VDBRecordData newRecord) {
	record = newRecord;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 18:11:46)
 * @param newTemplate_def boolean
 */
public void setTemplate_def(boolean newTemplate_def) {
	template_def = newTemplate_def;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 18:11:46)
 * @param newType int
 */
public void setType(int newType) {
	type = newType;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 18:11:46)
 * @param newValue java.lang.String
 */
public void setValue(java.lang.String newValue) {

	if ((value!=null) && !value.equals(newValue))
		com.cosylab.vdct.undo.UndoManager.getInstance().addAction(
			new com.cosylab.vdct.undo.FieldValueChangeAction(this, value, newValue)
		);
	value = newValue;
	if (record!=null) record.fieldValueChanged(this);
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 18:11:46)
 * @param newValue java.lang.String
 */
public void setValueSilently(java.lang.String newValue) {
	value = newValue;
}
}
