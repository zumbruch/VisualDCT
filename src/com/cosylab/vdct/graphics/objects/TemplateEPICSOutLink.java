package com.cosylab.vdct.graphics.objects;

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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import com.cosylab.vdct.Constants;
import com.cosylab.vdct.DataProvider;
import com.cosylab.vdct.events.CommandManager;
import com.cosylab.vdct.events.commands.LinkCommand;
import com.cosylab.vdct.vdb.VDBTemplateField;

/**
 * Insert the type's description here.
 * Creation date: (29.1.2001 21:26:07)
 * @author Matej Sekoranja 
 */
public class TemplateEPICSOutLink extends EPICSOutLink {

	private static final String linkString = "Link";

/**
 * EPICSOutLink constructor comment.
 * @param parent com.cosylab.vdct.graphics.objects.ContainerObject
 * @param fieldData com.cosylab.vdct.vdb.VDBFieldData
 */
public TemplateEPICSOutLink(ContainerObject parent, com.cosylab.vdct.vdb.VDBFieldData fieldData) {
	super(parent, fieldData);
	setWidth(Constants.TEMPLATE_WIDTH/2);

	// destroy !!!!
	DataProvider.getInstance().getLookupTable().put(fieldData.getFullName(), this);
}


/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 16:58:58)
 */
public void rotate() {
	// do not change rotation
}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 16:58:58)
 * @return String
 */
public String getLabel() {
	return ((VDBTemplateField)getFieldData()).getAlias();
}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 16:58:58)
 * @return boolean
 */
public boolean isRight()
{
	// super.super.isRigth() is the right solution, but ...
	return isStaticRight();
}	

class PopupMenuHandler implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    LinkCommand cmd = (LinkCommand)CommandManager.getInstance().getCommand("LinkCommand");
	    cmd.setData((LinkManagerObject)TemplateEPICSOutLink.this.getParent(), TemplateEPICSOutLink.this.getFieldData());
 		cmd.execute();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2.2.2001 23:00:51)
 * @return com.cosylab.vdct.graphics.objects.TemplateEPICSOutLink.PopupMenuHandler
 */
private com.cosylab.vdct.graphics.objects.TemplateEPICSOutLink.PopupMenuHandler createPopupmenuHandler() {
	return new PopupMenuHandler();
}


/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 11:23:59)
 * @return java.util.Vector
 */
public java.util.Vector getItems() {
	java.util.Vector items = super.getItems();

	if (isDisconnected())
	{
		ActionListener al = createPopupmenuHandler();
		
		items.add(new JSeparator());
		
		JMenuItem link = new JMenuItem(linkString);
		link.addActionListener(al);
		items.addElement(link);	
		
	}

	return items;
}



}
