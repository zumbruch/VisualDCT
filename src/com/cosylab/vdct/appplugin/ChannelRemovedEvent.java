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

package com.cosylab.vdct.appplugin;

import java.util.EventObject;


/**
 * <code>ChannelRemovedEvent</code> ...  DOCUMENT ME!
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class ChannelRemovedEvent extends EventObject
{
	private AppTreeChannelNode[] nodes = null;

	/**
	 * TODO DOCUMENT ME!
	 *
	 * @param source
	 * @param nodes DOCUMENT ME!
	 */
	public ChannelRemovedEvent(Object source, AppTreeChannelNode[] nodes)
	{
		// TODO Auto-generated constructor stub
		super(source);
		this.nodes = nodes;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public AppTreeChannelNode[] getChannelNode()
	{
		return nodes;
	}
}

/* __oOo__ */
