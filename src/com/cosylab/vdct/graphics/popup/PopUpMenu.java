package com.cosylab.vdct.graphics.popup;

import java.util.*;
import javax.swing.*;

/**
 * Insert the type's description here.
 * Creation date: (27.1.2001 17:30:55)
 * @author: Matej Sekoranja
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
