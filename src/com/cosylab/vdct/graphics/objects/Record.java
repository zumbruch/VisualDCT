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
import com.cosylab.vdct.DataProvider;
import com.cosylab.vdct.graphics.*;
import com.cosylab.vdct.util.StringUtils;
import com.cosylab.vdct.vdb.*;
import com.cosylab.vdct.dbd.DBDConstants;
import com.cosylab.vdct.dbd.DBDFieldData;
import com.cosylab.vdct.dbd.DBDRecordData;

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
public class Record 
	extends LinkManagerObject
	implements Clipboardable, Descriptable, Flexible, Hub, Morphable, Movable, MultiInLink, Rotatable, Selectable, Popupable, Inspectable, SaveObject
{

	//private final static String nullString = "";
	private final static String fieldMaxStr = "01234567890123456789012345";
	private final static int tailSizeOfR = 4;
	private static javax.swing.ImageIcon icon = null;
	protected VDBRecordData recordData = null;
	private CommentProperty commentProperty = null;
	// type label
	protected int rtypeLabelX;
	protected int rtypeLabelY;
	protected String label2;
	protected Font typeFont = null;
	// changed fields label
	protected int rfieldLabelX;
	protected int rfieldLabelY;
	protected int rfieldRowHeight;
	protected Font fieldFont = null;
	protected Vector changedFields;
	protected Vector outlinks;
	protected boolean disconnected = false;
	private boolean right = true;
	
	private static GUISeparator alphaSeparator = null;
	private static GUISeparator dbdSeparator = null;

	private static ArrayList modes = null;

	public final static int GUI_GROUP_ORDER = 0;  
	public final static int SORT_ORDER = 1;
	public final static int DBD_ORDER = 2;

/**
 * Group constructor comment.
 * @param parent com.cosylab.vdct.graphics.objects.ContainerObject
 */
public Record(ContainerObject parent, VDBRecordData recordData, int x, int y) {
	super(parent);
	this.recordData=recordData;
	setColor(Color.black);
	setWidth(Constants.RECORD_WIDTH);
	setHeight(Constants.RECORD_HEIGHT);
	setX(x); setY(y);

	changedFields = new Vector();
	outlinks = new Vector();

	VDBFieldData field;
	Enumeration e = recordData.getFieldsV().elements();
	while (e.hasMoreElements()) {
		field = (VDBFieldData)e.nextElement();
		if (field.getVisibility() == VDBFieldData.ALWAYS_VISIBLE ||
			(field.getVisibility() == VDBFieldData.NON_DEFAULT_VISIBLE && !field.hasDefaultValue()))
			changedFields.addElement(field);
	}

	forceValidation();
	
}
/**
 * Insert the method's description here.
 * Creation date: (5.2.2001 13:36:25)
 * @param oldRecordName java.lang.String
 * @param newRecordName java.lang.String
 */
public void _fixEPICSInLinks(String oldRecordName, String newRecordName) {
	if (oldRecordName.equals(newRecordName)) return;
	
	Object obj; String old;
	EPICSLinkOut outlink;
	Enumeration fields = getSubObjectsV().elements();
	Enumeration outs;
	while (fields.hasMoreElements()) {
		obj = fields.nextElement();
		if (obj instanceof EPICSVarLink) {
			outs = ((EPICSVarLink)obj).getStartPoints().elements();
			while (outs.hasMoreElements()) {
				obj = outs.nextElement();
				if (obj instanceof EPICSLinkOut) {
					outlink = (EPICSLinkOut)obj;
					old = outlink.getFieldData().getValue();
					if (old.startsWith(oldRecordName))
						outlink.getFieldData().setValue(newRecordName+old.substring(oldRecordName.length()));
						
				}
			}
		}					
	}

	// fix record inlink
	outs = getStartPoints().elements();
	while (outs.hasMoreElements()) {
		obj = outs.nextElement();
		if (obj instanceof EPICSLinkOut) {
			outlink = (EPICSLinkOut)obj;
			old = outlink.getFieldData().getValue();
			if (old.startsWith(oldRecordName))
				outlink.getFieldData().setValue(newRecordName+old.substring(oldRecordName.length()));
				
		}
	}
  	
}
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 20:46:35)
 * @param visitor com.cosylab.vdct.graphics.objects.Visitor
 */
