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
import com.cosylab.vdct.graphics.DrawingSurface;
import com.cosylab.vdct.graphics.objects.Group;
import com.cosylab.vdct.graphics.objects.Record;
import com.cosylab.vdct.Console;
import com.cosylab.vdct.Constants;

/**
 * This type was created in VisualAge.
 */
public class VDBData {
	private Vector records = null;
	private Hashtable templates = null;
	private Hashtable templateInstances = null;
	private Vector templateInstancesV = null;
/**
 * DBDData constructor comment.
 */
public VDBData() {
	records = new Vector();
	templates = new Hashtable();
	templateInstances = new Hashtable();
	templateInstancesV = new Vector();
}
/**
 * This method was created in VisualAge.
 * @param 
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
 * @param 
 */
public void addTemplate(VDBTemplate templ) {
	if (templ!=null)
		if (!templates.containsKey(templ.getId()))
			templates.put(templ.getId(), templ);
}

/**
 * This method was created in VisualAge.
 * @param 
 */
public void addTemplateInstance(VDBTemplateInstance ti) {
	if (ti!=null)
		if (!templateInstances.contains(ti))
		{
			templateInstancesV.addElement(ti);
			templateInstances.put(ti.getName(), ti);
		}
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
	targetField.setDbdData(sourceField.getDbdData());
	targetField.setVisibility(sourceField.getVisibility());
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

	if (db!=null) {

		// add records
		DBRecordData dbRecord;
		Enumeration e = db.getRecordsV().elements();
		while (e.hasMoreElements()) {
			dbRecord = (DBRecordData)(e.nextElement());
			vdb.addRecord(generateVDBRecordData(dbd, dbRecord));
		}

		// extract templates
		extractTemplates(dbd, db, vdb);
		
		
		// add template instances
		generateTemplateInstances(db, vdb);

	}

	
	return vdb;
}

/**
 * 
 */
public static void generateTemplateInstances(DBData db, VDBData vdb)
{
	Enumeration e;
	DBTemplateInstance dbTemplateInstance;
	e = db.getTemplateInstances().elements();
	while (e.hasMoreElements()) {
		dbTemplateInstance = (DBTemplateInstance)(e.nextElement());
		VDBTemplate t = (VDBTemplate)vdb.getTemplates().get(dbTemplateInstance.getTemplateClassID());
		if (t==null)
		{
			Console.getInstance().println(
				"Template instance "+dbTemplateInstance.getTemplateID()+" cannot be created since "
					+ dbTemplateInstance.getTemplateClassID()
					+ " does not exist - this definition will be ignored.");
			continue;
		}
		VDBTemplateInstance vti = new VDBTemplateInstance(dbTemplateInstance.getTemplateID(), t);
		vti.setProperties(dbTemplateInstance.getProperties());
			
		vti.setInputs(generateTemplateInstanceIOFields(vti, dbTemplateInstance.getValues(), t.getInputs(), t.getInputComments()));
		vti.setOutputs(generateTemplateInstanceIOFields(vti, dbTemplateInstance.getValues(), t.getOutputs(), t.getOutputComments()));
	
		vdb.addTemplateInstance(vti);
	}
}

/**
 * 
 */
private static Hashtable generateTemplateInstanceIOFields(VDBTemplateInstance vti, Hashtable values,
															Hashtable table, Hashtable descTable)
{
	Hashtable ios = new Hashtable();
	Enumeration keys = table.keys();
	while (keys.hasMoreElements())
	{
		String key = keys.nextElement().toString();
		VDBFieldData field = (VDBFieldData)table.get(key);
		VDBTemplateField tf = new VDBTemplateField(key, vti, field);
		tf.setDescription((String)descTable.get(key));
		String initVal = (String)values.get(key);
		if (initVal!=null)
			tf.setValueSilently(initVal);
		ios.put(key, tf);
	}
	return ios;
}


/**
 * 
 */
public static void extractTemplates(DBDData dbd, DBData db, VDBData vdb)
{
	Enumeration e;
	DBTemplate dbTemplate;
	e = db.getTemplates().elements();
	while (e.hasMoreElements()) {
		dbTemplate = (DBTemplate)(e.nextElement());
		
		VDBTemplate vt = new VDBTemplate(dbTemplate.getId(), dbTemplate.getFileName());
		vt.setDescription(dbTemplate.getDescription());
		
		// generate vt.group / VDB data
		Group root = Group.getRoot();
	
		try
		{
		
			vt.setGroup(new Group(null));
			vt.getGroup().setAbsoluteName("");
		
			Group.setRoot(vt.getGroup());
		
			VDBData vdbData = VDBData.generateVDBData(dbd, dbTemplate.getData());
			DrawingSurface.applyVisualData(false, vt.getGroup(), dbTemplate.getData(), vdbData);
			vt.getGroup().unconditionalValidateSubObjects(false);
			
			vt.setInputs(generateTemplateIOFields(dbTemplate, dbTemplate.getInputs(), "input"));
			vt.setOutputs(generateTemplateIOFields(dbTemplate, dbTemplate.getOutputs(), "output"));

			vt.setInputComments(dbTemplate.getInputComments());
			vt.setOutputComments(dbTemplate.getOutputComments());
			
			vdb.addTemplate(vt);
		}
		catch (Exception ex)
		{
			Console.getInstance().println();
			Console.getInstance().println("Exception caught while generating '"+dbTemplate.getId()+"' template.");
			Console.getInstance().println(ex);
			Console.getInstance().println();
		}
		finally
		{
			Group.setRoot(root); 
		}
	}


}
/**
 * 
 */
private static Hashtable generateTemplateIOFields(DBTemplate dbTemplate, Hashtable table, String ioType)
{
	Hashtable ios = new Hashtable();		
	Enumeration keys = table.keys();
	while (keys.hasMoreElements())
	{
		String key = keys.nextElement().toString();
		String field = table.get(key).toString();
		
		String recordName, fieldName;
		int pos = field.lastIndexOf(Constants.FIELD_SEPARATOR);
		if (pos>0)
		{
			recordName = field.substring(0, pos);
			fieldName = field.substring(pos + 1);
		}
		else
		{
			recordName = field;
			fieldName = "VAL";		/// always full specification required !!!
			Console.getInstance().println("Incomplete '"+dbTemplate.getId()+"' template "+ioType+" specification - field '"+field+"'. Defaulting to VAL field.");
		}	
		Record rec = (Record)Group.getRoot().findObject(recordName, true);
		if (rec!=null)
		{
			VDBFieldData fld = (VDBFieldData)rec.getRecordData().getField(fieldName);	 
			if (fld!=null)
			{
				//System.out.println("Adding "+ioType+": "+key+" = "+field);
				ios.put(key, fld);
			}
			else
				Console.getInstance().println("Invalid '"+dbTemplate.getId()+"' template "+ioType+" specification - field '"+field+"', record field '"+fieldName+"' does not exist. Skipping "+ioType+".");
		}
		else
			Console.getInstance().println("Invalid '"+dbTemplate.getId()+"' template "+ioType+" specification - field '"+field+"', record '"+recordName+"' does not exist. Skipping "+ioType+".");
		
	}
	return ios;
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
			vdbField.setVisibility(dbField.getVisibility());
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
/**
 * Returns the templateInstances.
 * @return Hashtable
 */
public Hashtable getTemplateInstances()
{
	return templateInstances;
}

/**
 * Returns the templateInstances.
 * @return Vector
 */
public Vector getTemplateInstancesV()
{
	return templateInstancesV;
}

/**
 * Returns the templates.
 * @return Hashtable
 */
public Hashtable getTemplates()
{
	return templates;
}

/**
 * Insert the method's description here.
 * Creation date: (10.1.2001 14:44:44)
 * @param record com.cosylab.vdct.vdb.VDBTemplate
 */
public void removeTemplate(VDBTemplate template) {
	templates.remove(template);
}
/**
 * Insert the method's description here.
 * Creation date: (10.1.2001 14:44:44)
 * @param record com.cosylab.vdct.vdb.VDBTemplateInstance
 */
public void removeTemplateInstance(VDBTemplateInstance templateInstance) {
	templateInstancesV.remove(templateInstance);
	templateInstances.remove(templateInstance);
}

}
