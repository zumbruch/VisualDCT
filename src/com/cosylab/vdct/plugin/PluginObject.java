package com.cosylab.vdct.plugin;

import java.io.*;
import java.awt.*;
import java.net.*;
import java.util.*;
import java.beans.*;

import javax.swing.*;

import org.w3c.dom.*;

import com.cosylab.vdct.xml.*;

/**
 * Insert the class' description here.
 * Creation date: (6.12.2001 22:40:36)
 * @author Matej Sekoranja
 */
 
public class PluginObject
{

	public static final int PLUGIN_NOT_LOADED = 0;
	public static final int PLUGIN_LOADED = 1;
	public static final int PLUGIN_INVALID = 2;
	public static final int PLUGIN_INITIALIZED = 3;
	public static final int PLUGIN_STARTED = 4;
	public static final int PLUGIN_STOPPED = 5;

	private final static String NOT_LOADED_PLUGIN = "Not loaded.";
	private final static String LOADED_PLUGIN = "Loaded.";
	private final static String INVALID_PLUGIN = "Invalid.";
	private final static String INITIALIZED_PLUGIN = "Initialized.";
	private final static String STARTED_PLUGIN = "Started.";
	private final static String STOPPED_PLUGIN = "Stopped.";
	
	private final static String AUTOSTART_PLUGIN = "Auto.";
	private final static String NO_AUTOSTART_PLUGIN = "Manual.";

	private int status = PLUGIN_NOT_LOADED;

	private Plugin plugin = null;

	private String className = null;
	private boolean autoStart = false;
	private Properties properties = null;
	
	private PropertyChangeSupport propChangeSupport = null;
	
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:46:06)
 * @param
 */
public PluginObject(Element pluginNode)
{
	propChangeSupport = new PropertyChangeSupport(this);

	loadConfig(pluginNode);

}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:46:06)
 * @param
 * @return
 */
public void addPropertyChangeListener(PropertyChangeListener listener)
{
	propChangeSupport.addPropertyChangeListener(listener);
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:46:06)
 * @param
 * @return
 */
public void destroy()
{
	if (plugin==null)
		return;

	plugin.destroy();
}
/**
 * Insert the method's description here.
 * Creation date: (8.12.2001 18:20:23)
 * @param property java.lang.String
 * @param oldValue java.lang.Object
 * @param newValue java.lang.Object
 */
public void firePropertyChange(String property, Object oldValue, Object newValue)
{
	propChangeSupport.firePropertyChange(property, oldValue, newValue);
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:46:06)
 * @param
 * @return
 */