public void accept(Visitor visitor) {
	visitor.visitGroup();
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 22:40:48)
 * @param link com.cosylab.vdct.graphics.objects.Linkable
 */
public void addLink(Linkable link) {
	if (!getSubObjectsV().contains(link)) {
		Field field = (Field)link;
		addSubObject(field.getFieldData().getName(), field);
		validateFields();
		revalidateFieldsPosition();
	}
}
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
 * Creation date: (4.2.2001 22:02:29)
 * @param group java.lang.String
 */
public boolean copyToGroup(java.lang.String group) {

	String newName;
	if (group.equals(nullString))
		newName = Group.substractObjectName(recordData.getName());
	else
		newName = group+Constants.GROUP_SEPARATOR+
				  Group.substractObjectName(recordData.getName());

	// object with new name already exists, add suffix ///!!!
	//Object obj;
	while (Group.getRoot().findObject(newName, true)!=null)
//		newName += Constants.COPY_SUFFIX;
			newName = StringUtils.incrementName(newName, Constants.COPY_SUFFIX);

	ViewState view = ViewState.getInstance();


	VDBRecordData theDataCopy = VDBData.copyVDBRecordData(recordData);
	theDataCopy.setName(newName);
	Record theRecordCopy = new Record(null, theDataCopy, getX(), getY());
	theRecordCopy.move(20-view.getRx(), 20-view.getRy());
	Group.getRoot().addSubObject(theDataCopy.getName(), theRecordCopy, true);
	//theRecordCopy.fixEPICSOutLinks(Group.substractParentName(recordData.getName()), group);
	theRecordCopy.manageLinks();
	unconditionalValidation();

	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:59:21)
 */
public void destroy() {
	if (!isDestroyed()) {
		super.destroy();
		destroyFields();
//		disconnected=true;
		
		if (outlinks.size()>0) {
			Object[] objs = new Object[outlinks.size()];
			outlinks.copyInto(objs);
			for(int i=0; i<objs.length; i++) {
				OutLink outlink = (OutLink)objs[i];
				OutLink start = EPICSLinkOut.getStartPoint(outlink);
				if(start instanceof EPICSLinkOut)
					((EPICSLinkOut)start).destroy();
				else if (start!=null)
					start.disconnect(this);
				else 
					outlink.disconnect(this);
			}
			outlinks.clear();
		}
		
		clear();
		getParent().removeObject(Group.substractObjectName(getName()));
	}
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:47:53)
 */
