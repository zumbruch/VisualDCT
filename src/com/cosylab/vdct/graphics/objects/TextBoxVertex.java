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

package com.cosylab.vdct.graphics.objects;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import com.cosylab.vdct.*;
import com.cosylab.vdct.graphics.*;
import com.cosylab.vdct.graphics.popup.*;
import com.cosylab.vdct.events.*;

/**
 * @author ssah
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
public class TextBoxVertex extends VisibleObject implements Movable, Selectable
{
	
private String hashId;
private TextBoxVertex partnerVertex;
private boolean isAlwaysDrawn;

private TextBox textBox;

private String getAvailableHashId()
{
	int grLineNumber = 0;
	String testHashId = "TextBox" + (new Integer(grLineNumber)).toString();
	
	while(getParent().containsObject(testHashId))
	{
		grLineNumber++;
		testHashId = "TextBox" + (new Integer(grLineNumber)).toString();		
	}

	return testHashId;
}

public boolean move(int dx, int dy)
{
	if(checkMove(dx, dy))
	{
		setX(super.getX() + dx);
		setY(super.getY() + dy);
		
		revalidatePosition();
		
		return true;
	}

	return false;
}

public TextBoxVertex(ContainerObject parent, int parX, int parY, TextBoxVertex parPartnerVertex)
{
	super(parent);

	setX(parX - Constants.CONNECTOR_WIDTH / 2);
	setY(parY - Constants.CONNECTOR_HEIGHT / 2);
	setWidth(Constants.CONNECTOR_WIDTH);
	setHeight(Constants.CONNECTOR_HEIGHT);
	
	partnerVertex = parPartnerVertex;
	isAlwaysDrawn = true;
	
	hashId = getAvailableHashId();
}

public void accept(Visitor visitor)
{
	visitor.visitGroup();
}

public boolean checkMove(int dx, int dy)
{
	ViewState view = ViewState.getInstance();

	if((getX() < - dx) || (getY() < - dy)
		|| (getX() > (view.getWidth() - getWidth() - dx))
		|| (getY() > (view.getHeight() - getHeight() - dy)))
	{
		return false;
	}
	
	return true;
}

protected void draw(Graphics g, boolean hilited)
{
	if((!isAlwaysDrawn) && (!hilited))
		return;

	ViewState view = ViewState.getInstance();

	int offsetX = view.getRx();
	int offsetY = view.getRy();
	
	int posX = getRx() - offsetX;
	int posY = getRy() - offsetY;
	int rwidth = getRwidth();
	int rheight = getRheight();

	if(!((posX > view.getViewWidth()) || (posY > view.getViewHeight())
		|| ((posX + rwidth) < 0) || ((posY + rheight) < 0)))
	{
		g.setColor(Constants.HILITE_COLOR);
		g.drawRect(posX, posY, rwidth, rheight);
	}

	g.setColor(Constants.SELECTION_COLOR);

	double scale = view.getScale();

	int posX2 = 0;
	int posY2 = 0;

	if(partnerVertex.getHashID().compareTo(hashId) < 0)
	{
		posX = getRx() + (int)(Constants.CONNECTOR_WIDTH * scale / 2) - offsetX;
		posY = getRy() + (int)(Constants.CONNECTOR_HEIGHT * scale / 2) - offsetY;
		
		posX2 = partnerVertex.getRx() + (int)(Constants.CONNECTOR_WIDTH * scale / 2) - offsetX;
		posY2 = partnerVertex.getRy() + (int)(Constants.CONNECTOR_HEIGHT * scale / 2) - offsetY;
	}
	else
	{
		posX = partnerVertex.getRx() + (int)(Constants.CONNECTOR_WIDTH * scale / 2) - offsetX;
		posY = partnerVertex.getRy() + (int)(Constants.CONNECTOR_HEIGHT * scale / 2) - offsetY;

		posX2 = getRx() + (int)(Constants.CONNECTOR_WIDTH * scale / 2) - offsetX;
		posY2 = getRy() + (int)(Constants.CONNECTOR_HEIGHT * scale / 2) - offsetY;
	}

	int dirX = (posX < posX2) ? (1) : (-1);
	int dirY = (posY < posY2) ? (1) : (-1);

	int curX = posX;

	while((curX * dirX) <= (posX2 * dirX))
	{
		int curX2 = curX + dirX * Constants.DASHED_LINE_DENSITY;
				
		if((curX2 * dirX) > (posX2 * dirX))
			curX2 = posX2;
				
		g.drawLine(curX, posY, curX2, posY);
		g.drawLine(curX, posY2, curX2, posY2);
				
		curX += 2 * dirX * Constants.DASHED_LINE_DENSITY;
	}

	int curY = posY;

	while((curY * dirY) <= (posY2 * dirY))
	{
		int curY2 = curY + dirY * Constants.DASHED_LINE_DENSITY;
				
		if((curY2 * dirY) > (posY2 * dirY))
			curY2 = posY2;
				
		g.drawLine(posX, curY, posX, curY2);
		g.drawLine(posX2, curY, posX2, curY2);
				
		curY += 2 * dirY * Constants.DASHED_LINE_DENSITY;
	}
}

public String getHashID()
{
	return hashId;
}

public TextBoxVertex getPartnerVertex()
{
	return partnerVertex;
}

public int getX()
{
	int posX = super.getX();
	if(Settings.getInstance().getSnapToGrid())
		return (posX + Constants.CONNECTOR_WIDTH / 2) - (posX + Constants.CONNECTOR_WIDTH / 2)
			% Constants.GRID_SIZE - Constants.CONNECTOR_WIDTH / 2;
		
	return posX;
}

public int getY()
{
	int posY = super.getY();
	if(Settings.getInstance().getSnapToGrid())
		return (posY + Constants.CONNECTOR_HEIGHT / 2) - (posY + Constants.CONNECTOR_HEIGHT / 2)
			% Constants.GRID_SIZE - Constants.CONNECTOR_HEIGHT / 2;
		
	return posY;
}
	
public void revalidatePosition()
{
	double rscale = getRscale();

	setRx((int)(getX() * rscale));
	setRy((int)(getY() * rscale));
}

public void setIsAlwaysDrawn(boolean parIsAlwaysDrawn)
{
	isAlwaysDrawn = parIsAlwaysDrawn;
}

public void setPartnerVertex(TextBoxVertex parPartnerVertex)
{
	partnerVertex = parPartnerVertex;
}

public void setTextBox(TextBox parTextBox)
{
	textBox = parTextBox;
}

public void setX(int parX)
{
	super.setX(parX);
	
	if(textBox != null)
		textBox.revalidatePosition();
}

public void setY(int parY)
{
	super.setY(parY);
	
	if(textBox != null)
		textBox.revalidatePosition();
}

protected void validate()
{
	revalidatePosition();
	
	double rscale = getRscale();

	setRwidth((int)(getWidth() * rscale));
	setRheight((int)(getHeight() * rscale));
}
	
}	
