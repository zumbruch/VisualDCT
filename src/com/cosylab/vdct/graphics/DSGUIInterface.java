package com.cosylab.vdct.graphics;

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

import java.io.*;
import java.util.*;

import javax.swing.SwingConstants;

import com.cosylab.vdct.*;
import com.cosylab.vdct.vdb.*;
import com.cosylab.vdct.undo.*;
import com.cosylab.vdct.graphics.objects.*;
import com.cosylab.vdct.db.DBResolver;
import com.cosylab.vdct.events.*;
import com.cosylab.vdct.events.commands.*;

/**
 * Insert the type's description here.
 * Creation date: (4.2.2001 15:32:01)
 * @author Matej Sekoranja
 */
public class DSGUIInterface implements GUIMenuInterface, VDBInterface {

	private static DSGUIInterface instance = null;
	
	private DrawingSurface drawingSurface;

	private ArrayList pasteNames = null;
	
	private static final String nullString = "";

/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:49)
 * @param drawingSurface com.cosylab.vdct.graphics.DrawingSurface
 */
public DSGUIInterface(DrawingSurface drawingSurface) {
	this.drawingSurface=drawingSurface;
	this.instance = this;
	pasteNames = new ArrayList();
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:12:21)
 */
public void moveOrigin(int direction)
{
	int dx = 0; 
	int dy = 0;
	int d = (int)(100*ViewState.getInstance().getScale()); 
	
	switch (direction)
	{
		case SwingConstants.WEST:
			dx =- d;
			break;
		case SwingConstants.EAST:
			dx =+ d;
			break;
		case SwingConstants.NORTH:
			dy =+ d;
			break;
		case SwingConstants.SOUTH:
			dy =- d;
			break;
	}
	
	if (ViewState.getInstance().moveOrigin(dx, dy))
	{
		drawingSurface.recalculateNavigatorPosition();
		drawingSurface.repaint();
	}
}

/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 */
public void baseView() {
	drawingSurface.baseView();
}
/**
 * Returns error message or null if OK
 * Creation date: (3.2.2001 22:11:01)
 * @return java.lang.String
 * @param name java.lang.String
 */
public java.lang.String checkGroupName(String name, boolean relative) {
	return checkRecordName(name, relative);
}
/**
 * Returns error message or null if OK
 * Creation date: (3.2.2001 22:11:01)
 * @return java.lang.String
 * @param name java.lang.String
 */