public void disconnect(Linkable disconnector) {
	if (!disconnected && outlinks.contains(disconnector)) {
		outlinks.removeElement(disconnector);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 20:46:35)
 * @param g java.awt.Graphics
 * @param hilited boolean
 */
protected void draw(Graphics g, boolean hilited) {

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

		// middle line
		int ox = (int) (10 * getRscale());
		int ly = (int) (rry + Constants.RECORD_HEIGHT * getRscale());
		g.drawLine(rrx + ox, ly, rrx + rwidth - ox, ly);

		if (getFont() != null) {
			g.setFont(getFont());
			g.drawString(getLabel(), rrx + getRlabelX(), rry + getRlabelY());
		}

		if (typeFont != null) {
			g.setFont(typeFont);
			g.drawString(label2, rrx + rtypeLabelX, rry + rtypeLabelY);
		}

		if (fieldFont != null) {
			g.setFont(fieldFont);
			FontMetrics fm = FontMetricsBuffer.getInstance().getFontMetrics(fieldFont);
			String val;
			VDBFieldData fd;
			int px = rrx + rfieldLabelX;
			int py = rry + rfieldLabelY;
			Enumeration e = changedFields.elements();
			while (e.hasMoreElements()) {
				fd = (VDBFieldData) (e.nextElement());
				val = fd.getName() + "=" + fd.getValue();
				while ((fm.stringWidth(val) + ox) > rwidth)
					val = val.substring(0, val.length() - 2);
				g.drawString(val, px, py);
				py += rfieldRowHeight;
			}
		}

		// fwdlink support
		if (!disconnected && (outlinks.size() > 0)) {

			Color recordColor = g.getColor();
			Color linkColor = recordColor;
			if (outlinks.firstElement() instanceof VisibleObject)
				linkColor = ((VisibleObject) outlinks.firstElement()).getColor();


			if (linkColor==Constants.BACKGROUND_COLOR)
				if (linkColor==Color.black)
					linkColor=Color.white;
				else
					linkColor=Color.black;

			// draw link and its tail
			boolean isRightSide = isRight();
			int r = (int)(Constants.LINK_RADIOUS * getRscale());
			int cy = (int)(getRscale()*getInY()- view.getRy());
			int ccx = (int)(getRscale()*getInX()- view.getRx());

			int cx;
			if (isRightSide) {
				cx = rrx + rwidth + r;
				g.drawOval(cx - r, cy - r, 2 * r, 2 * r);
				g.setColor(linkColor);
				g.drawLine(cx + 2 * r, cy, ccx, cy);
			} else {
				cx = rrx - r;
				g.drawOval(cx - r, cy - r, 2 * r, 2 * r);
				g.setColor(linkColor);
				g.drawLine(ccx, cy, cx - 2 * r, cy);
			}

			// !!! more intergroup inlinks?!
			LinkDrawer.drawInIntergroupLink(
				g,
				(OutLink) outlinks.firstElement(),
				this,
				isRightSide);
		}

	}

	if (!hilited)
		paintSubObjects(g, hilited);

}
/**
 * Insert the method's description here.
 * Creation date: (27.1.2001 16:12:03)
 * @param field com.cosylab.vdct.vdb.VDBFieldData
 */
public void fieldChanged(VDBFieldData field) {
	boolean repaint = false;

	if (manageLink(field)) repaint=true;
	
	int visibility = field.getVisibility();
		
	if (visibility == VDBFieldData.NEVER_VISIBLE ||
		(visibility == VDBFieldData.NON_DEFAULT_VISIBLE && field.hasDefaultValue())) {
		if (changedFields.contains(field)) {
				changedFields.removeElement(field);
				repaint = true;
		}
				
	}
	else {
		if (!changedFields.contains(field))
				changedFields.addElement(field);
		repaint=true;
	}
	if (repaint) {
		unconditionalValidation();
		com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5.2.2001 9:42:29)
 * @param prevGroup java.lang.String
 * @param group java.lang.String
 */
public void fixEPICSOutLinks(String prevGroup, String group) {
	super.fixEPICSOutLinks(recordData.getFieldsV().elements(), prevGroup, group);
}

/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 8:37:37)
 */
