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

import java.util.*;
import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import com.cosylab.vdct.Constants;

/**
 * Insert the type's description here.
 * Creation date: (29.1.2001 21:26:07)
 * @author Matej Sekoranja 
 */
public class EPICSOutLink extends EPICSLinkOut {

	protected static final String processString = "Process";
	protected static final String nppString = "NPP - No Process Passive";
	protected static final String ppString ="PP - Process Passive";
	protected static final String caString = "CA - Channel Access";

	private static final String severityString = "Severity";
	private static final String nmsString = "NMS - No Maximize Severity";
	private static final String msString = "MS - Maximize Severity";

	class PopupMenuHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			if (action.equals(nppString))
			{
				EPICSOutLink.this.getLinkProperties().setProcess("NPP");
				EPICSOutLink.this.getFieldData().setValue(EPICSOutLink.this.getLinkProperties().getCompactLinkDef());
			}
			else if (action.equals(ppString))
			{			
				EPICSOutLink.this.getLinkProperties().setProcess("PP");
				EPICSOutLink.this.getFieldData().setValue(EPICSOutLink.this.getLinkProperties().getCompactLinkDef());
			}
			else if (action.equals(caString))
			{			
				EPICSOutLink.this.getLinkProperties().setProcess("CA");
				EPICSOutLink.this.getFieldData().setValue(EPICSOutLink.this.getLinkProperties().getCompactLinkDef());
			}
			else if (action.equals(nmsString))
			{			
				EPICSOutLink.this.getLinkProperties().setMaximize("NMS");
				EPICSOutLink.this.getFieldData().setValue(EPICSOutLink.this.getLinkProperties().getCompactLinkDef());
			}
			else if (action.equals(msString))
			{			
				EPICSOutLink.this.getLinkProperties().setMaximize("MS");
				EPICSOutLink.this.getFieldData().setValue(EPICSOutLink.this.getLinkProperties().getCompactLinkDef());
			}
		}
	}
	
/**
 * EPICSOutLink constructor comment.
 * @param parent com.cosylab.vdct.graphics.objects.ContainerObject
 * @param fieldData com.cosylab.vdct.vdb.VDBFieldData
 */
public EPICSOutLink(ContainerObject parent, com.cosylab.vdct.vdb.VDBFieldData fieldData) {
	super(parent, fieldData);
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

	boolean rightSide = isRight();
	int arrowLength = 2*r;
	
	int rrx;
	if (rightSide)
		rrx = getRx()+getRwidth()-view.getRx();
	else 
		rrx = getRx()-view.getRx()-arrowLength;

	//int rry = getRy()+getRheight()/2-view.getRy();
	int rry = (int)(getRscale()*getOutY()- view.getRy());
	
	if (!hilited) g.setColor(Constants.FRAME_COLOR);
	else g.setColor((this==view.getHilitedObject()) ? 
					Constants.HILITE_COLOR : Constants.FRAME_COLOR);

	if (inlink!=null) {

		// draw arrow
		g.drawLine(rrx, rry-r, rrx+arrowLength, rry-r);
		g.drawLine(rrx, rry+r, rrx+arrowLength, rry+r);
		
		int dr=-r; 
		if (rightSide) {
			dr=-dr;
			rrx+=arrowLength;
		}
		g.drawLine(rrx, rry-r, rrx+dr, rry);
		g.drawLine(rrx, rry+r, rrx+dr, rry);

		if (font2!=null) {
			g.setFont(font2);
			rry += realHalfHeight;
			if (rightSide)
				rrx += (labelLen-realLabelLen)/2+arrowLength/2;
			else
				rrx += arrowLength-rtailLen+labelLen-realLabelLen;
			g.drawString(label2, rrx, rry);
		}
		
		//if (inlink.getLayerID().equals(getLayerID())) 

		//g.setColor(getColor());
		Color c = getColor();
		if (c==Constants.BACKGROUND_COLOR)
			if (c==Color.black)
				c=Color.white;
			else
				c=Color.white;
		g.setColor(c);

		LinkDrawer.drawLink(g, this, inlink, getQueueCount(), rightSide);
	} else {
		// draw cross
		if (!rightSide) rrx+=arrowLength;
		g.drawLine(rrx-r, rry-r, rrx+r, rry+r);
		g.drawLine(rrx+r, rry-r, rrx-r, rry+r);
	}

}

/**
 * Insert the method's description here.
 * Creation date: (2.2.2001 23:00:51)
 * @return com.cosylab.vdct.graphics.objects.EPICSOutnLink.PopupMenuHandler
 */
private com.cosylab.vdct.graphics.objects.EPICSOutLink.PopupMenuHandler createPopupmenuHandler() {
	return new PopupMenuHandler();
}

/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 11:23:59)
 * @return java.util.Vector
 */
public java.util.Vector getItems() {
	Vector items = super.getItems();

	ActionListener al = createPopupmenuHandler();

	items.add(new JSeparator());

	JMenu processMenu = new JMenu(processString);
	items.addElement(processMenu);
	
	JMenuItem npp = new JMenuItem(nppString);
	npp.addActionListener(al);
	processMenu.add(npp);	
	
	JMenuItem pp = new JMenuItem(ppString);
	pp.addActionListener(al);
	processMenu.add(pp);	

	JMenuItem ca = new JMenuItem(caString);
	ca.addActionListener(al);
	processMenu.add(ca);

	JMenu severityMenu = new JMenu(severityString);
	items.addElement(severityMenu);

	JMenuItem nms = new JMenuItem(nmsString);
	nms.addActionListener(al);
	severityMenu.add(nms);

	JMenuItem ms = new JMenuItem(msString);
	ms.addActionListener(al);
	severityMenu.add(ms);

	return items;
}


}
