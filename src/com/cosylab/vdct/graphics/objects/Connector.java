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
import javax.swing.*;
import java.awt.event.*;

/**
 * Insert the type's description here.
 * Creation date: (29.1.2001 20:05:51)
 * @author Matej Sekoranja
 */
public class Connector extends VisibleObject implements Descriptable, InLink, Movable, OutLink, Popupable {

	class PopupMenuHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			if (action.equals(descriptionString)) {
				setDescription();
				com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
			}
			else if (action.equals(addConnectorString)) {
				addConnector();
				com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
			}
			else if (action.equals(removeConnectorString)) {
				bypass();
				com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
			}
			
		}
	}
	protected InLink inlink = null;
	protected OutLink outlink = null;
	protected String ID;
	protected boolean disconnected = false;
	private static final String descriptionString = "Description";
	private static final String addConnectorString = "Add connector";
	private static final String removeConnectorString = "Remove connector";
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 17:22:29)
 * @param parent com.cosylab.vdct.graphics.objects.Record
 * @param outlink com.cosylab.vdct.graphics.objects.OutLink
 * @param inlink com.cosylab.vdct.graphics.objects.InLink
 */
public Connector(String id, Record parent, OutLink outlink, InLink inlink) {
	super(parent);
	setID(id);

	if (outlink instanceof VisibleObject)
		setColor(((VisibleObject)outlink).getColor());
	else
		setColor(Constants.FRAME_COLOR);
	
	if (inlink!=null) inlink.setOutput(this, outlink);
	if (outlink!=null) outlink.setInput(this);
	setInput(inlink); 
	setOutput(outlink, null);
	setWidth(Constants.CONNECTOR_WIDTH);
	setHeight(Constants.CONNECTOR_HEIGHT);

	/// !!! better initial layout
	if ((inlink==null) && (outlink==null)) {
		setX(parent.getX()-(3*parent.getWidth())/2);
		setY(parent.getY()+parent.getHeight()/2);
	}
	else if (inlink==null) {
		setX(outlink.getOutX()-(3*getWidth())+2);
		setY(outlink.getOutY()+(3*getHeight())+2);
	}
	else if (outlink==null) {
		setX(inlink.getInX()-(3*getWidth())+2);
		setY(inlink.getInY()+(3*getHeight())+2);
	}
	else if (!inlink.getLayerID().equals(outlink.getLayerID()))
	{
		setX(parent.getX()-(3*parent.getWidth())/2);
		setY(parent.getY()+parent.getHeight()/2);
	}
	else {
		setX((inlink.getInX()+outlink.getOutX())/2);
		setY((inlink.getInY()+outlink.getOutY())/2);
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 20:05:52)
 * @param visitor com.cosylab.vdct.graphics.objects.Visitor
 */
public void accept(Visitor visitor) {}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 12:50:51)
 */
public Connector addConnector() {
	EPICSLinkOut start = (EPICSLinkOut)EPICSLinkOut.getStartPoint(this);
	if (start==null) return null;
	String id = EPICSLinkOut.generateConnectorID(start);
	Connector connector = new Connector(id, (Record)getParent(), this, getInput());
	getParent().addSubObject(id, connector);
	return connector;
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 12:35:05)
 */
public void bypass() {
	InLink il = inlink;
	if (inlink!=null) inlink.setOutput(outlink, this);
	if (outlink!=null) outlink.setInput(il);
	destroy();
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 20:05:51)
 * @return boolean
 * @param dx int
 * @param dy int
 */
public boolean checkMove(int dx, int dy) {
	ViewState view = ViewState.getInstance();

	if ((getX()<-dx) || (getY()<-dy) || 
		(getX()>(view.getWidth()-getWidth()-dx)) || (getY()>(view.getHeight()-getHeight()-dy)) /*||
		// y must be between in/out !!!
		(((y+dy)-getOutY())*((y+dy)-getInY())>0)*/
		)
		return false;
	else
		return true;
}
/**
 * Insert the method's description here.
 * Creation date: (2.2.2001 23:00:51)
 * @return com.cosylab.vdct.graphics.objects.Connector.PopupMenuHandler
 */
private com.cosylab.vdct.graphics.objects.Connector.PopupMenuHandler createPopupmenuHandler() {
	return new PopupMenuHandler();
}
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 16:40:51)
 */
public void destroy() {
	if (!isDestroyed()) {
		super.destroy();
		getParent().removeObject(ID);
		setInput(null);
		setOutput(null, outlink);
	}
}
/**
 * This method destroys connector (connector should not be used afer this method was called)
 * Creation date: (29.1.2001 20:05:51)
 */