private void fixForwardLinks() {

	String targetName = getRecordData().getName();
	EPICSLinkOut source;
	Object unknownLink;
	Enumeration e = this.getStartPoints().elements();
	while (e.hasMoreElements())
	{
		unknownLink = e.nextElement();
		if (unknownLink instanceof EPICSLinkOut) 
				source = (EPICSLinkOut)unknownLink;  
			else
				continue;	// nothing to fix
		
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
/**
 * Goes through link fields (in, out, var, fwd) and cheks
 * if ther are OK, if not it fixes it
 * When record is moved, renames, etc. value of in, out, fwd
 * should be changed, but visual link is still preserved :)
 * (linked list). It compares start point end end point and ...
 * Creation date: (2.5.2001 19:37:46)
 */
public void fixLinks() {

	// links to this record
	fixForwardLinks();

	super.fixLinks();
}

/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 15:00:15)
 * @return com.cosylab.vdct.inspector.InspectableProperty
 */
public com.cosylab.vdct.inspector.InspectableProperty getCommentProperty() {
	if (commentProperty==null)
		commentProperty = new CommentProperty(recordData);
	return commentProperty;
}
/**
 * Insert the method's description here.
 * Creation date: (9.4.2001 13:12:33)
 * @return java.lang.String
 */
public java.lang.String getDescription() {
	return getName();
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 10:16:55)
 * @return java.lang.String
 */
public java.lang.String getFlexibleName() {
	return recordData.getName();
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 16:41:13)
 * @return java.lang.String
 */
public java.lang.String getHashID() {
	return Group.substractObjectName(getName());
}
/**
 * Insert the method's description here.
 * Creation date: (25.4.2001 17:58:03)
 * @return int
 */
public int getHeight() {
	forceValidation();
	return super.getHeight();
}
/**
 * Insert the method's description here.
 * Creation date: (10.1.2001 15:15:51)
 * @return javax.swing.Icon
 */
public javax.swing.Icon getIcon() {
	if (icon==null)
		icon = new javax.swing.ImageIcon(getClass().getResource("/images/record.gif"));
	return icon;
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 20:37:11)
 * @return java.lang.String
 */
public String getID() {
	return getName();
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:47:54)
 * @return int
 */
public int getInX() {
	if (isRight())
		return getX()+getWidth()+(tailSizeOfR+3)*Constants.LINK_RADIOUS;
	else
		return getX()-(tailSizeOfR+3)*Constants.LINK_RADIOUS;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:47:54)
 * @return int
 */
public int getInY() {
	return getY()+getHeight()/2;
}
/**
 * Insert the method's description here.
 * Creation date: (2.2.2001 20:31:29)
 * @return java.util.Vector
 */
public Vector getItems() {
	return getLinkMenus(recordData.getFieldsV().elements());
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:47:53)
 * @return java.lang.String
 */
public java.lang.String getLayerID() {
	return getParent().toString();
}
/**
 * Insert the method's description here.
 * Creation date: (4.5.2001 9:54:07)
 * @return java.util.Vector
 */
public int getLinkCount() {
	return outlinks.size();
}
/**
 * Insert the method's description here.
 * Creation date: (2.2.2001 21:40:05)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return recordData.getName();
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:47:53)
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
	 * Creation date: (3.2.2001 13:07:04)
	 * @return com.cosylab.vdct.vdb.GUISeparator
	 */
	public static com.cosylab.vdct.vdb.GUISeparator getAlphaSeparator() {
		if (alphaSeparator==null) alphaSeparator = new GUISeparator("Alphabetical");
		return alphaSeparator;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (3.2.2001 13:07:04)
	 * @return com.cosylab.vdct.vdb.GUISeparator
	 */
	public static com.cosylab.vdct.vdb.GUISeparator getDBDSeparator() {
		if (dbdSeparator==null) dbdSeparator = new GUISeparator("DBD Order");
		return dbdSeparator;
	}

/**
 * Return properties to be inspected
 * Creation date: (11.1.2001 21:43:31)
 * @return com.cosylab.vdct.inspector.InspectableProperty[]
 */
public com.cosylab.vdct.inspector.InspectableProperty[] getProperties(int mode) {
	
	if (mode == GUI_GROUP_ORDER)
	{
		int size = 0;
		VDBFieldData field;	Integer key;
		Hashtable groups = new Hashtable();
		Enumeration e = recordData.getFieldsV().elements();
		while (e.hasMoreElements()) {
			field = (VDBFieldData)e.nextElement();
			/*if (field.getDbdData().getField_type() != 
				com.cosylab.vdct.dbd.DBDConstants.DBF_NOACCESS)*/ {
	
				key = new Integer(field.getGUI_type());
				if (groups.containsKey(key)) {
					((Vector)(groups.get(key))).addElement(field);
					size++;
				}
				// do not add fields with undefined GUI type
				else if (key.intValue()!=DBDConstants.GUI_UNDEFINED) {
					Vector v = new Vector();
					v.addElement(field);
					groups.put(key, v);
					size+=2;	// separator + property
				}
	
			}
		}
		
		Object[] grps;
		grps = new com.cosylab.vdct.util.IntegerQuickSort().sortEnumeration(groups.keys());
	
		Vector all = new Vector();
		
		Vector items; int grp;
		for (int gn=0; gn < grps.length; gn++) {
			items = (Vector)groups.get(grps[gn]);
			grp = ((VDBFieldData)(items.firstElement())).getGUI_type();
			all.addElement(new GUISeparator(com.cosylab.vdct.dbd.DBDResolver.getGUIString(grp)));
			all.addAll(items);
		}
		
		InspectableProperty[] properties = new InspectableProperty[all.size()];
		all.copyInto(properties);
		return properties;
	}
	else if ((mode == SORT_ORDER) ||
	 		  (mode == DBD_ORDER)) {

		VDBFieldData field;
		Vector all = new Vector();

		if (mode == SORT_ORDER)
			all.addElement(getAlphaSeparator());
		else
			all.addElement(getDBDSeparator());
	
		if (mode==DBD_ORDER)
		{
			DBDFieldData dbdField;
		 	Enumeration e = ((DBDRecordData)DataProvider.getInstance().getDbdDB().getDBDRecordData(recordData.getType())).getFieldsV().elements();
			while (e.hasMoreElements()) {
				dbdField = (DBDFieldData)e.nextElement();
				field = (VDBFieldData)recordData.getField(dbdField.getName());
				if (/*(field.getDbdData().getField_type() != com.cosylab.vdct.dbd.DBDConstants.DBF_NOACCESS) &&*/
					(field.getGUI_type()!=DBDConstants.GUI_UNDEFINED))
						all.addElement(field);
			}
		}
		else
		{
			Enumeration e = recordData.getFieldsV().elements();
			while (e.hasMoreElements()) {
				field = (VDBFieldData)e.nextElement();
				if (/*(field.getDbdData().getField_type() != com.cosylab.vdct.dbd.DBDConstants.DBF_NOACCESS) &&*/
					(field.getGUI_type()!=DBDConstants.GUI_UNDEFINED))
						all.addElement(field);
				}
		}				
		InspectableProperty[] properties = new InspectableProperty[all.size()];
		all.copyInto(properties);
	
		if (mode == SORT_ORDER)
			if (properties.length>1)
				new com.cosylab.vdct.util.StringQuickSort().sort(properties, 1, properties.length-1);
	
		return properties;
	}
	else
		return null;
}

/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 21:18:50)
 * @return com.cosylab.vdct.vdb.VDBRecordData
 */
public com.cosylab.vdct.vdb.VDBRecordData getRecordData() {
	return recordData;
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
 * Returned value inicates change
 * Creation date: (21.12.2000 22:21:12)
 * @return com.cosylab.visible.objects.VisibleObject
 * @param x int
 * @param y int
 */
public VisibleObject hiliteComponentsCheck(int x, int y) {

	ViewState view = ViewState.getInstance();
	VisibleObject spotted = null;
	
	Enumeration e = subObjectsV.elements();
	VisibleObject vo;
	while (e.hasMoreElements()) {
		vo = (VisibleObject)(e.nextElement());
		vo = vo.intersects(x, y);
		if (vo!=null) {
			spotted=vo;
			if (view.getHilitedObject()!=vo) return vo;
		}
	}

	return spotted;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 9:36:15)
 * @param field com.cosylab.vdct.vdb.VDBFieldData
 */
public EPICSLink initializeLinkField(VDBFieldData field) {

	if (!this.containsObject(field.getName()))
	{
		EPICSLink link = null;	
		int type = LinkProperties.getType(field);
		switch (type) {
			case LinkProperties.INLINK_FIELD:
				link = new EPICSInLink(this, field);
				break;
			case  LinkProperties.OUTLINK_FIELD:
				link = new EPICSOutLink(this, field);
				break;
			case LinkProperties.FWDLINK_FIELD:
				link = new EPICSFwdLink(this, field);
				break;
			case LinkProperties.VARIABLE_FIELD:
				link = new EPICSVarLink(this, field);
				break;
		}

		if (link!=null)	addLink(link);
		return link;
	}
	else
		return null;
}
/**
 * Default impmlementation for square (must be rescaled)
 * Creation date: (19.12.2000 20:20:20)
 * @return com.cosylab.visible.objects.VisibleObject
 * @param px int
 * @param py int
 */
public VisibleObject intersects(int px, int py) {
/*
  	if ((getRx()<=px) && (getRy()<=py) && 
		((getRx()+getRwidth())>=px) && 
		((getRy()+getRheight())>=py))
		return this;
	else 
		return hiliteComponentsCheck(px, py);
*/
	// first check on small sub-objects like connectors
	VisibleObject spotted = hiliteComponentsCheck(px, py);
  	if ((spotted==null) &&
  		(getRx()<=px) && (getRy()<=py) && 
		((getRx()+getRwidth())>=px) && 
		((getRy()+getRheight())>=py))
		spotted = this;
	return spotted;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:47:53)
 * @return boolean
 */
public boolean isConnectable() {
	return !disconnected;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:47:53)
 * @return boolean
 */
public boolean isDisconnected() {
	return disconnected;
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 22:54:43)
 * @return boolean
 * @param field com.cosylab.vdct.graphics.objects.Field
 */
public boolean isFirstField(Field field) {
	// find first field and compare
		
	Enumeration e = subObjectsV.elements();
	Object obj;
	while (e.hasMoreElements()) {
		obj = e.nextElement();
		if (obj instanceof Field)
			if (obj==field)
				return true;
			else
				return false;
	}
	
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 22:53:47)
 * @param field com.cosylab.vdct.graphics.objects.Field
 */
public boolean isLastField(Field field) {
	for (int i= subObjectsV.size()-1; i>=0; i--)
		if (subObjectsV.elementAt(i) instanceof Field)
			if (subObjectsV.elementAt(i)==field)
				return true;
			else
				return false;
	return false;
	
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 16:58:58)
 * @return boolean
 */
public boolean isRight() {
	if (disconnected || outlinks.size()!=1)
		return right;
	else {
		OutLink first = (OutLink)outlinks.firstElement();
		if (first.getLayerID().equals(getLayerID()))
			return (first.getOutX()>(getX()+getWidth()/2));
		else
			return right;
	}
}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:35:39)
 */
public void manageLinks() {
	VDBFieldData field;
	Enumeration e = recordData.getFieldsV().elements();
	while (e.hasMoreElements()) {
		field = (VDBFieldData)e.nextElement();
		manageLink(field);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 21:58:46)
 * @param newType java.lang.String
 */
public void morph(java.lang.String newType) {}
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
		moveConnectors(dx, dy);
		revalidatePosition();
		return true;
	}
	else 
		return false;
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 22:36:11)
 * @param field com.cosylab.vdct.graphics.objects.Field
 */