public java.lang.String checkRecordName(String name, boolean relative) {

	if (name.trim().length()==0) {
		return "Empty name!";
	}	
	else if (name.length()>Constants.MAX_RECORD_NAME_LENGTH) {
		return "Name is too long: "+name.length()+" (max. "+
				Constants.MAX_RECORD_NAME_LENGTH+") characters!";
	}
	else if (name.indexOf(' ')!=-1) return "No spaces allowed!";

	else if (!relative && (Group.getRoot().findObject(name, true)!=null)) 
		return "Name already exists!";
	else if (relative && (drawingSurface.getViewGroup().findObject(name, true)!=null)) 
		return "Name already exists!";
	else
		return null;
		
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 */
public void copy() {
	ViewState view = ViewState.getInstance();
	if (view.getSelectedObjects().size()==0) return;
	Group.getClipboard().destroy();

	pasteNames.clear();
	
	Object obj;
	Enumeration selected = view.getSelectedObjects().elements();
	while (selected.hasMoreElements()) {
		obj = selected.nextElement();
		if (obj instanceof Flexible)
			((Flexible)obj).copyToGroup(Constants.CLIPBOARD_NAME);
	}
	view.deselectAll();
	drawingSurface.repaint();
}

public BoxVertex createBoxVertex(BoxVertex partnerVertex)
{
	Group parentGroup = drawingSurface.getViewGroup();

	ViewState view = ViewState.getInstance();
	double scale = view.getScale();
	int rx = (int)(view.getRx() / scale);
	int ry = (int)(view.getRy() / scale);
	
	BoxVertex boxVertex = new BoxVertex(parentGroup,
		(int)((drawingSurface.getPressedX() + view.getRx()) / scale),
		(int)((drawingSurface.getPressedY() + view.getRy()) / scale), partnerVertex);

	String name = nullString;

	String parentName = parentGroup.getAbsoluteName();
	if(parentName.length() > 0)
		name = parentName + Constants.GROUP_SEPARATOR;

	name += boxVertex.getHashID();

 	Group.getRoot().addSubObject(name, boxVertex, true);

	drawingSurface.repaint();
	
	return boxVertex;
}

public LineVertex createLineVertex(LineVertex partnerVertex)
{
	Group parentGroup = drawingSurface.getViewGroup();

	ViewState view = ViewState.getInstance();
	double scale = view.getScale();
	int rx = (int)(view.getRx() / scale);
	int ry = (int)(view.getRy() / scale);
	
	LineVertex lineVertex = new LineVertex(parentGroup,
		(int)((drawingSurface.getPressedX() + view.getRx()) / scale),
		(int)((drawingSurface.getPressedY() + view.getRy()) / scale), partnerVertex);

	String name = nullString;

	String parentName = parentGroup.getAbsoluteName();
	if(parentName.length() > 0)
		name = parentName + Constants.GROUP_SEPARATOR;

	name += lineVertex.getHashID();

 	Group.getRoot().addSubObject(name, lineVertex, true);

	drawingSurface.repaint();
	
	return lineVertex;
}

public TextBox createTextBox(TextBoxVertex startVertex, TextBoxVertex endVertex)
{
	Group parentGroup = drawingSurface.getViewGroup();

	TextBox textBox = new TextBox(parentGroup, startVertex, endVertex);

	String name = nullString;

	String parentName = parentGroup.getAbsoluteName();
	if(parentName.length() > 0)
		name = parentName + Constants.GROUP_SEPARATOR;

	name += textBox.getHashID();

 	Group.getRoot().addSubObject(name, textBox, true);

	drawingSurface.repaint();
	
	return textBox;
}

public TextBoxVertex createTextBoxVertex(TextBoxVertex partnerVertex)
{
	Group parentGroup = drawingSurface.getViewGroup();

	ViewState view = ViewState.getInstance();
	double scale = view.getScale();
	int rx = (int)(view.getRx() / scale);
	int ry = (int)(view.getRy() / scale);
	
	TextBoxVertex textBoxVertex = new TextBoxVertex(parentGroup,
		(int)((drawingSurface.getPressedX() + view.getRx()) / scale),
		(int)((drawingSurface.getPressedY() + view.getRy()) / scale), partnerVertex);

	String name = nullString;

	String parentName = parentGroup.getAbsoluteName();
	if(parentName.length() > 0)
		name = parentName + Constants.GROUP_SEPARATOR;

	name += textBoxVertex.getHashID();

 	Group.getRoot().addSubObject(name, textBoxVertex, true);

	drawingSurface.repaint();
	
	return textBoxVertex;
}
/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 23:27:30)
 * @param name java.lang.String
 * @param type java.lang.String
 * @param relative boolean
 */
