package com.cosylab.vdct;

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
import com.cosylab.vdct.dbd.*;
import com.cosylab.vdct.vdb.*;
import com.cosylab.vdct.graphics.objects.*;
import com.cosylab.vdct.inspector.*;

/**
 * Insert the type's description here.
 * Creation date: (8.1.2001 18:21:54)
 * @author Matej Sekoranja
 */
 
public class DataProvider {
	private static DataProvider instance = null;
	
	// DBD
	private DBDData dbdDB = null;
	
	private Vector inspectableListeners = null;
	
	// list of all loaded DBDs
	private Vector loadedDBDs = null;

	// list of all loaded DBDs
	private Vector currentDBDs = null;

/**
 * DataProvider constructor comment.
 */
protected DataProvider() {
	inspectableListeners = new Vector();
	loadedDBDs = new Vector();
	currentDBDs = new Vector();
}
/**
 * Insert the method's description here.
 * Creation date: (17.4.2001 17:23:00)
 * @param listener com.cosylab.vdct.inspector.InspectableObjectsListener
 */
public void addInspectableListener(InspectableObjectsListener listener) {
	if (!inspectableListeners.contains(listener))
		inspectableListeners.addElement(listener);
}
/**
 * Insert the method's description here.
 * Creation date: (17.4.2001 17:25:49)
 * @param object com.cosylab.vdct.inspector.Inspectable
 */
public void fireInspectableObjectAdded(Inspectable object) {
	Enumeration e = inspectableListeners.elements();
	while (e.hasMoreElements())
		((InspectableObjectsListener)e.nextElement()).inspectableObjectAdded(object);
}
/**
 * Insert the method's description here.
 * Creation date: (17.4.2001 17:26:16)
 * @param object com.cosylab.vdct.inspector.Inspectable
 */
public void fireInspectableObjectRemoved(Inspectable object) {
	Enumeration e = inspectableListeners.elements();
	while (e.hasMoreElements())
		((InspectableObjectsListener)e.nextElement()).inspectableObjectRemoved(object);
}
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 18:25:39)
 * @return com.cosylab.vdct.dbd.DBDData
 */
public com.cosylab.vdct.dbd.DBDData getDbdDB() {
	return dbdDB; //!!!
}
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 22:03:39)
 * @return java.util.Vector
 */
public Vector getInspectable() {
	Vector objs = new Vector();
	getInspectable(Group.getRoot(), objs, true);
	return objs;
}
/**
 * Insert the method's description here.
 * Creation date: (2.2.2001 21:59:25)
 * @param group com.cosylab.vdct.graphics.objects.Group
 * @param deep boolean
 */
private void getInspectable(Group group, Vector objs, boolean deep) {
	Enumeration e = group.getSubObjectsV().elements();
	Object obj;
	while (e.hasMoreElements()) {
		obj = e.nextElement();
		if (obj instanceof com.cosylab.vdct.inspector.Inspectable)
			objs.addElement(obj);
		else if (deep && (obj instanceof Group))
			getInspectable((Group)obj, objs, deep);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 18:22:58)
 * @return com.cosylab.vdct.DataProvider
 */
public static DataProvider getInstance() {
	if (instance==null) instance = new DataProvider();
	return instance;
}
/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 19:56:16)
 * @return java.lang.String[]
 */
public Object[] getRecordTypes() {
	Object[] records;
	records = new com.cosylab.vdct.util.StringQuickSort().sortEnumeration(
		dbdDB.getRecordNames());
	return records;
	
}
/**
 * Insert the method's description here.
 * Creation date: (17.4.2001 17:23:30)
 * @param listener com.cosylab.vdct.inspector.InspectableObjectsListener
 */
public void removeInspectableListener(InspectableObjectsListener listener) {
	if (inspectableListeners.contains(listener))
		inspectableListeners.removeElement(listener);
}
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 18:25:39)
 * @param newDbdDB com.cosylab.vdct.dbd.DBDData
 */
public void setDbdDB(com.cosylab.vdct.dbd.DBDData newDbdDB) {
	dbdDB = newDbdDB;
}
	/**
	 * Returns the currentDBDs.
	 * @return Vector
	 */
	public Vector getDBDs()
	//public Vector getCurrentDBDs()
	{
		return currentDBDs;
	}

	/**
	 * Returns the loadedDBDs.
	 * @return Vector
	 */
	public Vector getLoadedDBDs()
	{
		return loadedDBDs;
	}

}
