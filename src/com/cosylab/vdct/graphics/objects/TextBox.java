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
public class TextBox extends VisibleObject implements Movable, Selectable, Popupable
{

class PopupMenuHandler implements ActionListener
{
	
public void actionPerformed(ActionEvent event)
{
	String action = event.getActionCommand();
	if(action.equals(changeTextString))
	{
	}
	else if(action.equals(colorString))
	{
		Color newColor = JColorChooser.showDialog(null, colorTitleString, getColor());
		
		if(newColor != null)
		{
			setColor(newColor);
				
			currentColor = newColor;
		}
		
		CommandManager.getInstance().execute("RepaintWorkspace");
	}
}

}
	
private String hashId;
private TextBoxVertex startVertex;
private TextBoxVertex endVertex;

private static final String colorString = "Color...";
private static final String colorTitleString = "Comment Color";
private static final String changeTextString = "Change Text...";

private static Color currentColor = Constants.LINE_COLOR;

private String getAvailableHashId()
{
	int grLineNumber = 0;
	String testHashId = "TB" + (new Integer(grLineNumber)).toString();
	
	while(getParent().containsObject(testHashId))
	{
		grLineNumber++;
		testHashId = "TB" + (new Integer(grLineNumber)).toString();		
	}

	return testHashId;
}

public boolean move(int dx, int dy)
{
	if(checkMove(dx, dy) && (startVertex.checkMove(dx, dy)) && (endVertex.checkMove(dx, dy)))
	{
		startVertex.move(dx, dy);
		endVertex.move(dx, dy);
		
		revalidatePosition();
		
		return true;
	}

	return false;
}

public TextBox(ContainerObject parent, TextBoxVertex parStartVertex, TextBoxVertex parEndVertex)
{
	super(parent);

	startVertex = parStartVertex;
	endVertex = parEndVertex;

	revalidatePosition();
	
	setColor(currentColor);
	
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

	if(hilited)
		g.setColor(Constants.HILITE_COLOR);
	else
		g.setColor(Constants.LINE_COLOR);
	
	if(!((posX > view.getViewWidth()) || (posY > view.getViewHeight())
		|| ((posX + rwidth) < 0) || ((posY + rheight) < 0)))
	{
		if(hilited)
			g.setColor(Constants.HILITE_COLOR);
		else
			g.setColor(Constants.LINE_COLOR);
		
		g.drawRect(posX + 2, posY + 2, rwidth - 4, rheight - 4);
	}
}

public String getHashID()
{
	return hashId;
}

public Vector getItems()
{
	Vector items = new Vector();

	JMenuItem changeTextItem = new JMenuItem(changeTextString);
	changeTextItem.addActionListener(new PopupMenuHandler());
	items.addElement(changeTextItem);

	JMenuItem colorItem = new JMenuItem(colorString);
	colorItem.addActionListener(new PopupMenuHandler());
	items.addElement(colorItem);

	return items;
}

public TextBoxVertex getStartVertex()
{
	return startVertex;
}

public TextBoxVertex getEndVertex()
{
	return endVertex;
}

public int getX()
{
	int posX = super.getX();
	if(Settings.getInstance().getSnapToGrid())
		return posX  - posX % Constants.GRID_SIZE;
		
	return posX;
}

public int getY()
{
	int posY = super.getY();
	if(Settings.getInstance().getSnapToGrid())
		return posY - posY % Constants.GRID_SIZE;
		
	return posY;
}
	
public void revalidatePosition()
{
	int posX = startVertex.getX();
	int posY = startVertex.getY();
	
	int posX2 = endVertex.getX();
	int posY2 = endVertex.getY();

	setX(Math.min(posX, posX2) + Constants.CONNECTOR_WIDTH / 2);
	setY(Math.min(posY, posY2) + Constants.CONNECTOR_HEIGHT / 2);
	setWidth(Math.abs(posX2 - posX));
	setHeight(Math.abs(posY2 - posY));

	double rscale = getRscale();

	setRx((int)(getX() * rscale));
	setRy((int)(getY() * rscale));
}

protected void validate()
{
	revalidatePosition();
	
	double rscale = getRscale();

	setRwidth((int)(getWidth() * rscale));
	setRheight((int)(getHeight() * rscale));
}
	
}
