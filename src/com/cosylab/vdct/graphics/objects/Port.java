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

import java.awt.*;
import java.util.*;
import com.cosylab.vdct.Constants;
import com.cosylab.vdct.graphics.*;

import com.cosylab.vdct.graphics.popup.*;
import com.cosylab.vdct.inspector.Inspectable;
import com.cosylab.vdct.inspector.InspectableProperty;
import com.cosylab.vdct.vdb.GUIHeader;
import com.cosylab.vdct.vdb.GUISeparator;
import com.cosylab.vdct.vdb.LinkProperties;
import com.cosylab.vdct.vdb.NameValueInfoProperty;
import com.cosylab.vdct.vdb.PortDescriptionProperty;
import com.cosylab.vdct.vdb.VDBPort;
import com.cosylab.vdct.vdb.VDBTemplate;

import javax.swing.*;
import java.awt.event.*;

/**
 * Insert the type's description here.
 * Creation date: (29.1.2001 20:05:51)
 * @author Matej Sekoranja
 */
public class Port extends VisibleObject implements Descriptable, Movable, OutLink, Popupable, Selectable, Inspectable
{
	class PopupMenuHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			if (action.equals(colorString))
			{			
				Color newColor = ColorChooser.getColor(selectTitle, getColor());
				if (newColor!=null)
					setColor(newColor);
				com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
			}
			else if (action.equals(addConnectorString))
			{
				//addConnector();
				com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
			}
			else if (action.equals(removeString))
			{
				destroy();
			}
			
		}
	}
	
	protected InLink inlink = null;
	protected boolean disconnected = false;
	protected LinkProperties properties = null;
	private boolean hasEndpoint = false;

	private static final String descriptionString = "Description";
	private static final String selectTitle = "Select link color...";
	private static final String addConnectorString = "Add connector";
	private static final String colorString = "Color...";
	private static final String removeString = "Remove Port";

	private static final String nullString = "";

	private static javax.swing.ImageIcon icon = null;
	private static GUISeparator portSeparator = null;
	protected VDBPort data = null;

/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 17:22:29)
 */
public Port(VDBPort data, ContainerObject parent, int x, int y) {
	super(parent);
	this.data = data;
	
	setColor(Constants.FRAME_COLOR);
	
	setWidth(Constants.LINK_STUB_SIZE);
	setHeight(Constants.LINK_STUB_SIZE);

	setColor(Color.black);
	setX(x); setY(y);

	properties = new LinkProperties(data);
		
	data.setVisibleObject(this);
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 20:05:52)
 * @param visitor com.cosylab.vdct.graphics.objects.Visitor
 */
public void accept(Visitor visitor) {}

/**
 * Insert the method's description here.
 * Creation date: (25.12.2000 14:14:35)
 * @return boolean
 * @param dx int
 * @param dy int
 */
public boolean checkMove(int dx, int dy) {
	ViewState view = ViewState.getInstance();

	if ((getX()<-dx) || (getY()<-dy) || 
		(getX()>(view.getWidth()-getWidth()-dx)) || (getY()>(view.getHeight()-getHeight()-dy)))
		return false;
	else
		return true;
}
/**
 * Insert the method's description here.
 * Creation date: (2.2.2001 23:00:51)
 * @return com.cosylab.vdct.graphics.objects.Connector.PopupMenuHandler
 */
private com.cosylab.vdct.graphics.objects.Port.PopupMenuHandler createPopupmenuHandler() {
	return new PopupMenuHandler();
}
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 16:40:51)
 */
public void destroy() {
	if (!isDestroyed()) {
		super.destroy();
		disconnected = true;
		EPICSLinkOut.destroyChain(inlink, this);
		setInput(null);
		data.setValue(nullString);
		properties = new LinkProperties(data);
		//getParent().removeObject(getID());
	}
}
/**
 * ...
 * Creation date: (29.1.2001 20:05:51)
 */
