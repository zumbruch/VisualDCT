package com.cosylab.vdct.vdb;

/**
 * Copyright (c) 2002, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution. 
 * Neither the name of the Cosylab, Ltd., Control System Laboratory nor the names
 * of its contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.*;
import java.util.*;
import com.cosylab.vdct.db.*;
import com.cosylab.vdct.dbd.*;
import com.cosylab.vdct.Console;

/**
 * This type was created in VisualAge.
 */
public class VDBData {
	private Vector records = null;
/**
 * DBDData constructor comment.
 */
public VDBData() {
	records = new Vector();
}
/**
 * This method was created in VisualAge.
 * @param rd VisualDCTPackage.RecordData
 */
public void addRecord(VDBRecordData rd) {
	if (rd!=null)
//	if (!records.containsKey(rd.name))
//		records.put(rd.name, rd);
	if (!records.contains(rd))
		records.addElement(rd);
}
/**
 * This method was created in VisualAge.
 * @return com.cosylab.vdct.vdb.VDBFieldData
 * @param dbd com.cosylab.vdct.dbd.DBDData
 * @param dbRecord com.cosylab.vdct.db.DBRecordData
 * @param dbdField com.cosylab.vdct.dbd.DBDFieldData
 */
public static VDBFieldData copyVDBFieldData(VDBFieldData sourceField) {

	VDBFieldData vdbField = new VDBFieldData();

	copyVDBFieldData(sourceField, vdbField);

	return vdbField;
}
/**
 * This method was created in VisualAge.
 * @return com.cosylab.vdct.vdb.VDBFieldData
 * @param dbd com.cosylab.vdct.dbdDBDData
 * @param dbRecord com.cosylab.vdct.db.DBRecordData
 * @param dbdField com.cosylab.vdct.dbd.DBDFieldData
 */
public static void copyVDBFieldData(VDBFieldData sourceField, VDBFieldData targetField) {
	targetField.setType(sourceField.getType());
	targetField.setName(sourceField.getName());
	targetField.setValue(sourceField.getValue());
	targetField.setInit_value(sourceField.getInit_value());
	targetField.setGUI_type(sourceField.getGUI_type());
	targetField.setTemplate_def(sourceField.isTemplate_def());
	targetField.setDbdData(sourceField.getDbdData());
}
/**
 * This method was created in VisualAge.
 * @return com.cosylab.vdct.vdb.VDBRecordData
 * @param dbd com.cosylab.vdct.dbd.DBDData
 * @param dbRecord com.cosylab.vdct.db.DBRecordData
 */
public static VDBRecordData copyVDBRecordData(VDBRecordData source) {

	VDBRecordData vdbRecord = new VDBRecordData();
	VDBFieldData sourceField, targetField;

	vdbRecord.setType(source.getType());
	vdbRecord.setName(source.getName());
	//vdbRecord.setDTYP_type(source.getDTYP_type());;

	
	Enumeration e = source.getFieldsV().elements();
	while (e.hasMoreElements()) {
		sourceField = (VDBFieldData)(e.nextElement());
		targetField = copyVDBFieldData(sourceField);
		targetField.setRecord(vdbRecord);
		vdbRecord.addField(targetField);
	}

	return vdbRecord;
}
/**
 * This method was created in VisualAge.
 * @return com.cosylab.vdct.vdb.VDBData
 * @param dbd com.cosylab.vdct.dbd.DBDData
 * @param db com.cosylab.vdct.db.DBData
 */
public static VDBData generateVDBData(DBDData dbd, DBData db) {
	
	VDBData vdb = new VDBData();
	DBRecordData dbRecord;

	if (db!=null) {
		Enumeration e = db.getRecordsV().elements();
		while (e.hasMoreElements()) {
			dbRecord = (DBRecordData)(e.nextElement());
			vdb.addRecord(generateVDBRecordData(dbd, dbRecord));
		}
	}

	
	return vdb;
}
/**
 * This method was created in VisualAge.
 * @return com.cosylab.vdct.vdb.VDBFieldData
 * @param dbd com.cosylab.vdct.dbd.DBDData
 * @param dbRecord com.cosylab.vdct.db.DBRecordData
 * @param dbdField com.cosylab.vdct.dbd.DBDFieldData
 */
public static VDBFieldData generateVDBFieldData(DBDData dbd, DBRecordData dbRecord, VDBRecordData vdbRecord, DBDFieldData dbdField) {

	VDBFieldData vdbField = new VDBFieldData();

	boolean monitor = com.cosylab.vdct.undo.UndoManager.getInstance().isMonitor();
	com.cosylab.vdct.undo.UndoManager.getInstance().setMonitor(false);

	vdbField.setType(dbdField.getField_type());
	vdbField.setName(dbdField.getName());
	vdbField.setValue(dbdField.getInit_value());
	vdbField.setInit_value(dbdField.getInit_value());
	vdbField.setGUI_type(dbdField.getGUI_type());
	vdbField.setDbdData(dbdField);

	if (dbRecord!=null) {
		DBFieldData dbField = (DBFieldData)(dbRecord.getFields().get(vdbField.name));
		if (dbField!=null) {
	 	 	if (dbField.getValue()!=null) vdbField.setValue(dbField.getValue());
			vdbField.setComment(dbField.getComment());
			vdbField.setTemplate_def(dbField.isTemplate_def());
		}
	}

	final String nullString = "";

	if (vdbField.value.equals(nullString) || vdbField.value.equals(dbdField.getInit_value())) 
	 if (dbdField.getField_type()==DBDConstants.DBF_MENU) {
		 // gets first element
	 /*		 DBDMenuData md = (DBDMenuData)(dbd.getDBDMenuData(dbdField.getMenu_name()));
		 if (md!=null) vdbField.setValue(md.getChoices().elements().nextElement().toString());
		 else {
		   System.out.println("Menu '"+dbdField.getMenu_name()+"' not defined in DBD file...");	 
		   return null;
		 }  */ 
		 	//vdbField.setValue(com.cosylab.vdct.Constants.NONE);
		 	if (!dbdField.getInit_value().equals(nullString))
			 	vdbField.setValue(dbdField.getInit_value()+com.cosylab.vdct.Constants.MENU_DEFAULT_VALUE_INDICATOR);
			else
			 	vdbField.setValue(com.cosylab.vdct.Constants.NONE);
			 
	 }
	 else if (dbdField.getField_type()==DBDConstants.DBF_DEVICE)
/*	  if (vdbRecord!=null) {
		 Enumeration e = dbd.getDevices().elements();
		 DBDDeviceData dev;
		 
		 while (e.hasMoreElements()) {
			 dev = (DBDDeviceData)(e.nextElement());
			 if (dev.getRecord_type().equals(vdbRecord.record_type)) {
			 	vdbField.setValue(dev.getChoice_string());
			 	break;
			 }	
		 }
		 
	 }*/ 
			//vdbField.setValue(com.cosylab.vdct.Constants.NONE);
		 	if (!dbdField.getInit_value().equals(nullString))
			 	vdbField.setValue(dbdField.getInit_value()+com.cosylab.vdct.Constants.MENU_DEFAULT_VALUE_INDICATOR);
			else
			 	vdbField.setValue(com.cosylab.vdct.Constants.NONE);

	vdbField.setRecord(vdbRecord);

	com.cosylab.vdct.undo.UndoManager.getInstance().setMonitor(monitor);
	
	return vdbField;
}
/**
 * This method was created in VisualAge.
 * @return com.cosylab.vdct.vdb.VDBRecordData
 * @param dbd com.cosylab.vdct.dbd.DBDData
 * @param dbRecord com.cosylab.vdct.db.DBRecordData
 */
public static VDBRecordData generateVDBRecordData(DBDData dbd, DBRecordData dbRecord) {

	DBDRecordData dbdRecord = dbd.getDBDRecordData(dbRecord.getRecord_type());
	if (dbdRecord==null) {
		Console.getInstance().println("Record '"+dbRecord.getRecord_type()+"' declared in DB file is not defined in DBD file...");
		return null;
	}

	VDBRecordData vdbRecord = new VDBRecordData();
	//DBFieldData dbField;
	DBDFieldData dbdField;

	vdbRecord.setType(dbRecord.getRecord_type());
	vdbRecord.setName(dbRecord.getName());
	vdbRecord.setComment(dbRecord.getComment());
/*	
	Enumeration e = dbRecord.getFieldsV().elements();
	while (e.hasMoreElements()) {
		dbField = (DBFieldData)(e.nextElement());
		dbdField = (DBDFieldData)(dbdRecord.getFields().get(dbField.getName()));
		vdbRecord.addField(generateVDBFieldData(dbd, dbRecord, vdbRecord, dbdField));
	}

	e = dbdRecord.getFieldsV().elements();
	while (e.hasMoreElements()) {
		dbdField = (DBDFieldData)(e.nextElement());
		if (!vdbRecord.getFields().containsKey(dbdField.getName()))
			vdbRecord.addField(generateVDBFieldData(dbd, dbRecord, vdbRecord, dbdField));
	}
*/

	/// should I add NOACCESS fields, etc... !!!
	Enumeration e = dbdRecord.getFieldsV().elements();
	while (e.hasMoreElements()) {
		dbdField = (DBDFieldData)(e.nextElement());
		vdbRecord.addField(generateVDBFieldData(dbd, dbRecord, vdbRecord, dbdField));
	}

	//vdbRecord.updateDTYP(dbd);

	return vdbRecord;
}
/**
 * This method was created in VisualAge.
 * @return epics.vdb.VDBRecordData
 * @param dbd epics.dbd.DBDData
 * @param recordType java.lang.String
 * @param recordName java.lang.String
 */
public static VDBRecordData getNewVDBRecordData(DBDData dbd, String recordType, String recordName) {

	DBDRecordData dbdRecord = dbd.getDBDRecordData(recordType);
	if (dbdRecord==null) {
		Console.getInstance().println("Record '"+recordType+"' is not defined in DBD file...");
		return null;
	}

	VDBRecordData vdbRecord = new VDBRecordData();
	vdbRecord.setType(recordType);
	vdbRecord.setName(recordName);

	DBDFieldData dbdField;
	Enumeration e = dbdRecord.getFieldsV().elements();
	while (e.hasMoreElements()) {
		dbdField = (DBDFieldData)(e.nextElement());
		vdbRecord.addField(generateVDBFieldData(dbd, null, vdbRecord, dbdField));
	}

	//vdbRecord.updateDTYP(dbd);
	
	return vdbRecord;
}
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 20:51:38)
 * @return java.util.Vector
 */
