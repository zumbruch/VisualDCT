package com.cosylab.vdct.plugin.export;

import java.util.*;
import java.beans.*;
import com.cosylab.vdct.plugin.*;

/**
 * CURRENTLY NOT NEEDED
 * Creation date: (7.12.2001 13:57:49)
 * @author Matej Sekoranja
 */
public final class PluginExportManager implements PluginListener, PropertyChangeListener
{
    private static PluginExportManager instance = null;

    private LinkedList list = null;
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 14:00:41)
 */
protected PluginExportManager()
{
	list = new LinkedList();
		
	PluginManager.getInstance().addPluginListener(this);
}
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 14:01:03)
 * @return com.cosylab.vdct.plugin.PluginExportManager
 */
public static PluginExportManager getInstance() {
	if (instance==null) instance = new PluginExportManager();
	return instance;
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:23:57)
 * @param
 * @return
 */
public void pluginAdded(PluginObject plugin)
{
	if (plugin.getPlugin() instanceof ExportPlugin)
	{
		if (!list.contains(plugin))
		{
			list.add(plugin);
			plugin.addPropertyChangeListener(this);
			com.cosylab.vdct.Console.getInstance().println(plugin.getName()+" is registered as export plugin.");
		}

	}
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:23:57)
 * @param
 * @return
 */
public void pluginRemoved(PluginObject plugin)
{
	if (plugin instanceof ExportPlugin)
	{
		list.remove(plugin);
		plugin.removePropertyChangeListener(this);
	}
}
/**
 * Not implemented
 * Creation date: (6.12.2001 22:23:57)
 * @param
 * @return
 */
public void propertyChange(PropertyChangeEvent evt)
{
	PluginObject plugin = (PluginObject)evt.getSource();
	String propertyName = evt.getPropertyName();

	if (propertyName.equals("Status"))
	{
		if (plugin.getStatus() == PluginObject.PLUGIN_STARTED)
		{
		}
		else if (plugin.getStatus() == PluginObject.PLUGIN_STOPPED)
		{
		}
	}
}
}
