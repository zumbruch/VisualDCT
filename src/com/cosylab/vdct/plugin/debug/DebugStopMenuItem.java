package com.cosylab.vdct.plugin.debug;

/**
 * Copyright (c) 2002, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution. 
 * Neither the name of the Cosylab, Ltd., Control System Laboratory nor the names
 * of its contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import javax.swing.*;

import java.util.*;
import java.awt.event.*;

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
