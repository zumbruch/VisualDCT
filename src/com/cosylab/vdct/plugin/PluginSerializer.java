package com.cosylab.vdct.plugin;

/**
 * Insert the class' description here.
 * Creation date: (7.12.2001 14:47:29)
 * @author Matej Sekoranja
 */
public interface PluginSerializer
{
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 14:47:43)
 * @param
 * @return
 */
public void exportPlugins(String fileName, PluginManager pluginManager) throws Exception;
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 14:48:14)
 * @param
 */
public void importPlugins(String fileName, PluginManager pluginManager) throws Exception;
}
