package com.cosylab.vdct.db;

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
/**
 * DBDData constructor comment.
 */
public DBData() {
	records = new Hashtable();
	recordsV = new Vector();
	groups = new Hashtable();
	links = new Hashtable();
	connectors = new Hashtable();
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
}
