package com.cosylab.vdct.plugin.export;

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
public class ExportMenu extends JMenu implements PluginListener
{
	private class ExportPluginMenuItem extends JMenuItem implements PropertyChangeListener, ActionListener
	{
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 17:06:52)
 * @param
 */
public ExportPluginMenuItem(PluginObject plugin)
{
	addActionListener(this);
    setPlugin(plugin);
}

public void actionPerformed(ActionEvent event)
{
	if (plugin!=null)
		((ExportPlugin)plugin.getPlugin()).export(com.cosylab.vdct.graphics.objects.Group.getRoot());
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
public ExportMenu()
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
	if (plugin.getPlugin() instanceof ExportPlugin)
	{
		if( plugin.getStatus()==PluginObject.PLUGIN_NOT_LOADED ||
		    plugin.getStatus()==PluginObject.PLUGIN_INVALID )
			    return;

		ExportPluginMenuItem menu = new ExportPluginMenuItem(plugin);

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
	if (plugin.getPlugin() instanceof ExportPlugin)
	{
		ExportPluginMenuItem menuItem = (ExportPluginMenuItem)exportMenuItems.remove(plugin);

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
