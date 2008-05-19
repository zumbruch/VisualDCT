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

package com.cosylab.vdct.irmis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import com.cosylab.vdct.Console;
import com.cosylab.vdct.Constants;
import com.cosylab.vdct.DataProvider;
import com.cosylab.vdct.db.DBConstants;
import com.cosylab.vdct.db.DBData;
import com.cosylab.vdct.db.DBFieldData;
import com.cosylab.vdct.db.DBRecordData;
import com.cosylab.vdct.db.DBResolver;
import com.cosylab.vdct.db.DBTemplateInstance;
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
import com.cosylab.vdct.vdb.VDBTemplateInstance;

/**
 * @author ssah
 *
 */
public class Rdb {

	private String user = null;
	private String password = null;
	private String host = null;
	private String group = null;
	private String database = null;
	private Connection connection = null;
	
	private static Rdb rdb = null;
	
	private static final String emptyString = "";

	private static final String defaultUser = "user";
	private static final String defaultPassword = "password";
	private static final String defaultHost = "localhost";
	private static final String defaultGroup = "Group";
	private static final String defaultDatabase = "epics";

	private static final String recordDefDescription = "Saved by VDCT";
	
	public static Rdb getInstance() throws Exception {
		if (rdb == null) {
			rdb = new Rdb();
		}
		return rdb;
	}

	public DBData loadDbGroup() {

		DBData data = null;
		createConnection();
		if (connection == null) {
			return null;
		}
		
		try {
			data = new DBData(group, group);
			loadRecords(data, group);
			
			connection.commit();
		} catch (SQLException exception) {
			displayExceptionMessage("Error while loading database!", exception);
		} finally {
		    closeConnection(false);
	    }
		return data;
	}
	
	public void saveDbGroup() {
		
		createConnection();
		if (connection == null) {
			return;
		}
		
		boolean rollback = false;
		try {
			saveRecords();
			connection.commit();
		} catch (SQLException exception) {
			displayExceptionMessage("Error while saving database!", exception);
			rollback = true;
		} finally {
    		closeConnection(rollback);
		}
	}
	
	private Rdb() throws Exception {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		user = defaultUser;
		password = defaultPassword;
		host = defaultHost;
		group = defaultGroup;
		database = defaultDatabase;
		
		// TODO:REM
		user = "testuser";
		group = "AirC";
	}
	
	private void displayExceptionMessage(String message, SQLException exception) {
	    Console.getInstance().println(message);
	    Console.getInstance().println("SQLException: " + exception.getMessage());
	    Console.getInstance().println("SQLState: " + exception.getSQLState());
	    Console.getInstance().println("VendorError: " + exception.getErrorCode());
	    exception.printStackTrace();
	}
	
	private void createConnection() {
		connection = null;
		try {
			String connectionString = "jdbc:mysql://" + host + "/" + database
			        + "?user=" + user + "&password=" + password;
			connection = DriverManager.getConnection(connectionString);
			connection.setAutoCommit(false);
		} catch (SQLException exception) {
			displayExceptionMessage("Error creating connection!", exception);
		}
	}

	private void closeConnection(boolean rollback) {
		try {
			if (rollback) {
			    connection.rollback();
			}
			connection.close();
		} catch (SQLException exception) {
			displayExceptionMessage("Error closing connection!", exception);
		}
	}
	
	private String getList(Object[] elements) {
		StringBuffer buffer = new StringBuffer();
		int len = elements.length;
		for (int e = 0; e < len; e++) {
			buffer.append(elements[e].toString());
			if (e < len - 1) {
				buffer.append(",");
			}
		}
		return buffer.toString();
	}

	private String getQuotedList(Object[] elements) {
		StringBuffer buffer = new StringBuffer();
		int len = elements.length;
		for (int e = 0; e < len; e++) {
			buffer.append("'");
			buffer.append(elements[e].toString());
			buffer.append("'");
			if (e < len - 1) {
				buffer.append(",");
			}
		}
		return buffer.toString();
	}

	private String getEqualityList(Object[][] elements) {
		StringBuffer buffer = new StringBuffer();
		int rows = elements.length;
		if (rows < 2) {
			return emptyString;
		}
		
		int len = elements[0].length;
		for (int e = 0; e < len; e++) {
			buffer.append(elements[0][e].toString());
			buffer.append("='");
			buffer.append(elements[1][e].toString());
			buffer.append("'");
			if (e < len - 1) {
				buffer.append(",");
			}
		}
		return buffer.toString();
	}
	
	private ResultSet loadRows(String table, Object[] columns, String condition) throws SQLException {

        String columnsString = getList(columns);
        PreparedStatement statement = connection.prepareStatement(
        		"SELECT " + columnsString + " FROM " + table + " WHERE " + condition);
		return statement.executeQuery();
	}
	
