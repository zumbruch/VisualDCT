package com.cosylab.vdct.graphics.popup;

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

import java.util.*;
import javax.swing.*;

/**
 * Insert the type's description here.
 * Creation date: (27.1.2001 17:30:55)
 * @author Matej Sekoranja
 */
public class PopUpMenu extends JPopupMenu {
	private static PopUpMenu instance = null;
	private static final String moreString = "More";
	private static final int ITEMS_PER_MENU = 10;
/**
 * LinkingPopupMenu constructor comment.
 */
public PopUpMenu() {
	
}
/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 10:37:54)
 * @return javax.swing.JMenu
 * @param menuItem javax.swing.JMenuItem
 * @param menu javax.swing.JMenu
 */

public static JMenu addItem(JMenuItem item, JMenu menu, int count) {
  if ((count>0) && ((count%ITEMS_PER_MENU)==0)) menu=addMoreMenu(menu);
  menu.add(item);
  return menu;
}
/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 10:39:45)
 * @return javax.swing.JMenu
 * @param menu javax.swing.JMenu
 */
public static JMenu addMoreMenu(JMenu menu) {
	JMenu more = new JMenu(moreString);
	menu.addSeparator();
	menu.add(more);
	return more;
}
/**
 * Insert the method's description here.
 * Creation date: (2.2.2001 20:18:55)
 * @return com.cosylab.vdct.graphics.popup.PopUpMenu
 */
public static PopUpMenu getInstance() {
	if (instance==null) instance = new PopUpMenu();
	return instance;
}
/**
 * Insert the method's description here.
 * Creation date: (2.2.2001 20:17:37)
 * @param object com.cosylab.vdct.graphics.popup.Popupable
 * @param component javax.swing.JComponent
 * @param x int
 * @param y int
 */
public void show(Popupable object, JComponent component, int x, int y) {
	setLabel(object.getLabel());
	if (getComponentCount()>0) removeAll();
	Vector items = object.getItems();
	if ((items==null) || (items.size()==0)) return;
		
	Object obj;
	Enumeration e = items.elements();
	while (e.hasMoreElements())
	{
		obj = e.nextElement();
		if (obj instanceof JMenuItem)
			add((JMenuItem)obj);
		else if (obj instanceof JSeparator)
			add((JSeparator)obj);
	}
	show(component, x, y);
}
}
