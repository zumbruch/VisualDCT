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

import com.cosylab.vdct.Constants;
import com.cosylab.vdct.db.DBPort;
import com.cosylab.vdct.dbd.DBDConstants;
import com.cosylab.vdct.graphics.objects.Descriptable;
import com.cosylab.vdct.graphics.objects.Group;
import com.cosylab.vdct.graphics.objects.Template;
import com.cosylab.vdct.inspector.InspectableProperty;
import com.cosylab.vdct.inspector.InspectorManager;

/**
 * @author Matej
 */
public class VDBTemplatePort extends VDBFieldData implements Descriptable
{
	public DBPort port = null;
	public VDBTemplateInstance templateInstance = null;
	
	/**
	 */
	public VDBTemplatePort(VDBTemplateInstance templateInstance, DBPort port)
	{
		super();
		this.port=port;
		this.templateInstance = templateInstance;
	}
	

	/**
	 * Insert the method's description here.
	 * Creation date: (1.2.2001 12:11:29)
	 * @return java.lang.String
	 */
	public String toString() {
		return templateInstance.getName()+com.cosylab.vdct.Constants.FIELD_SEPARATOR+port.getName();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1.2.2001 12:11:29)
	 * @return java.lang.String
	 */
	public String getName() {
		return port.getName();
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (1.2.2001 12:11:29)
	 * @return java.lang.String
	 */
	public String getFullName() {
		if (templateInstance==null)
			return "(undefined)"+com.cosylab.vdct.Constants.FIELD_SEPARATOR+name;
		else
		{
			// optimizie
			StringBuffer fullName = new StringBuffer();
			fullName.append("$(");
			fullName.append(templateInstance.getName());
			fullName.append(com.cosylab.vdct.Constants.FIELD_SEPARATOR);
			fullName.append(port.getName());
			fullName.append(")");

			String fn = VDBTemplateInstance.applyProperties(fullName.toString(), templateInstance.getProperties());
			return fn;
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (11.1.2001 21:30:04)
	 * @return int
	 */
	public int getVisibility()
	{
		return InspectableProperty.UNDEFINED_VISIBILITY;
	}
	
	/**
	 * Sets the visibility.
	 * @param visibility The visibility to set
	 */
	public void setVisibility(int visibility)
	{
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (26.1.2001 15:03:07)
	 * @return java.lang.String
	 */
	public java.lang.String getHelp() {
		return port.getDescription();
	}
	
	/**
	 * @see com.cosylab.vdct.graphics.objects.Descriptable#getDescription()
	 */
	public String getDescription()
	{
		return port.getDescription();
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.Descriptable#setDescription(String)
	 */
	public void setDescription(String description)
	{
	}

	/**
	 * Returns the templateInstance.
	 * @return VDBTemplateInstance
	 */
	public VDBTemplateInstance getTemplateInstance()
	{
		return templateInstance;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (7.12.2001 19:13:20)
	 * @param value java.lang.String
	 */
	public void setDebugValue(String newValue)
	{
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9.12.2000 18:11:46)
	 * @param newValue java.lang.String
	 */
	public void setValue(java.lang.String newValue)
	{
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9.12.2000 18:11:46)
	 * @return java.lang.String
	 */
	public java.lang.String getValue() {
		if (!com.cosylab.vdct.plugin.debug.PluginDebugManager.isDebugState())
			return port.getTarget();
		else
			return port.getTarget();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9.12.2000 18:11:46)
	 */
	public void updateInspector()
	{
		Template visualTemplate = (Template)Group.getRoot().findObject(templateInstance.getName(), true);
		if (visualTemplate==null) {
			//com.cosylab.vdct.Console.getInstance().println("o) Internal error: no visual representaton of record "+getName()+" found.");
			return;
		}
	
		InspectorManager.getInstance().updateProperty(visualTemplate, this);
	}

}
