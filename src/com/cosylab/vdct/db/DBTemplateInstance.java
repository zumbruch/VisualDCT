package com.cosylab.vdct.db;

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

import java.util.Hashtable;


/**
 * @author Matej
 */
public class DBTemplateInstance
{
	String templateID;
	Hashtable properties = null;

	protected int x = -1;			// used for layout
	protected int y = -1;
	protected java.awt.Color color;
	protected String description;

	/**
	 * Constructor.
	 */
	public DBTemplateInstance(String templateID, int x, int y, java.awt.Color color, String description)
	{
		properties = new Hashtable();
		this.templateID = templateID;
		this.x=x; this.y=y;
		this.color=color;
		this.description=description;
	}
	
	/**
	 * Returns the properties.
	 * @return Hashtable
	 */
	public Hashtable getProperties()
	{
		return properties;
	}

	/**
	 * Returns the templateID.
	 * @return String
	 */
	public String getTemplateID()
	{
		return templateID;
	}

	/**
	 * Sets the properties.
	 * @param properties The properties to set
	 */
	public void setProperties(Hashtable properties)
	{
		this.properties = properties;
	}

	/**
	 * Sets the templateID.
	 * @param templateID The templateID to set
	 */
	public void setTemplateID(String templateID)
	{
		this.templateID = templateID;
	}

	/**
	 * Returns the color.
	 * @return java.awt.Color
	 */
	public java.awt.Color getColor()
	{
		return color;
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
	 * Returns the x.
	 * @return int
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * Returns the y.
	 * @return int
	 */
	public int getY()
	{
		return y;
	}

	/**
	 * Sets the color.
	 * @param color The color to set
	 */
	public void setColor(java.awt.Color color)
	{
		this.color = color;
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
	 * Sets the x.
	 * @param x The x to set
	 */
	public void setX(int x)
	{
		this.x = x;
	}

	/**
	 * Sets the y.
	 * @param y The y to set
	 */
	public void setY(int y)
	{
		this.y = y;
	}

}
