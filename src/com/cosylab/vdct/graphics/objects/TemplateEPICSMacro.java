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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import com.cosylab.vdct.Constants;
import com.cosylab.vdct.events.CommandManager;
import com.cosylab.vdct.events.commands.LinkCommand;
import com.cosylab.vdct.graphics.ColorChooser;
import com.cosylab.vdct.inspector.InspectableProperty;
import com.cosylab.vdct.vdb.GUIHeader;
import com.cosylab.vdct.vdb.GUISeparator;
import com.cosylab.vdct.vdb.NameValueInfoProperty;
import com.cosylab.vdct.vdb.VDBFieldData;
import com.cosylab.vdct.vdb.VDBTemplateMacro;

/**
 * Insert the type's description here.
 * Creation date: (29.1.2001 21:27:30)
 * @author Matej Sekoranja
 */

public class TemplateEPICSMacro extends EPICSOutLink implements TemplateEPICSLink {

 	private String lastUpdatedFullName = null;
	private static GUISeparator macroSeparator = null;
	private static javax.swing.ImageIcon icon = null;

	private static final String selectTitle = "Select link color...";
	private static final String startLinkingString = "Start linking...";
	private static final String addConnectorString = "Add connector";
	private static final String colorString = "Color...";
	private static final String moveUpString = "Move Up";
	private static final String moveDownString = "Move Down";
	private static final String removeString = "Remove Link";

/**
 * EPICSVarLink constructor comment.
 * @param parent com.cosylab.vdct.graphics.objects.ContainerObject
 * @param fieldData com.cosylab.vdct.vdb.VDBFieldData
 */
public TemplateEPICSMacro(ContainerObject parent, VDBFieldData fieldData) {
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
 * e.g. for rename
 * updates lookup table and fixes source
 */
public void fixTemplateLink()
{
	updateTemplateLink();
	LinkManagerObject.fixLink(this);
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

	int rrx;			// rrx, rry is center
	if (isRightSide)
		rrx = getRx()+getRwidth()-view.getRx();
	else
		rrx = getRx()-view.getRx();
	
	int rry = (int)(getRscale()*getInY()- view.getRy());
	
	if (!hilited) g.setColor(Constants.FRAME_COLOR);
	else g.setColor((this==view.getHilitedObject()) ? 
					Constants.HILITE_COLOR : Constants.FRAME_COLOR);


	int mode = InLink.OUTPUT_MACRO_MODE;
	Macro visibleMacro = ((VDBTemplateMacro)getFieldData()).getMacro().getVisibleObject();
	if (visibleMacro!=null)
		mode = visibleMacro.getMode();
	
	if (mode == InLink.INPUT_MACRO_MODE)
	{
		// input link
		int arrowLength = 2*r;
		
		if (!isRightSide)
			rrx -= arrowLength;

		// draw arrow
		g.drawLine(rrx, rry-r, rrx+arrowLength, rry-r);
		g.drawLine(rrx, rry+r, rrx+arrowLength, rry+r);
		
		int dr=-r; 
		if (isRightSide) {
			dr=-dr;
			rrx+=arrowLength;
		}
		g.drawLine(rrx, rry-r, rrx+dr, rry);
		g.drawLine(rrx, rry+r, rrx+dr, rry);
	}
	else if (mode == InLink.OUTPUT_MACRO_MODE)
	{
		// output link	
		int arrowLength = 3*r;

		if (!isRightSide)
			rrx -= arrowLength;

		// draw arrow
		g.drawLine(rrx, rry-r, rrx+arrowLength, rry-r);
		g.drawLine(rrx, rry+r, rrx+arrowLength, rry+r);
		
		int dr=r; 
		if (isRightSide) {
			dr=-dr;
			rrx+=arrowLength;
		}
		g.drawLine(rrx, rry-r, rrx+dr, rry);
		g.drawLine(rrx, rry+r, rrx+dr, rry);
	}
	//else 	
		// constant (none)



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
public static com.cosylab.vdct.vdb.GUISeparator getMacroSeparator() {
	if (macroSeparator==null) macroSeparator = new GUISeparator("Macro");
	return macroSeparator;
}

/**
 * Return properties to be inspected
 * Creation date: (1.2.2001 22:22:37)
 * @return com.cosylab.vdct.inspector.InspectableProperty[]
 */
public com.cosylab.vdct.inspector.InspectableProperty[] getProperties(int mode) {

/*
	OutLink out;
	Vector starts = new Vector();
	Enumeration e = outlinks.elements();
	while (e.hasMoreElements()) {
		out = EPICSLinkOut.getStartPoint((Linkable)e.nextElement());
		if (out instanceof EPICSLinkOut) starts.addElement(out);
	}
*/

	InspectableProperty[] properties = new InspectableProperty[1+3/*+2*starts.size()*/];

	properties[0]=GUIHeader.getDefaultHeader();
	properties[1]=getMacroSeparator();
	properties[2]=getFieldData();
	properties[3]=new NameValueInfoProperty("Description", getFieldData().getHelp());

/*
	int i = 4;
	VDBFieldData fieldData;
	e = starts.elements();
	while (e.hasMoreElements())
	{
		fieldData = ((EPICSLinkOut)e.nextElement()).getFieldData();
		properties[i++]=new GUISeparator(fieldData.getFullName());
		properties[i++]=fieldData;
	}
	*/
	return properties;
}

/**
 * Insert the method's description here.
 * Creation date: (4.5.2001 9:20:14)
 * @return java.lang.String
 */
public String toString() {
	return "Macro: "+getName();
}

/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 22:22:37)
 * @return javax.swing.Icon
 */
public javax.swing.Icon getIcon() {
	if (icon==null)
		icon = new javax.swing.ImageIcon(getClass().getResource("/images/link.gif"));
	return icon;
}

/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 12:07:15)
 * @return java.lang.String
 */
public String getDescription() {
	return ((VDBTemplateMacro)fieldData).getDescription();
}

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
			addConnector();
			com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
		}
		else if (action.equals(moveUpString))
		{
		}
		else if (action.equals(moveDownString))
		{
		}
		else if (action.equals(startLinkingString))
		{
			LinkCommand cmd = (LinkCommand)CommandManager.getInstance().getCommand("LinkCommand");
			cmd.setData((Template)TemplateEPICSMacro.this.getParent(), TemplateEPICSMacro.this.getFieldData());
			cmd.execute();
		}
		else if (action.equals(removeString))
		{
			destroy();
		}
			
	}
}

