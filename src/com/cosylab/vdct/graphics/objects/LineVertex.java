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
public class LineVertex extends VisibleObject implements Movable, Selectable, Popupable
{

class PopupMenuHandler implements ActionListener
{
	
public void actionPerformed(ActionEvent event)
{
	String action = event.getActionCommand();
	if(action.equals(colorString))
	{
		Color newColor = JColorChooser.showDialog(null, colorTitleString, getColor());
		
		if(newColor != null)
		{
			setColor(newColor);
			if(partnerVertex != null)
				partnerVertex.setColor(newColor);
				
			currentColor = newColor;
		}
		
		CommandManager.getInstance().execute("RepaintWorkspace");
	}
	else if(action.equals(isDashedString))
	{
		isDashed = !isDashed;
		if(partnerVertex != null)
			partnerVertex.setIsDashed(isDashed);
			
		currentIsDashed = isDashed;

		CommandManager.getInstance().execute("RepaintWorkspace");
	}
	else if(action.equals(isArrowString))
	{
		isArrow = !isArrow;

		currentIsArrow = isArrow;

		CommandManager.getInstance().execute("RepaintWorkspace");
	}
}

}
	
private String hashId;
private LineVertex partnerVertex;
private boolean isDashed;
private boolean isArrow;

private static final String colorString = "Color...";
private static final String colorTitleString = "Line color";
private static final String isDashedString = "Dashed";
private static final String isArrowString = "Arrow";

private static Color currentColor = Constants.LINE_COLOR;
private static boolean currentIsDashed = false;
private static boolean currentIsArrow = false;

private static final String hashIdPrefix = "Line";

private String getAvailableHashId()
{
	int grLineNumber = 0;
	String testHashId = hashIdPrefix + String.valueOf(grLineNumber);
	
	while(getParent().containsObject(testHashId))
	{
		grLineNumber++;
		testHashId = hashIdPrefix + String.valueOf(grLineNumber);		
	}

	return testHashId;
}

public static boolean getCurrentIsArrow()
{
	return currentIsArrow;
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

public LineVertex(ContainerObject parent, int parX, int parY, LineVertex parPartnerVertex)
{
	super(parent);

	ViewState view = ViewState.getInstance();

	setX(parX - Constants.CONNECTOR_WIDTH / 2);
	setY(parY - Constants.CONNECTOR_HEIGHT / 2);
	setWidth(Constants.CONNECTOR_WIDTH);
	setHeight(Constants.CONNECTOR_HEIGHT);

	setColor(currentColor);
	isDashed = currentIsDashed;
	isArrow = false;
	
	partnerVertex = parPartnerVertex;
	
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
	ViewState view = ViewState.getInstance();

	int offsetX = view.getRx();
	int offsetY = view.getRy();
	
	int posX = getRx() - offsetX;
	int posY = getRy() - offsetY;
	int rwidth = getRwidth();
	int rheight = getRheight();

	if((hilited) && (!((posX > view.getViewWidth()) || (posY > view.getViewHeight())
		|| ((posX + rwidth) < 0) || ((posY + rheight) < 0))))
	{
		g.setColor(Constants.HILITE_COLOR);
		g.drawRect(posX, posY, rwidth, rheight);
	}

	g.setColor(getColor());

	double scale = view.getScale();
	
	int rw = (int)(Constants.CONNECTOR_WIDTH * scale / 2);
	int rh = (int)(Constants.CONNECTOR_HEIGHT * scale / 2);
	
	posX = getRx() + rw - offsetX;
	posY = getRy() + rh - offsetY;
	
	int posX2 = partnerVertex.getRx() + rw - offsetX;
	int posY2 = partnerVertex.getRy() + rh - offsetY;

	int dirX = (posX < posX2) ? (1) : (-1);
	int dirY = (posY < posY2) ? (1) : (-1);

	if(partnerVertex.getHashID().compareTo(hashId) < 0)
	{
		if((isDashed) && ((posX != posX2) || (posY != posY2)))
		{
			double angle = Math.atan(((double)(Math.abs(posY2 - posY))) / Math.abs(posX2 - posX));
			double cos = dirX * Math.cos(angle) * Constants.DASHED_LINE_DENSITY;
			double sin = dirY * Math.sin(angle) * Constants.DASHED_LINE_DENSITY;
			
			double curX = posX;
			double curY = posY;

			int step = 0;
						
			while(((curX * dirX) <= (posX2 * dirX)) && ((curY * dirY) <= (posY2 * dirY)))
			{
				step++;

				double curX2 = posX + cos * step;
				double curY2 = posY + sin * step;
				
				if(((curX2 * dirX) > (posX2 * dirX)) || ((curY2 * dirY) > (posY2 * dirY)))
				{			
					curX2 = posX2;
					curY2 = posY2;
				}
				
				g.drawLine((int)curX, (int)curY, (int)curX2, (int)curY2);
				step++;
				
				curX = posX + cos * step;
				curY = posY + sin * step;
			}
		}
		else
			g.drawLine(posX, posY, posX2, posY2);
	}

	if(isArrow)
	{
		double angle = Math.atan(((double)(Math.abs(posY2 - posY))) / Math.abs(posX2 - posX));
		
		int[] vertexX = new int[3];
		int[] vertexY = new int[3];
			
		vertexX[0] = posX;
		vertexY[0] = posY;
		
		double arrowSize = Constants.ARROW_SIZE * getRscale();
		double lineLength = Math.sqrt((posX2 - posX) * (posX2 - posX) + (posY2 - posY) * (posY2 - posY));
			
		if(arrowSize > lineLength / 2)
			arrowSize = lineLength / 2;
		
		vertexX[1] = (int)(posX + dirX * Math.cos(angle + Constants.ARROW_SHARPNESS) * arrowSize);
		vertexY[1] = (int)(posY + dirY * Math.sin(angle + Constants.ARROW_SHARPNESS) * arrowSize);
			
		vertexX[2] = (int)(posX + dirX * Math.cos(angle - Constants.ARROW_SHARPNESS) * arrowSize);
		vertexY[2] = (int)(posY + dirY * Math.sin(angle - Constants.ARROW_SHARPNESS) * arrowSize);

		g.fillPolygon(vertexX, vertexY, 3);
	}
}

public boolean getIsArrow()
{
	return isArrow;
}

public boolean getIsDashed()
{
	return isDashed;
}

public String getHashID()
{
	return hashId;
}

public Vector getItems()
{
	Vector items = new Vector();

	ActionListener al = new PopupMenuHandler();

	JMenuItem colorItem = new JMenuItem(colorString);
	colorItem.addActionListener(new PopupMenuHandler());
	items.addElement(colorItem);

	JCheckBoxMenuItem isDashedItem = new JCheckBoxMenuItem(isDashedString);
	isDashedItem.setSelected(isDashed);
	isDashedItem.addActionListener(al);
	items.addElement(isDashedItem);

	JCheckBoxMenuItem isArrowItem = new JCheckBoxMenuItem(isArrowString);
	isArrowItem.setSelected(isArrow);
	isArrowItem.addActionListener(al);
	items.addElement(isArrowItem);

	return items;
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

public void setIsArrow(boolean parIsArrow)
{
	isArrow = parIsArrow;
}

public void setIsDashed(boolean parIsDashed)
{
	isDashed = parIsDashed;
}

public void setPartnerVertex(LineVertex parPartnerVertex)
{
	partnerVertex = parPartnerVertex;
}

protected void validate()
{
	revalidatePosition();
	
	double rscale = getRscale();

	setRwidth((int)(getWidth() * rscale));
	setRheight((int)(getHeight() * rscale));
}
	
}
