/*
 * Copyright (c) 2004 by Cosylab d.o.o.
 *
 * The full license specifying the redistribution, modification, usage and other
 * rights and obligations is included with the distribution of this project in
 * the file license.html. If the license is not included you may find a copy at
 * http://www.cosylab.com/legal/abeans_license.htm or may write to Cosylab, d.o.o.
 *
 * THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND, NOT EVEN THE
 * IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR OF THIS SOFTWARE, ASSUMES
 * _NO_ RESPONSIBILITY FOR ANY CONSEQUENCE RESULTING FROM THE USE, MODIFICATION,
 * OR REDISTRIBUTION OF THIS SOFTWARE.
 */

package com.cosylab.vdct.appplugin.archiver;

import com.cosylab.vdct.DataProvider;
import com.cosylab.vdct.appplugin.Channel;
import com.cosylab.vdct.graphics.objects.Record;
import com.cosylab.vdct.plugin.Plugin;
import com.cosylab.vdct.plugin.PluginContext;

import java.util.Properties;
import java.util.Vector;


/**
 * <code>ArchiverPlugin</code> ...  DOCUMENT ME!
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class ArchiverPlugin implements Plugin
{
	private Archiver archiver;

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.plugin.Plugin#destroy()
	 */
	public void destroy()
	{
		if (archiver != null) {
			archiver.dispose();
		}
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.plugin.Plugin#getAuthor()
	 */
	public String getAuthor()
	{
		return "jaka.bobnar@cosylab.com";
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.plugin.Plugin#getDescription()
	 */
	public String getDescription()
	{
		return "ArchiverPlugin is ChannelArchiver, which enables editing XML archive files";
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.plugin.Plugin#getName()
	 */
	public String getName()
	{
		return "Archiver";
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.plugin.Plugin#getVersion()
	 */
	public String getVersion()
	{
		return "0.1";
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.plugin.Plugin#init(java.util.Properties, com.cosylab.vdct.plugin.PluginContext)
	 */
	public void init(Properties properties, PluginContext context)
	{
		// TODO Auto-generated method stub        
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.plugin.Plugin#start()
	 */
	public void start()
	{
		if (archiver == null) {
			archiver = new Archiver();
		}

		archiver.clear();

		try {
			Vector data = DataProvider.getInstance().getInspectable();

			for (int i = 0; i < data.size(); i++) {
				Object o = data.get(i);

				if (o instanceof Record) {
					archiver.addChannel(new Channel(((Record)o).getName()));
				}
			}
		} catch (Exception e) {
			//
		}

		archiver.setVisible(true);
		archiver.toFront();
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.plugin.Plugin#stop()
	 */
	public void stop()
	{
		if (archiver != null) {
			archiver.dispose();
		}
	}
}

/* __oOo__ */
