package com.cosylab.vdct.plugins;

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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import com.cosylab.vdct.Console;
import com.cosylab.vdct.graphics.objects.Record;
import com.cosylab.vdct.plugin.menu.MenuPlugin;
import com.cosylab.vdct.plugin.popup.*;

/**
 * CosyBeans VisualDCT popup plugin.
 * Add the following line to the ${user.home}/.vdctplugins.xml file:
 * <pre>
 * 		&lt;plugin class="com.cosylab.vdct.plugins.CosyBeansPlugin" autostart="true" /&gt;
 * </pre>
 * Creation date: (8.12.2001 13:29:26)
 * @author Matej Sekoranja
 */
public class CosyBeansPlugin implements ContextPopupPlugin, MenuPlugin {


	class MenuHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			Console.getInstance().println("CosyBeansPlugin action: "+action);
			
			// call abeans
			
		}
	}

	protected CosyBeansPlugin.MenuHandler menuHandler = null;
	protected JMenu menu = null;

/**
 * Insert the method's description here.
 * Creation date: (2.2.2001 23:00:51)
 * @return com.cosylab.vdct.graphics.objects.Connector.PopupMenuHandler
 */
private CosyBeansPlugin.MenuHandler getMenuHandler() {
	if (menuHandler==null)
		menuHandler = new MenuHandler();
	return menuHandler;
}

/**
 * Insert the method's description here.
 * Creation date: (2.2.2001 23:00:51)
 * @return com.cosylab.vdct.graphics.objects.Connector.PopupMenuHandler
 */
protected JMenu getPluginMenu() {
	if (menu==null)
	{
		menu = new JMenu(getName());
		
		JMenuItem item = new JMenuItem("WhatToPutHere?");
		//item.addActionListener(getMenuHandler());
		menu.add(item);

	}
	return menu;
}

/**
 * Insert the method's description here.
 * Creation date: (8.12.2001 13:29:26)
 * @param 
 * @return
 */
public void destroy() {}

/**
 * Insert the method's description here.
 * Creation date: (8.12.2001 13:29:26)
 * @return java.lang.String
 */
public String getAuthor() {
	return "matej.sekoranja@cosylab.com";
}
/**
 * Insert the method's description here.
 * Creation date: (8.12.2001 13:29:26)
 * @return java.lang.String
 */
public String getDescription() {
	return "CosyBeans plugin.";
}
/**
 * Insert the method's description here.
 * Creation date: (8.12.2001 13:29:26)
 * @param 
 * @return
 */
public String getName() {
	return "CosyBeans";
}
/**
 * Insert the method's description here.
 * Creation date: (8.12.2001 13:29:26)
 * @return java.lang.String
 */
public String getVersion() {
	return "0.1";
}
/**
 * Insert the method's description here.
 * Creation date: (8.12.2001 13:29:26)
 * @param 
 * @return
 */
public void init(java.util.Properties properties, com.cosylab.vdct.plugin.PluginContext context) {}
/**
 * Insert the method's description here.
 * Creation date: (8.12.2001 13:29:26)
 * @param 
 * @return
 */
public void start() {}
/**
 * Insert the method's description here.
 * Creation date: (8.12.2001 13:29:26)
 * @param 
 * @return
 */
public void stop() {}

/**
 * @see com.cosylab.vdct.plugin.popup.ContextPopupPlugin#getItems(Vector)
 */
public Vector getItems(Vector selectedObjects)
{
	Vector items = null;

	// workspace popup
	if (selectedObjects == null)
	{
		// nothing to do here
		return null;
	}
	// we have some selected objects
	else
	{
		JMenu popupMenu = new JMenu(getName());
		

		Enumeration e = selectedObjects.elements();
		while (e.hasMoreElements())
		{
			Object obj = e.nextElement();
			if (obj instanceof Record)
			{
				Record rec = (Record)obj;
				
				JMenuItem item = new JMenuItem(rec.getName());
				item.addActionListener(getMenuHandler());
				
				popupMenu.add(item);	
			}
		}

		// add group launcher	
		if (popupMenu.getItemCount()>1)
		{
			JMenuItem item = new JMenuItem("Show all "+popupMenu.getItemCount()+" records");
			item.addActionListener(getMenuHandler());
			
			popupMenu.add(new JSeparator());
			popupMenu.add(item);	
		}

		// add group launcher	
		if (popupMenu.getItemCount()>0)
		{
			items = new Vector();
			items.addElement(popupMenu);
		}
	}
	

	return items;
}

/**
 * @see com.cosylab.vdct.plugin.menu.MenuPlugin#getMenu()
 */
public JMenu getMenu()
{
	return getPluginMenu();
}


}
