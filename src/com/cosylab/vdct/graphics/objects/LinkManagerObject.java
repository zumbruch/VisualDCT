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
import com.cosylab.vdct.vdb.*;
import com.cosylab.vdct.dbd.DBDConstants;

import com.cosylab.vdct.inspector.*;

import com.cosylab.vdct.graphics.popup.*;
import javax.swing.*;
import java.awt.event.*;

import com.cosylab.vdct.events.*;
import com.cosylab.vdct.events.commands.*;

/**
 * Insert the type's description here.
 * Creation date: (21.12.2000 20:46:35)
 * @author Matej Sekoranja
 */
public abstract class LinkManagerObject extends ContainerObject implements Hub, Inspectable, Popupable {

	class PopupMenuHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
		    LinkCommand cmd = (LinkCommand)CommandManager.getInstance().getCommand("LinkCommand");
		    cmd.setData(LinkManagerObject.this, LinkManagerObject.this.getField(e.getActionCommand()));
	 		cmd.execute();
		}
	}

	public final static String nullString = "";

	// GUI linking support
    private LinkSource targetLink = null;
	public final static String inlinkString = "INLINK";
	public final static String outlinkString = "OUTLINK";
	public final static String fwdlinkString = "FWDLINK";
	public final static String varlinkString = "VARIABLE";
	public final static String varlinkPortString = "VARIABLE to PORT";

/**
 * LinkManagerObject constructor comment.
 * @param parent com.cosylab.vdct.graphics.objects.ContainerObject
 */
public LinkManagerObject(ContainerObject parent)
{
	super(parent);
}

/**
 */
public void addInvalidLink(EPICSLink field)
{
}

/**
 */
public void removeInvalidLink(EPICSLink field)
{
}

/**
 */
public abstract VDBFieldData getField(String name);

/**
 * Insert the method's description here.
 * Creation date: (27.1.2001 16:12:03)
 * @param field com.cosylab.vdct.vdb.VDBFieldData
 */
public abstract void fieldChanged(VDBFieldData field);

/**
 * Insert the method's description here.
 * Creation date: (5.2.2001 9:42:29)
 * @param e java.util.Enumeration list of VDBFieldData fields
 * @param prevGroup java.lang.String
 * @param group java.lang.String
 */
public void fixEPICSOutLinks(Enumeration e, String prevGroup, String group) {
	if (prevGroup.equals(group)) return;
	
	String prefix;
	if (group.equals(nullString)) prefix=nullString;
	else prefix=group+Constants.GROUP_SEPARATOR;

	String old; 
	int type; VDBFieldData field;
	while (e.hasMoreElements()) {
		field = (VDBFieldData)e.nextElement();
		type = LinkProperties.getType(field);
		if (type != LinkProperties.VARIABLE_FIELD) {
			old = field.getValue();
			if (!old.equals(nullString) && !old.startsWith(Constants.HARDWARE_LINK) &&
				old.startsWith(prevGroup)) {
				if (prevGroup.equals(nullString))
					field.setValue(prefix+old);
				else
					field.setValue(prefix+old.substring(prevGroup.length()+1));
			}
		}
	}

}
/**
 * Goes through link fields (in, out, var, fwd) and cheks
 * if ther are OK, if not it fixes it
 * When record is moved, renames, etc. value of in, out, fwd
 * should be changed, but visual link is still preserved :)
 * (linked list). It compares start point end end point and ...
 * Creation date: (2.5.2001 19:37:46)
 */
public void fixLinks() {

	Object unknownField;
	
	Enumeration e = getSubObjectsV().elements();
	while (e.hasMoreElements())
	{
			unknownField = e.nextElement();
			
			// go and find source
			if (unknownField instanceof EPICSVarLink)
			{
				fixLink((EPICSVarLink)unknownField);
			}
			else if (unknownField instanceof EPICSLinkOutIn)
			{
				fixLink((EPICSLinkOutIn)unknownField);
			}

	}
	
}