public void moveFieldDown(Field field) {
	// move visual field
	Vector fields = getSubObjectsV();
	int pos = fields.indexOf(field);

	pos++;
	while (pos<fields.size() && !(fields.elementAt(pos) instanceof Field))
		pos++;

	if (pos<fields.size()) {
		fields.removeElement(field);
		fields.insertElementAt(field, pos);
		revalidateFieldsPosition();
	}
	com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
	com.cosylab.vdct.undo.UndoManager.getInstance().addAction(new com.cosylab.vdct.undo.MoveFieldDownAction(field));
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 22:36:11)
 * @param field com.cosylab.vdct.graphics.objects.Field
 */
public void moveFieldUp(Field field) {
	// move visual field
	Vector fields = getSubObjectsV();
	int pos = fields.indexOf(field);
	pos--;
	while (pos>=0 && !(fields.elementAt(pos) instanceof Field))
		pos--;

	if (pos>=0) {
		fields.removeElement(field);
		fields.insertElementAt(field, pos);
		revalidateFieldsPosition();
	}
	
	com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
	com.cosylab.vdct.undo.UndoManager.getInstance().addAction(new com.cosylab.vdct.undo.MoveFieldUpAction(field));
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 22:02:29)
 * @param group java.lang.String
 */
public boolean moveToGroup(java.lang.String group) {
	String currentParent = Group.substractParentName(recordData.getName());
	if (group.equalsIgnoreCase(currentParent)) return false;
	
	String oldName = getName();
	String newName;
	if (group.equals(nullString))
		newName = Group.substractObjectName(recordData.getName());
	else
		newName = group+Constants.GROUP_SEPARATOR+
				  Group.substractObjectName(recordData.getName());;

	// object with new name already exists, add suffix // !!!
	Object obj;
	boolean renameNeeded = false;
	while ((obj=Group.getRoot().findObject(newName, true))!=null)
	{
		if (obj==this)	// it's me :) already moved, fix data
		{
			recordData.setName(newName);
			fixLinks();
			return true;
		}
		else
		{
			renameNeeded = true;
			newName = StringUtils.incrementName(newName, Constants.MOVE_SUFFIX);
		}
	}

	if (renameNeeded)
		return rename(newName);
	
	getParent().removeObject(Group.substractObjectName(getName()));
	setParent(null);
	Group.getRoot().addSubObject(newName, this, true);

	//String oldGroup = Group.substractParentName(recordData.getName());
	recordData.setName(newName);
	/*//fixEPICSInLinks(recordData.getName(), newName);
	//fixEPICSOutLinks(oldGroup, group);			// only if target is moving !!!*/
	fixLinks();
	unconditionalValidation();

	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 21:58:56)
 * @param g java.awt.Graphics
 * @param hilited boolean
 */
