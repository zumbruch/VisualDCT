/**
 * Copyright (c) 2007, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
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

package com.cosylab.vdct.rdb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import com.cosylab.vdct.Constants;
import com.cosylab.vdct.DataProvider;
import com.cosylab.vdct.db.DBData;
import com.cosylab.vdct.db.DBFieldData;
import com.cosylab.vdct.db.DBRecordData;
import com.cosylab.vdct.dbd.DBDConstants;
import com.cosylab.vdct.dbd.DBDData;
import com.cosylab.vdct.dbd.DBDDeviceData;
import com.cosylab.vdct.dbd.DBDFieldData;
import com.cosylab.vdct.dbd.DBDMenuData;
import com.cosylab.vdct.dbd.DBDRecordData;
import com.cosylab.vdct.dbd.DBDResolver;
import com.cosylab.vdct.graphics.objects.Group;
import com.cosylab.vdct.graphics.objects.Record;
import com.cosylab.vdct.graphics.objects.Template;
import com.cosylab.vdct.util.StringUtils;
import com.cosylab.vdct.vdb.VDBFieldData;
import com.cosylab.vdct.vdb.VDBRecordData;

/**
 * @author ssah
 *
 */
public class DataMapper {

	private RdbConnection helper = null;
	
	private String group = null; 
	
	private static final String emptyString = "";
	private static final String recordDefDescription = "Saved by VDCT";
	private static final String iocDeviceIdString = "Unknown device";
	private static final String dtypString = "DTYP";

	public DataMapper() throws Exception {
		helper = new RdbConnection();
	}
	
	public void setConnectionParameters(String host, String database, String user, String password) {
		helper.setParameters(host, database, user, password);
	}

	public DBData loadDbGroup(String group) throws Exception {

		this.group = group;
		if (!helper.isConnection()) {
		    helper.createConnection();
		}
		if (!helper.isConnection()) {
			return null;
		}
		
		DBData data = null;
		Exception exception = null;
		try {
			data = new DBData(group, group);
			loadRecords(data, group);
			loadTemplates(data, group);			
			
			helper.commit();
		} catch (SQLException sqlException) {
			exception = new Exception("Error while loading database: " + sqlException.getMessage());
		}
		
		if (exception != null) {
			throw exception;
		}
		return data;
	}
	
	public void saveDbGroup(String group) throws Exception {
		
		this.group = group;
		if (!helper.isConnection()) {
		    helper.createConnection();
		}
		if (!helper.isConnection()) {
			return;
		}

		Exception exception = null;
		try {
			saveDataDef();
			saveGroup(group);			
            saveRecords();
            helper.commit();
		} catch (SQLException sqlException) {
			exception = new Exception("Error while saving database: " + sqlException.getMessage());
			helper.rollbackConnection();
		}
		
		if (exception != null) {
			throw exception;
		}
	}
	
	public Connection createNewConnection() throws SQLException {
		return helper.createConnection();
	}

	public boolean isConnection() {
		return helper.isConnection();
	}
	
	public void closeConnection() throws SQLException {
		helper.closeConnection();
	}
	
	private void loadRecords(DBData data, String group) throws SQLException {

		/* TODO: rec_code_type usage
		Object[] columns = {"sgnl_id", "sgnl_rec.rec_type_id"};  
		Object[][] conditions = {{"epics_grp_id", "sgnl_rec.rec_type_id", "rec_type_code"},
                {group,            "sgnl_rec_type.rec_type_id", "E"}};
		ResultSet set = loadRows("sgnl_rec, sgnl_rec_type", columns, conditions);
        */

		Object[] columns = {"sgnl_id", "rec_type_id"};  
		Object[][] conditions = {{"epics_grp_id"}, {group}};
		
		ResultSet set = helper.loadRows("sgnl_rec", columns, conditions);
        
        while (set.next()) {
    		DBRecordData record = new DBRecordData();
    		String name = set.getString(1);
    		String type = set.getString(2);
    		record.setName(name);
    		record.setRecord_type(type);
    		loadRecord(record);
    		data.addRecord(record);
    		
    		loadRecordDef(type);
        }
	}

	private void loadTemplates(DBData data, String group) throws SQLException {
		// Currently disabled.
		/*
		Object[] columns = {"expand_id", "template_id"};  
		ResultSet set = loadRows("expand", columns, "");
        while (set.next()) {
    		DBTemplateInstance templateInstance =
    			new DBTemplateInstance(set.getString(2), set.getString(1));
    		data.addTemplateInstance(templateInstance);
        }
        */
	}
	
	private DBRecordData loadRecord(DBRecordData record) throws SQLException {
        
		Object[] columns = {"fld_id", "ext_val"};  
		Object[][] conditions = {{"sgnl_id"},
                {record.getName()}};
		
		ResultSet set = helper.loadRows("sgnl_fld", columns, conditions);
        while (set.next()) {
        	// value in DBFieldData can be null, in this case it will be set to default.
        	record.addField(new DBFieldData(set.getString(1), set.getString(2)));
        }
		return record;
	}
	
