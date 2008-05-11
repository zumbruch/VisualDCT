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

import com.cosylab.vdct.DataProvider;
import com.cosylab.vdct.db.DBData;
import com.cosylab.vdct.db.DBFieldData;
import com.cosylab.vdct.db.DBRecordData;
import com.cosylab.vdct.dbd.DBDData;
import com.cosylab.vdct.dbd.DBDDeviceData;
import com.cosylab.vdct.dbd.DBDFieldData;
import com.cosylab.vdct.dbd.DBDMenuData;
import com.cosylab.vdct.dbd.DBDRecordData;
import com.cosylab.vdct.dbd.DBDResolver;

/**
 * @author ssah
 *
 */
public class RdbLoader {

	private static RdbLoader rdbLoader = null;
	
	private static final String database = "epics";
	
	public static RdbLoader getInstance() throws Exception {
		if (rdbLoader == null) {
			rdbLoader = new RdbLoader();
		}
		return rdbLoader;
	}

	public DBData loadDbGroup(String user, String password, String host, String group) {
		DBData data = null;
		try {
			Connection connection = createConnection(user, password, host);
			data = loadRecords(group, connection);
			connection.close();
		} catch (SQLException exception) {
		    System.out.println("SQLException: " + exception.getMessage());
		    System.out.println("SQLState: " + exception.getSQLState());
		    System.out.println("VendorError: " + exception.getErrorCode());
		}
		return data;
	}
	
	private RdbLoader() throws Exception {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
	}
	
	private Connection createConnection(String user, String password, String host) throws SQLException {
		String connectionString =
			"jdbc:mysql://" + host + "/" + database + "?user=" + user + "&password=" + password;		
		return DriverManager.getConnection(connectionString);
	}
	
	private DBData loadRecords(String group, Connection connection) throws SQLException {
		DBData data = new DBData(group, group);
		
        PreparedStatement statement = connection.prepareStatement("SELECT sgnl_id, rec_type_id FROM sgnl_rec WHERE epics_grp_id=?");
        statement.setString(1, group);
        ResultSet set = statement.executeQuery();
        while (set.next()) {
    		DBRecordData record = new DBRecordData();
    		String name = set.getString(1);
    		String type = set.getString(2);
    		record.setName(name);
    		record.setRecord_type(type);
    		loadRecord(record, connection);
    		data.addRecord(record);
    		
    		loadRecordDef(type, connection);
        }
		return data;
	}

	private DBRecordData loadRecord(DBRecordData record, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT fld_id, ext_val FROM sgnl_fld WHERE sgnl_id=?");
        statement.setString(1, record.getName());
        ResultSet set = statement.executeQuery();
        while (set.next()) {
        	// value in DBFieldData can be null, in this case it will be set to default.
        	record.addField(new DBFieldData(set.getString(1), set.getString(2)));
        }
		return record;
	}
	
	private void loadRecordDef(String name, Connection connection) throws SQLException {

		DBDData definitions = DataProvider.getInstance().getDbdDB();
		if (definitions.getDBDRecordData(name) == null) {
			DBDRecordData recordDef = new DBDRecordData();
			recordDef.setName(name);
			
	        PreparedStatement statement = connection.prepareStatement("SELECT"
	        		+ " fld_id, fld_prmt_grp, fld_type_id, fld_init, fld_prmt, fld_base, fld_size, sgnl_fld_menu_id"
	        		+ " FROM sgnl_fld_def WHERE rec_type_id=?");
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
	        	    loadMenuOrDeviceDef(menuName, connection);
	        	}
	        	recordDef.addField(fieldDef);
	        }
			
			definitions.addRecord(recordDef);
		}
	}

	private void loadMenuOrDeviceDef(String name, Connection connection) throws SQLException {
		if (name.endsWith("DTYP")) {
			loadDeviceDef(name, connection);
		} else {
			loadMenuDef(name, connection);
		}
	}

	private void loadMenuDef(String name, Connection connection) throws SQLException {
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

	private void loadDeviceDef(String name, Connection connection) throws SQLException {
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
}