	private void saveRow(String table, Object[][] keyPairs, Object[][] valuePairs) throws SQLException {
		
		// Check for row existence.
		String condition = getEqualityList(keyPairs);
		Object[] columns = {keyPairs[0][0]};  
		
		ResultSet set = loadRows(table, columns, condition);
		boolean exists = set.next(); 
		
        PreparedStatement statement = null;
		
		if (exists) {
			String setString = getEqualityList(valuePairs);
			statement = connection.prepareStatement(
	        		"UPDATE " + table + " SET " + setString + " WHERE " + condition);
			
			System.out.println(setString + ":" + condition);
		} else {
			String columnString = getList(valuePairs[0]);
			String valueString = getQuotedList(valuePairs[1]);
			statement = connection.prepareStatement("INSERT INTO " + table
					+ " (" + columnString + ") VALUES (" + valueString + ")");

			System.out.println(columnString + ":" + valueString);
		}
        statement.execute();
	}
	
	private void loadRecords(DBData data, String group) throws SQLException {
		Object[] columns = {"sgnl_id", "sgnl_rec.rec_type_id"};  

		ResultSet set = loadRows("sgnl_rec, sgnl_rec_type", columns,
				"epics_grp_id=" + group + " AND sgnl_rec.rec_type_id = sgnl_rec_type.rec_type_id AND rec_type_code='E'");
        
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
		Object[] columns = {"expand_id", "template_id"};  
		ResultSet set = loadRows("expand", columns, "");
        while (set.next()) {
    		DBTemplateInstance templateInstance =
    			new DBTemplateInstance(set.getString(2), set.getString(1));
    		data.addTemplateInstance(templateInstance);
        }
	}
	
