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
import com.cosylab.vdct.vdb.MacroDescriptionProperty;
import com.cosylab.vdct.vdb.VDBMacro;

import javax.swing.*;

import java.awt.event.*;

/**
 * Insert the type's description here.
 * Creation date: (29.1.2001 20:05:51)
 * @author Matej Sekoranja
 */
public class Macro extends VisibleObject implements Descriptable, Movable, InLink, Popupable, Selectable, Inspectable
{
	class PopupMenuHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			/*
			if (action.equals(colorString))
			{			
				Color newColor = ColorChooser.getColor(selectTitle, getColor());
				if (newColor!=null)
					setColor(newColor);
				com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
			}
			else*/ if (action.equals(addConnectorString))
			{
				//addConnector();
				com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
			}
			/*
			else if (action.equals(removeLinkString))
			{
				removeLink();
				com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
			}
			*/
			else if (action.equals(removeMacroString))
			{
				//destroy();
				//com.cosylab.vdct.undo.UndoManager.getInstance().addAction(new com.cosylab.vdct.undo.DeleteAction(Macro.this));
				data.getTemplate().removeMacro(getName());
				com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
			}
			else if (action.equals(inputString)) {
				setMode(InLink.INPUT_MACRO_MODE);
				com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
			}
			else if (action.equals(outputString)) {
				setMode(InLink.OUTPUT_MACRO_MODE);
				com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
			}
			
		}
	}
	
	protected boolean disconnected = true;

	private static final String descriptionString = "Description";
	private static final String selectTitle = "Select link color...";
	private static final String addConnectorString = "Add connector";
	private static final String colorString = "Color...";
	//private static final String removeLinkString = "Remove Link";
	private static final String removeMacroString = "Remove Macro";

	private static final String modeString = "Macro Mode";
	private static final String inputString = "INPUT";
	private static final String outputString = "OUTPUT";

	private static final String nullString = "";

	private int mode = InLink.INPUT_MACRO_MODE;

	protected Vector outlinks;
	private static javax.swing.ImageIcon icon = null;
	private static GUISeparator macroSeparator = null;
	protected VDBMacro data = null;
	
	protected int rightXtranslation = 0;
	protected int rightYtranslation = 0;
	protected int leftXtranslation = 0;
	protected int leftYtranslation = 0;
	protected Polygon leftPoly;
	protected Polygon rightPoly;

	private int r = 0;

 	private String lastUpdatedFullName = null;
	
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 17:22:29)
 */