public String getAuthor()
{
	if (plugin==null)
		return "";

	try
	{
		return plugin.getAuthor();
	}
	catch (Exception e)
	{
		com.cosylab.vdct.Console.getInstance().println("An error occured while obtaining author of plugin instance of "+ className);
		com.cosylab.vdct.Console.getInstance().println(e);
		return "";
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:46:06)
 * @param
 * @return
 */
public String getAutoStartString()
{
	if (autoStart)
		return AUTOSTART_PLUGIN;
	else
		return NO_AUTOSTART_PLUGIN;
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:46:06)
 * @param
 * @return
 */
public String getDescription()
{
	if (plugin==null)
		return "";

	try
	{
		return plugin.getDescription();
	}
	catch (Exception e)
	{
		com.cosylab.vdct.Console.getInstance().println("An error occured while obtaining description of plugin instance of "+ className);
		com.cosylab.vdct.Console.getInstance().println(e);
		return "";
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:46:06)
 * @param
 * @return
 */
public String getName()
{
	if (plugin==null)
		return "";

	try
	{
		return plugin.getName();
	}
	catch (Exception e)
	{
		com.cosylab.vdct.Console.getInstance().println("An error occured while obtaining name of plugin instance of "+ className);
		com.cosylab.vdct.Console.getInstance().println(e);
		return "";
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:46:06)
 * @param
 * @return
 */
public Plugin getPlugin()
{
	return plugin;
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:46:06)
 * @param
 * @return
 */
public int getStatus()
{
	return status;
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:46:06)
 * @param
 * @return
 */
public String getStatusString()
{
	if (status==PLUGIN_NOT_LOADED)
		return NOT_LOADED_PLUGIN;
	else if (status==PLUGIN_LOADED)
		return LOADED_PLUGIN;
	else if (status==PLUGIN_INVALID)
		return INVALID_PLUGIN;
	else if (status==PLUGIN_INITIALIZED)
		return INITIALIZED_PLUGIN;
	else if (status==PLUGIN_STARTED)
		return STARTED_PLUGIN;
	else if (status==PLUGIN_STOPPED)
		return STOPPED_PLUGIN;
	else
		return "";
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:46:06)
 * @param
 * @return
 */
public String getVersion()
{
	if (plugin!=null)
		return plugin.getVersion();
	else
		return "";
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:42:08)
 * @param
 * @return
 */
public void init()
{
	if (plugin!=null)
		return;

	try
	{
		plugin = loadPlugin(className);
		setStatus(PLUGIN_LOADED);
	}
	catch (Throwable t)	{
		com.cosylab.vdct.Console.getInstance().println("An error occured while loading plugin instance of "+ className);
		com.cosylab.vdct.Console.getInstance().println(t);

		plugin = null;
		setStatus(PLUGIN_INVALID);
		return;
	}

	plugin.init(properties, new PluginContext());
	setStatus(PLUGIN_INITIALIZED);
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:42:08)
 * @param
 * @return
 */
public boolean isAutoStart()
{
	return autoStart;
}
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 14:31:00)
 * @param pluginNode org.w3c.dom.Element
 */
private void loadConfig(Element pluginNode)
{
	className = XMLManager.getNodeAttribute(pluginNode, "class");
	autoStart = XMLManager.getNodeAttribute(pluginNode, "autostart").equals("true");
	
	properties = new Properties();

	System.out.println("Loading plugin: "+className);
	
	Node node = pluginNode.getFirstChild();
	while (node!=null)
	{
		if (node instanceof Element)
			properties.put(XMLManager.getNodeAttribute(node, "name"),
						   XMLManager.getNodeAttribute(node, "value"));

		node = node.getNextSibling();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 14:07:42)
 * @param
 * @return
 */
public static Plugin loadPlugin(String className) throws Exception
{
	if (className==null)
		return null;
	
	try
	{
		Class classObj = Class.forName(className);
		Object obj = classObj.newInstance();
		
		return (Plugin)obj;
	}
	catch (Exception e)
	{
		//com.cosylab.vdct.Console.getInstance().println("An error occured while loading the class: " + className);
		//com.cosylab.vdct.Console.getInstance().println(e);
		throw e;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:42:08)
 * @param
 * @return
 */
public void removePropertyChangeListener(PropertyChangeListener listener)
{
	propChangeSupport.removePropertyChangeListener(listener);
}
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 15:01:22)
 * @param pluginNode org.w3c.dom.Element
 */
public void saveConfig(Document doc, Element pluginNode)
{
    pluginNode.setAttribute("class", className);
	pluginNode.setAttribute("autostart", autoStart ? "true" : "false");

	Enumeration enum = properties.keys();
	while (enum.hasMoreElements())
	{
		Element element = (Element)doc.createElement("param");
		String key = enum.nextElement().toString();
		element.setAttribute("name", key);
		element.setAttribute("value", properties.get(key).toString());
		pluginNode.appendChild(element);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:42:08)
 * @param
 * @return
 */
public void setAutoStart(boolean autoStart)
{
	boolean oldAutoStart = autoStart;

	this.autoStart = autoStart;
	propChangeSupport.firePropertyChange("AutoStart", autoStart, oldAutoStart);
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:42:08)
 * @param
 * @return
 */
private void setStatus(int newStatus)
{
	int oldStatus = status;
	status = newStatus;
	propChangeSupport.firePropertyChange("Status", status, oldStatus);
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:42:08)
 * @param
 * @return
 */
public void start()
{
	if (plugin==null)
		return;

	try
	{
		plugin.start();
		setStatus(PLUGIN_STARTED);
	}
	catch (Exception e)
	{
		com.cosylab.vdct.Console.getInstance().println("An error occured while starting plugin instance of "+ className);
		com.cosylab.vdct.Console.getInstance().println(e);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:42:08)
 * @param
 * @return
 */
public void stop()
{
	if (plugin==null)
		return;

	try
	{
		plugin.stop();
		setStatus(PLUGIN_STOPPED);
	}
	catch (Exception e)
	{
		com.cosylab.vdct.Console.getInstance().println("An error occured while stopping plugin instance of "+ className);
		com.cosylab.vdct.Console.getInstance().println(e);
	}
}
}
