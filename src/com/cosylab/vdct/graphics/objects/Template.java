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

import java.awt.Graphics;
import java.util.Vector;

import javax.swing.Icon;

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
	implements Movable, Inspectable, Popupable, Flexible, Selectable, Clipboardable
{

	VDBTemplateInstance templateData = null;


	/**
	 * Insert the method's description here.
	 * Creation date: (21.12.2000 20:40:53)
	 * @param parent com.cosylab.vdct.graphics.objects.ContainerObject
	 * @param templateData The templateData to set
	 */
	public Template(ContainerObject parent, VDBTemplateInstance templateData) {
		super(parent);
		this.templateData = templateData;
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.VisibleObject#draw(Graphics, boolean)
	 */
	protected void draw(Graphics g, boolean hilited)
	{
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.VisibleObject#getHashID()
	 */
	public String getHashID()
	{
		return null;
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.VisibleObject#revalidatePosition()
	 */
	public void revalidatePosition()
	{
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.VisibleObject#validate()
	 */
	protected void validate()
	{
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.Movable#checkMove(int, int)
	 */
	public boolean checkMove(int dx, int dy)
	{
		return false;
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.Movable#move(int, int)
	 */
	public boolean move(int dx, int dy)
	{
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

}
