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

import java.awt.Color;
import java.awt.Graphics;
import java.util.Enumeration;
import java.util.Vector;

import com.cosylab.vdct.Constants;
import com.cosylab.vdct.DataProvider;
import com.cosylab.vdct.graphics.ViewState;
import com.cosylab.vdct.inspector.InspectableProperty;
import com.cosylab.vdct.vdb.GUISeparator;
import com.cosylab.vdct.vdb.NameValueInfoProperty;
import com.cosylab.vdct.vdb.VDBFieldData;
import com.cosylab.vdct.vdb.VDBTemplateField;
import com.cosylab.vdct.vdb.VDBTemplatePort;

/**
 * Insert the type's description here.
 * Creation date: (29.1.2001 21:27:30)
 * @author Matej Sekoranja
 */

public class TemplateEPICSPort extends EPICSVarLink implements TemplateEPICSLink {

 	private String lastUpdatedFullName = null;
	private static GUISeparator portSeparator = null;

/**
 * EPICSVarLink constructor comment.
 * @param parent com.cosylab.vdct.graphics.objects.ContainerObject
 * @param fieldData com.cosylab.vdct.vdb.VDBFieldData
 */
public TemplateEPICSPort(ContainerObject parent, VDBFieldData fieldData) {
	super(parent, fieldData);
	setWidth(Constants.TEMPLATE_WIDTH/2);

	updateTemplateLink();
}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 16:58:58)
 * @return boolean
 */
public void updateTemplateLink()
{
	if (lastUpdatedFullName!=null && getFieldData().getFullName().equals(lastUpdatedFullName))
		return;
		
	// remove old one		
	if (lastUpdatedFullName!=null)
		Group.getRoot().getLookupTable().remove(lastUpdatedFullName);
	
	// ups, we already got this registered
	if (Group.getRoot().getLookupTable().containsKey(getFieldData().getFullName()))
	{
		lastUpdatedFullName = null;
		((LinkManagerObject)getParent()).addInvalidLink(this);
	}
	// everything is OK
	else
	{
		lastUpdatedFullName = getFieldData().getFullName();
		Group.getRoot().getLookupTable().put(lastUpdatedFullName, this);
		LinkManagerObject.fixLink(this);
		((LinkManagerObject)getParent()).removeInvalidLink(this);
	}
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
	return getFieldData().getName();
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

/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 22:10:37)
 * @param g java.awt.Graphics
 * @param hilited boolean
 */
protected void draw(Graphics g, boolean hilited) {
	super.draw(g, hilited);

	com.cosylab.vdct.graphics.ViewState view = com.cosylab.vdct.graphics.ViewState.getInstance();
	boolean isRightSide = isRight();

	int len;
	int rrx;			// rrx, rry is center
	if (isRightSide)
	{
		rrx = getRx()+getRwidth()-view.getRx();
		len = 3*r;
	}
	else
	{ 
		rrx = getRx()-view.getRx();
		len = - 3*r;
	}
	
	int rryField = getRy()-view.getRy();
	int rry = (int)(getRscale()*getInY()- view.getRy());
	
	if (!hilited) g.setColor(Constants.FRAME_COLOR);
	else g.setColor((this==view.getHilitedObject()) ? 
					Constants.HILITE_COLOR : Constants.FRAME_COLOR);

	g.drawLine(rrx, rryField, rrx+len, rry);
	g.drawLine(rrx, rryField+getRheight(), rrx+len, rry);



	if (lastUpdatedFullName==null)
	{
		//ViewState view = ViewState.getInstance();
		//int rrx = getRx()-view.getRx();
		//int rry = getRy()-view.getRy();
		int rwidth = getRwidth();
		int rheight = getRheight();
			
		// clipping
		if ((rrx>view.getViewWidth()) || (rry>view.getViewHeight())
		    || ((rrx+rwidth)<0) || ((rry+rheight)<0)) return;
	
		g.setColor(Color.red);

		g.drawLine(rrx, rry, rrx+rwidth, rry+rheight);
		g.drawLine(rrx+rwidth, rry, rrx, rry+rheight);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:59:21)
 */
public void destroyAndRemove() {
	super.destroy();

	if (lastUpdatedFullName!=null)
		Group.getRoot().getLookupTable().remove(getFieldData().getFullName());
	else
		((LinkManagerObject)getParent()).removeInvalidLink(this);

}

/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 13:07:04)
 * @return com.cosylab.vdct.vdb.GUISeparator
 */
public static com.cosylab.vdct.vdb.GUISeparator getPortSeparator() {
	if (portSeparator==null) portSeparator = new GUISeparator("Port");
	return portSeparator;
}

/**
 * Return properties to be inspected
 * Creation date: (1.2.2001 22:22:37)
 * @return com.cosylab.vdct.inspector.InspectableProperty[]
 */
public com.cosylab.vdct.inspector.InspectableProperty[] getProperties(int mode) {

	OutLink out;
	Vector starts = new Vector();
	Enumeration e = outlinks.elements();
	while (e.hasMoreElements()) {
		out = EPICSLinkOut.getStartPoint((Linkable)e.nextElement());
		if (out instanceof EPICSLinkOut) starts.addElement(out);
	}

	InspectableProperty[] properties = new InspectableProperty[2+2*starts.size()];

	properties[0]=getPortSeparator();
	properties[1]=new NameValueInfoProperty("Value", getFieldData().getValue());

	int i = 2;
	VDBFieldData fieldData;
	e = starts.elements();
	while (e.hasMoreElements())
	{
		fieldData = ((EPICSLinkOut)e.nextElement()).getFieldData();
		properties[i++]=new GUISeparator(fieldData.getFullName());
		properties[i++]=fieldData;
	}
	return properties;
}



}