private void paintSubObjects(Graphics g, boolean hilited) {
	Enumeration e = subObjectsV.elements();
	VisibleObject vo;
	while (e.hasMoreElements()) {
		vo = (VisibleObject)(e.nextElement());
			vo.paint(g, hilited);
	}
	
}

/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 22:40:48)
 * @param link com.cosylab.vdct.graphics.objects.Linkable
 */
public void removeLink(Linkable link) {
	if (getSubObjectsV().contains(link)) {
		Field field = (Field)link;
		removeObject(field.getFieldData().getName());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2.5.2001 23:23:32)
 * @param newName java.lang.String
 */
public boolean rename(java.lang.String newName) {
	
	// name has to be valid
	
	String newObjName = Group.substractObjectName(newName);
	String oldObjName = Group.substractObjectName(getName());


	if (!oldObjName.equals(newObjName))
	{
		getParent().removeObject(oldObjName);
		String fullName = com.cosylab.vdct.util.StringUtils.replaceEnding(getName(), oldObjName, newObjName);
		getRecordData().setName(fullName);
		getParent().addSubObject(newObjName, this);

		// fix connectors IDs
		Enumeration e = subObjectsV.elements();
		Object obj; Connector connector;
		while (e.hasMoreElements()) {
			obj = e.nextElement();
			if (obj instanceof Connector)
			{
				connector = (Connector)obj;
				String id = connector.getID();
				id = com.cosylab.vdct.util.StringUtils.replaceEnding(id, oldObjName, newObjName);
				connector.setID(id);
			}
		}
	}
	
	// move if needed
	if (!moveToGroup(Group.substractParentName(newName)))
		fixLinks();			// fix needed

	return true;
	
}
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 17:18:51)
 */
