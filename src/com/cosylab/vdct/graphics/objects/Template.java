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
import java.util.Vector;

import javax.swing.Icon;

import com.cosylab.vdct.Constants;
import com.cosylab.vdct.graphics.FontMetricsBuffer;
import com.cosylab.vdct.graphics.ViewState;
import com.cosylab.vdct.graphics.popup.Popupable;
import com.cosylab.vdct.inspector.Inspectable;
import com.cosylab.vdct.inspector.InspectableProperty;
import com.cosylab.vdct.vdb.VDBTemplateInstance;

/**
 * Graphical representation of templates.
 * @author Matej
 */
public class Template
	extends VisibleObject
	implements Descriptable, Movable, Inspectable, Popupable, Flexible, Selectable, Clipboardable
{

	VDBTemplateInstance templateData = null;
	String description = null;

	/**
	 * Insert the method's description here.
	 * Creation date: (21.12.2000 20:40:53)
	 * @param parent com.cosylab.vdct.graphics.objects.ContainerObject
	 * @param templateData The templateData to set
	 */
	public Template(ContainerObject parent, VDBTemplateInstance templateData) {
		super(parent);
		this.templateData = templateData;

		//!!!
		setColor(Color.black);
		setWidth(Constants.GROUP_WIDTH);
		setHeight(Constants.GROUP_HEIGHT);
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
		
			g.fillRect(rrx, rry, rwidth, rheight);
			if (!hilited) g.setColor(Constants.FRAME_COLOR);
			else g.setColor((this==view.getHilitedObject()) ? 
							Constants.HILITE_COLOR : Constants.FRAME_COLOR);
	
			g.drawRect(rrx, rry, rwidth, rheight);
	
			if (getFont()!=null) {
				g.setFont(getFont());
				g.drawString(getLabel(), rrx+getRlabelX(), rry+getRlabelY());
			}
		
		}
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.VisibleObject#getHashID()
	 */
	public String getHashID()
	{
		return "en ID"; // !!!id of getTemplaTeData() iz DB fajla
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.VisibleObject#revalidatePosition()
	 */
	public void revalidatePosition()
	{
		  setRx((int)(getX()*getRscale()));
		  setRy((int)(getY()*getRscale()));
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.VisibleObject#validate()
	 */
	protected void validate()
	{
		  revalidatePosition();
			
		  double scale = getRscale();
		  int rwidth = (int)(getWidth()*scale);
		  int rheight = (int)(getHeight()*scale);
		  setRwidth(rwidth);
		  setRheight(rheight);
		
		  // set appropriate font size
		  int x0 = (int)(8*scale);		// insets
		  int y0 = (int)(4*scale);
		
		  setLabel(getDescription());
		
		  Font font;
		  font = FontMetricsBuffer.getInstance().getAppropriateFont(
			  			Constants.DEFAULT_FONT, Font.PLAIN, 
			  			getLabel(), rwidth-x0, rheight-y0);
		
		  if (rwidth<(2*x0)) font = null;
		  else
		  if (font!=null) {
			  FontMetrics fm = FontMetricsBuffer.getInstance().getFontMetrics(font);
			  setRlabelX((rwidth-fm.stringWidth(getLabel()))/2);
		 	  setRlabelY((rheight-fm.getHeight())/2+fm.getAscent());
		  }
		  setFont(font);
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
		return null;
	}

	/**
	 * @see com.cosylab.vdct.inspector.Inspectable#getName()
	 */
	public String getName()
	{
		return null;
	}

	/**
	 * @see com.cosylab.vdct.inspector.Inspectable#getProperties(int)
	 */
	public InspectableProperty[] getProperties(int mode)
	{
		return null;
	}

	/**
	 * @see com.cosylab.vdct.graphics.popup.Popupable#getItems()
	 */
	public Vector getItems()
	{
		return null;
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.Flexible#copyToGroup(String)
	 */
	public boolean copyToGroup(String group)
	{
		return false;
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.Flexible#getFlexibleName()
	 */
	public String getFlexibleName()
	{
		return null;
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.Flexible#moveToGroup(String)
	 */
	public boolean moveToGroup(String group)
	{
		return false;
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.Flexible#rename(String)
	 */
	public boolean rename(String newName)
	{
		return false;
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

}