/**
 * Insert the method's description here.
 * Creation date: (2.2.2001 23:00:51)
 * @return com.cosylab.vdct.graphics.objects.EPICSLinkOut.PopupMenuHandler
 */
private TemplateEPICSMacro.PopupMenuHandler createPopupmenuHandler() {
	return new PopupMenuHandler();
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
	addItem.addActionListener(al);
	items.addElement(addItem);

	items.add(new JSeparator());

//TODO
/*
	if (getParent() instanceof Record)
	{
		Record parRec = (Record)getParent();
		boolean isFirst = parRec.isFirstField(this);
		boolean isLast = parRec.isLastField(this);
		
	
		if (!isFirst)
		{
			JMenuItem upItem = new JMenuItem(moveUpString);
			upItem.addActionListener(al);
			upItem.setIcon(new ImageIcon(getClass().getResource("/images/up.gif")));
			items.addElement(upItem);
		}
	
		if (!isLast)
		{
			JMenuItem downItem = new JMenuItem(moveDownString);
			downItem.addActionListener(al);
			downItem.setIcon(new ImageIcon(getClass().getResource("/images/down.gif")));
			items.addElement(downItem);
		}
	
		if (!(isFirst && isLast))
			items.add(new JSeparator());
	}
*/	
	JMenuItem linkItem = new JMenuItem(startLinkingString);
	linkItem.addActionListener(al);
	items.addElement(linkItem);

	JMenuItem removeItem = new JMenuItem(removeString);
	removeItem.addActionListener(al);
	items.addElement(removeItem);

	return items;
}

}
