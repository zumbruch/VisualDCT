package com.cosylab.vdct.plugin.debug;

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
public class DebugStopMenuItem extends JMenuItem implements ActionListener//implements PluginListener, PropertyChangeListener
{
/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 17:08:53)
 * @param
 */
public DebugStopMenuItem()
{
	addActionListener(this);
}
/**
 * Invoked when an action occurs.
 */
public void actionPerformed(java.awt.event.ActionEvent e)
{
	stopDebugging();
}
/**
 * Insert the method's description here.
 * Creation date: (8.12.2001 18:57:13)
 */
public static void stopDebugging()
{
	DebugPlugin debugPlugin = PluginDebugManager.getDebugPlugin();
	if (debugPlugin!=null)
	{
		debugPlugin.deregisterAll();
		debugPlugin.stopDebugging();
		PluginDebugManager.setDebugState(false);
		PluginDebugManager.setDebugPlugin(null);

		/// !!! to be reimplemented
		/// for the time being ALL fields in the current group are updated
		/// current group can be different from the debugging!!!
		com.cosylab.vdct.graphics.objects.Group group = com.cosylab.vdct.graphics.DrawingSurface.getInstance().getViewGroup();
		Enumeration e = group.getSubObjectsV().elements();
		while (e.hasMoreElements())
		{
			Object obj = e.nextElement();
			if (obj instanceof com.cosylab.vdct.graphics.objects.Record)
			{
				com.cosylab.vdct.vdb.VDBRecordData rec = ((com.cosylab.vdct.graphics.objects.Record)obj).getRecordData();
				Enumeration e2 = rec.getFieldsV().elements();
				while (e2.hasMoreElements())
					rec.fieldValueChanged((com.cosylab.vdct.vdb.VDBFieldData)e2.nextElement());
					
			}
		}
		
	}
}
}
