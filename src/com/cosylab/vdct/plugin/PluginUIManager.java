package com.cosylab.vdct.plugin;

import java.util.*;
import java.beans.*;

/**
 * Insert the class' description here.
 * Creation date: (7.12.2001 13:57:49)
 * @author Matej Sekoranja
 */
public final class PluginUIManager implements PluginListener, PropertyChangeListener
{
    private static PluginUIManager instance = null;
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 14:00:41)
 */
protected PluginUIManager()
{
	PluginManager.getInstance().addPluginListener(this);
}
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 14:01:03)
 * @return com.cosylab.vdct.plugin.PluginUIManager
 */
public static PluginUIManager getInstance() {
	if (instance==null) instance = new PluginUIManager();
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
	// if right plugin instance
	plugin.addPropertyChangeListener(this);
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:23:57)
 * @param
 * @return
 */
public void pluginRemoved(PluginObject plugin)
{
	// if right plugin instance
	plugin.removePropertyChangeListener(this);
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

com.cosylab.vdct.Console.getInstance().println(plugin.getName()+ " started.");
			
		}
		else if (plugin.getStatus() == PluginObject.PLUGIN_STOPPED)
		{

com.cosylab.vdct.Console.getInstance().println(plugin.getName()+ " stopped.");

		}
	}
}
}