public void disconnect(Linkable disconnector) {
	if (!disconnected && (inlink==disconnector) ) {
		disconnected = true;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 20:05:52)
 * @param g java.awt.Graphics
 * @param hilited boolean
 */
protected void draw(java.awt.Graphics g, boolean hilited) {

	ViewState view = ViewState.getInstance();

	int rrx = getRx() - view.getRx();
	int rry = getRy() - view.getRy();

	int rwidth = getRwidth();
	int rheight = getRheight();

	// clipping
	if (!((rrx > view.getViewWidth())
		|| (rry > view.getViewHeight())
		|| ((rrx + rwidth) < 0)
		|| ((rry + rheight) < 0))) {

		if (!hilited)
			g.setColor(Constants.RECORD_COLOR);
		else
			if (view.isPicked(this))
				g.setColor(Constants.PICK_COLOR);
			else
				if (view.isSelected(this) || view.isBlinking(this))
					g.setColor(Constants.SELECTION_COLOR);
				else
					g.setColor(Constants.RECORD_COLOR);

		g.fillRect(rrx, rry, rwidth, rheight);
		if (!hilited)
			g.setColor(Constants.FRAME_COLOR);
		else
			g.setColor(
				(this == view.getHilitedObject())
					? Constants.HILITE_COLOR
					: Constants.FRAME_COLOR);

		g.drawRect(rrx, rry, rwidth, rheight);


		int r = 5;//!!!
		int arrowLength = 2*r;
	boolean rightSide = isRight();

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
	/*
			if (font2!=null) {
				g.setFont(font2);
				rry += realHalfHeight;
				if (rightSide)
					rrx += (labelLen-realLabelLen)/2+arrowLength/2;
				else
					rrx += arrowLength-rtailLen+labelLen-realLabelLen;
				g.drawString(label2, rrx, rry);
			}
	*/		
			//if (inlink.getLayerID().equals(getLayerID())) 
	
			//g.setColor(getColor());
			Color c = getColor();
			if (c==Constants.BACKGROUND_COLOR)
				if (c==Color.black)
					c=Color.white;
				else
					c=Color.black;
			g.setColor(c);
	
			LinkDrawer.drawLink(g, this, inlink, getQueueCount(), rightSide);
		} else {
			// draw cross
			if (!rightSide) rrx+=arrowLength;
			g.drawLine(rrx-r, rry-r, rrx+r, rry+r);
			g.drawLine(rrx+r, rry-r, rrx-r, rry+r);
		}



	}
		
}
/**
 * Insert the method's description here.
 * Creation date: (24.4.2001 18:04:05)
 * @return java.lang.String
 */
public java.lang.String getDescription() {
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 16:43:08)
 * @return java.lang.String
 */
public java.lang.String getHashID() {
	return getID();
}
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 17:31:26)
 * @return java.lang.String
 */
public java.lang.String getID() {
	return data.getName();
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 20:05:52)
 * @return com.cosylab.vdct.graphics.objects.InLink
 */
public InLink getInput() {
	return inlink;
}
/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 11:23:59)
 * @return java.util.Vector
 */
public java.util.Vector getItems() {
	Vector items = new Vector();

	ActionListener al = createPopupmenuHandler();

	JMenuItem colorItem = new JMenuItem(colorString);
	colorItem.addActionListener(al);
	items.addElement(colorItem);

	JMenuItem addItem = new JMenuItem(addConnectorString);
	addItem.setEnabled(!isDisconnected());
	addItem.addActionListener(al);
	items.addElement(addItem);

	items.add(new JSeparator());

	JMenuItem descItem = new JMenuItem(descriptionString);
	descItem.setEnabled(false); //!!!
	descItem.addActionListener(al);
	items.addElement(descItem);
	
	return items;
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 20:05:51)
 * @return java.lang.String
 */
public String getLayerID() {
	return getParent().toString();
}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 16:58:58)
 * @return boolean
 */
public boolean isRight() {
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

/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 22:22:13)
 * @return int
 */
public int getOutX() {
	if (isRight())
		return getX()+getWidth()+Constants.TAIL_LENGTH;
	else 
		return getX()-Constants.TAIL_LENGTH;
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 22:22:13)
 * @return int
 */
public int getOutY() {
	return getY()+getHeight()/2;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 14:50:26)
 * @return int
 */
public int getQueueCount() {
		return 0;
}
/**
 * Insert the method's description here.
 * Creation date: (25.4.2001 22:13:55)
 * @return int
 */
