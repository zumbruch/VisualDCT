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

import java.util.*;
import com.cosylab.vdct.graphics.objects.*;

/**
 * This type was created in VisualAge.
 */

public class VDBRecordData {
	protected String record_type;
	protected String name;
	protected Hashtable fields = null;
	protected Vector fieldsV = null;
	//public String DTYP_type = com.cosylab.vdct.Constants.NONE;
	protected String comment;
/**
 * RecordData constructor comment.
 */
public VDBRecordData() {
	fields = new Hashtable();
	fieldsV = new Vector();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String _getDTYPType() {
//	return DTYP_type;
	return null;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean _isHardwareDTYP() {
/*	if (DTYP_type.equalsIgnoreCase("CONSTANT") || 
		DTYP_type.equalsIgnoreCase("PV_LINK") ||
		DTYP_type.equalsIgnoreCase("<macro>") ||
		DTYP_type.equalsIgnoreCase(com.cosylab.vdct.Constants.NONE)) return false;
	else*/ return true;

}
/**
 * This method was created in VisualAge.
 * @param dbd DBDData
 */
public void _updateDTYP(com.cosylab.vdct.dbd.DBDData dbd) {
/*	VDBFieldData dtyp = (VDBFieldData)(fields.get("DTYP"));
	
	if (dtyp==null) {
		System.out.println("Error: Device "+this.record_type+" does not have DTYP field...");
		return;	
	}	
	else if (dtyp.value.equals(com.cosylab.vdct.Constants.NONE)) return;
	else if ((dtyp.value.indexOf("$")!=-1) || dtyp.value.equals("<template definition>")) {
		DTYP_type = "<macro>";
		return;
	}

	epics.dbd.DBDDeviceData dev = (epics.dbd.DBDDeviceData)(dbd.getDBDDeviceData(this.record_type+"/"+dtyp.value));
	if (dev==null) {
		System.out.println("Error: Device "+this.record_type+"/"+dtyp.value+" not found...");
		return;	
	}
		
	DTYP_type = dev.link_type;*/
}
/**
 * This method was created in VisualAge.
 * @param fd VisualDCTPackage.FieldData
 */
public void addField(VDBFieldData fd) {
   if (fd!=null)
	if (!fields.containsKey(fd.name.toUpperCase())) {
		fields.put(fd.name.toUpperCase(), fd);
		fieldsV.addElement(fd);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (28.1.2001 13:12:23)
 * @param field com.cosylab.vdct.vdb.VDBFieldData
 */
public void fieldValueChanged(VDBFieldData field) {
	Record visualRecord = (Record)Group.getRoot().findObject(getName(), true);
	if (visualRecord==null) {
		//com.cosylab.vdct.Console.getInstance().println("o) Internal error: no visual representaton of record "+getName()+" found.");
		return;
	}

	com.cosylab.vdct.inspector.InspectorManager.getInstance().updateProperty(visualRecord, field);
	visualRecord.fieldChanged(field);
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 18:13:17)
 * @return java.lang.String
 */
public java.lang.String getComment() {
	return comment;
}
/**
 * This method was created in VisualAge.
 * @return epics.vdb.VDBFieldData
 * @param fieldName java.lang.String
 */
public VDBFieldData getField(String fieldName) {
	return (VDBFieldData)(fields.get(fieldName.toUpperCase()));
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 18:13:17)
 * @return java.util.Hashtable
 */
public Hashtable getFields() {
	return fields;
}
/**
 * Insert the method's description here.
 * Creation date: (6.1.2001 20:53:54)
 * @return java.util.Vector
 */
public java.util.Vector getFieldsV() {
	return fieldsV;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 18:13:17)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 18:13:17)
 * @return java.lang.String
 */
public java.lang.String getType() {
	return record_type;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 18:13:17)
 * @param newComment java.lang.String
 */
public void setComment(java.lang.String newComment) {
	comment = newComment;

	Record visualRecord = (Record)Group.getRoot().findObject(getName(), true);
	if (visualRecord==null) {
		//com.cosylab.vdct.Console.getInstance().println("o) Internal error: no visual representaton of record "+getName()+" found.");
		return;
	}

	com.cosylab.vdct.inspector.InspectorManager.getInstance().updateCommentProperty(visualRecord);
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 18:13:17)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	name = newName;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 18:13:17)
 * @param newRecord_type java.lang.String
 */
public void setType(java.lang.String newRecord_type) {
	record_type = newRecord_type;
}
/**
 * Insert the method's description here.
 * Creation date: (2.2.2001 21:51:41)
 * @return java.lang.String
 */
public String toString() {
	return name+" ("+record_type+")";
}
}
