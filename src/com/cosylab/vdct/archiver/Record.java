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

import java.io.Serializable;


/**
 * <code>Record</code> represents an EPICS record
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class Record implements TreeUserElement, Serializable
{
	private String name;

	/**
	 * Creates a new Record object.
	 *
	 * @param name name of the record
	 */
	public Record(String name)
	{
		this.name = name;
	}

	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return getName();
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.archiver.TreeUserElement#setName(java.lang.String)
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/*
	 *  (non-Javadoc)
	 * @see com.cosylab.vdct.archiver.TreeUserElement#getName()
	 */
	public String getName()
	{
		return name;
	}
}

/* __oOo__ */
