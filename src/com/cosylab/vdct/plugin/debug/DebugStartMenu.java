package com.cosylab.vdct.plugin.debug;

import javax.swing.*;

import java.util.*;
import java.beans.*;
import java.awt.event.*;

import com.cosylab.vdct.plugin.*;

/**
 * Insert the class' description here.
 * Creation date: (7.12.2001 17:15:12)
 * @author Matej Sekoranja
 */
public class DebugStartMenu extends JMenu implements PluginListener
{
	private class DebugPluginMenuItem extends JMenuItem implements PropertyChangeListener, ActionListener
	{
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 17:06:52)
 * @param
 */
public DebugPluginMenuItem(PluginObject plugin)
{
	addActionListener(this);
    setPlugin(plugin);
}

public void actionPerformed(ActionEvent event)
{
	if (plugin!=null)
	{

		if (PluginDebugManager.getDebugPlugin()!=null)
			DebugStopMenuItem.stopDebugging();
		
		DebugPlugin debugPlugin = (DebugPlugin)plugin.getPlugin();
		PluginDebugManager.setDebugPlugin(debugPlugin);
		PluginDebugManager.setDebugState(true);
		debugPlugin.startDebugging();

		/// !!! to be reimplemented
		/// for the time being ALL (only VAL) fields in the current group are registered
		/// all new (or deleted) filed are not updated
		com.cosylab.vdct.graphics.objects.Group group = com.cosylab.vdct.graphics.DrawingSurface.getInstance().getViewGroup();
		Enumeration e = group.getSubObjectsV().elements();
		while (e.hasMoreElements())
		{
			Object obj = e.nextElement();
			if (obj instanceof com.cosylab.vdct.graphics.objects.Record)
			{
				/*com.cosylab.vdct.vdb.VDBFieldData field = ((com.cosylab.vdct.graphics.objects.Record)obj).getRecordData().getField("VAL");
				if (field!=null)
					debugPlugin.registerMonitor(field);*/

				com.cosylab.vdct.vdb.VDBRecordData rec = ((com.cosylab.vdct.graphics.objects.Record)obj).getRecordData();
				Enumeration e2 = rec.getFieldsV().elements();
				while (e2.hasMoreElements())
					debugPlugin.registerMonitor((com.cosylab.vdct.graphics.objects.Debuggable)e2.nextElement());
					
			}
		}
	}
}

/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 17:05:28)
 * @param
 * @return
 */
public void setPlugin(PluginObject newPlugin)
{
	if (plugin!=null)
		plugin.removePropertyChangeListener(this);

	plugin = newPlugin;

	if (plugin!=null)
	{
		plugin.addPropertyChangeListener(this);
		updateStatus();
	}
}

/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 17:05:56)
 * @param
 */
public void propertyChange(PropertyChangeEvent evt)
{
	if (evt.getPropertyName().equals("Status"))
		updateStatus();
}

/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 17:05:42)
 * @param
 * @return
 */
private void updateStatus()
{
	if (plugin!=null)
	{
		this.setEnabled(plugin.getStatus()==PluginObject.PLUGIN_STARTED);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 17:06:11)
 * @param
 * @return
 */	
public String getText()
{
	if( plugin!=null )
		return plugin.getName();
	else
		return "";
}

/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 17:06:38)
 * @param
 * @return
 */
public Icon getIcon()
{
	/*if( plugin!=null )
		return plugin.getIcon();
	else*/
		return null;
}



		private PluginObject plugin = null;
	}

	private Map exportMenuItems = new HashMap();
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 17:08:53)
 * @param
 */
public DebugStartMenu()
{
}
/**
 * Insert the method's description here.
 * Needed to add plugins after possible other menu items
 * Creation date: (7.12.2001 17:55:14)
 */
public void init()
{
	PluginManager.getInstance().addPluginListener(this);
	if (getItemCount()==0)
		setEnabled(false);

}
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 17:09:23)
 * @param
 */
public void pluginAdded(PluginObject plugin)
{
	if (plugin.getPlugin() instanceof DebugPlugin)
	{
		if( plugin.getStatus()==PluginObject.PLUGIN_NOT_LOADED ||
		    plugin.getStatus()==PluginObject.PLUGIN_INVALID )
			    return;

		DebugPluginMenuItem menu = new DebugPluginMenuItem(plugin);

		add(menu);
		exportMenuItems.put(plugin, menu);

		if (getItemCount()>0)
			setEnabled(true);
	}

}
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 17:10:37)
 * @param
 */
public void pluginRemoved(PluginObject plugin)
{
	if (plugin.getPlugin() instanceof DebugPlugin)
	{
		DebugPluginMenuItem menuItem = (DebugPluginMenuItem)exportMenuItems.remove(plugin);

		if (menuItem!=null)
		{
			remove(menuItem);
			menuItem.setPlugin(null);

			if (getItemCount()==0)
				setEnabled(false);
		}
	}
}
}
