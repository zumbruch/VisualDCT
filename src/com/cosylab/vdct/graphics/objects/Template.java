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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.cosylab.vdct.Constants;
import com.cosylab.vdct.graphics.FontMetricsBuffer;
import com.cosylab.vdct.graphics.ViewState;
import com.cosylab.vdct.graphics.popup.Popupable;
import com.cosylab.vdct.inspector.Inspectable;
import com.cosylab.vdct.inspector.InspectableProperty;
import com.cosylab.vdct.inspector.InspectorManager;
import com.cosylab.vdct.vdb.GUISeparator;
import com.cosylab.vdct.vdb.LinkProperties;
import com.cosylab.vdct.vdb.MonitoredActionProperty;
import com.cosylab.vdct.vdb.MonitoredProperty;
import com.cosylab.vdct.vdb.MonitoredPropertyListener;
import com.cosylab.vdct.vdb.NameValueInfoProperty;
import com.cosylab.vdct.vdb.VDBData;
import com.cosylab.vdct.vdb.VDBFieldData;
import com.cosylab.vdct.vdb.VDBTemplateField;
import com.cosylab.vdct.vdb.VDBTemplateInstance;

/**
 * Graphical representation of templates.
 * @author Matej
 */
public class Template
	extends LinkManagerObject
	implements Descriptable, Movable, Inspectable, Popupable, Flexible, Selectable, Clipboardable, Hub, MonitoredPropertyListener
{

	VDBTemplateInstance templateData = null;
	String description = null;

	private static ImageIcon icon = null;

	// properties field
	protected int rfieldLabelX;
	protected int rfieldLabelY;
	protected int rfieldRowHeight;
	protected Font fieldFont = null;

	// templateid (fileName) label
	protected int ridLabelX;
	protected int ridLabelY;
	protected String idlabel;
	protected Font idFont = null;

	protected int initY;
	protected int rlinkY;

	private static GUISeparator templateSeparator = null;
	private static GUISeparator inputsSeparator = null;
	private static GUISeparator outputsSeparator = null;
	private static GUISeparator templateInstanceSeparator = null;
	private static GUISeparator propertiesSeparator = null;
	private final static String fieldMaxStr = "01234567890123456789012345";

	protected Vector invalidLinks = null;
	
	/**
	 * Insert the method's description here.
	 * Creation date: (21.12.2000 20:40:53)
	 * @param parent com.cosylab.vdct.graphics.objects.ContainerObject
	 * @param templateData The templateData to set
	 */
	public Template(ContainerObject parent, VDBTemplateInstance templateData) {
		super(parent);
		this.templateData = templateData;

		invalidLinks = new Vector();

		setColor(Color.black);
		setWidth(Constants.TEMPLATE_WIDTH);
		setHeight(Constants.TEMPLATE_INITIAL_HEIGHT);
		
		initializeLinkFields();		
		//forceValidation();

	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.VisibleObject#draw(Graphics, boolean)
	 */
	protected void draw(Graphics g, boolean hilited)
	{
		ViewState view = ViewState.getInstance();
	
		int rrx = getRx()-view.getRx();
		int rry = getRy()-view.getRy();
		int rwidth = getRwidth();
		int rheight = getRheight();
			
		// clipping
		if ((!(rrx>view.getViewWidth()) || (rry>view.getViewHeight())
		    || ((rrx+rwidth)<0) || ((rry+rheight)<0))) {
	
			if (!hilited) g.setColor(Constants.RECORD_COLOR);
			else if (view.isPicked(this)) g.setColor(Constants.PICK_COLOR);
			else if (view.isSelected(this) ||
					 view.isBlinking(this)) g.setColor(Constants.SELECTION_COLOR);
			else g.setColor(Constants.RECORD_COLOR);

			Color fillColor = g.getColor();
					
			g.fillRect(rrx, rry, rwidth, rheight);


			if (!hilited) g.setColor(Constants.FRAME_COLOR);
			else g.setColor((this==view.getHilitedObject()) ? 
							Constants.HILITE_COLOR : Constants.FRAME_COLOR);
	
			g.drawRect(rrx, rry, rwidth, rheight);
	
			// colors
			if (invalidLinks.size()>0)
			{
				if (fillColor!=Color.red)
					g.setColor(Color.red);
				else
					g.setColor(Color.black);
			}

			if (getFont()!=null) {
				g.setFont(getFont());
				g.drawString(getLabel(), rrx+getRlabelX(), rry+getRlabelY());
			}

			if (idFont!=null) {
				g.setFont(idFont);
				g.drawString(idlabel, rrx+ridLabelX, rry+ridLabelY);
			}

			// middle line
			int ox = (int) (10 * getRscale());

			//g.drawLine(rrx + ox, rry + rinitY, rrx + rwidth - ox, rry + rinitY);
			//g.drawLine(rrx + ox, rry + rlinkY, rrx + rwidth - ox, rry + rlinkY);

			if (fieldFont != null)
			{
				g.setFont(fieldFont);
				FontMetrics fm = FontMetricsBuffer.getInstance().getFontMetrics(fieldFont);
				String val;
				int px = rrx + rfieldLabelX;
				int py = rry + rfieldLabelY;
				
		 		java.util.Iterator e = templateData.getProperties().keySet().iterator();
				while (e.hasNext())
				{				
					String name = e.next().toString();
					val = name + "=" + templateData.getProperties().get(name).toString();
					while ((fm.stringWidth(val) + ox) > rwidth)
						val = val.substring(0, val.length() - 2);
					g.drawString(val, px, py);
					py += rfieldRowHeight;
				}

/*
				Enumeration e = templateData.getProperties().keys();
				while (e.hasMoreElements()) {
					String name = e.nextElement().toString();
					val = name + "=" + templateData.getProperties().get(name).toString();
					while ((fm.stringWidth(val) + ox) > rwidth)
						val = val.substring(0, val.length() - 2);
					g.drawString(val, px, py);
					py += rfieldRowHeight;
				}

 */ 
 
			}

		}

		paintSubObjects(g, hilited);

	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.VisibleObject#getHashID()
	 */
	public String getHashID()
	{
		return templateData.getName();
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.VisibleObject#revalidatePosition()
	 */
	public void revalidatePosition()
	{
		  setRx((int)(getX()*getRscale()));
		  setRy((int)(getY()*getRscale()));

		  // sub-components
		  revalidateFieldsPosition();
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.VisibleObject#validate()
	 */
	protected void validate()
	{
		  revalidatePosition();

		  double scale = getRscale();
		  int rwidth = (int)(getWidth()*scale);
		  int height = Constants.TEMPLATE_INITIAL_HEIGHT;

		  initY = height;

		  int irheight = (int)(scale*height);

		  // set appropriate font size
		  int x0 = (int)(24*scale);		// insets
		  int y0 = (int)(12*scale);


		  // fields

		  int fieldRows = Math.max(templateData.getInputs().size(), templateData.getOutputs().size());
		  height += fieldRows * Constants.FIELD_HEIGHT;
		 // height = Math.max(height, Constants.TEMPLATE_MIN_HEIGHT);
		  int frheight = (int)(scale*height);

		  rlinkY = frheight;
		  
		  // properties

		  int xx0 = (int)(14*scale);		// insets
		  int yy0 = (int)(8*scale);

		  if (rwidth<(2*xx0)) fieldFont = null;
		  else
			  fieldFont = FontMetricsBuffer.getInstance().getAppropriateFont(
				  			 Constants.DEFAULT_FONT, Font.PLAIN, 
			 	 			 fieldMaxStr, rwidth-x0, frheight-y0);

		  int ascent = 0;
		  rfieldRowHeight = 0;
		  if (fieldFont!=null)
		  {
			  FontMetrics fm = FontMetricsBuffer.getInstance().getFontMetrics(fieldFont);
			  rfieldLabelX = xx0;
		 	  rfieldLabelY = frheight+2*fm.getAscent();
			  rfieldRowHeight = fm.getHeight();
			  ascent = fm.getAscent();
		  }
		
		  int rheight = frheight + yy0 + rfieldRowHeight*templateData.getProperties().size()+ascent;
		  setHeight((int)(rheight/scale));

		  setRwidth(rwidth);
		  setRheight(rheight);
		
		  // description

		  int idLabelHeight = (int)(Constants.FIELD_HEIGHT*scale);
		  
		  setLabel(getDescription());
		
		  Font font;
		  font = FontMetricsBuffer.getInstance().getAppropriateFont(
			  			Constants.DEFAULT_FONT, Font.PLAIN, 
			  			getLabel(), rwidth-x0, irheight-y0/*-idLabelHeight*/);
		
		  if (rwidth<(2*x0)) font = null;
		  else
		  if (font!=null) {
			  FontMetrics fm = FontMetricsBuffer.getInstance().getFontMetrics(font);
			  setRlabelX((rwidth-fm.stringWidth(getLabel()))/2);
		 	  setRlabelY((irheight-fm.getHeight()/*+idLabelHeight*/)/2+fm.getAscent());
		  }
		  setFont(font);

		  // id label
		  
		  idlabel = templateData.getTemplate().getId();
		  if (rwidth<(2*x0)) idFont = null;
		  else
			  idFont = FontMetricsBuffer.getInstance().getAppropriateFont(
				  			 Constants.DEFAULT_FONT, Font.PLAIN, 
			 	 			 idlabel, rwidth-x0, idLabelHeight);
		
		  if (idFont!=null) {
			  FontMetrics fm = FontMetricsBuffer.getInstance().getFontMetrics(idFont);
			  ridLabelX = (rwidth-fm.stringWidth(idlabel))/2;
		 	  ridLabelY = (idLabelHeight-fm.getHeight())/2+fm.getAscent();
		  }

		  
	      // sub-components
		  revalidatePosition();
		  validateFields();
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.Movable#checkMove(int, int)
	 */
	public boolean checkMove(int dx, int dy)
	{
		ViewState view = ViewState.getInstance();
	
		if ((getX()<-dx) || (getY()<-dy) || 
			(getX()>(view.getWidth()-getWidth()-dx)) || (getY()>(view.getHeight()-getHeight()-dy)))
			return false;
		else
			return true;
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.Movable#move(int, int)
	 */
	public boolean move(int dx, int dy)
	{
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
			icon = new javax.swing.ImageIcon(getClass().getResource("/images/template.gif"));
		return icon;
	}

	/**
	 * @see com.cosylab.vdct.inspector.Inspectable#getName()
	 */
	public String getName()
	{
		return templateData.getName();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10.1.2001 14:49:50)
	 * @return java.lang.String
	 */
	public String toString() {
		return getDescription() + " [" + templateData.getName() + "]";
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (3.2.2001 13:07:04)
	 * @return com.cosylab.vdct.vdb.GUISeparator
	 */
	public static com.cosylab.vdct.vdb.GUISeparator getTemplateSeparator() {
		if (templateSeparator==null) templateSeparator = new GUISeparator("Template");
		return templateSeparator;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (3.2.2001 13:07:04)
	 * @return com.cosylab.vdct.vdb.GUISeparator
	 */
	public static com.cosylab.vdct.vdb.GUISeparator getTemplateInstanceSeparator() {
		if (templateInstanceSeparator==null) templateInstanceSeparator = new GUISeparator("Template Instance");
		return templateInstanceSeparator;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (3.2.2001 13:07:04)
	 * @return com.cosylab.vdct.vdb.GUISeparator
	 */
	public static com.cosylab.vdct.vdb.GUISeparator getInputsSeparator() {
		if (inputsSeparator==null) inputsSeparator = new GUISeparator("Inputs");
		return inputsSeparator;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (3.2.2001 13:07:04)
	 * @return com.cosylab.vdct.vdb.GUISeparator
	 */
	public static com.cosylab.vdct.vdb.GUISeparator getOutputsSeparator() {
		if (outputsSeparator==null) outputsSeparator = new GUISeparator("Outputs");
		return outputsSeparator;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (3.2.2001 13:07:04)
	 * @return com.cosylab.vdct.vdb.GUISeparator
	 */
	public static com.cosylab.vdct.vdb.GUISeparator getPropertiesSeparator() {
		if (propertiesSeparator==null) propertiesSeparator = new GUISeparator("Properties");
		return propertiesSeparator;
	}

	/**
	 * @see com.cosylab.vdct.inspector.Inspectable#getProperties(int)
	 */
	public InspectableProperty[] getProperties(int mode)
	{
		Vector items = new Vector();

		items.addElement(getTemplateSeparator());
		items.addElement(new NameValueInfoProperty("Class", templateData.getTemplate().getId()));
		items.addElement(new NameValueInfoProperty("FileName", templateData.getTemplate().getFileName()));

		items.addElement(getTemplateInstanceSeparator());

		items.addElement(getInputsSeparator());
		Enumeration e = templateData.getInputs().elements();
		while (e.hasMoreElements())
			items.addElement(e.nextElement());

		items.addElement(getOutputsSeparator());
		e = templateData.getOutputs().elements();
		while (e.hasMoreElements())
			items.addElement(e.nextElement());
		
		items.addElement(getPropertiesSeparator());

/*
  		e = templateData.getProperties().keys();
		while (e.hasMoreElements())
		{
			String name = e.nextElement().toString();
			items.addElement(new MonitoredProperty(name, (String)templateData.getProperties().get(name), this));
		}
*/		
  		java.util.Iterator i = templateData.getProperties().keySet().iterator();
		while (i.hasNext())
		{
			String name = i.next().toString();
			items.addElement(new MonitoredProperty(name, (String)templateData.getProperties().get(name), this));
		}

		String addString = "Add property...";
		items.addElement(new MonitoredActionProperty(addString, this));
	
		InspectableProperty[] properties = new InspectableProperty[items.size()];
		items.copyInto(properties);
		return properties;
	}

	/**
	 * @see com.cosylab.vdct.graphics.popup.Popupable#getItems()
	 */
	public Vector getItems()
	{
		Hashtable allLinks = (Hashtable)templateData.getInputs().clone();
		allLinks.putAll(templateData.getOutputs());
		return getLinkMenus(allLinks.elements());
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.Flexible#getFlexibleName()
	 */
	public String getFlexibleName()
	{
		return templateData.getName();
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.Visitable#accept(Visitor)
	 */
	public void accept(Visitor visitor)
	{
	}

	/**
	 * Returns the templateData.
	 * @return VDBTemplateInstance
	 */
	public VDBTemplateInstance getTemplateData()
	{
		return templateData;
	}

	/**
	 * Returns the description.
	 * @return String
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description.
	 * @param description The description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
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
 * Default impmlementation for square (must be rescaled)
 * Creation date: (19.12.2000 20:20:20)
 * @return com.cosylab.visible.objects.VisibleObject
 * @param px int
 * @param py int
 */
public VisibleObject intersects(int px, int py) {

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
 * Creation date: (26.1.2001 17:18:51)
 */
private void revalidateFieldsPosition() {

  int lx = getX();
  int rx = getX()+getWidth()/2; 
  int ly = getY()+initY;
  int ry = getY()+initY;
  Enumeration e = subObjectsV.elements();
  EPICSLink field; Object obj;
  while (e.hasMoreElements()) {
	obj = e.nextElement();
	if (obj instanceof Field) {
		field = (EPICSLink)obj;
		if (field.isRight())
		{
			field.revalidatePosition(rx, ry);
			ry+=field.getHeight();
		}
		else
		{
			field.revalidatePosition(lx, ly);
			ly+=field.getHeight();
		}
	}
  }

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
 * Creation date: (26.1.2001 17:19:47)
 */
private void validateFields() {

	Enumeration e = subObjectsV.elements();
	Object obj;
	while (e.hasMoreElements()) {
		obj = e.nextElement();
		if (obj instanceof Field)
			((VisibleObject)obj).validate();
	}
	
}

/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 17:19:47)
 */
private void updateTemplateFields() {

	Enumeration e = subObjectsV.elements();
	Object obj;
	while (e.hasMoreElements()) {
		obj = e.nextElement();
		if (obj instanceof TemplateEPICSLink)
			((TemplateEPICSLink)obj).updateTemplateLink();
	}
	
}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 9:36:15)
 * @param field com.cosylab.vdct.vdb.VDBFieldData
 */
private EPICSLink createLinkField(VDBFieldData field)
{
	EPICSLink link = null;	
	int type = LinkProperties.getType(field);
	switch (type) {
		case LinkProperties.INLINK_FIELD:
			link = new TemplateEPICSInLink(this, field);
			break;
		case LinkProperties.OUTLINK_FIELD:
			link = new TemplateEPICSOutLink(this, field);
			break;
		case LinkProperties.FWDLINK_FIELD:
			link = new TemplateEPICSFwdLink(this, field);
			break;
		case LinkProperties.VARIABLE_FIELD:
			link = new TemplateEPICSVarLink(this, field);
			break;
	}
	return link;
}

/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 17:19:47)
 */
private void initializeLinkFields()
{
	clear();

	// inputs
	Enumeration e = templateData.getInputs().elements();
	while (e.hasMoreElements())
	{
		VDBTemplateField tf = (VDBTemplateField)e.nextElement();
		EPICSLink link = createLinkField(tf);
		link.setRight(false);
		if (link!=null)
			addSubObject(tf.getAlias(), link);
	}

	// outputs
	e = templateData.getOutputs().elements();
	while (e.hasMoreElements())
	{
		VDBTemplateField tf = (VDBTemplateField)e.nextElement();
		EPICSLink link = createLinkField(tf);
		link.setRight(true);
		if (link!=null)
			addSubObject(tf.getAlias(), link);
	}
}

/**
 * @param link com.cosylab.vdct.graphics.objects.Linkable
 */
public void addLink(Linkable link)
{
}

/**
 * @param link com.cosylab.vdct.graphics.objects.Linkable
 */
public void removeLink(Linkable link)
{
}

/**
 * Insert the method's description here.
 * Creation date: (27.1.2001 16:12:03)
 * @param field com.cosylab.vdct.vdb.VDBFieldData
 */
public void fieldChanged(VDBFieldData field) {
	boolean repaint = false;

	if (manageLink(field)) repaint=true;
	
	if (repaint) {
		unconditionalValidation();
		com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
	}
}

/**
 */
public VDBFieldData getField(String name) {
	VDBFieldData field = (VDBFieldData)templateData.getInputs().get(name);
	if (field==null)
		field = (VDBFieldData)templateData.getOutputs().get(name);
	return field;
}


/**
 * @see com.cosylab.vdct.vdb.MonitoredPropertyListener#addProperty()
 */
public void addProperty()
{
	String message = "Enter property name:";
	int type = JOptionPane.QUESTION_MESSAGE;
	while (true)
	{
		String reply = JOptionPane.showInputDialog( null,
			                           message,
			                           "Add property...",
			                           type );
		if (reply!=null)
		{
			if (!templateData.getProperties().containsKey(reply))
			{
				templateData.getProperties().put(reply, nullString);
				updateTemplateFields();
				InspectorManager.getInstance().updateObject(this);
				unconditionalValidation();
				com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
			}
			else
			{
				message = "Property '"+reply+"' already exists. Enter other name:";
				type = JOptionPane.WARNING_MESSAGE;
				continue;
			}
		}
		
		break;
	}
}

/**
 * @see com.cosylab.vdct.vdb.MonitoredPropertyListener#propertyChanged(MonitoredProperty)
 */
public void propertyChanged(MonitoredProperty property)
{
	templateData.getProperties().put(property.getName(), property.getValue());

	updateTemplateFields();
	InspectorManager.getInstance().updateProperty(this, null);
	unconditionalValidation();
	com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
}

/**
 * @see com.cosylab.vdct.vdb.MonitoredPropertyListener#removeProperty(MonitoredProperty)
 */
public void removeProperty(MonitoredProperty property)
{
	templateData.getProperties().remove(property.getName());
	
	updateTemplateFields();
	InspectorManager.getInstance().updateObject(this);
	unconditionalValidation();
	com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
	
}

/**
 * @see com.cosylab.vdct.vdb.MonitoredPropertyListener#renameProperty(MonitoredProperty)
 */
public void renameProperty(MonitoredProperty property)
{
	String message = "Enter new property name of '"+property.getName()+"':";
	int type = JOptionPane.QUESTION_MESSAGE;
	while (true)
	{
		String reply = JOptionPane.showInputDialog( null,
			                           message,
			                           "Rename property...",
			                            type);
		if (reply!=null)
		{
			if (!templateData.getProperties().containsKey(reply))
			{
				Object value = templateData.getProperties().remove(property.getName());
				templateData.getProperties().put(reply, value);

				updateTemplateFields();
				InspectorManager.getInstance().updateObject(this);
				unconditionalValidation();
				com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
			}
			else
			{
				message = "Property '"+reply+"' already exists. Enter other name:";
				type = JOptionPane.WARNING_MESSAGE;
				continue;
			}
		
		}
		
		break;
	}
}

/**
 */
public void addInvalidLink(EPICSLink field)
{
	if (!invalidLinks.contains(field))
		invalidLinks.addElement(field);
}

/**
 */
public void removeInvalidLink(EPICSLink field)
{
	invalidLinks.remove(field);
}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:59:21)
 */
public void destroy() {
	if (!isDestroyed()) {
		super.destroy();
		destroyFields();
		
		clear();
		getParent().removeObject(Group.substractObjectName(getName()));
	}
}

/**
 * @see com.cosylab.vdct.graphics.objects.Flexible#copyToGroup(String)
 */
public boolean copyToGroup(java.lang.String group) {

	String newName;
	if (group.equals(nullString))
		newName = Group.substractObjectName(templateData.getName());
	else
		newName = group+Constants.GROUP_SEPARATOR+
				  Group.substractObjectName(templateData.getName());

	// object with new name already exists, add suffix ///!!!
	//Object obj;
	while (Group.getRoot().findObject(newName, true)!=null)
		newName += Constants.COPY_SUFFIX;

	ViewState view = ViewState.getInstance();


	VDBTemplateInstance theDataCopy = VDBData.copyVDBTemplateInstance(templateData);
	theDataCopy.setName(newName);
	Template theTemplateCopy = new Template(null, theDataCopy);
	Group.getRoot().addSubObject(theDataCopy.getName(), theTemplateCopy, true);
	theTemplateCopy.setDescription(getTemplateData().getTemplate().getDescription());
	theTemplateCopy.setX(getX()); theTemplateCopy.setY(getY());
	theTemplateCopy.move(20-view.getRx(), 20-view.getRy());
	theTemplateCopy.updateTemplateFields();
	unconditionalValidation();
	return true;
}

/**
 * @see com.cosylab.vdct.graphics.objects.Flexible#moveToGroup(String)
 */
public boolean moveToGroup(java.lang.String group) {
	String currentParent = Group.substractParentName(templateData.getName());
	if (group.equalsIgnoreCase(currentParent)) return false;
	
	String oldName = getName();
	String newName;
	if (group.equals(nullString))
		newName = Group.substractObjectName(templateData.getName());
	else
		newName = group+Constants.GROUP_SEPARATOR+
				  Group.substractObjectName(templateData.getName());;

	// object with new name already exists, add suffix // !!!
	Object obj;
	while ((obj=Group.getRoot().findObject(newName, true))!=null)
	{
		if (obj==this)	// it's me :) already moved, fix data
		{
			templateData.setName(newName);
			this.updateTemplateFields();
			fixLinks();
			return true;
		}
		else
			//return false;
			newName += Constants.MOVE_SUFFIX;
			return rename(newName);
	}
	
	getParent().removeObject(Group.substractObjectName(getName()));
	setParent(null);
	Group.getRoot().addSubObject(newName, this, true);

	templateData.setName(newName);
	this.updateTemplateFields();
	fixLinks();
	unconditionalValidation();

	return true;
}

/**
 * @see com.cosylab.vdct.graphics.objects.Flexible#rename(String)
 */
public boolean rename(java.lang.String newName) {
	
	String newObjName = Group.substractObjectName(newName);
	String oldObjName = Group.substractObjectName(getName());


	if (!oldObjName.equals(newObjName))
	{
		getParent().removeObject(oldObjName);
		String fullName = com.cosylab.vdct.util.StringUtils.replace(getName(), oldObjName, newObjName);
		templateData.setName(fullName);
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
				id = com.cosylab.vdct.util.StringUtils.replace(id, oldObjName, newObjName);
				connector.setID(id);
			}
		}
	}
	
	this.updateTemplateFields();

	// move if needed
	if (!moveToGroup(Group.substractParentName(newName)))
		fixLinks();			// fix needed

	return true;
	
}


/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:59:54)
 */
protected void destroyFields() {

	Object[] objs = new Object[subObjectsV.size()];
	subObjectsV.copyInto(objs);
	for (int i=0; i < objs.length; i++)
	{
		if (objs[i] instanceof TemplateEPICSLink)
		{
			TemplateEPICSLink tel = (TemplateEPICSLink)objs[i];
			tel.destroyAndRemove();
		}
		else
			((VisibleObject)objs[i]).destroy();
	}
}




}