public Macro(VDBMacro data, ContainerObject parent, int x, int y) {
	super(parent);
	this.data = data;

	setColor(Constants.FRAME_COLOR);
	
	setWidth(Constants.LINK_STUB_SIZE);
	setHeight(Constants.LINK_STUB_SIZE);

	setX(x); setY(y);
	
	data.setVisibleObject(this);
	
	// initialize polygon so that it contains 5 points
	int[] pts = new int[5];
	leftPoly = new Polygon(pts, pts, 5);
	rightPoly = new Polygon(pts, pts, 5);

	outlinks = new Vector();
	
	updateTemplateLink();
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
private com.cosylab.vdct.graphics.objects.Macro.PopupMenuHandler createPopupmenuHandler() {
	return new PopupMenuHandler();
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 21:59:34)
 */
public void destroy() {
	if (!isDestroyed()) {
		super.destroy();
		if (outlinks.size()>0) {
			Object[] objs = new Object[outlinks.size()];
			outlinks.copyInto(objs);
			for(int i=0; i<objs.length; i++) {
				OutLink outlink = (OutLink)objs[i];
				OutLink start = EPICSLinkOut.getStartPoint(outlink);
				if((start instanceof EPICSLinkOut))
					((EPICSLinkOut)start).sourceDestroyed();
				else if (start!=null)
					start.disconnect(this);
				else 
					outlink.disconnect(this);
			}
			outlinks.clear();
		}

		if (lastUpdatedFullName!=null)
			Group.getRoot().getLookupTable().remove(data.getFullName());
		//else
		//	((LinkManagerObject)getParent()).removeInvalidLink(this);
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 21:23:04)
 */
public void disconnect(Linkable disconnector) {
	if (!disconnected && outlinks.contains(disconnector)) {
		outlinks.removeElement(disconnector);
		if (outlinks.size()==0) {
			destroy();
		}
		else if (outlinks.size()==1)
			if (outlinks.firstElement() instanceof VisibleObject)
				setColor(((VisibleObject)outlinks.firstElement()).getColor());
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

	boolean rightSide = isRight();

	// clipping
	if (!((rrx > view.getViewWidth())
		|| (rry > view.getViewHeight())
		|| ((rrx + rwidth) < 0)
		|| ((rry + rheight) < 0))) {

	
		Polygon poly = null;
		if (rightSide)
		{
			poly = rightPoly;
			poly.translate(rrx-rightXtranslation, rry-rightYtranslation);
			rightXtranslation = rrx; rightYtranslation = rry;
		}
		else
		{
			poly = leftPoly;
			poly.translate(rrx-leftXtranslation, rry-leftYtranslation);
			leftXtranslation = rrx; leftYtranslation = rry;
		}
		
	
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
	
		g.fillPolygon(poly);

		Color drawColor;
		if (!hilited)
			drawColor = Constants.FRAME_COLOR;
		else
			drawColor =
				(this == view.getHilitedObject())
					? Constants.HILITE_COLOR
					: Constants.FRAME_COLOR;

		g.setColor(drawColor);
		g.drawPolygon(poly);
		
		g.drawOval(rrx-r, rry+(rheight-r)/2, r, r);

		if (getFont()!=null) {
			//g.setColor(drawColor);
			g.setFont(getFont());
			g.drawString(getLabel(), rrx+getRlabelX(), rry+getRlabelY());
		}
	}

	if (false)///!!!
	{
		
		// draw link
		Color c = getColor();
		if (c==Constants.BACKGROUND_COLOR)
			if (c==Color.black)
				c=Color.white;
			else
				c=Color.black;
		g.setColor(c);

		//LinkDrawer.drawLink(g, this, inlink, getQueueCount(), rightSide);
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
 * Creation date: (3.2.2001 11:23:59)
 * @return java.util.Vector
 */
public java.util.Vector getItems() {
	Vector items = new Vector();

	ActionListener al = createPopupmenuHandler();
/*
	JMenuItem colorItem = new JMenuItem(colorString);
	colorItem.addActionListener(al);
	items.addElement(colorItem);
*/
	// no connectors for macros yet
	/*
	JMenuItem addItem = new JMenuItem(addConnectorString);
	addItem.setEnabled(!isDisconnected());
	addItem.addActionListener(al);
	items.addElement(addItem);
	*/
	
	// modes
	items.addElement(new JSeparator());
	
	JMenu modeMenu = new JMenu(modeString);
	items.addElement(modeMenu);

	JRadioButtonMenuItem inputModeItem = new JRadioButtonMenuItem(inputString, getMode()==InLink.INPUT_MACRO_MODE);
	inputModeItem.setEnabled(getMode()!=InLink.INPUT_MACRO_MODE);
	inputModeItem.addActionListener(al);
	modeMenu.add(inputModeItem);

	JRadioButtonMenuItem outputModeItem = new JRadioButtonMenuItem(outputString, getMode()==InLink.OUTPUT_MACRO_MODE);
	outputModeItem.setEnabled(getMode()!=InLink.OUTPUT_MACRO_MODE);
	outputModeItem.addActionListener(al);
	modeMenu.add(outputModeItem);


	/*
	items.add(new JSeparator());

	JMenuItem descItem = new JMenuItem(descriptionString);
	descItem.setEnabled(false); 
	descItem.addActionListener(al);
	items.addElement(descItem);
	*/
	
	/*	
	items.add(new JSeparator());

	JMenuItem removeMacroItem = new JMenuItem(removeMacroString);
	removeMacroItem.addActionListener(al);
	items.addElement(removeMacroItem);
	*/
	
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
 * Creation date: (29.1.2001 22:22:13)
 * @return int
 */
public int getInX() {
	// ??? what is nicer
	//boolean right = !isRight();
	
	boolean right = isRight();

	if (right)
		return getX()-r;
	else 
		return getX()+getWidth()+r;
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 22:22:13)
 * @return int
 */
public int getInY() {
	return getY()+getHeight()/2;
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

  leftXtranslation = 0; leftYtranslation = 0;
  rightXtranslation = 0; rightYtranslation = 0;
  
  int rwidth = 0;
  int rheight = 0;
  
  if (getMode() == InLink.OUTPUT_MACRO_MODE)
  {
	  setWidth(Constants.LINK_STUB_SIZE);
	  setHeight(Constants.LINK_STUB_SIZE);

	  // to make it nice, do /2)*2
	  rwidth = (int)(getWidth()*getRscale()/2)*2;
	  rheight = (int)(getHeight()*getRscale()/2)*2;
	  
	  setRwidth(rwidth);
	  setRheight(rheight);

	  // left poly
	  leftPoly.xpoints[0]=0;
	  leftPoly.xpoints[1]=rwidth/2;
	  leftPoly.xpoints[2]=rwidth;
	  leftPoly.xpoints[3]=leftPoly.xpoints[2];
	  leftPoly.xpoints[4]=leftPoly.xpoints[1];
	
	  leftPoly.ypoints[0]=rheight/2;
	  leftPoly.ypoints[1]=rheight;
	  leftPoly.ypoints[2]=leftPoly.ypoints[1];
	  leftPoly.ypoints[3]=0;
	  leftPoly.ypoints[4]=leftPoly.ypoints[3];

	
	  // right poly
	  rightPoly.xpoints[0]=0;
	  rightPoly.xpoints[1]=rightPoly.xpoints[0];
	  rightPoly.xpoints[2]=rwidth/2;
	  rightPoly.xpoints[3]=rwidth;
	  rightPoly.xpoints[4]=rightPoly.xpoints[2];
	
	  rightPoly.ypoints[0]=0;
	  rightPoly.ypoints[1]=rheight;
	  rightPoly.ypoints[2]=rightPoly.ypoints[1];
	  rightPoly.ypoints[3]=rheight/2;
	  rightPoly.ypoints[4]=rightPoly.ypoints[0];
  }
  else if (getMode() == InLink.INPUT_MACRO_MODE)
  {
	  setWidth(Constants.LINK_STUB_SIZE);
	  setHeight(Constants.LINK_STUB_SIZE);

	  // to make it nice, do /2)*2
	  rwidth = (int)(getWidth()*getRscale()/2)*2;
	  rheight = (int)(getHeight()*getRscale()/2)*2;
	  
	  setRwidth(rwidth);
	  setRheight(rheight);

	  // left poly
	  leftPoly.xpoints[0]=rwidth/2;
	  leftPoly.xpoints[1]=0;
	  leftPoly.xpoints[2]=rwidth;
	  leftPoly.xpoints[3]=leftPoly.xpoints[2];
	  leftPoly.xpoints[4]=leftPoly.xpoints[1];
	
	  leftPoly.ypoints[0]=rheight/2;
	  leftPoly.ypoints[1]=rheight;
	  leftPoly.ypoints[2]=leftPoly.ypoints[1];
	  leftPoly.ypoints[3]=0;
	  leftPoly.ypoints[4]=leftPoly.ypoints[3];

	
	  // right poly
	  rightPoly.xpoints[0]=0;
	  rightPoly.xpoints[1]=rightPoly.xpoints[0];
	  rightPoly.xpoints[2]=rwidth;
	  rightPoly.xpoints[3]=rwidth/2;
	  rightPoly.xpoints[4]=rightPoly.xpoints[2];
	
	  rightPoly.ypoints[0]=0;
	  rightPoly.ypoints[1]=rheight;
	  rightPoly.ypoints[2]=rightPoly.ypoints[1];
	  rightPoly.ypoints[3]=rheight/2;
	  rightPoly.ypoints[4]=rightPoly.ypoints[0];
  }

  setLabel(getName());

	///!!! optimize static

  Font font = FontMetricsBuffer.getInstance().getAppropriateFont(
	  			Constants.DEFAULT_FONT, Font.PLAIN, 
	  			getLabel(), rwidth*4, rheight);

  if (font!=null)
  {
	  FontMetrics fm = FontMetricsBuffer.getInstance().getFontMetrics(font);
	  setRlabelX(rwidth/2-fm.stringWidth(getLabel())/2);
 	  setRlabelY(-fm.getHeight()+fm.getAscent());
  }
  
  setFont(font);

  r = (int)(getRscale()*Constants.LINK_RADIOUS*2);

}

/**
 * @see com.cosylab.vdct.graphics.objects.OutLink#getMode()
 */
public int getMode()
{
	return mode;
}

/**
 */
public void setMode(int mode)
{
	this.mode = mode;
	
	// reload symbol
	forceValidation();
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
		icon = new javax.swing.ImageIcon(getClass().getResource("/images/macro.gif"));
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
public static com.cosylab.vdct.vdb.GUISeparator getMacroSeparator() {
	if (macroSeparator==null) macroSeparator = new GUISeparator("Macro");
	return macroSeparator;
}

/**
 * @see com.cosylab.vdct.inspector.Inspectable#getProperties(int)
 */
public InspectableProperty[] getProperties(int mode)
{
	InspectableProperty[] properties = new InspectableProperty[4];

	properties[0]=GUIHeader.getDefaultHeader();
	properties[1]=getMacroSeparator();
	properties[2]=data;
	properties[3]=new MacroDescriptionProperty(data);

	return properties;
}



/**
 * @see com.cosylab.vdct.graphics.objects.VisibleObject#setDestroyed(boolean)
 */
public void setDestroyed(boolean newDestroyed)
{
	super.setDestroyed(newDestroyed);
	
	if (!newDestroyed)
	{
		// set data appropriate visibleObject
		data.setVisibleObject(this);
	}
}

/**
 */
public void rename(String oldName, String newName)
{
	getParent().removeObject(oldName);
	getParent().addSubObject(newName, this);

	unconditionalValidation();
	com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
}

/**
 * Returns the data.
 * @return VDBMacro
 */
public VDBMacro getData()
{
	return data;
}

/**
 * Insert the method's description here.
 * Creation date: (5.2.2001 12:10:18)
 * @return java.util.Vector
 */
public Vector getStartPoints() {
	OutLink out;
	Vector starts = new Vector();
	Enumeration e = outlinks.elements();
	while (e.hasMoreElements()) {
		out = EPICSLinkOut.getStartPoint((Linkable)e.nextElement());
		if (out!=null) starts.addElement(out);
	}
	return starts;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 16:58:58)
 * @return boolean
 */
public boolean isRight() {
	if (disconnected || outlinks.size()!=1)
		return true;
	else {
		OutLink first = (OutLink)outlinks.firstElement();
		if (first.getLayerID().equals(getLayerID()))
			return (first.getOutX()>(getX()+getWidth()/2));
		else
			return true;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 21:34:27)
 * @param output com.cosylab.vdct.graphics.objects.OutLink
 * @param prevOutput com.cosylab.vdct.graphics.objects.OutLink
 */
public void setOutput(OutLink output, OutLink prevOutput) {
	if (prevOutput!=null) outlinks.removeElement(prevOutput);
	if (!outlinks.contains(output)) {
		outlinks.addElement(output);
		if (outlinks.size()>0) disconnected=false;
	}

	if (outlinks.firstElement() instanceof VisibleObject)
		setColor(((VisibleObject)outlinks.firstElement()).getColor());


}

/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 21:34:27)
 * @return com.cosylab.vdct.graphics.objects.OutLink
 */
public OutLink getOutput() {
	if (outlinks.size()==1)
		return (OutLink)outlinks.firstElement();
	else
		return null;
}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 16:58:58)
 * @return boolean
 */
public void updateTemplateLink()
{
	if (lastUpdatedFullName!=null && data.getFullName().equals(lastUpdatedFullName))
		return;
		
	// remove old one		
	if (lastUpdatedFullName!=null)
		Group.getRoot().getLookupTable().remove(lastUpdatedFullName);
	
	// ups, we already got this registered
	if (Group.getRoot().getLookupTable().containsKey(data.getFullName()))
	{
		lastUpdatedFullName = null;
		//!!! this should never happen, but...
		//((LinkManagerObject)getParent()).addInvalidLink(this);
	}
	// everything is OK
	else
	{
		lastUpdatedFullName = data.getFullName();
		Group.getRoot().getLookupTable().put(lastUpdatedFullName, this);
		//!!! this should never happen, but...
		//((LinkManagerObject)getParent()).removeInvalidLink(this);
	}
}

}