public void fixLinks_() {

	Object unknownField;
	EPICSVarLink varlink;
	
	Enumeration e = getSubObjectsV().elements();
	while (e.hasMoreElements())
	{
			unknownField = e.nextElement();
			
			// go and find source
			if (unknownField instanceof EPICSVarLink)
			{
				varlink = (EPICSVarLink)unknownField;
				fixLink(varlink);
			}

/*
			else if (unknownField instanceof EPICSLinkOut)
			{
				source = (EPICSLinkOut)unknownField;
				InLink inlink = EPICSLinkOut.getEndPoint(source);
				if (inlink!=null && inlink instanceof EPICSVarLink)
				{
					varlink = (EPICSVarLink)inlink;
					targetName = varlink.getFieldData().getFullName();
					// now I got source and target, compare values
					String oldTarget = LinkProperties.getTarget(source.getFieldData());
					if (!oldTarget.equalsIgnoreCase(targetName))
					{
						// not the same, fix it gently as a doctor :)
						String value = source.getFieldData().getValue();
						value = targetName + com.cosylab.vdct.util.StringUtils.removeBegining(value, oldTarget);
						source.getFieldData().setValueSilently(value);
						source.fixLinkProperties();
					}
				}
			}

*/
			
	}
	
}

public static void fixLink(EPICSVarLink varlink)
{
	LinkSource data = null;
	String targetName = varlink.getFieldData().getFullName();
	
	Enumeration e2 = varlink.getStartPoints().elements();
	while (e2.hasMoreElements())
	{
		Object unknownField = e2.nextElement();
		if (unknownField instanceof OutLink) 
		{
			if (unknownField instanceof EPICSLink)
				data = ((EPICSLink)unknownField).getFieldData();
			else if (unknownField instanceof Port)
				data = ((Port)unknownField).getData();
		}
		else
			continue;	// nothing to fix
	
		// now I got source and target, compare values
		String oldTarget = LinkProperties.getTarget(data);
		if (!oldTarget.equalsIgnoreCase(targetName))
		{
			// not the same, fix it gently as a doctor :)
			String value = data.getValue();
			// value = targetName + link properties
			value = targetName + com.cosylab.vdct.util.StringUtils.removeBegining(value, oldTarget);
			data.setValueSilently(value);
			
			// !!!!
			if (unknownField instanceof EPICSLink)
				((EPICSLink)unknownField).fixLinkProperties();
			else if (unknownField instanceof Port)
				((Port)unknownField).fixLinkProperties();
		}
	}
}

public static void fixLink(EPICSLinkOutIn linkoutin)
{
	LinkSource data = null;
	String targetName = linkoutin.getFieldData().getFullName();
	
	Enumeration e2 = linkoutin.getStartPoints().elements();
	while (e2.hasMoreElements())
	{
		Object unknownField = e2.nextElement();
		if (unknownField instanceof OutLink) 
		{
			if (unknownField instanceof EPICSLink)
				data = ((EPICSLink)unknownField).getFieldData();
			else if (unknownField instanceof Port)
				data = ((Port)unknownField).getData();
		}
		else
			continue;	// nothing to fix
	
		// now I got source and target, compare values
		String oldTarget = LinkProperties.getTarget(data);
		if (!oldTarget.equalsIgnoreCase(targetName))
		{
			// not the same, fix it gently as a doctor :)
			String value = data.getValue();
			// value = targetName + link properties
			value = targetName + com.cosylab.vdct.util.StringUtils.removeBegining(value, oldTarget);
			data.setValueSilently(value);
			
			// !!!!
			if (unknownField instanceof EPICSLink)
				((EPICSLink)unknownField).fixLinkProperties();
			else if (unknownField instanceof Port)
				((Port)unknownField).fixLinkProperties();
		}
	}
}

public static void fixLink_(EPICSVarLink varlink)
{
	EPICSLinkOut source;
	String targetName = varlink.getFieldData().getFullName();
	
	Enumeration e2 = varlink.getStartPoints().elements();
	while (e2.hasMoreElements())
	{
		Object unknownField = e2.nextElement();
		if (unknownField instanceof EPICSLinkOut) 
			source = (EPICSLinkOut)unknownField;  
		else
			continue;	// nothing to fix
	
		// now I got source and target, compare values
		String oldTarget = LinkProperties.getTarget(source.getFieldData());
		if (!oldTarget.equalsIgnoreCase(targetName))
		{
			// not the same, fix it gently as a doctor :)
			String value = source.getFieldData().getValue();
			// value = targetName + link properties
			value = targetName + com.cosylab.vdct.util.StringUtils.removeBegining(value, oldTarget);
			source.getFieldData().setValueSilently(value);
			source.fixLinkProperties();
		}
	}
}

