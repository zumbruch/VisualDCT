package com.cosylab.vdct.graphics.objects;

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
 * @author: Matej Sekoranja
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

	int rry = getRy()+getRheight()/2-view.getRy();
	
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
		g.setColor(getColor());
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
	Vector items = new Vector();

/*	JMenuItem colorItem = new JMenuItem(colorString);
	colorItem.addActionListener(createPopupmenuHandler());
	items.addElement(colorItem);

	JMenuItem addItem = new JMenuItem(addConnectorString);
	addItem.addActionListener(createPopupmenuHandler());
	items.addElement(addItem);

	items.add(new JSeparator());
*/
	Record parRec = (Record)getParent();
	boolean isFirst = parRec.isFirstField(this);
	boolean isLast = parRec.isLastField(this);
	

	if (!isFirst)
	{
		JMenuItem upItem = new JMenuItem(moveUpString);
		upItem.addActionListener(createPopupmenuHandler());
		upItem.setIcon(new ImageIcon(getClass().getResource("/up.gif")));
		items.addElement(upItem);
	}

	if (!isLast)
	{
		JMenuItem downItem = new JMenuItem(moveDownString);
		downItem.addActionListener(createPopupmenuHandler());
		downItem.setIcon(new ImageIcon(getClass().getResource("/down.gif")));
		items.addElement(downItem);
	}

	if (!(isFirst && isLast))
		items.add(new JSeparator());

	JMenuItem removeItem = new JMenuItem(removeString);
	removeItem.addActionListener(createPopupmenuHandler());
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
	return fieldData.getRecord().getName()+Constants.FIELD_SEPARATOR+fieldData.getName();
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
public com.cosylab.vdct.inspector.InspectableProperty[] getProperties() {

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
}
