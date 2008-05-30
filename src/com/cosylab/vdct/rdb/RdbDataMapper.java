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
import java.util.Vector;

import com.cosylab.vdct.Constants;
import com.cosylab.vdct.DataProvider;
import com.cosylab.vdct.db.DBData;
import com.cosylab.vdct.db.DBFieldData;
import com.cosylab.vdct.db.DBRecordData;
import com.cosylab.vdct.db.DBResolver;
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
public class RdbDataMapper {

	// Reactivate when definitions saving use cases defined. 
	private static boolean manageDbds = false;
	private RdbConnection helper = null;

	private Integer iocId = null; 
	private Integer pDbdId = null; 
	private Integer pDbId = null; 
	
	private static final String emptyString = "";
	private static final String recordDefDescription = "Saved by VDCT";
	private static final String dtypString = "DTYP";

	public RdbDataMapper() throws Exception {
		helper = new RdbConnection();
	}
	
	public void setConnectionParameters(String host, String database, String user, String password) {
		helper.setParameters(host, database, user, password);
	}

	public DBData loadRdbData(RdbDataId dataId) throws Exception {

		DBData data = null;
		Exception exception = null;
		try {
			if (loadDbId(dataId)) {
				data = new DBData(dataId.toString(), dataId.getFileName());
				loadRecords(data);
				loadTemplates(data);
				loadVdctData(data);				
			}
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
			exception = new Exception("Error while loading database: " + sqlException.getMessage());
		}
		
		if (exception != null) {
			throw exception;
		}
		return data;
	}
	