/**
 * Returns true if link is softeare link
 * Creation date: (30.1.2001 9:36:15)
 * @return boolean
 * @param field com.cosylab.vdct.vdb.VDBFieldData
 */
public static boolean isSoftwareLink(VDBFieldData field)
{
	if (field.getValue().startsWith(Constants.HARDWARE_LINK) ||
		field.getValue().startsWith("@") ||    // !!!??
		field.getValue().equals(nullString) ||
		Character.isDigit(field.getValue().charAt(0))) 
		return false; 	//!!!
	else
		return true;
}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 9:36:15)
 * @return boolean
 * @param field com.cosylab.vdct.vdb.VDBFieldData
 */
public boolean manageLink(VDBFieldData field) {

	int type = LinkProperties.getType(field);
	if (type == LinkProperties.VARIABLE_FIELD)
	{
		// invoke validateLink at the end

		EPICSVarOutLink link = null;
		
		if (this.containsObject(field.getName()))
			link = (EPICSVarOutLink)getSubObject(field.getName());

		
		if (link!=null && link.getInput()!=null)
		{
			link.validateLink();
			return true;
		}
		
		
		// check new VAR->PORT link
		LinkProperties properties = new LinkProperties(field);
		InLink portLink = EPICSLinkOut.getTarget(properties);
		
		if (portLink==null || !(portLink instanceof TemplateEPICSPort))
		{
			if (link!=null)
			{
				link.validateLink();
				return true;
			}
			else
				return false;
		}
		
		// create a new one	
		if (link==null)
		{
			link = new EPICSVarOutLink(this, field);
			addLink(link);
		}

		portLink.setOutput(link, null);
		link.setInput(portLink);

		if (link!=null)
			link.validateLink();
			
		return true;

	}	
	else
	{
		
		if (this.containsObject(field.getName()))
		{
			// existing link
			EPICSLinkOut link = (EPICSLinkOut)getSubObject(field.getName());
			link.valueChanged();
			link.setDestroyed(false);
			return true;
			
		}
		else
		{
			if (!LinkManagerObject.isSoftwareLink(field))
				return false;
				
			// new link
			LinkProperties properties = new LinkProperties(field);
			InLink varlink = EPICSLinkOut.getTarget(properties);
			// can point to null? OK, cross will be showed

			EPICSLinkOut outlink = null;
			
			if (type==LinkProperties.INLINK_FIELD)
				outlink = new EPICSInLink(this, field);
			else if (type==LinkProperties.OUTLINK_FIELD)
				outlink = new EPICSOutLink(this, field);
			else /*if (type==LinkProperties.FWDLINK_FIELD)*/
				outlink = new EPICSFwdLink(this, field);
		
			addLink(outlink);

			if (varlink!=null) varlink.setOutput(outlink, null);
			outlink.setInput(varlink);

			return true;
		}
	}
}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 9:36:15)
 * @return boolean
 * @param field com.cosylab.vdct.vdb.VDBFieldData
 */
