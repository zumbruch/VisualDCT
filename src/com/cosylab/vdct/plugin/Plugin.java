package com.cosylab.vdct.plugin;

/**
 * Insert the class' description here.
 * Creation date: (6.12.2001 22:04:25)
 * @author Matej Sekoranja
 */
public interface Plugin
{
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:04:45)
 * @param 
 * @return
 */
public void destroy();
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:10:35)
 * @return java.lang.String
 */
String getAuthor();
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:09:48)
 * @return java.lang.String
 */
public String getDescription();
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:04:45)
 * @param 
 * @return
 */
public String getName();
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:10:05)
 * @return java.lang.String
 */
public String getVersion();
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:04:45)
 * @param 
 * @return
 */
public void init(java.util.Properties properties, PluginContext context);
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:04:45)
 * @param 
 * @return
 */
public void start();
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:04:45)
 * @param 
 * @return
 */
public void stop();
}
