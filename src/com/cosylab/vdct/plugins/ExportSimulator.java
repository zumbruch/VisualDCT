package com.cosylab.vdct.plugins;

import com.cosylab.vdct.plugin.export.*;

/**
 * Insert the type's description here.
 * Creation date: (8.12.2001 13:29:26)
 * @author: 
 */
public class ExportSimulator implements ExportPlugin {
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
 * @param mainGroup com.cosylab.vdct.graphics.objects.Group
 */
public void export(com.cosylab.vdct.graphics.objects.Group mainGroup) {
	com.cosylab.vdct.Console.getInstance().println(getName()+ " should now export, but since it is only a simulator of a plugin... ");
}
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
	return "Simple export plugin simulator";
}
/**
 * Insert the method's description here.
 * Creation date: (8.12.2001 13:29:26)
 * @param 
 * @return
 */
public String getName() {
	return "Export Simulator";
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
}