public boolean manageLink_(VDBFieldData field) {

	int type = LinkProperties.getType(field);
	if (type == LinkProperties.VARIABLE_FIELD)
	{
		if (this.containsObject(field.getName()))
		{
			EPICSVarLink link = (EPICSVarLink)getSubObject(field.getName());
			link.validateLink();
			return true;			
		}
		return false;
	}	
	else
	{
		
		if (this.containsObject(field.getName()))
		{
			// existing link
			EPICSLinkOut link = (EPICSLinkOut)getSubObject(field.getName());
			link.valueChanged();
			link.setDestroyed(false);
			return true;
			
		}
		else
		{
			if (!LinkManagerObject.isSoftwareLink(field))
				return false;
				
			// new link
			LinkProperties properties = new LinkProperties(field);
			InLink varlink = EPICSLinkOut.getTarget(properties);
			// can point to null? OK, cross will be showed

			EPICSLinkOut outlink = null;
			
			if (type==LinkProperties.INLINK_FIELD)
				outlink = new EPICSInLink(this, field);
			else if (type==LinkProperties.OUTLINK_FIELD)
				outlink = new EPICSOutLink(this, field);
			else /*if (type==LinkProperties.FWDLINK_FIELD)*/
				outlink = new EPICSFwdLink(this, field);
		
			addLink(outlink);
			/*if (!properties.isIsInterGroupLink())
			{
				String id = EPICSLinkOut.generateConnectorID(outlink);
				Connector connector = new Connector(id, this, outlink, varlink);
				if (varlink!=null)
				{
					connector.setX((outlink.getOutX()+varlink.getInX())/2);
					connector.setY((outlink.getOutY()+varlink.getInY())/2);
				}
				addSubObject(id, connector);
			}
			else*/
			{
				if (varlink!=null) varlink.setOutput(outlink, null);
				outlink.setInput(varlink);
			}

			return true;
		}
	}
}

/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 17:38:36)
 * @param dx int
 * @param dy int
 */
public void moveConnectors(int dx, int dy) {
	
  ViewState view = ViewState.getInstance();
  Enumeration e = subObjectsV.elements();
  Connector con; Object obj;
  while (e.hasMoreElements()) {
	obj = e.nextElement();
	if (obj instanceof Connector) {
		con = (Connector)obj;
		if (view.isSelected(con))
			continue;	// will move by itself
		InLink endpoint = EPICSLinkOut.getEndPoint(con);
		/*OutLink startpoint = EPICSLinkOut.getStartPoint(con);
		EPICSLinkOut lo = null;
		if (!(startpoint instanceof EPICSLinkOut))
			lo = (EPICSLinkOut)startpoint;*/
		if (((endpoint instanceof EPICSLink) &&
			(view.isSelected(((EPICSLink)endpoint).getParent())) /*||
			((lo!=null) && lo.getLinkProperties().isIsInterGroupLink())*/)
			||
			((endpoint instanceof LinkManagerObject) && view.isSelected(endpoint)))
			con.move(dx, dy);
	}
  }
}

/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 21:58:56)
 * @param g java.awt.Graphics
 * @param hilited boolean
 */
public void postDraw(Graphics g, boolean hilited) {
	Enumeration e = subObjectsV.elements();
	VisibleObject vo;
	while (e.hasMoreElements()) {
		vo = (VisibleObject)(e.nextElement());
		if (vo instanceof Connector)
			vo.paint(g, hilited);
	}
	
}

/**
 * Insert the method's description here.
 * Creation date: (2.2.2001 20:31:29)
 * @return java.util.Vector
 */