	private DBRecordData loadRecord(DBRecordData record) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
        		"SELECT fld_id, ext_val FROM sgnl_fld WHERE sgnl_id=?");
        statement.setString(1, record.getName());
        ResultSet set = statement.executeQuery();
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
			
	        PreparedStatement statement = connection.prepareStatement("SELECT"
	        		+ " fld_id, fld_prmt_grp, fld_type_id, fld_init, fld_desc, fld_base, fld_size, sgnl_fld_menu_id"
	        		+ " FROM sgnl_fld_def WHERE rec_type_id=? ORDER BY prmpt_ord");
	        statement.setString(1, name);
	        ResultSet set = statement.executeQuery();
	        while (set.next()) {
	        	DBDFieldData fieldDef = new DBDFieldData();

	        	String fieldName = set.getString(1);
	        	int guiType = DBDResolver.getGUIType(set.getString(2));
	        	int type = DBDResolver.getFieldType(set.getString(3));
	        	String initValue = set.getString(4);
	        	String promptValue = set.getString(5);
	        	int baseType = DBDResolver.getGUIType(set.getString(6));
	        	int sizeValue = set.getInt(7);
	        	String menuName = set.getString(8);
	        	
	        	fieldDef.setName(fieldName);
	        	fieldDef.setGUI_type(guiType);
	        	fieldDef.setField_type(type);
	        	if (initValue != null) {
	        	    fieldDef.setInit_value(initValue);
	        	}
	        	if (promptValue != null) {
	        	    fieldDef.setPrompt_value(promptValue);
	        	}
	        	fieldDef.setBase_type(baseType);
	        	fieldDef.setSize_value(sizeValue);
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
		if (name.endsWith("DTYP")) {
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
			
	        PreparedStatement statement = connection.prepareStatement(
	        		"SELECT fld_menu_val FROM sgnl_fld_menu WHERE sgnl_fld_menu_id=?");
	        statement.setString(1, name);
	        ResultSet set = statement.executeQuery();
	        while (set.next()) {
	        	String value = set.getString(1);
	        	menuDef.addMenuChoice(value, value);
	        }
			definitions.addMenu(menuDef);
		}
	}

	private void loadDeviceDef(String name) throws SQLException {
		String recordName = name.substring(0, name.lastIndexOf("DTYP"));
		
		DBDData definitions = DataProvider.getInstance().getDbdDB();
		if (definitions.getDBDDeviceData(recordName) == null) {
	        PreparedStatement statement = connection.prepareStatement(
	        		"SELECT fld_menu_val FROM sgnl_fld_menu WHERE sgnl_fld_menu_id=?");
	        statement.setString(1, name);
	        ResultSet set = statement.executeQuery();
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

				Object[][] keyPairs = {{"sgnl_fld_menu_id"}, {menuName}};
				Object[][] valuePairs = {{"sgnl_fld_menu_id", "fld_menu_val"},
    					      {menuName,           menuChoice}};
    			saveRow("sgnl_fld_menu", keyPairs, valuePairs);
			}
		}
	}

	private void saveRecordsDef() throws SQLException{
		DBDData definitions = DataProvider.getInstance().getDbdDB();
		Enumeration recordNames = definitions.getRecordNames();
		DBDRecordData record = null;
		String recordName = null;
		Iterator fields = null;
		DBDFieldData field = null;
		
		while (recordNames.hasMoreElements()) {
			recordName = (String)recordNames.nextElement();

			Object[][] keyPairs = {{"rec_type_id"}, {recordName}};
			Object[][] valuePairs = {{"rec_type_id", "type_desc"},
    					             {recordName,     recordDefDescription}};
   			saveRow("sgnl_rec_type", keyPairs, valuePairs);

   			record = definitions.getDBDRecordData(recordName);
   			fields = record.getFieldsV().iterator();
   			while (fields.hasNext()) {
   				field = (DBDFieldData)fields.next();
   				
   				
   			}
		
		}
	}

	private void saveFieldsDef(DBDRecordData record) throws SQLException{

		/*
table:
sgnl_fld_def

getName->fld_id
type(from recdef)->rec_type_id
getGuiTypeString(getGUI_Type)->fld_prmt_grp
getFieldTypeString(getFieldType)->fld_type_id
getInitValue->fld_init
getProptValue->fld_desc
getBaseValue->fld_base
getSizeValue->fld_size
		 */
		
		Iterator fields = record.getFieldsV().iterator();
		DBDFieldData field = null;
		String recordName = record.getName();
		String fieldName = null;
		
		while (fields.hasNext()) {
			field = (DBDFieldData)fields.next();
			fieldName = field.getName();
			
			Object[][] keyPairs = {{"rec_type_id",   "fld_id"},
					{recordName, fieldName}};
			Object[][] valuePairs = {{"rec_type_id", "fld_id", "fld_prmt_grp", "fld_type_id", "fld_init", "fld_desc", "fld_base", "fld_size"},
					{recordName,
				fieldName,
				DBDResolver.getGUIString(field.getGUI_type()),
				DBDResolver.getFieldType(field.getField_type()),
				field.getInit_value(),
				field.getPrompt_value(),
				DBDResolver.getBaseType(field.getBase_type()),
				String.valueOf(field.getSize_value())}
			};
			saveRow("sgnl_fld_def", keyPairs, valuePairs);
		}
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
		VDBTemplateInstance template = null;
		String name = null;
        
        while (iterator.hasNext()) {
        	Object object = iterator.next();
        	
        	if (object instanceof Record) {
        		recordData = ((Record)object).getRecordData();
        		name = recordData.getName();
        		
        		if (!name.startsWith(Constants.CLIPBOARD_NAME)) {
        			Object[][] keyPairs = {{"sgnl_id"}, {name}};
        			Object[][] valuePairs = {{"sgnl_id", "epics_grp_id", "rec_type_id"},
        					                 {name,      group,          recordData.getType()}};
        			saveRow("sgnl_rec", keyPairs, valuePairs);
            		saveFields(recordData);
        		}
        	} else if (object instanceof Template) {
        		template = ((Template)object).getTemplateData();

        		name = template.getName();

        		if (!name.startsWith(Constants.CLIPBOARD_NAME)) {
        			Object[][] keyPairs = {{"export_id"}, {name}};
        			Object[][] valuePairs = {{"export_id", "template_id"},
        					                 {name,        template.getTemplate().getId()}};
        			saveRow("export", keyPairs, valuePairs);
        		}
        	}
        }
	}
	
	private void saveFields(VDBRecordData recordData) throws SQLException {
		
		PreparedStatement statement = connection.prepareStatement(
        		"SELECT fld_id FROM sgnl_fld WHERE sgnl_id=? AND rec_type_id=?");
        statement.setString(1, recordData.getName());
        statement.setString(2, recordData.getType());

        ResultSet set = statement.executeQuery();
        HashSet rdbFields = new HashSet();
        while (set.next()) {
        	rdbFields.add(set.getString(1));
        }
		
        Iterator iterator = recordData.getFieldsV().iterator();
        VDBFieldData fieldData = null;
        String name = null;
        String value = null;
        
        while (iterator.hasNext()) {
        	fieldData = (VDBFieldData)iterator.next();
        	
        	name = fieldData.getName();
        	value = StringUtils.removeQuotes(fieldData.getValue());
        	
        	if (fieldData.hasDefaultValue() || value.equals(emptyString)) {
        		if (rdbFields.contains(name)) {
        			statement = connection.prepareStatement("DELETE FROM sgnl_fld"
        					+ " WHERE sgnl_id=? AND fld_id=? AND rec_type_id=?");
        			statement.setString(1, recordData.getName());
        			statement.setString(2, name);
        			statement.setString(3, recordData.getType());
            		statement.executeUpdate();
        		}
        	} else { 
        		if (rdbFields.contains(name)) {
        			statement = connection.prepareStatement("UPDATE sgnl_fld SET ext_val=?"
        					+ " WHERE sgnl_id=? AND fld_id=? AND rec_type_id=?");
        			statement.setString(1, value);
        			statement.setString(2, recordData.getName());
        			statement.setString(3, name);
        			statement.setString(4, recordData.getType());
        		} else {
        			statement = connection.prepareStatement("INSERT INTO sgnl_fld"
        					+ " (sgnl_id, fld_id, rec_type_id, ext_val) VALUES (?,?,?,?)");
        			statement.setString(1, recordData.getName());
        			statement.setString(2, name);
        			statement.setString(3, recordData.getType());
        			statement.setString(4, value);
        		}
        		statement.executeUpdate();
            }
        }
	}
}
