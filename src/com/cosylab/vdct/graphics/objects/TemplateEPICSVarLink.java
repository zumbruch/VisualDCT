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

import com.cosylab.vdct.Constants;
import com.cosylab.vdct.DataProvider;
import com.cosylab.vdct.graphics.ViewState;
import com.cosylab.vdct.vdb.VDBFieldData;
import com.cosylab.vdct.vdb.VDBTemplateField;

/**
 * Insert the type's description here.
 * Creation date: (29.1.2001 21:27:30)
 * @author Matej Sekoranja
 */

	// TBD !!! removal from DataProvider lookup table


public class TemplateEPICSVarLink extends EPICSVarLink implements TemplateEPICSLink {

 	private String lastUpdatedFullName = null;

/**
 * EPICSVarLink constructor comment.
 * @param parent com.cosylab.vdct.graphics.objects.ContainerObject
 * @param fieldData com.cosylab.vdct.vdb.VDBFieldData
 */
public TemplateEPICSVarLink(ContainerObject parent, com.cosylab.vdct.vdb.VDBFieldData fieldData) {
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
		DataProvider.getInstance().getLookupTable().remove(lastUpdatedFullName);
	
	// ups, we already got this registered
	if (DataProvider.getInstance().getLookupTable().containsKey(getFieldData().getFullName()))
	{
		lastUpdatedFullName = null;
		((LinkManagerObject)getParent()).addInvalidLink(this);
	}
	// everything is OK
	else
	{
		lastUpdatedFullName = getFieldData().getFullName();
		DataProvider.getInstance().getLookupTable().put(lastUpdatedFullName, this);
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

/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 22:10:37)
 * @param g java.awt.Graphics
 * @param hilited boolean
 */
protected void draw(Graphics g, boolean hilited) {
	super.draw(g, hilited);

	if (lastUpdatedFullName==null)
	{
		ViewState view = ViewState.getInstance();
		int rrx = getRx()-view.getRx();
		int rry = getRy()-view.getRy();
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

}