	public void saveRdbData(RdbDataId dataId) throws Exception {
		
		Exception exception = null;
		try {
			if (loadDbId(dataId)) {
				if (manageDbds) {
					saveDataDef();
				}
				saveDefinitionFile();
				saveRecords();
				saveVdctData();
				
				helper.commit();
			}
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
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
	
	public int createAnIoc() throws SQLException {
		saveIoc();
        helper.commit();
		return iocId.intValue();
	}
	
	/** Returns Vector of String objects representing IOCs.
	 */ 
	public Vector getIocs() throws SQLException {

		Object[] columns = {"ioc_id"};  
		Object[][] conditions = {{}, {}};
		ResultSet set = helper.loadRows("ioc", columns, conditions, null, "ioc_id");
        
        Vector iocs = new Vector();
		while (set.next()) {
        	iocs.add(set.getString(1));
        }
        return iocs;
	}

	/** Returns Vector of String objects representing db files under the given IOC.
	 */ 
	public Vector getRdbDatas(String iocId) throws SQLException {

		Object[] columns = {"p_db_file_name"};  
		Object[][] conditions = {{"ioc_id_FK"}, {iocId}};
		ResultSet set = helper.loadRows("p_db", columns, conditions, null, "p_db_file_name");
        
        Vector groups = new Vector();
        while (set.next()) {
        	groups.add(set.getString(1));
        }
        return groups;
	}

	/** Returns Vector of String objects representing versions of the given group.
	 */ 
	public Vector getVersions(String group, String iocId) throws SQLException {

		Object[] columns = {"p_db_version"};  
		Object[][] conditions = {{"ioc_id_FK", "p_db_file_name"}, {iocId, group}};
		ResultSet set = helper.loadRows("p_db", columns, conditions, null, "p_db_version");
        
        Vector versions = new Vector();
        while (set.next()) {
        	versions.add(set.getString(1));
        }
        return versions;
	}
	
	public void addRdbDataId(RdbDataId dataId, String desription) throws SQLException {

    	Object[][] keyPairs = {{"p_db_file_name", "ioc_id_FK", "p_db_version"},
    			{dataId.getFileName(), dataId.getIoc(), dataId.getVersion()}};
		Object[][] valuePairs = {{"p_db_desc"}, {desription}};
   		helper.saveRow("p_db", keyPairs, valuePairs);
        helper.commit();
	}
	
	private boolean loadDbId(RdbDataId dataId) throws SQLException {
	
		Object[] columns = {"p_db_id"};  
		Object[][] conditions = {{"p_db_file_name", "p_db_version", "ioc_id_FK"},
				{dataId.getFileName(), dataId.getVersion(), dataId.getIoc()}};

		ResultSet set = helper.loadRows("p_db", columns, conditions);
        
        if (set.next()) {
        	pDbId = new Integer(set.getInt(1));
        	return true;
        }
        return false;
	}
	
	private void loadRecords(DBData data) throws SQLException {

		/* Rec_code_type usage.
		Object[] columns = {"sgnl_id", "sgnl_rec.rec_type_id"};  
		Object[][] conditions = {{"epics_grp_id", "sgnl_rec.rec_type_id", "rec_type_code"},
                {group,            "sgnl_rec_type.rec_type_id", "E"}};
		ResultSet set = loadRows("sgnl_rec, sgnl_rec_type", columns, conditions);
        */

		Object[] columns = {"p_rec_id", "p_rec_nm", "p_rec_type_id_FK"};  
		Object[][] conditions = {{"p_db_id_FK"}, {pDbId}};
		
		ResultSet set = helper.loadRows("p_rec", columns, conditions);
        
		Integer id = null;
		String name = null;
		String type = null;
        while (set.next()) {
    		DBRecordData record = new DBRecordData();
    		id = new Integer(set.getInt(1));
    		name = set.getString(2);
    		type = set.getString(3);
    		record.setName(name);
    		record.setRecord_type(type);
    		loadFields(record, id);
    		data.addRecord(record);
    		
    		if (manageDbds) {
    		  loadRecordDef(type);
    		}
        }
	}

	private void loadTemplates(DBData data) throws SQLException {
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
	
	private DBRecordData loadFields(DBRecordData record, Integer recordId) throws SQLException {
        
		Object[] columns = {"p_fld_type", "p_fld_val"};  
		Object[][] conditions = {{"p_rec_id_FK"}, {recordId}};
		
		ResultSet set = helper.loadRows("p_fld, p_fld_type", columns, conditions, "p_fld_type_id_FK=p_fld_type_id");
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
			
			
			ResultSet set = helper.loadRows("sgnl_fld_def", columns, conditions, null, "prmpt_ord");
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
   			System.out.println("Saving record: " + recordName);
   			saveFieldsDef(record);
   			System.out.println("Ending save record: " + recordName);
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
	
	private void saveMinRecordDef(String type) throws SQLException {
		
		Object[][] keyPairs = {{"p_rec_type_id", "p_dbd_id_FK"}, {type, pDbdId}};
		Object[][] valuePairs = {{"p_rec_type_code", "p_type_desc"}, {"E", recordDefDescription}};
		helper.appendRow("p_rec_type", keyPairs, valuePairs);
	}
	
	private int saveMinFieldDef(String fieldName, String recordType) throws SQLException {
		Object[][] keyPairs = {{"p_rec_type_id_FK", "p_dbd_id_FK", "p_fld_type"}, {recordType, pDbdId, fieldName}};
		Object[][] valuePairs = {{}, {}};
		helper.appendRow("p_fld_type", keyPairs, valuePairs);

		Object[] columns = {"p_fld_type_id"};
		ResultSet set = helper.loadRows("p_fld_type", columns, keyPairs);
		return set.next() ? set.getInt(1) : 0;
	}
	
	private void saveIoc() throws SQLException {
		Object[][] keyPairs = {{}, {}};
		Object[][] valuePairs = {{}, {}};
		helper.appendRow("ioc", keyPairs, valuePairs);
		
		Object[] columns = {"ioc_id"};
		ResultSet set = helper.loadRows("ioc", columns, keyPairs);
		if (set.next()) {
			iocId = new Integer(set.getInt(1));
		}
	}

	private void saveDefinitionFile() throws SQLException {
		Object[][] keyPairs = {{}, {}};
		Object[][] valuePairs = {{}, {}};
		helper.appendRow("p_dbd", keyPairs, valuePairs);
		
		Object[] columns = {"p_dbd_id"};
		ResultSet set = helper.loadRows("p_dbd", columns, keyPairs);
		if (set.next()) {
			pDbdId = new Integer(set.getInt(1));
		}
	}
	
	private void saveRecords() throws SQLException {

		// rec type code usage: remove when Group and rec_type_code usage is defined  
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
        			saveRecord(recordData);
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
	
	private void saveRecord(VDBRecordData recordData) throws SQLException  {
		String name = recordData.getName();
		String type = recordData.getType();
		saveMinRecordDef(type);
		
		Object[][] keyPairs = {{"p_rec_nm", "p_db_id_FK", "p_rec_type_id_FK"}, {name, pDbId, type}};
		Object[][] valuePairs = {{}, {}};
		helper.saveRow("p_rec", keyPairs, valuePairs);
		
		Object[] columns = {"p_rec_id"};
		ResultSet set = helper.loadRows("p_rec", columns, keyPairs);
		
		Integer recId = new Integer(set.next() ? set.getInt(1) : 0);
		
		Iterator iterator = recordData.getFieldsV().iterator();
		while (iterator.hasNext()) {
			saveField((VDBFieldData)iterator.next(), recId, type);
		}
	}
	
	private void saveField(VDBFieldData fieldData, Integer recId, String recordType) throws SQLException {

		String name = fieldData.getName();
    	String value = StringUtils.removeQuotes(fieldData.getValue());
    	String table = "p_fld";
		
    	Integer fldTypeId = new Integer(saveMinFieldDef(name, recordType));
    	
    	Object[][] keyPairs = {{"p_rec_id_FK", "p_fld_type_id_FK"}, {recId, fldTypeId}};
		Object[][] valuePairs = {{"p_fld_val"}, {value}};
		
    	if (!fieldData.hasDefaultValue() && !value.equals(emptyString)) {
    		helper.saveRow(table, keyPairs, valuePairs);
        }
	}
	
	private void loadVdctData(DBData data) throws SQLException {

		Object[] columns = {"p_db_vdct"};
    	Object[][] keyPairs = {{"p_db_id"}, {pDbId}};
		
   		ResultSet set = helper.loadRows("p_db", columns, keyPairs);
   		if (set.next()) {
   			DBResolver.readVdctData(data, set.getString(1), data.getTemplateData().getId());
   		}
	}
	
	private void saveVdctData() throws SQLException {
		String string = Group.getVDCTData();
    	Object[][] keyPairs = {{"p_db_id"}, {pDbId}};
		Object[][] valuePairs = {{"p_db_vdct"}, {string}};
   		helper.saveRow("p_db", keyPairs, valuePairs);
	}
}
