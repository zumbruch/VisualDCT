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

package com.cosylab.vdct.archiver;

/**
 * <code>ArchiverEngineConfigRoot</code> is the root element of for the
 * <code>ArhiverTree</code>. It holds information about all
 * <code>ArchiverTreeGroup</code> which paths are connected to this root.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class EngineConfigRoot extends ArchiverTreeElement
{
	/**
	 * Creates a new ArchiverEngineConfigRoot object.
	 */
	public EngineConfigRoot()
	{
		super("EngineConfig");
	}

	/*
	 *  (non-Javadoc)
	 * @see com.cosylab.vdct.archiver.ArchiverTreeElement#getName()
	 * This method always return "EngineConfig".
	 */
	public String getName()
	{
		return "EngineConfig";
	}

	/*
	 *  (non-Javadoc)
	 * @see com.cosylab.vdct.archiver.TreeUserElement#setName(java.lang.String)
	 * Renaming the root is not allowed
	 */
	public void setName(String name)
	{
		//do nothig
	}
}

/* __oOo__ */