	private void loadRecordDef(String name) throws SQLException {

		DBDData definitions = DataProvider.getInstance().getDbdDB();
		if (definitions.getDBDRecordData(name) == null) {
			DBDRecordData recordDef = new DBDRecordData();
			recordDef.setName(name);
			
			Object[] columns = {"fld_id", "fld_prmt_grp", "fld_type_id", "fld_init", "fld_desc", "fld_base", "fld_size", "sgnl_fld_menu_id"};
			Object[][] conditions = {{"rec_type_id"}, {name}};
			
			
			ResultSet set = helper.loadRows("sgnl_fld_def", columns, conditions, "prmpt_ord");
	        while (set.next()) {
	        	DBDFieldData fieldDef = new DBDFieldData();

	        	fieldDef.setName(set.getString(1));
	        	fieldDef.setGUI_type(DBDResolver.getGUIType(set.getString(2)));
	        	fieldDef.setField_type(DBDResolver.getFieldType(set.getString(3)));
	        	
	        	String initValue = set.getString(4);
	        	if (initValue != null) {
	        	    fieldDef.setInit_value(initValue);
	        	}
	        	String promptValue = set.getString(5);
	        	if (promptValue != null) {
	        	    fieldDef.setPrompt_value(promptValue);
	        	}
	        	fieldDef.setBase_type(DBDResolver.getGUIType(set.getString(6)));
	        	fieldDef.setSize_value(set.getInt(7));
	        	String menuName = set.getString(8);
	        	if (menuName != null) {
	        	    fieldDef.setMenu_name(menuName);
	        	    loadMenuOrDeviceDef(menuName);
	        	}
	        	recordDef.addField(fieldDef);
	        }
			
			definitions.addRecord(recordDef);
		}
	}

	private void loadMenuOrDeviceDef(String name) throws SQLException {
		if (name.endsWith(dtypString)) {
			loadDeviceDef(name);
		} else {
			loadMenuDef(name);
		}
	}

	private void loadMenuDef(String name) throws SQLException {
		DBDData definitions = DataProvider.getInstance().getDbdDB();
		
		if (definitions.getDBDMenuData(name) == null) {
			DBDMenuData menuDef = new DBDMenuData();
			menuDef.setName(name);

			Object[] columns = {"fld_menu_val"};
			Object[][] conditions = {{"sgnl_fld_menu_id"}, {name}};
			
			ResultSet set = helper.loadRows("sgnl_fld_menu", columns, conditions);
	        while (set.next()) {
	        	String value = set.getString(1);
	        	menuDef.addMenuChoice(value, value);
	        }
			definitions.addMenu(menuDef);
		}
	}

	private void loadDeviceDef(String name) throws SQLException {
		String recordName = name.substring(0, name.lastIndexOf(dtypString));
		
		DBDData definitions = DataProvider.getInstance().getDbdDB();
		if (definitions.getDBDDeviceData(recordName) == null) {
			
			Object[] columns = {"fld_menu_val"};  
			Object[][] conditions = {{"sgnl_fld_menu_id"}, {name}};
			
			ResultSet set = helper.loadRows("sgnl_fld_menu", columns, conditions);
	        while (set.next()) {
	        	DBDDeviceData deviceDef = new DBDDeviceData();
				deviceDef.setRecord_type(recordName);
				deviceDef.setLink_type("CONSTANT");
				deviceDef.setChoice_string(set.getString(1));
				definitions.addDevice(deviceDef);
	        }
		}
	}
	
	private void saveDataDef() throws SQLException {
		saveMenusDef();
		saveDevicesDef();
		saveRecordsDef();
	}

	private void saveMenusDef() throws SQLException{
		DBDData definitions = DataProvider.getInstance().getDbdDB();
		Hashtable menus = definitions.getMenus();
		Enumeration menusKeys = menus.keys();
		DBDMenuData menuData = null;
		String menuName = null;
		Iterator menuChoices = null;
		String menuChoice = null;
		
		while (menusKeys.hasMoreElements()) {
			menuName = (String)menusKeys.nextElement();
			menuData = (DBDMenuData)menus.get(menuName);
			
			menuChoices = menuData.getChoices().values().iterator();
			while (menuChoices.hasNext()) {
				menuChoice = (String)menuChoices.next();

				Object[][] keyPairs = {{"sgnl_fld_menu_id", "fld_menu_val"},
					      {menuName,           menuChoice}};
				Object[][] valuePairs = {{}, {}};
				helper.saveRow("sgnl_fld_menu", keyPairs, valuePairs);
			}
		}
	}

	private void saveDevicesDef() throws SQLException{

		DBDData definitions = DataProvider.getInstance().getDbdDB();
		Iterator devices = definitions.getDevices().values().iterator();
		DBDDeviceData device = null;
		String recordType = null;
		String menuName = null;
		String choice = null;
		
		while (devices.hasNext()) {
			device = (DBDDeviceData)devices.next();
			recordType = device.getRecord_type();
			menuName = recordType + dtypString;
			choice = device.getChoice_string();
			
			Object[][] menuKeys = {{"sgnl_fld_menu_id", "fld_menu_val"},
					{menuName,           choice}};
			Object[][] menuValues = {{}, {}};
			helper.saveRow("sgnl_fld_menu", menuKeys, menuValues);
		}
	}
	