public java.util.Vector getRecords() {
	return records;
}
/**
 * This method was created in VisualAge.
 * @return epics.vdb.VDBRecordData
 * @param dbd epics.dbd.DBDData
 * @param dbRecord epics.db.DBRecordData
 */
public static VDBRecordData morphVDBRecordData(DBDData dbd, VDBRecordData source, String recordType, String recordName) {

	VDBRecordData vdbRecord = getNewVDBRecordData(dbd, recordType, recordName);
	if (vdbRecord==null) return null;
	
	VDBFieldData sourceField;
	VDBFieldData targetField;

	DBDMenuData menu;
	String menuName;
	String devName;

	vdbRecord.setType(recordType);
	vdbRecord.setName(recordName);
	//vdbRecord.setDTYP_type(source.getDTYP_type());;

	Enumeration e = vdbRecord.getFieldsV().elements();
	while (e.hasMoreElements()) {
		targetField = (VDBFieldData)(e.nextElement());
		sourceField = source.getField(targetField.getName());
		if (sourceField!=null) {
		  if (targetField.getType()==DBDConstants.DBF_MENU) {
			  menuName = dbd.getDBDRecordData(recordType).getDBDFieldData(targetField.getName()).getMenu_name();
			  menu = dbd.getDBDMenuData(menuName);
			  if (menu.containsValue(sourceField.getValue())) 
			  		copyVDBFieldData(sourceField, targetField);
		  }
		  else if (targetField.getType()==DBDConstants.DBF_DEVICE) {
			 devName = recordType+"/"+sourceField.getValue();
			 if (dbd.getDBDDeviceData(devName)!=null)
			  		copyVDBFieldData(sourceField, targetField);
		  }
		  else copyVDBFieldData(sourceField, targetField);
	      targetField.setRecord(vdbRecord);
		}
	}

	return vdbRecord;
}
/**
 * Insert the method's description here.
 * Creation date: (10.1.2001 14:44:44)
 * @param record com.cosylab.vdct.vdb.VDBRecordData
 */
public void removeRecord(VDBRecordData record) {
	records.removeElement(record);
}
}