private void revalidateFieldsPosition() {

  int nx, ny;
  ny = getY()+getHeight();
  Enumeration e = subObjectsV.elements();
  Field field; Object obj;
  while (e.hasMoreElements()) {
	obj = e.nextElement();
	if (obj instanceof Field) {
		field = (Field)obj;
		nx = getX()+(getWidth()-field.getWidth())/2;
		field.revalidatePosition(nx, ny);
		ny+=field.getHeight();
	}
  }

}
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 21:22:45)
 */
public void revalidatePosition() {
  setRx((int)(getX()*getRscale()));
  setRy((int)(getY()*getRscale()));

  // sub-components
  revalidateFieldsPosition();
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 16:58:58)
 */
public void rotate() { right=!right; }
/**
 * Insert the method's description here.
 * Creation date: (27.12.2000 12:45:23)
 * @return boolean
 */
public boolean selectAllComponents() {
	
	ViewState view = ViewState.getInstance();
	boolean anyNew = false;
	
	Enumeration e = subObjectsV.elements();
	VisibleObject vo;
	while (e.hasMoreElements()) {
		vo = (VisibleObject)(e.nextElement());
		if (vo instanceof Selectable)
			if (view.setAsSelected(vo)) anyNew = true;
	}

	return anyNew;
}
/**
 * Insert the method's description here.
 * Creation date: (24.4.2001 17:40:55)
 * @param description java.lang.String
 */
public void setDescription(java.lang.String description) {}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:47:53)
 * @param id java.lang.String
 */
public void setLayerID(java.lang.String id) {
	// not needed, id is retrieved dynamicaly via parent
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:47:54)
 * @param output com.cosylab.vdct.graphics.objects.OutLink
 * @param prevOutput com.cosylab.vdct.graphics.objects.OutLink
 */