public void createRecord(String name, String type, boolean relative) {
	if (relative)
	{
		String parentName = drawingSurface.getViewGroup().getAbsoluteName();
		if (parentName.length()>0)
			name = parentName + Constants.GROUP_SEPARATOR + name;
	}
	
	VDBRecordData recordData = VDBData.getNewVDBRecordData(
			DataProvider.getInstance().getDbdDB(), type, name);
	if (recordData==null) {
		Console.getInstance().println("o) Interal error: failed to create record "+name+" ("+type+")!");
		return;
	}

	ViewState view = ViewState.getInstance();
	double scale = view.getScale();
	
	Record record = new Record(null, 
							   recordData,
							   (int)((drawingSurface.getPressedX() + view.getRx()) / scale),
							   (int)((drawingSurface.getPressedY() + view.getRy()) / scale));

	Group.getRoot().addSubObject(name, record, true);

	UndoManager.getInstance().addAction(new CreateAction(record));

	//drawingSurface.setModified(true);
	drawingSurface.repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 */
public void cut() {
	ViewState view = ViewState.getInstance();
	if (view.getSelectedObjects().size()==0) return;
	Group.getClipboard().destroy();

	pasteNames.clear();
	
	Object obj;
	Enumeration selected = view.getSelectedObjects().elements();
	while (selected.hasMoreElements()) {
		obj = selected.nextElement();
		if (obj instanceof Flexible) {
			Flexible flex = (Flexible)obj;
			String oldGroup = Group.substractParentName(flex.getFlexibleName());
			if (flex.moveToGroup(Constants.CLIPBOARD_NAME))
			{
				pasteNames.add(oldGroup);
				if (obj instanceof Movable)
					((Movable)obj).move(-view.getRx(), -view.getRy());
			}
		}
	}
	view.deselectAll();
	drawingSurface.setModified(true);
	drawingSurface.repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 */
public void delete() {
	ViewState view = ViewState.getInstance();
	if (view.getSelectedObjects().size()==0) return;

	try	{
	
		UndoManager.getInstance().startMacroAction();
	
		VisibleObject obj;
		Enumeration selected = view.getSelectedObjects().elements();
		while (selected.hasMoreElements())
		{
			obj = (VisibleObject)selected.nextElement();
			obj.destroy();

			UndoManager.getInstance().addAction(new DeleteAction(obj));
			
		}

	}
	catch (Exception e)
	{
	}
	finally
	{
		UndoManager.getInstance().stopMacroAction();
	}

	view.deselectAll();
	drawingSurface.repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 18:08:42)
 * @return com.cosylab.vdct.graphics.DSGUIInterface
 */
public static DSGUIInterface getInstance() {
	return instance;
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 */
public void group(String groupName) {
	ViewState view = ViewState.getInstance();
	if (view.getSelectedObjects().size()==0) return;

	ComposedAction composedAction = new ComposedAction();

	Group g = (Group)Group.getRoot().findObject(groupName, true);
	if (g==null)
	{
		g = Group.createGroup(groupName);
		composedAction.addAction(new CreateAction(g));
	}
		
	int n = 0; int avgX = 0; int avgY = 0;
	Object obj; Flexible flex; String oldGroup;
	Enumeration selected = view.getSelectedObjects().elements();
	while (selected.hasMoreElements()) {
		obj = selected.nextElement();
		if (obj instanceof Flexible)
		{
			flex = (Flexible)obj; oldGroup = Group.substractParentName(flex.getFlexibleName()); 
			flex.moveToGroup(groupName);
	
			composedAction.addAction(new MoveToGroupAction(flex, oldGroup, groupName));		// if true ?!!!
			
			if (obj instanceof VisibleObject)
			{
				VisibleObject vo = (VisibleObject)obj;
				avgX += vo.getX();	avgY += vo.getY(); n++;
			}
		}
	}

	UndoManager.getInstance().addAction(composedAction);

	//g = (Group)Group.getRoot().findObject(groupName, true);
	if ((g!=null) && (n!=0)) {
		// center of all
		g.setX(avgX/n); g.setY(avgY/n);
	}
	view.deselectAll();
	if (g.getParent()==drawingSurface.getViewGroup())
		view.setAsSelected(g);
	drawingSurface.getViewGroup().manageLinks(true);
	drawingSurface.repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 * @param file java.io.File
 */
public void importDB(java.io.File file) throws IOException {
	drawingSurface.open(file, true);
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 * @param file java.io.File
 */
public void importDBD(java.io.File file) throws IOException {
	drawingSurface.openDBD(file, true);
}
/**
 * Insert the method's description here.
 * Creation date: (29.4.2001 11:37:22)
 * @return boolean
 */
public boolean isModified() {
	return drawingSurface.isModified();
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 */
public void levelUp() {
	drawingSurface.moveLevelUp();
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 */
public void newCmd() {
	drawingSurface.initializeWorkspace();
	
	SetWorkspaceFile cmd = (SetWorkspaceFile)CommandManager.getInstance().getCommand("SetFile");
	cmd.setFile(null);
	cmd.execute();
	
	SetWorkspaceGroup cmd2 = (SetWorkspaceGroup)CommandManager.getInstance().getCommand("SetGroup");
	cmd2.setGroup(null);
	cmd2.execute();
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 * @param file java.io.File
 */
public void openDB(java.io.File file) throws IOException {
	if (drawingSurface.open(file))
	{
		SetWorkspaceFile cmd = (SetWorkspaceFile)CommandManager.getInstance().getCommand("SetFile");
		cmd.setFile(file.getCanonicalPath());
		cmd.execute();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 * @param file java.io.File
 */
public void openDBD(java.io.File file) throws IOException {
	drawingSurface.openDBD(file);
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 */
public void paste() {
	ViewState view = ViewState.getInstance();
	String currentGroupName = drawingSurface.getViewGroup().getAbsoluteName();
	int size = Group.getClipboard().getSubObjectsV().size();
	if (size==0) return;

	Object objs[] = new Object[size];
	Group.getClipboard().getSubObjectsV().copyInto(objs);

	for(int i=0; i<size; i++) {
		if (objs[i] instanceof Flexible)
				view.setAsSelected((VisibleObject)objs[i]);
	}

	boolean isCopy = pasteNames.size()!=size;
	ComposedAction composedAction = new ComposedAction();

	Flexible flex; String oldGroup;
	for(int i=0; i<size; i++) {
		if (objs[i] instanceof Flexible) {
			flex = (Flexible)objs[i];
			if (flex.moveToGroup(currentGroupName))
			{
				if (isCopy)
					composedAction.addAction(new CreateAction((VisibleObject)objs[i]));		// if true ?!!!
				else {
					//System.out.println("Cut/paste:"+pasteNames.get(i).toString()+"->"+currentGroupName);	
					composedAction.addAction(new MoveToGroupAction(flex, pasteNames.get(i).toString(), currentGroupName));
				}

				
				if (objs[i] instanceof Movable)
					((Movable)objs[i]).move(view.getRx(), view.getRy());
			}
			else
				view.deselectObject((VisibleObject)objs[i]);
		}
	}

	UndoManager.getInstance().addAction(composedAction);

	drawingSurface.getViewGroup().manageLinks(true);
	drawingSurface.repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 */
public void print() {}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 */
public void redo() {
	ViewState.getInstance().deselectAll();
	UndoManager.getInstance().redo();
	updateMenuItems();
	drawingSurface.repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 */
public void rename() {

	ViewState view = ViewState.getInstance();
	int size = view.getSelectedObjects().size();
	if (size==0) return;

	Object objs[] = new Object[size];
	view.getSelectedObjects().copyInto(objs);

	for(int i=0; i<size; i++)
		if (objs[i] instanceof Flexible)
		{
			// call gui
			ShowRenameDialog cmd = (ShowRenameDialog)CommandManager.getInstance().getCommand("ShowRenameDialog");
			cmd.setOldName(((Flexible)objs[i]).getFlexibleName());
			cmd.execute();
		}
	view.deselectAll();
	drawingSurface.getViewGroup().manageLinks(true);

	drawingSurface.repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 10:05:02)
 */
public void rename(java.lang.String oldName, java.lang.String newName) {
	ViewState view = ViewState.getInstance();
	Object obj = Group.getRoot().findObject(oldName, true);
	if (obj instanceof Flexible)
	{
		Flexible flex = (Flexible)obj;
		if (flex.rename(newName))
		{
			UndoManager.getInstance().addAction(new RenameAction(flex, oldName, newName));
			
			view.deselectObject((VisibleObject)obj);
			drawingSurface.getViewGroup().manageLinks(true);
			drawingSurface.repaint();
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:48:27)
 * @param file java.io.File
 */
public void save(java.io.File file) throws IOException {
 Group.save(Group.getRoot(), file, false);
 
 // if ok
 drawingSurface.setModified(false);

 SetWorkspaceFile cmd = (SetWorkspaceFile)CommandManager.getInstance().getCommand("SetFile");
 cmd.setFile(file.getCanonicalPath());
 cmd.execute();

}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 * @param file java.io.File
 */
public void saveAsGroup(java.io.File file) throws IOException {
 Group.save(drawingSurface.getViewGroup(), file, false);

}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:48:27)
 * @param file java.io.File
 */
public void export(java.io.File file) throws IOException {
 Group.save(Group.getRoot(), file, true);
 
 // if ok
 drawingSurface.setModified(false);

 SetWorkspaceFile cmd = (SetWorkspaceFile)CommandManager.getInstance().getCommand("SetFile");
 cmd.setFile(file.getCanonicalPath());
 cmd.execute();

}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 * @param file java.io.File
 */
public void exportAsGroup(java.io.File file) throws IOException {
 Group.save(drawingSurface.getViewGroup(), file, true);

}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 */
public void selectAll() {
	if (drawingSurface.getViewGroup().selectAllComponents())
		drawingSurface.repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 * @param state boolean
 */
public void setFlatView(boolean state) {
	drawingSurface.getView().setFlat(state);
	drawingSurface.getViewGroup().unconditionalValidateSubObjects(state);
	drawingSurface.repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:43:50)
 * @param scale double
 */
public void setScale(double scale) {
	drawingSurface.setScale(scale);
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 * @param state boolean
 */
public void showGrid(boolean state) {
	drawingSurface.repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (27.4.2001 19:54:27)
 * @param state boolean
 */
public void showNavigator(boolean state) {
	drawingSurface.repaint();
}
/**
 * Zoom selection
 * Creation date: (4.2.2001 15:57:56)
 */
public void smartZoom() {
	ViewState view = ViewState.getInstance();
	if (view.getSelectedObjects().size()==0) return;

	int minX = Integer.MAX_VALUE;
	int maxX = Integer.MIN_VALUE;
	int minY = Integer.MAX_VALUE;
	int maxY = Integer.MIN_VALUE;

	VisibleObject vo;
	Enumeration e = view.getSelectedObjects().elements();
	while (e.hasMoreElements())
	{
		vo = (VisibleObject)e.nextElement();
		minX = Math.min(minX, vo.getRx());
		minY = Math.min(minY, vo.getRy());
		maxX = Math.max(maxX, vo.getRx()+vo.getRwidth());
		maxY = Math.max(maxY, vo.getRy()+vo.getRheight());
	}

	int space = (minX+minY+maxX+maxY)/75;
	drawingSurface.zoomArea(minX-space-view.getRx(), minY-space-view.getRy(),
							maxX+space-view.getRx(), maxY+space-view.getRy());

}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 * @param state boolean
 */
public void snapToGrid(boolean state) {
	drawingSurface.getViewGroup().unconditionalValidateSubObjects(drawingSurface.isFlat());
	drawingSurface.repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 */
public void undo() {
	ViewState.getInstance().deselectAll();
	UndoManager.getInstance().undo();
	updateMenuItems();
	drawingSurface.repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 */
public void ungroup() {
	ViewState view = ViewState.getInstance();
	int size = view.getSelectedObjects().size();
	if (size==0) return;

	ComposedAction composedAction = new ComposedAction();

	String currentGroupName = drawingSurface.getViewGroup().getAbsoluteName();
		
	Object objs2[]; int size2;
	Group group;

	Object objs[] = new Object[size]; 
	view.getSelectedObjects().copyInto(objs);
	for (int i=0; i<size; i++) {
		if (objs[i] instanceof Group) {
			group = (Group)objs[i];
			view.deselectObject(group);
			size2 = group.getSubObjectsV().size();
			objs2 = new Object[size2];
			group.getSubObjectsV().copyInto(objs2);
			for (int j=0; j<size2; j++)
			{
			/*!!!can be outside	if (objs2[i] instanceof Movable)
					((Movable)objs2[i]).move(view.getRx()-group.getInternalRx(),
											 view.getRy()-group.getInternalRy());
					
			*/
				if (objs2[i] instanceof Flexible) {
					Flexible flex = (Flexible)objs2[j];
					flex.moveToGroup(currentGroupName);

					composedAction.addAction(new MoveToGroupAction(flex, group.getAbsoluteName(), currentGroupName));		// if true ?!!!

					
					view.setAsSelected((VisibleObject)objs2[j]);
				}
			}

			if (group.getSubObjectsV().size()==0) {
				group.destroy();
				composedAction.addAction(new DeleteAction(group));
			}
		}
	}

	UndoManager.getInstance().addAction(composedAction);
	
	drawingSurface.getViewGroup().manageLinks(true);
	drawingSurface.repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (22.4.2001 18:12:34)
 */
public void updateMenuItems() {
	SetRedoMenuItemState cmd = (SetRedoMenuItemState)CommandManager.getInstance().getCommand("SetRedoMenuItemState");
	cmd.setState(UndoManager.getInstance().actions2redo()>0);
	cmd.execute();
	
	SetUndoMenuItemState cmd2 = (SetUndoMenuItemState)CommandManager.getInstance().getCommand("SetUndoMenuItemState");
	cmd2.setState(UndoManager.getInstance().actions2undo()>0);
	cmd2.execute();
}
}
