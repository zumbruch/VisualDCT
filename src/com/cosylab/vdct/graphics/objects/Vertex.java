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
import com.cosylab.vdct.*;
import com.cosylab.vdct.graphics.*;
import com.cosylab.vdct.graphics.popup.*;
import com.cosylab.vdct.undo.DeleteAction;
import com.cosylab.vdct.undo.UndoManager;
import com.cosylab.vdct.util.*;

/**
 * @author ssah
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
public class Vertex extends VisibleObject implements Movable, Popupable
{
	
private String hashId;
private String name;
private VisibleObject owner;

private static final String nullString = "";
private static final String hashIdPrefix = "Vertex";

private String getAvailableHashId()
{
	int number = 0;
	String testHashId = hashIdPrefix + String.valueOf(number);
	
	while(getParent().containsObject(testHashId))
	{
		number++;
		testHashId = hashIdPrefix + String.valueOf(number);		
	}

	return testHashId;
}

public Vertex(String parName, ContainerObject parentGroup, String parentName, int parX, int parY)
{
	super(parentGroup);

	ViewState view = ViewState.getInstance();

	setX(parX - Constants.CONNECTOR_WIDTH / 2);
	setY(parY - Constants.CONNECTOR_HEIGHT / 2);
	setWidth(Constants.CONNECTOR_WIDTH);
	setHeight(Constants.CONNECTOR_HEIGHT);

	if(parName == null)
	{
		hashId = getAvailableHashId();
	
		if(parentName.length() > 0)
			name = parentName + Constants.GROUP_SEPARATOR + hashId;
		else
			name = hashId;
	}
	else
		name = parName;	
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

public Vertex copyToGroup(String group)
{
	String newName;
	if(group.equals(nullString))
		newName = Group.substractObjectName(getName());
	else
		newName = group + Constants.GROUP_SEPARATOR + Group.substractObjectName(getName());

// object with new name already exists, add suffix ///!!!
	while(Group.getRoot().findObject(newName, true) != null)
		newName = StringUtils.incrementName(newName, Constants.COPY_SUFFIX);

	Vertex grVertex = new Vertex(newName, null, null, super.getX(), super.getY());

	Group.getRoot().addSubObject(newName, grVertex, true);

	grVertex.move(20, 20);

	unconditionalValidation();
	return grVertex;
}

public void destroy()
{
	super.destroy();
	if(getParent() != null)
		getParent().removeObject(Group.substractObjectName(name));
	
	UndoManager.getInstance().addAction(new DeleteAction(this));

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
		
		if(owner instanceof TextBox)
			((TextBox)owner).drawDashedBorder(g, hilited);
	}
}

public String getHashID()
{
	return hashId;
}

public Vector getItems()
{
	if(owner instanceof Box)
		return ((Box)(owner)).getItems();
		
	if(owner instanceof Line)
		return ((Line)(owner)).getItems(this);
	
	return null;
}

public String getName()
{
	return name;
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

public boolean moveToGroup(String group)
{
	String currentParent = Group.substractParentName(getName());
	if(group.equalsIgnoreCase(currentParent))
		return false;
	
	String oldName = getName();
	String newName;
	if (group.equals(nullString))
		newName = Group.substractObjectName(getName());
	else
		newName = group + Constants.GROUP_SEPARATOR + Group.substractObjectName(getName());

// object with new name already exists, add suffix // !!!
	Object obj;
	while((obj = Group.getRoot().findObject(newName, true)) != null)
	{
		if(obj == this)	// it's me :) already moved, fix data
		{
			name = newName;
			return true;
		}
		else
			newName = StringUtils.incrementName(newName, Constants.MOVE_SUFFIX);
			
		return rename(newName);
	}
	
	getParent().removeObject(Group.substractObjectName(getName()));
	setParent(null);
	Group.getRoot().addSubObject(newName, this, true);

	name = newName;
	unconditionalValidation();

	return true;
}

public boolean rename(String newName)
{
	String newObjName = Group.substractObjectName(newName);
	String oldObjName = Group.substractObjectName(getName());

	if(!oldObjName.equals(newObjName))
	{
		getParent().removeObject(oldObjName);
		String fullName = StringUtils.replace(getName(), oldObjName, newObjName);
		name = fullName;
		getParent().addSubObject(newObjName, this);
	}
	
// move if needed
	moveToGroup(Group.substractParentName(newName));

	return true;
}
	
public void revalidatePosition()
{
	double rscale = getRscale();

	setRx((int)(getX() * rscale));
	setRy((int)(getY() * rscale));
}

public void setOwner(VisibleObject parOwner)
{
	owner = parOwner;	
}

public void setX(int parX)
{
	super.setX(parX);
	
	if(owner != null)
		owner.revalidatePosition();
}

public void setY(int parY)
{
	super.setY(parY);
	
	if(owner != null)
		owner.revalidatePosition();
}

protected void validate()
{
	revalidatePosition();
	
	double rscale = getRscale();

	setRwidth((int)(getWidth() * rscale));
	setRheight((int)(getHeight() * rscale));
}
	
}
