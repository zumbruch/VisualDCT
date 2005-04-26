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

import com.cosylab.vdct.appplugin.Root;


/**
 * <code>ArchiverEngineConfigRoot</code> is the root element of for the
 * <code>ArhiverTree</code>. It holds information about all
 * <code>ArchiverTreeGroup</code> which paths are connected to this root. This
 * element is not  editable.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class EngineConfigRoot extends Root
{
	/**
	 * Creates a new ArchiverEngineConfigRoot object. The name for the
	 * EngineConfig should be  fileName;
	 */
	public EngineConfigRoot()
	{
		super("EngineConfig");
		isEditable = false;
	}
}

/* __oOo__ */
