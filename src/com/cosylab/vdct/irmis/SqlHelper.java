/**
 * Copyright (c) 2008, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
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

import com.cosylab.vdct.Console;

/**
 * @author ssah
 *
 */
public class SqlHelper {

	private String user = null;
	private String password = null;
	private String host = null;
	private String group = null;
	private String database = null;
	private Connection connection = null;
	
	private static SqlHelper sqlHelper = null;
	
	private static final String defaultUser = "user";
	private static final String defaultPassword = "password";
	private static final String defaultHost = "localhost";
	private static final String defaultGroup = "Group";
	private static final String defaultDatabase = "epics";

	public static SqlHelper getInstance() throws Exception {
		if (sqlHelper == null) {
			sqlHelper = new SqlHelper();
		}
		return sqlHelper;
	}
	
	private SqlHelper() throws Exception {
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
	
	public boolean isConnection() {
		return connection != null;
	}
	
	public String getGroup() {
		return group;
	}
	
	public void commit() throws SQLException {
		connection.commit();
	}
	
	public void displayExceptionMessage(String message, SQLException exception) {
	    Console.getInstance().println(message);
	    Console.getInstance().println("SQLException: " + exception.getMessage());
	    Console.getInstance().println("SQLState: " + exception.getSQLState());
	    Console.getInstance().println("VendorError: " + exception.getErrorCode());
	    exception.printStackTrace();
	}
	
	public void createConnection() {
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

	public void closeConnection(boolean rollback) {
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

	private String getQuestionMarkList(int length) {
		StringBuffer buffer = new StringBuffer();
		for (int e = 0; e < length - 1; e++) {
			buffer.append("?,");
		}
		buffer.append("?");
		return buffer.toString();
	}
	
	private String getEqualityList(Object[] elements) {
		return getEqualityExpression(elements, ",");
	}

	private String getEqualityStatement(Object[] elements) {
		return getEqualityExpression(elements, " AND ");
	}

	private String getEqualityExpression(Object[] elements, String delimiter) {
		StringBuffer buffer = new StringBuffer();
		int len = elements.length;
		for (int e = 0; e < len; e++) {
			buffer.append(elements[e].toString());
			buffer.append("=?");
			if (e < len - 1) {
				buffer.append(delimiter);
			}
		}
		return buffer.toString();
	}
	
	private void insertValues(PreparedStatement statement, Object[] elements) throws SQLException {
		insertValues(statement, elements, 0);
	}

	private void insertValues(PreparedStatement statement, Object[] elements1, Object[] elements2) throws SQLException {
		insertValues(statement, elements1, 0);
		insertValues(statement, elements2, elements1.length);
	}
	
	private void insertValues(PreparedStatement statement, Object[] elements, int offset) throws SQLException {
		for (int i = 0; i < elements.length; i++) {
        	statement.setString(i + offset + 1, String.valueOf(elements[i]));
        }
	}

	public ResultSet loadRows(String table, Object[] columns, Object[][] keyPairs) throws SQLException {
		return loadRows(table, columns, keyPairs, null);
	}
	
	public ResultSet loadRows(String table, Object[] columns, Object[][] keyPairs, String orderBy) throws SQLException {

        String columnsString = getList(columns);
        String condition = getEqualityStatement(keyPairs[0]);
        
        String sqlString = "SELECT " + columnsString + " FROM "
        	+ table + " WHERE " + condition + (orderBy != null ? " ORDER BY " + orderBy : "");
        PreparedStatement statement = connection.prepareStatement(sqlString);
        insertValues(statement, keyPairs[1]);
		return statement.executeQuery();
	}
	
	private boolean isRowPresent(String table, Object[][] keyPairs) throws SQLException {
		Object[] columns = {keyPairs[0][0]};  
		ResultSet set = loadRows(table, columns, keyPairs);
		return set.next(); 
	}

	public void saveRow(String table, Object[][] keyPairs, Object[][] valuePairs) throws SQLException {
		saveRow(table, keyPairs, valuePairs, true);
	}

	/* Only saves data if the row doesn't exist yet.
	 */
	public void appendRow(String table, Object[][] keyPairs, Object[][] valuePairs) throws SQLException {
		saveRow(table, keyPairs, valuePairs, false);
	}
	
	private void saveRow(String table, Object[][] keyPairs, Object[][] valuePairs, boolean update) throws SQLException {
		
		boolean exists = isRowPresent(table, keyPairs);
		if (!exists) {
			int keyLen = keyPairs[1].length;
			int valueLen = valuePairs[1].length;
			
			String columnString = getList(keyPairs[0]) + (valueLen > 0 ?  "," + getList(valuePairs[0]) : "");
			String valueString = getQuestionMarkList(keyLen + valueLen);
			String sqlString = "INSERT INTO " + table
			      + " (" + columnString + ") VALUES (" + valueString + ")";
			
			PreparedStatement statement = connection.prepareStatement(sqlString);
	        insertValues(statement, keyPairs[1], valuePairs[1]);
			statement.execute();
		} else if (update && valuePairs[0].length > 0) {
			String condition = getEqualityStatement(keyPairs[0]);
			String setString = getEqualityList(valuePairs[0]);
			String sqlString = "UPDATE " + table + " SET " + setString + " WHERE " + condition;
			PreparedStatement statement = connection.prepareStatement(sqlString);
	        insertValues(statement, keyPairs[1], valuePairs[1]);
			statement.execute();
		}
	}

	public void deleteRow(String table, Object[][] keyPairs) throws SQLException {
		String sqlString = "DELETE FROM " + table + " WHERE " + getEqualityStatement(keyPairs[0]);
		PreparedStatement statement = connection.prepareStatement(sqlString);
        insertValues(statement, keyPairs[1]);
		statement.execute();
	}
}
