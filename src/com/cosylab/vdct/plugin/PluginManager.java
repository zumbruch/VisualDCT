package com.cosylab.vdct.plugin;

import java.util.*;

/**
 * Insert the class' description here.
 * Creation date: (6.12.2001 22:14:18)
 * @author Matej Sekoranja
 */
public final class PluginManager
{

    private static final String PLUGINS_FILE = "plugins.xml";

	private static PluginManager instance = null;
    private LinkedList pluginListeners = null;
    private LinkedList plugins = null;

    private PluginSerializer pluginSerializer = null;
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:25:50)
 */
protected PluginManager() 
{
    pluginListeners = new LinkedList();
    plugins = new LinkedList();

    pluginSerializer = new PluginXMLSerializer();

	// load
	load();
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:17:37)
 * @param
 * @return
 */
public void addPlugin(PluginObject plugin)
{
	if(plugins.contains(plugin))
		return;

	plugins.add(plugin);
	plugin.init();

	for(int i=0; i<pluginListeners.size(); i++)
		((PluginListener)pluginListeners.get(i)).pluginAdded(plugin);
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:17:37)
 * @param
 * @return
 */
public void addPluginListener(PluginListener listener)
{
	if(!pluginListeners.contains(listener))
	{
		pluginListeners.add(listener);
		updateListener(listener);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:17:37)
 * @param
 * @return
 */
public void checkAutoStartPlugins()
{
	Iterator it = plugins.iterator();
	PluginObject plugin;

	while(it.hasNext())
	{
		plugin = (PluginObject)it.next();
		if(plugin.isAutoStart())
			plugin.start();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:17:37)
 * @param
 * @return
 */
public void destroyAllPlugins()
{
	Iterator it = plugins.iterator();
	PluginObject plugin;

	while(it.hasNext())
	{
		plugin = (PluginObject)it.next();
		plugin.stop();
		plugin.destroy();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:26:27)
 * @return com.cosylab.vdct.plugin.PluginManager
 */
public static PluginManager getInstance() {
	if (instance==null)
	{
		instance = new PluginManager();

		// !!! find a better place
		// create plugin managers
		PluginUIManager.getInstance();
		com.cosylab.vdct.plugin.debug.PluginDebugManager.getInstance();
		com.cosylab.vdct.plugin.export.PluginExportManager.getInstance();
	}
		
	return instance;
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:17:37)
 * @param
 * @return
 */
public Iterator getPlugins()
{
	return plugins.iterator();
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:17:37)
 * @param
 * @return
 */
private void load()
{
	try
	{
		String fileName = com.cosylab.vdct.Settings.getInstance().getDefaultDir()+com.cosylab.vdct.Constants.CONFIG_DIR+PLUGINS_FILE;
		pluginSerializer.importPlugins(fileName, this);
	}
	catch (Exception e)
	{
		com.cosylab.vdct.Console.getInstance().println("An error occured while loading the plugins list!");
		com.cosylab.vdct.Console.getInstance().println(e);

		plugins.clear();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:17:37)
 * @param
 * @return
 */
public void removePlugin(PluginObject plugin)
{
	if(!plugins.contains(plugin))
		return;

	plugin.stop();
	plugin.destroy();
	plugins.remove(plugin);

	for( int i=0; i<pluginListeners.size(); i++ )
		((PluginListener)pluginListeners.get(i)).pluginRemoved(plugin);
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:17:37)
 * @param
 * @return
 */
public void removePluginListener(PluginListener listener)
{
	pluginListeners.remove(listener);
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:17:37)
 * @param
 * @return
 */
public void save()
{
	try
	{
		String fileName = com.cosylab.vdct.Settings.getInstance().getDefaultDir()+com.cosylab.vdct.Constants.CONFIG_DIR+PLUGINS_FILE;
		pluginSerializer.exportPlugins(fileName, this);
	}
	catch (Exception e)
	{
		com.cosylab.vdct.Console.getInstance().println("An error occured while saving the plugins list!");
		com.cosylab.vdct.Console.getInstance().println(e);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:22:20)
 * @param
 * @return
 */
private void updateListener(PluginListener listener)
{
	Iterator it = plugins.iterator();
	while( it.hasNext() )
		listener.pluginAdded( (PluginObject)it.next() );
}
}