public void disconnect(Linkable disconnector) {
	if (!disconnected && ((inlink==disconnector) || (outlink==disconnector))) {
		if (disconnector==inlink) inlink=null; //setInput(null);
		if (disconnector==outlink) outlink=null; //setOutput(null, outlink);
		if (((inlink==null) || inlink.isDisconnected()) &&
			((outlink==null) || outlink.isDisconnected()))
				destroy();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 20:05:52)
 * @param g java.awt.Graphics
 * @param hilited boolean
 */
protected void draw(java.awt.Graphics g, boolean hilited) {
	if (disconnected) return;
	
	ViewState view = ViewState.getInstance();

	int rrx = getRx()-view.getRx();
	int rry = getRy()-view.getRy();
	int rwidth = getRwidth();
	int rheight = getRheight();
		
	// clipping
	if ((!(rrx>view.getViewWidth()) || (rry>view.getViewHeight())
	    || ((rrx+rwidth)<0) || ((rry+rheight)<0))) {

		// fill or not to fill ?!!

		if (!hilited) g.setColor(getColor());
		else g.setColor((this==view.getHilitedObject()) ? 
						Constants.HILITE_COLOR : getColor());

		if (inlink!=null) 
			g.drawRect(rrx, rry, rwidth, rheight);
		else {
			g.drawLine(rrx, rry, rrx+rwidth, rry+rheight);
			g.drawLine(rrx+rwidth, rry, rrx, rry+rheight);
		}
	
	}

	if (!hilited && inlink!=null) {
		g.setColor(getColor());
		LinkDrawer.drawLink(g, this, inlink, getQueueCount(), 
							getOutX()<inlink.getInX());
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
	return ID;
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
 * Creation date: (29.1.2001 20:05:51)
 * @return int
 */
public int getInX() {
	return getX()+getWidth()/2;
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 20:05:51)
 * @return int
 */
public int getInY() {
	return getY()+getHeight()/2;
}
/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 11:23:59)
 * @return java.util.Vector
 */
public java.util.Vector getItems() {
	Vector items = new Vector();

	JMenuItem descItem = new JMenuItem(descriptionString);
	descItem.addActionListener(createPopupmenuHandler());
	items.addElement(descItem);

	JMenuItem addItem = new JMenuItem(addConnectorString);
	addItem.addActionListener(createPopupmenuHandler());
	items.addElement(addItem);
	
	JMenuItem removeItem = new JMenuItem(removeConnectorString);
	removeItem.addActionListener(createPopupmenuHandler());
	items.addElement(removeItem);
	
	return items;
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 20:05:51)
 * @return java.lang.String
 */
public String getLayerID() {
	return getParent().getParent().toString();
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 20:05:51)
 * @return com.cosylab.vdct.graphics.objects.OutLink
 */
public OutLink getOutput() {
	return outlink;
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 20:05:52)
 * @return int
 */
public int getOutX() {
	return getX()+getWidth()/2;
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 20:05:52)
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
	if (!disconnected && (outlink!=null))
		return outlink.getQueueCount()+1;
	else
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
		return posX - posX % Constants.GRID_SIZE - Constants.CONNECTOR_WIDTH / 2;
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
		return posY - posY % Constants.GRID_SIZE - Constants.CONNECTOR_HEIGHT / 2;
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
 * Creation date: (29.1.2001 20:05:51)
 * @return boolean
 * @param dx int
 * @param dy int
 */
public boolean move(int dx, int dy) {
	if (checkMove(dx, dy)) {
		setX(super.getX()+dx); 
		/*
		if ((outlink!=null) && (inlink!=null))
			if (((y+dy)-getOutY())*((y+dy)-getInY())>0) {	// not in between
				// fix !!!?
			}
			else
				y+=dy;
		else*/
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
 * Creation date: (4.2.2001 12:53:08)
 */
public void setDescription() {}
/**
 * Insert the method's description here.
 * Creation date: (24.4.2001 18:04:05)
 * @param description java.lang.String
 */
public void setDescription(java.lang.String description) {}
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 17:31:26)
 * @param newID java.lang.String
 */
public void setID(java.lang.String newID) {
	ID = newID;
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 20:05:52)
 */
public void setInput(InLink input) {
	if (inlink==input) return;
	if (inlink!=null) inlink.disconnect(this);
	inlink=input;
	if ((inlink!=null) && (outlink!=null)) disconnected=false;
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
 * Creation date: (29.1.2001 20:05:51)
 * @param output com.cosylab.vdct.graphics.objects.OutLink
 * @param prevOutput com.cosylab.vdct.graphics.objects.OutLink
 */
public void setOutput(OutLink output, OutLink prevOutput) {
	if (outlink==output) return;
	if (outlink!=null) outlink.disconnect(this);
	outlink=output;
	if ((inlink!=null) && (outlink!=null)) disconnected=false;

	if (outlink instanceof VisibleObject)
		setColor(((VisibleObject)outlink).getColor());
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
}