public Vector getLinkMenus(Enumeration vdbFields) {
	Vector items = new Vector();
	ActionListener l = createPopupmenuHandler();
	VDBFieldData field;
	JMenuItem menuitem;

	boolean port2All = (getTargetLink() instanceof VDBPort);
	
	if (getTargetLink()==null || port2All) {
		
		JMenu inlinks = new JMenu(inlinkString);
		JMenu outlinks = new JMenu(outlinkString);
		JMenu fwdlinks = new JMenu(fwdlinkString);
		JMenu varlinks = null;
		if (port2All)
			varlinks = new JMenu(varlinkString);
		else
			varlinks = new JMenu(varlinkPortString); 
		
		JMenu inMenu = inlinks;	
		JMenu outMenu = outlinks;	
		JMenu fwdMenu = fwdlinks;	
		JMenu varMenu = varlinks;	
		
		int inpItems, outItems, fwdItems;
		int varItems;
		inpItems=outItems=fwdItems=0;
		varItems=0; 

		while (vdbFields.hasMoreElements()) {
			field = (VDBFieldData)(vdbFields.nextElement());
			if (field.getValue().equals(nullString)) {
				switch (field.getType()) {
					case DBDConstants.DBF_INLINK:
						 menuitem = new JMenuItem(field.getName());
						 menuitem.addActionListener(l);
						 inlinks = PopUpMenu.addItem(menuitem, inlinks, inpItems); 
						 inpItems++;
						 break;
					case DBDConstants.DBF_OUTLINK: 
						 menuitem = new JMenuItem(field.getName());
						 menuitem.addActionListener(l);
						 outlinks = PopUpMenu.addItem(menuitem, outlinks, outItems); 
						 outItems++;
						 break;
					case DBDConstants.DBF_FWDLINK:
						 menuitem = new JMenuItem(field.getName());
						 menuitem.addActionListener(l);
						 fwdlinks = PopUpMenu.addItem(menuitem, fwdlinks, fwdItems); 
						 fwdItems++;
						 break;
					default:

						 // no not add fields with undefined GUI type
						 if (!port2All && field.getGUI_type() == DBDConstants.GUI_UNDEFINED)
						 	break;

						 menuitem = new JMenuItem(field.getName());
						 menuitem.addActionListener(l);
						 varlinks = PopUpMenu.addItem(menuitem, varlinks, varItems); 
						 varItems++;
						 break;
					
				}
			}
		}

		if (inMenu.getItemCount() > 0)
			items.addElement(inMenu);
		if (outMenu.getItemCount() > 0)
			items.addElement(outMenu);
		if (fwdMenu.getItemCount() > 0)
			items.addElement(fwdMenu);
		if (varMenu.getItemCount() > 0)
			items.addElement(varMenu);

	}
	else if ((getTargetLink().getType() == DBDConstants.DBF_INLINK) ||
		  	  (getTargetLink().getType() == DBDConstants.DBF_OUTLINK) ||
			  (getTargetLink().getType() == DBDConstants.DBF_FWDLINK)) { // no targets (only ports) for VAR->PORTS
		int count = 0;
		JMenu varlinkItem = new JMenu(varlinkString);
		JMenu menu = varlinkItem;
		
		while (vdbFields.hasMoreElements()) {
			field = (VDBFieldData)(vdbFields.nextElement());
/*			switch (field.getType()) {
				case DBDConstants.DBF_CHAR: 
				case DBDConstants.DBF_UCHAR: 
				case DBDConstants.DBF_SHORT: 
				case DBDConstants.DBF_USHORT: 
				case DBDConstants.DBF_LONG: 
				case DBDConstants.DBF_ULONG: 
				case DBDConstants.DBF_FLOAT: 
				case DBDConstants.DBF_DOUBLE: 
				case DBDConstants.DBF_STRING:
				case DBDConstants.DBF_NOACCESS:	
				case DBDConstants.DBF_ENUM:
				case DBDConstants.DBF_MENU:
				case DBDConstants.DBF_DEVICE:  // ?
				  menuitem = new JMenuItem(field.getName());
				  menuitem.addActionListener(l);
				  menu = PopUpMenu.addItem(menuitem, menu, count);
				  count++; 
			}
*/
			if (field.getType()!=DBDConstants.DBF_INLINK &&
				field.getType()!=DBDConstants.DBF_OUTLINK &&
				field.getType()!=DBDConstants.DBF_FWDLINK)
			{
				  menuitem = new JMenuItem(field.getName());
				  menuitem.addActionListener(l);
				  menu = PopUpMenu.addItem(menuitem, menu, count);
				  count++; 
			}

		}
		if (count > 0) items.addElement(varlinkItem);
		
	}
		
	return items;
}

/**
 * Insert the method's description here.
 * Creation date: (2.2.2001 23:00:51)
 * @return com.cosylab.vdct.graphics.objects.LinkManagerObject.PopupMenuHandler
 */
private com.cosylab.vdct.graphics.objects.LinkManagerObject.PopupMenuHandler createPopupmenuHandler() {
	return new PopupMenuHandler();
}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:59:54)
 */
protected void destroyFields() {

	Object[] objs = new Object[subObjectsV.size()];
	subObjectsV.copyInto(objs);
	for (int i=0; i < objs.length; i++)
		((VisibleObject)objs[i]).destroy();
	
}


	/**
	 * Returns the targetLink.
	 * @return LinkSource
	 */
	public LinkSource getTargetLink()
	{
		return targetLink;
	}

	/**
	 * Sets the targetLink.
	 * @param targetLink The targetLink to set
	 */
	public void setTargetLink(LinkSource targetLink)
	{
		this.targetLink = targetLink;
	}

}
