package com.cosylab.vdct.dbd;

import java.util.*;
import com.cosylab.vdct.Console;

/**
 * This type was created in VisualAge.
 */
 
public class DBDData {
	protected Hashtable records = null;
	protected Hashtable menus = null;
	protected Hashtable devices = null;
/**
 * DBDData constructor comment.
 */
public DBDData() {
	records = new Hashtable();
	menus = new Hashtable();
	devices = new Hashtable();
}
/**
 * This method was created in VisualAge.
 * @param dd com.cosylab.vdct.dbd.DBDDeviceData
 */
public void addDevice(DBDDeviceData dd) {
	// key def. : <recordtype>+"/"+<device choice string>
	
	if (!devices.containsKey((dd.record_type+"/"+dd.choice_string)))
		devices.put((dd.record_type+"/"+dd.choice_string), dd);
	else
		Console.getInstance().println("Device "+dd.record_type+"/"+dd.choice_string+" already exists in DBD - ignoring this definition.");
}
/**
 * This method was created in VisualAge.
 * @param md com.cosylab.vdct.MenuData
 */
public void addMenu(DBDMenuData md) {
	if (!menus.containsKey(md.getName()))
		menus.put(md.getName(), md);
	else
		Console.getInstance().println("Menu "+md.getName()+" already exists in DBD - ignoring this definition.");
}
/**
 * This method was created in VisualAge.
 * @param rd com.cosylab.vdct.RecordData
 */
public void addRecord(DBDRecordData rd) {
	if (!records.containsKey(rd.name))
		records.put(rd.name, rd);
	else
		Console.getInstance().println("Record "+rd.getName()+" already exists in DBD - ignoring this definition.");
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param db com.cosylab.vdct.db.DBData
 */
public boolean consistencyCheck(com.cosylab.vdct.db.DBData db) {
	String illegalString;
	
	Console console = Console.getInstance();
	
	console.println();
	console.print("o) Checking consistency with DBD file...");
	if (db==null) {
		console.println();
		console.println("\t No DB data defined.");
		console.println();
		return false;
	}
	
	boolean isOK = true;
	com.cosylab.vdct.db.DBRecordData dbRecord;
	DBDRecordData dbdRecord;
	Enumeration e2;
	com.cosylab.vdct.db.DBFieldData dbField;
	DBDFieldData dbdField;
	DBDMenuData menu;
	String dev;
	
	// records check
	Enumeration e = db.getRecordsV().elements();
	while (e.hasMoreElements()) {
		dbRecord = (com.cosylab.vdct.db.DBRecordData)(e.nextElement());
		dbdRecord = this.getDBDRecordData(dbRecord.getRecord_type());
		if (dbdRecord!=null) {
			
			// fields check (fields, menus, devices)
			e2 = dbRecord.getFieldsV().elements();
			while (e2.hasMoreElements()) {
				dbField = (com.cosylab.vdct.db.DBFieldData)(e2.nextElement());
				dbdField = dbdRecord.getDBDFieldData(dbField.getName());
				if (dbdField!=null) {

					// device check
					if (dbdField.getField_type() == DBDConstants.DBF_DEVICE) {
						dev = dbRecord.getRecord_type()+"/"+dbField.getValue();
						if (dbField.getValue().indexOf("$")!=-1) {
								console.println();
								console.print("\t Warning: Value '"+dbField.getValue()+"' is not valid device -> template definition?...");
								dbField.setTemplate_def(true);
						}
						else if (this.getDBDDeviceData(dev) == null) {
							console.println();
							console.print("\t Warning: Device '"+dev+"' is not defined DBD file. Using defaults...");

						    illegalString="# field("+dbField.getName()+",\""+dbField.getValue()+"\")";
							if (dbField.getComment()!=null)	illegalString=dbField.getComment()+"\n"+illegalString;
							dbField.setComment(illegalString);
							
							dbField.setValue("");
						}
					}
					// menu check
					else if (dbdField.getField_type() == DBDConstants.DBF_MENU) {
						menu = this.getDBDMenuData(dbdField.getMenu_name());
						if (menu==null) {
							isOK = false;
							console.println();
							console.print("\t Menu '"+dbdField.getMenu_name()+"' is not defined DBD file (DBD file error)...");
						}
						else {
							if (!menu.containsValue(dbField.getValue())) {
								console.println();
							/*	console.print("\t Warning: Value '"+dbField.getValue()+"' is not valid for menu '"+dbdField.getMenu_name()+". Using defaults...");

								illegalString="# field("+dbField.getName()+",\""+dbField.getValue()+"\")";
								if (dbField.getComment()!=null)	illegalString=dbField.getComment()+"\n"+illegalString;
								dbField.setComment(illegalString);
							
								dbField.setValue("");*/
								
								console.print("\t Warning: Value '"+dbField.getValue()+"' is not valid for menu '"+dbdField.getMenu_name()+" -> template definition?...");
								dbField.setTemplate_def(true);
								
							}
						}
					}
		
					
				}
				else {
					isOK=false;
					console.println();
					console.print("\t Field '"+dbField.getName()+"' in record '"+dbRecord.getRecord_type()+"' is not defined in DBD file.");
					
					illegalString="# illegal line - undefined field: field("+dbField.getName()+",\""+dbField.getValue()+"\")";
					if (dbRecord.getComment()!=null) illegalString=dbRecord.getComment()+"\n"+illegalString;
					dbRecord.setComment(illegalString);
				}
			}
		}
		else {
			isOK=false;
			console.println();
			console.print("\t Record type '"+dbRecord.getRecord_type()+"' is not defined in DBD file.");
		}
	}

	if (isOK) console.print(" OK");
	else {
		console.println();
		console.print("o) DB file is not consistent with DBD file!");
	}
	console.println(); console.println();
	return isOK;
}
/**
 * This method was created in VisualAge.
 * @return com.cosylab.vdct.dbd.DBDDeviceData
 * @param deviceName java.lang.String
 */
public DBDDeviceData getDBDDeviceData(String deviceName) {
	return (DBDDeviceData)(devices.get(deviceName));
}
/**
 * This method was created in VisualAge.
 * @return com.cosylab.vdct.dbd.DBDMenuData
 * @param menuName java.lang.String
 */
public DBDMenuData getDBDMenuData(String menuName) {
	return (DBDMenuData)(menus.get(menuName));
}
/**
 * This method was created in VisualAge.
 * @returncom.cosylab.vdct.dbd.DBDRecordData
 * @param recordName java.lang.String
 */
public DBDRecordData getDBDRecordData(String recordName) {
	return (DBDRecordData)(records.get(recordName));
}
/**
 * Insert the method's description here.
 * Creation date: (10.1.2001 17:26:33)
 * @return java.util.Hashtable
 */
public java.util.Hashtable getDevices() {
	return devices;
}
/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 19:58:09)
 * @return java.util.Enumeration
 */
public Enumeration getRecordNames() {
	return records.keys();
}
}