public int getX() {
	int posX = super.getX();
	if (com.cosylab.vdct.Settings.getInstance().getSnapToGrid())
		return posX - posX % Constants.GRID_SIZE;
	else
		return posX;
}
/**
 * Insert the method's description here.
 * Creation date: (25.4.2001 22:13:55)
 * @return int
 */
public int getY() {
	int posY = super.getY();
	if (com.cosylab.vdct.Settings.getInstance().getSnapToGrid())
		return posY - posY % Constants.GRID_SIZE;
	else
		return posY;
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 20:05:52)
 * @return boolean
 */
public boolean isConnectable() {
	return !disconnected;
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 20:05:51)
 * @return boolean
 */
public boolean isDisconnected() {
	return disconnected;
}
/**
 * Insert the method's description here.
 * Creation date: (25.12.2000 14:14:35)
 * @return boolean
 * @param dx int
 * @param dy int
 */
public boolean move(int dx, int dy) {
	if (checkMove(dx, dy)) {
		setX(super.getX()+dx);
		setY(super.getY()+dy);
		revalidatePosition();
		return true;
	}
	else 
		return false;
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 20:05:52)
 */
public void revalidatePosition() {
  setRx((int)(getX()*getRscale()));
  setRy((int)(getY()*getRscale()));
}
/**
 * Insert the method's description here.
 * Creation date: (24.4.2001 18:04:05)
 * @param description java.lang.String
 */
public void setDescription(java.lang.String description) {}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 20:05:52)
 */
public void setInput(InLink input) {
	if (inlink==input) return;
	if (inlink!=null)
		inlink.disconnect(this);
	inlink=input;
	if (inlink!=null) disconnected=false;
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 20:05:51)
 * @param id java.lang.String
 */
public void setLayerID(String id) {
	// not needed, dynamicaly retrieved via parent
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 20:05:52)
 */
protected void validate() {
  revalidatePosition();
	
  setRwidth((int)(getWidth()*getRscale()));
  setRheight((int)(getHeight()*getRscale()));
}

/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 12:46:30)
 * @param newColor java.awt.Color
 */
public void setColor(Color newColor) {
		super.setColor(newColor);
		Linkable link = this;
		while (link instanceof OutLink) {
			link = ((OutLink)link).getInput();
			if (link instanceof VisibleObject) 
				((VisibleObject)link).setColor(newColor);
		}
		if (link instanceof VisibleObject) 
			((VisibleObject)link).setColor(newColor);
}

/**
 * @see com.cosylab.vdct.graphics.objects.OutLink#getMode()
 */
public int getMode()
{
	return NORMAL_MODE;
}

/**
 * @see com.cosylab.vdct.inspector.Inspectable#getCommentProperty()
 */
public InspectableProperty getCommentProperty()
{
	return null;
}

/**
 * @see com.cosylab.vdct.inspector.Inspectable#getIcon()
 */
public Icon getIcon()
{
	if (icon==null)
		icon = new javax.swing.ImageIcon(getClass().getResource("/images/port.gif"));
	return icon;
}

/**
 * @see com.cosylab.vdct.inspector.Inspectable#getModeNames()
 */
public ArrayList getModeNames()
{
	return null;
}

/**
 * @see com.cosylab.vdct.inspector.Inspectable#getName()
 */
public String getName()
{
	return getID();
}

/**
 * @see java.lang.String#toString()
 */
public String toString()
{
	return getID();
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
 * @see com.cosylab.vdct.inspector.Inspectable#getProperties(int)
 */
public InspectableProperty[] getProperties(int mode)
{
	InspectableProperty[] properties = new InspectableProperty[4];

	properties[0]=GUIHeader.getDefaultHeader();
	properties[1]=getPortSeparator();
	properties[2]=data;
	properties[3]=new PortDescriptionProperty(data);

	return properties;
}



/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 12:25:44)
 */
private void updateLink() {
	LinkProperties newProperties = new LinkProperties(data);

	if (newProperties.getRecord()==null) {			// empty field
		destroy();
		return;
	}
	else if (!newProperties.getRecord().equals(properties.getRecord()) ||
			 !newProperties.getVarName().equals(properties.getVarName()) ||
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
		InLink il = EPICSLinkOut.getTarget(newProperties);
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
 * Creation date: (30.1.2001 12:25:44)
 */
public void valueChanged()
{
	updateLink();

	unconditionalValidation();
	com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
}

}
