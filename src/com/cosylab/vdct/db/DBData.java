package com.cosylab.vdct.db;

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

import com.cosylab.vdct.Console;
import com.cosylab.vdct.dbd.*;
import java.util.*;

/**
 * This type was created in VisualAge.
 */
public class DBData {
	protected Hashtable records = null;
	protected Vector recordsV = null;			// ordered
	protected Hashtable groups = null;
	protected Hashtable links = null;
	protected Hashtable connectors = null;
	protected Hashtable templates = null;		// !!! temp
	protected Hashtable templateInstances = null;
/**
 * DBDData constructor comment.
 */
public DBData() {
	records = new Hashtable();
	recordsV = new Vector();
	groups = new Hashtable();
	links = new Hashtable();
	connectors = new Hashtable();
	templates = new Hashtable();
	templateInstances = new Hashtable();
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 21:16:22)
 * @param connector com.cosylab.vdct.db.DBConnectorData
 */
public void addConnector(DBConnectorData connector) {
	if (!connectors.containsKey(connector.getConnectorID()))
		connectors.put(connector.getConnectorID(), connector);
}
/**
 * This method was created in VisualAge.
 * @param gd com.cosylab.vdct.db.DBGroupData
 */
public void addGroup(DBGroupData gd) {
	if (!groups.containsKey(gd.getName()))
		groups.put(gd.getName(), gd);
}
/**
 * This method was created in VisualAge.
 * @param ld com.cosylab.vdct.db.DBLinkData
 */
public void addLink(DBLinkData ld) {
	if (!links.containsKey(ld.getFieldName()))
		links.put(ld.getFieldName(), ld);
}
/**
 * This method was created in VisualAge.
 * @param rd com.cosylab.vdct.db.RecordData
 */
public void addRecord(DBRecordData rd) {
	if (!records.containsKey(rd.getName())) {
		records.put(rd.getName(), rd);
		recordsV.addElement(rd);
	}
	else
		Console.getInstance().println("Warning: Record with name '"+rd.getName()+"' already exists, skiping...");
}
/**
 * This method was created in VisualAge.
 * @param rd com.cosylab.vdct.db.DBTemplateInstance
 */
public void addTemplateInstance(DBTemplateInstance ti) {
	if (!templateInstances.containsKey(ti.getTemplateID())) {
		templateInstances.put(ti.getTemplateID(), ti);
	}
	else
		Console.getInstance().println("Warning: Template instance of '"+ti.getTemplateID()+"' already exists, skiping...");
}
/**
 * Insert the method's description here.
 * Creation date: (18.11.1999 18:26:27)
 */

public static void checkDTYPfield(DBData db, DBDData dbd) {

 DBDFieldData dbdField;
 DBDRecordData dbdRecord;

 DBRecordData dbRecord;
 Enumeration e = db.getRecordsV().elements();
 Enumeration e2;
 while (e.hasMoreElements()) {
	dbRecord = (DBRecordData)e.nextElement();
	dbdRecord = dbd.getDBDRecordData(dbRecord.getRecord_type());
	if (dbdRecord!=null) {
		
		DBFieldData dbField2 = null;
		DBFieldData dbField = (DBFieldData)dbRecord.getFields().get("DTYP");
		if (dbField!=null) {
			e2 = dbRecord.getFieldsV().elements();
			dbField2 = (DBFieldData)e2.nextElement();
			while (dbField!=dbField2) {
				dbdField = dbdRecord.getDBDFieldData(dbField2.getName());
				if ((dbdField.getField_type()==DBDConstants.DBF_INLINK) ||
					(dbdField.getField_type()==DBDConstants.DBF_OUTLINK)) break;
				else
					dbField2 = (DBFieldData)e2.nextElement();
			}

			if (dbField!=dbField2) 
				Console.getInstance().println("Warning: "+dbRecord.name+" -> DTYP field must be defined before any DBF_INPUT/DBF_OUTPUT fields...");
		}

	}
 }			 

}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 21:15:41)
 * @return java.util.Hashtable
 */
public java.util.Hashtable getConnectors() {
	return connectors;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:07:57)
 * @return java.util.Hashtable
 */
public Hashtable getGroups() {
	return groups;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:07:57)
 * @return java.util.Hashtable
 */
public Hashtable getLinks() {
	return links;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:07:57)
 * @return java.util.Hashtable
 */
public Hashtable getRecords() {
	return records;
}
/**
 * Returns ordered (as read) list of records
 * Creation date: (6.1.2001 20:37:16)
 * @return java.util.Vector
 */
public Vector getRecordsV() {
	return recordsV;
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
	 * Returns the templates.
	 * @return Hashtable
	 */
	public Hashtable getTemplates()
	{
		return templates;
	}

}
