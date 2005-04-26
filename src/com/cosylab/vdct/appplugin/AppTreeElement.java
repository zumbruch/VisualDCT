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

import java.io.Serializable;


/**
 * <code>ArchiverTreeElement</code> is an abstract implementation of
 * <code>TreeUserElement</code> which should all non-Record elements in the
 * tree extend.  <code>ArchiverTreeElement</code> can hold information about
 * its properties and other elements attached to it and triggers
 * TreeUserElementEvent when new elements are added or removed.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public abstract class AppTreeElement implements Serializable
{
	protected String name = "";
	protected boolean isEditable = true;

	/**
	 * Creates a new ArchiverTreeElement object.
	 *
	 * @param name the name of the element
	 */
	public AppTreeElement(String name)
	{
		setName(name);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param name DOCUMENT ME!
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getName()
	{
		return name;
	}

	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return getName();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean isEditable()
	{
		return isEditable;
	}
}

/* __oOo__ */
