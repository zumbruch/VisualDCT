package com.cosylab.vdct.plugin.export;

import com.cosylab.vdct.plugin.Plugin;
import com.cosylab.vdct.graphics.objects.Group;

/**
 * Insert the type's description here.
 * Creation date: (8.12.2001 12:43:54)
 * @author Matej Sekoranja 
 */
public interface ExportPlugin extends Plugin {
/**
 * Insert the method's description here.
 * Creation date: (8.12.2001 12:45:31)
 * @param mainGroup com.cosylab.vdct.graphics.objects.Group
 */
public void export(Group mainGroup);
}