	private void saveRecordsDef() throws SQLException {
		DBDData definitions = DataProvider.getInstance().getDbdDB();
		Enumeration recordNames = definitions.getRecordNames();
		DBDRecordData record = null;
		String recordName = null;
		
		while (recordNames.hasMoreElements()) {
			recordName = (String)recordNames.nextElement();

			Object[][] keyPairs = {{"rec_type_id"},
		             {recordName}};
			Object[][] valuePairs = {{"rec_type_code", "type_desc"},
		             {"E", recordDefDescription}};
			helper.saveRow("sgnl_rec_type", keyPairs, valuePairs);

   			record = definitions.getDBDRecordData(recordName);
   			saveFieldsDef(record);
		}
	}

	private void saveFieldsDef(DBDRecordData record) throws SQLException {

		Iterator fields = record.getFieldsV().iterator();
		DBDFieldData field = null;
		String recordName = record.getName();
		String fieldName = null;
		int fieldType = DBDConstants.NOT_DEFINED;
		String menuName = null;
		
		while (fields.hasNext()) {
			field = (DBDFieldData)fields.next();
			fieldName = field.getName();
			
			fieldType = field.getField_type();
			menuName = fieldType == DBDConstants.DBF_DEVICE
				? recordName + dtypString : field.getMenu_name();
			
			Object[][] keyPairs = {{"rec_type_id",   "fld_id"},
					{recordName, fieldName}};
			Object[][] valuePairs = {{"fld_prmt_grp", "fld_type_id", "fld_init", "fld_desc", "fld_base", "fld_size", "sgnl_fld_menu_id"},
					{
				DBDResolver.getGUIString(field.getGUI_type()),
				DBDResolver.getFieldType(fieldType),
				field.getInit_value(),
				field.getPrompt_value(),
				DBDResolver.getBaseType(field.getBase_type()),
				String.valueOf(field.getSize_value()),
			    menuName}
			};
			helper.saveRow("sgnl_fld_def", keyPairs, valuePairs);
		}
	}

	private void saveGroup(String name) throws SQLException {
		Object[][] keyPairs = {{"epics_grp_id"}, {name}};
		Object[][] valuePairs = {{"ioc_dvc_id"}, {iocDeviceIdString}};
		helper.saveRow("epics_grp", keyPairs, valuePairs);
	}
	
	private void saveRecords() throws SQLException {

		// TODO: remove when Group and rec_type_code usage is defined  
		/*
		PreparedStatement statement = connection.prepareStatement(
        		"SELECT sgnl_id FROM sgnl_rec, sgnl_rec_type"
        		+ " WHERE epics_grp_id=? AND sgnl_rec.rec_type_id = sgnl_rec_type.rec_type_id"
        		+ " AND rec_type_code='E'");
        */
		
        Iterator iterator = Group.getRoot().getStructure().iterator();

		VDBRecordData recordData = null;
		String name = null;
        
        while (iterator.hasNext()) {
        	Object object = iterator.next();
        	
        	if (object instanceof Record) {
        		recordData = ((Record)object).getRecordData();
        		name = recordData.getName();
        		
        		if (!name.startsWith(Constants.CLIPBOARD_NAME)) {
        			Object[][] keyPairs = {{"sgnl_id"}, {name}};
        			Object[][] valuePairs = {{"epics_grp_id", "rec_type_id"},
        					                 {group,          recordData.getType()}};
        			helper.saveRow("sgnl_rec", keyPairs, valuePairs);
            		saveFields(recordData);
        		}
        	} else if (object instanceof Template) {
        		// Not implemented yet.
        		/*
        		VDBTemplateInstance template = ((Template)object).getTemplateData();

        		name = template.getName();

        		if (!name.startsWith(Constants.CLIPBOARD_NAME)) {
        			Object[][] keyPairs = {{"export_id"}, {name}};
        			Object[][] valuePairs = {{"export_id", "template_id"},
        					                 {name,        template.getTemplate().getId()}};
        			saveRow("export", keyPairs, valuePairs);
        		}
        		*/
        	}
        }
	}
	
	private void saveFields(VDBRecordData recordData) throws SQLException {
		
        Iterator iterator = recordData.getFieldsV().iterator();
        VDBFieldData fieldData = null;
        String name = null;
        String value = null;
        String recordName = recordData.getName();
        String recordType = recordData.getType();
		String table = "sgnl_fld";
        
        while (iterator.hasNext()) {
        	fieldData = (VDBFieldData)iterator.next();
        	
        	name = fieldData.getName();
        	value = StringUtils.removeQuotes(fieldData.getValue());
        	
        	Object[][] keyPairs = {{"sgnl_id",  "fld_id", "rec_type_id"},
                				   {recordName, name,     recordType}};
			Object[][] valuePairs = {{"ext_val"},
					                 {value}};
			
        	if (fieldData.hasDefaultValue() || value.equals(emptyString)) {
        		helper.deleteRow(table, keyPairs);
        	} else {
        		helper.saveRow(table, keyPairs, valuePairs);
            }
        }
	}
}