public void setOutput(OutLink output, OutLink prevOutput) {
	if (prevOutput!=null) outlinks.removeElement(prevOutput);
	if (!outlinks.contains(output)) {
		outlinks.addElement(output);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 16:58:58)
 * @param state boolean
 */
public void setRight(boolean state) { right=state; }
/**
 * Insert the method's description here.
 * Creation date: (10.1.2001 14:49:50)
 * @return java.lang.String
 */
public String toString() {
	return recordData.toString();
	// recordData.getName()+" ("+recordData.getType()+")"
}
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 20:46:35)
 */
protected void validate() {

  double scale = getRscale();
  int rwidth = (int)(getWidth()*scale);
  int rheight = (int)(Constants.RECORD_HEIGHT*scale);

  setRheight(rheight);
  setRwidth(rwidth);

  // set appropriate font size
  int x0 = (int)(8*scale);		// insets
  int y0 = (int)(4*scale);

  Font font;
  setLabel(recordData.getName());
  if (rwidth<(2*x0)) font = null;
  else
	  font = FontMetricsBuffer.getInstance().getAppropriateFont(
		  			Constants.DEFAULT_FONT, Font.PLAIN, 
	 	 			getLabel(), rwidth-x0, (rheight-y0)/2);

  if (font!=null) {
	  FontMetrics fm = FontMetricsBuffer.getInstance().getFontMetrics(font);
	  setRlabelX((rwidth-fm.stringWidth(getLabel()))/2);
 	  setRlabelY(rheight/2+(rheight/2-fm.getHeight())/2+fm.getAscent());
  }
  setFont(font);
  
  label2 = recordData.getType();
  if (rwidth<(2*x0)) typeFont = null;
  else
	  typeFont = FontMetricsBuffer.getInstance().getAppropriateFont(
		  			 Constants.DEFAULT_FONT, Font.PLAIN, 
	 	 			 label2, rwidth-x0, (rheight-y0)/2);

  if (typeFont!=null) {
	  FontMetrics fm = FontMetricsBuffer.getInstance().getFontMetrics(typeFont);
	  rtypeLabelX = (rwidth-fm.stringWidth(label2))/2;
 	  rtypeLabelY = (rheight/2-fm.getHeight())/2+fm.getAscent();
  }

  if (rwidth<(2*x0)) fieldFont = null;
  else
	  fieldFont = FontMetricsBuffer.getInstance().getAppropriateFont(
		  			 Constants.DEFAULT_FONT, Font.PLAIN, 
	 	 			 fieldMaxStr, rwidth-x0, rheight-y0);

  int ascent = 0;
  rfieldRowHeight = 0;
  if (fieldFont!=null) {
	  FontMetrics fm = FontMetricsBuffer.getInstance().getFontMetrics(fieldFont);
	  rfieldLabelX = x0;
 	  rfieldLabelY = rheight+2*fm.getAscent();
	  rfieldRowHeight = fm.getHeight();
	  ascent = fm.getAscent();
  }

  rheight += y0+rfieldRowHeight*changedFields.size()+ascent;
  setHeight((int)(rheight/scale));

  // round fix
  rheight = (int)((getY()+getHeight())*scale)-(int)(getY()*scale);
  setRheight(rheight);

  // sub-components
  revalidatePosition();		// rec's height can be different
  validateFields();
 
}
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 17:19:47)
 */
private void validateFields() {

	Enumeration e = subObjectsV.elements();
	Object obj;
	while (e.hasMoreElements()) {
		obj = e.nextElement();
		if (obj instanceof Field ||
			obj instanceof Connector)
			((VisibleObject)obj).validate();
	}
	
}

/**
 */
public VDBFieldData getField(String name) {
	return recordData.getField(name);
}

/**
 * @see com.cosylab.vdct.inspector.Inspectable#getModeNames()
 */
public ArrayList getModeNames()
{
	return this.getModes();
}

private static ArrayList getModes()
{
	if (modes==null)
	{
		modes = new ArrayList();
		modes.add("Group");
		modes.add("Alphabetical");
		modes.add("DBD Order");
	}
	return modes;
}


}
