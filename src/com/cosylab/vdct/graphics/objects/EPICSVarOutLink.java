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
import com.cosylab.vdct.Constants;
import com.cosylab.vdct.DataProvider;

import javax.swing.*;
import java.awt.event.*;
import com.cosylab.vdct.vdb.*;
import com.cosylab.vdct.graphics.*;
import com.cosylab.vdct.inspector.*;
import com.cosylab.vdct.graphics.popup.*;

/**
 * Insert the type's description here.
 * Creation date: (29.1.2001 20:05:51)
 * @author Matej Sekoranja
 */
public class EPICSVarOutLink extends EPICSVarLink implements OutLink
{

	class PopupMenuHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			/*
			if (action.equals())
			{			
			}
			else if (action.equals(addConnectorString))
			{
				//addConnector();
				com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
			}
			else if (action.equals(removeLinkString))
			{
				removeLink();
				com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
			}
			*/
		}
	}
	
	protected InLink inlink = null;
	protected boolean disconnected = true;
	protected LinkProperties properties = null;
	private boolean hasEndpoint = false;

	private static final String addConnectorString = "Add connector";
	private static final String removeLinkString = "Remove Link";

	private static final String nullString = "";

/**
 * EPICSVarLink constructor comment.
 * @param parent com.cosylab.vdct.graphics.objects.ContainerObject
 * @param fieldData com.cosylab.vdct.vdb.VDBFieldData
 */
public EPICSVarOutLink(ContainerObject parent, com.cosylab.vdct.vdb.VDBFieldData fieldData) {
	super(parent, fieldData);
	properties = new LinkProperties(fieldData);
	//updateLink(); // this causes problems in applyVisualData (connectors are not completed)
}

/**
 * Insert the method's description here.
 * Creation date: (2.2.2001 23:00:51)
 * @return com.cosylab.vdct.graphics.objects.Connector.PopupMenuHandler
 */
private com.cosylab.vdct.graphics.objects.EPICSVarOutLink.PopupMenuHandler createPopupmenuHandler() {
	return new PopupMenuHandler();
}
	/**
	 * @see com.cosylab.vdct.graphics.objects.OutLink#getInput()
	 */
	public InLink getInput()
	{
		return inlink;
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.OutLink#getMode()
	 */
	public int getMode()
	{
		return OutLink.NORMAL_MODE;
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.OutLink#getOutX()
	 */
	public int getOutX()
	{
		return getInX();
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.OutLink#getOutY()
	 */
	public int getOutY()
	{
		return getInY();
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.OutLink#getQueueCount()
	 */
	public int getQueueCount()
	{
		return 0;
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.OutLink#setInput(InLink)
	 */
	public void setInput(InLink input)
	{
		if (inlink==input) return;
		if (inlink!=null)
			inlink.disconnect(this);
		inlink=input;
		if (inlink!=null) disconnected=false;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (29.1.2001 22:11:34)
	 */
	public void destroy() {
		if (!isDestroyed()) {
			super.destroy();
			EPICSLinkOut.destroyChain(inlink, this);
			setInput(null);
			getFieldData().setValue(nullString);
			properties = new LinkProperties(fieldData);
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (29.1.2001 21:23:04)
	 */
	public void disconnect(Linkable disconnector) {
		if (/*!disconnected &&*/ (outlinks.contains(disconnector) || disconnector==inlink)) {
			if (outlinks.contains(disconnector)) outlinks.removeElement(disconnector);
			
			if (inlink==disconnector) 
			{
				removeLink();
			}
			
			if (outlinks.size()==0 && inlink==null) {
				destroy();
			}
			else if (outlinks.size()==1)
				if (outlinks.firstElement() instanceof VisibleObject)
					setColor(((VisibleObject)outlinks.firstElement()).getColor());
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (29.1.2001 22:10:37)
	 * @param g java.awt.Graphics
	 * @param hilited boolean
	 */
	protected void draw(Graphics g, boolean hilited) {
		super.draw(g, hilited);
	
		if (inlink!=null) {
	
			Color c = getColor();
			if (c==Constants.BACKGROUND_COLOR)
				if (c==Color.black)
					c=Color.white;
				else
					c=Color.black;
			g.setColor(c);
	
			// draw missing tail
			if (outlinks.size()==0)
			{
				//!!!
			}
	
			LinkDrawer.drawLink(g, this, inlink, getQueueCount(), isRight());
		}
	
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.EPICSVarLink#validateLink()
	 */
	public void validateLink()
	{
		if (inlink!=null)
			updateLink();
			
		if (inlink==null && outlinks.size()==0)
			destroy();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1.2.2001 16:40:51)
	 */
	public void removeLink()
	{
		if (!isDestroyed()) {
			disconnected = true;
			EPICSLinkOut.destroyChain(inlink, this);
			setInput(null);
			getFieldData().setValue(nullString);
			properties = new LinkProperties(getFieldData());
		}
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (30.1.2001 12:25:44)
	 */
	private void updateLink() {
		LinkProperties newProperties = new LinkProperties(getFieldData());

		if (newProperties.getRecord()==null) {			// empty field
			removeLink();
			return;
		}
		
		else if (!newProperties.getTarget().equals(properties.getTarget()) ||
				 !hasEndpoint) {
			// find endpoint
			Linkable preendpoint = this;
			Linkable endpoint = getInput();
			while ((endpoint instanceof InLink) && (endpoint instanceof OutLink)) {
				preendpoint = endpoint;
				endpoint = ((OutLink)endpoint).getInput();
			}
			if ((endpoint!=null) && hasEndpoint) ((InLink)endpoint).disconnect(preendpoint);
			//OutLink lol = getTarget(properties).getOutput();
			InLink il = EPICSLinkOut.getTarget(newProperties, true);
			OutLink ol = (OutLink)preendpoint;
			ol.setInput(il);
			if (il!=null) { 
				il.setOutput(ol, null);
				hasEndpoint = true;
			}
			else hasEndpoint = false;
		}
	
		properties = newProperties;
	
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (30.1.2001 16:58:58)
	 * @return boolean
	 */
/*	public boolean isRight() {
		if (disconnected || inlink==null ||
			!inlink.getLayerID().equals(getLayerID())) 
			return true;
		else {
			if (inlink instanceof Connector) {	
				return (inlink.getInX()>(getX()+getWidth()/2));
			}
			else if (inlink instanceof VisibleObject) {			// do not cycle !!!
				VisibleObject obj = (VisibleObject)inlink;
				return ((obj.getX()+obj.getWidth()/2)>(getX()+getWidth()/2));
			}
			else 
				return true;
		}
	}
*/
}
