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
 * <code>TreeUserElement</code> should be implemented by the Object which is
 * wrapped as UserObject in the <code>ArchiverTreeNode</code>. This interface
 * provides access to the name of the object and is used by
 * <code>ArchiverTree.CellEditor</code>.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public interface TreeUserElement
{
	/**
	 * Sets new name for the object.
	 *
	 * @param name new name
	 */
	public void setName(String name);

	/**
	 * Returns the name of the object.
	 *
	 * @return name
	 */
	public String getName();
}

/* __oOo__ */
