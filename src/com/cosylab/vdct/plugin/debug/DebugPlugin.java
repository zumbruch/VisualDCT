package com.cosylab.vdct.plugin.debug;

import com.cosylab.vdct.plugin.Plugin;
import com.cosylab.vdct.graphics.objects.Debuggable;

/**
 * Narrow interface to be added
 * Creation date: (7.12.2001 19:09:18)
 * @author Matej Sekoranja
 */
public interface DebugPlugin extends Plugin {
/**
 * Insert the method's description here.
 * Creation date: (8.12.2001 17:44:51)
 */
public void deregisterAll();
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 19:20:55)
 * @param field com.cosylab.vdct.graphics.objects.Debuggable
 */
void deregisterMonitor(Debuggable field);
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 19:09:51)
 * @return java.lang.String
 * @param field java.lang.String
 */
public String getValue(String field);
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 19:20:37)
 * @param field com.cosylab.vdct.graphics.objects.Debuggable
 */
public void registerMonitor(Debuggable field);
/**
 * Insert the method's description here.
 * Creation date: (8.12.2001 17:59:52)
 */
public void startDebugging();
/**
 * Insert the method's description here.
 * Creation date: (8.12.2001 18:00:07)
 */
public void stopDebugging();
}
