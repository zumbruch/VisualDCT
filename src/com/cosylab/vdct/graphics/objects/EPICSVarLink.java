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
import javax.swing.*;
import com.cosylab.vdct.vdb.*;
import java.awt.event.*;
import com.cosylab.vdct.Constants;
import com.cosylab.vdct.inspector.*;
import com.cosylab.vdct.graphics.popup.*;

/**
 * Insert the type's description here.
 * Creation date: (29.1.2001 21:27:30)
 * @author Matej Sekoranja
 */
public class EPICSVarLink extends EPICSLink implements MultiInLink, Popupable, Inspectable {

	class PopupMenuHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
/*			if (action.equals(colorString))
			{			
				Color newColor = JColorChooser.showDialog(null, selectTitle, getColor());
				setColor(newColor);
				com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
			}
			else if (action.equals(addConnectorString))
			{
				addConnector();
				com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
			}
			else*/ if (action.equals(moveUpString))
			{
				((Record)getParent()).moveFieldUp(EPICSVarLink.this);
			}
			else if (action.equals(moveDownString))
			{
				((Record)getParent()).moveFieldDown(EPICSVarLink.this);
			}
			else if (action.equals(removeString))
			{
				destroy();
			}
			
		}
	}
	protected Vector outlinks;
	private static javax.swing.ImageIcon icon = null;
	/*private static final String addConnectorString = "Add connector";
	private static final String colorString = "Color...";*/
	private static final String moveUpString = "Move Up";
	private static final String moveDownString = "Move Down";
	private static final String removeString = "Remove Link";
	private static GUISeparator linkSeparator = null;
	 
/**
 * EPICSVarLink constructor comment.
 * @param parent com.cosylab.vdct.graphics.objects.ContainerObject
 * @param fieldData com.cosylab.vdct.vdb.VDBFieldData
 */
public EPICSVarLink(ContainerObject parent, com.cosylab.vdct.vdb.VDBFieldData fieldData) {
	super(parent, fieldData);
	outlinks = new Vector();
}
/**
 * Insert the method's description here.
 * Creation date: (2.2.2001 23:00:51)
 * @return com.cosylab.vdct.graphics.objects.EPICSLinkOut.PopupMenuHandler
 */
private com.cosylab.vdct.graphics.objects.EPICSVarLink.PopupMenuHandler createPopupmenuHandler() {
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
				if(start instanceof EPICSLinkOut)
					((EPICSLinkOut)start).destroy();
				else if (start!=null)
					start.disconnect(this);
				else 
					outlink.disconnect(this);
			}
			outlinks.clear();
		}
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
		rrx = getRx()+r+getRwidth()-view.getRx();
	else 
		rrx = getRx()-r-view.getRx();

	//int rry = getRy()+getRheight()/2-view.getRy();
	int rry = (int)(getRscale()*getInY()- view.getRy());
	
	if (!hilited) g.setColor(Constants.FRAME_COLOR);
	else g.setColor((this==view.getHilitedObject()) ? 
					Constants.HILITE_COLOR : Constants.FRAME_COLOR);

	g.drawOval(rrx-r, rry-r, 2*r,2*r);

	if (!disconnected && (outlinks.size()>0)) {
		/*// shorten tail if needed
		if (outlinks.size()==1) {
			int rlx = (int)(((OutLink)outlinks.firstElement()).getOutX()*scale);
			if (isRightSide) {
				if ((rrx+rtailLen)>rlx) rtailLen = rlx-rrx; 
			} 
			else {
				if ((rrx-rtailLen)<rlx) rtailLen = rrx-rlx; 
			}
			if (rtailLen<0) rtailLen=0;
		}*/

		// tail
		// g.setColor(getColor());
		Color c = getColor();
		if (c==Constants.BACKGROUND_COLOR)
			if (c==Color.black)
				c=Color.white;
			else
				c=Color.black;
		g.setColor(c);

		if (isRightSide)
			g.drawLine(rrx+2*r, rry, rrx+rtailLen-r, rry);
		else 
			g.drawLine(rrx-rtailLen+r, rry, rrx-3*r, rry);

		// !!! more intergroup inlinks?!
		LinkDrawer.drawInIntergroupLink(g, (OutLink)outlinks.firstElement(), this, isRightSide);
	}

}
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 22:22:37)
 * @return com.cosylab.vdct.inspector.InspectableProperty
 */
public com.cosylab.vdct.inspector.InspectableProperty getCommentProperty() {
	return null;
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
 * Creation date: (29.1.2001 21:34:27)
 * @return int
 */
public int getInX() {
	if (isRight())
		return getX()+getWidth()+Constants.TAIL_LENGTH;
	else
		return getX()-Constants.TAIL_LENGTH;
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 21:34:27)
 * @return int
 */
public int getInY() {
	return getY()+getHeight()/2;
}
/**
 * Insert the method's description here.
 * Creation date: (4.5.2001 8:00:56)
 * @return java.util.Vector
 */
public java.util.Vector getItems() {
	
	if (getLinkCount()==0) return null;
	
	Vector items = new Vector();

	ActionListener al = createPopupmenuHandler();

/*	JMenuItem colorItem = new JMenuItem(colorString);
	colorItem.addActionListener(al);
	items.addElement(colorItem);

	JMenuItem addItem = new JMenuItem(addConnectorString);
	addItem.addActionListener(al);
	items.addElement(addItem);

	items.add(new JSeparator());
*/
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
	
	JMenuItem removeItem = new JMenuItem(removeString);
	removeItem.addActionListener(al);
	items.addElement(removeItem);

	return items;
}
/**
 * Insert the method's description here.
 * Creation date: (4.5.2001 9:53:35)
 * @return java.util.Vector
 */
public int getLinkCount() {
	return outlinks.size();
}
/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 13:07:04)
 * @return com.cosylab.vdct.vdb.GUISeparator
 */
public static com.cosylab.vdct.vdb.GUISeparator getLinkSeparator() {
	if (linkSeparator==null) linkSeparator = new GUISeparator("Link");
	return linkSeparator;
}
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 22:22:37)
 * @return java.lang.String
 */
public String getName() {
	return fieldData.getFullName();
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

	InspectableProperty[] properties = new InspectableProperty[starts.size()*5];

	int i = 0;
	VDBFieldData fieldData;
	e = starts.elements();
	while (e.hasMoreElements())
	{
		fieldData = ((EPICSLinkOut)e.nextElement()).getFieldData();
		properties[i++]=getLinkSeparator();
		properties[i++]=new GUISeparator(fieldData.getFullName());
		properties[i++]=new FieldInfoProperty(fieldData.getRecord().getField("DTYP"));
		properties[i++]=EPICSLinkOut.getFieldSeparator();
		properties[i++]=fieldData;
	}
	return properties;
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
		return super.isRight();
	else {
		OutLink first = (OutLink)outlinks.firstElement();
		if (first.getLayerID().equals(getLayerID()))
			return (first.getOutX()>(getX()+getWidth()/2));
		else
			return super.isRight();
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
 * Creation date: (4.5.2001 9:20:14)
 * @return java.lang.String
 */
public String toString() {
	return "Variable: "+getName();
}
/**
 * Insert the method's description here.
 * Creation date: (24.4.2001 19:08:57)
 */
public void validateLink() {
	if (outlinks.size()==0)
		destroy();
}

/**
 * @see com.cosylab.vdct.inspector.Inspectable#getModeNames()
 */
public ArrayList getModeNames()
{
	return null;
}


}
