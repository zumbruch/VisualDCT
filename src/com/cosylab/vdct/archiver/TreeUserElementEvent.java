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

import java.util.EventObject;


/**
 * <code>TreeUserElementEvent</code> is triggered by
 * <code>TreeUserElement</code> after it received new elements.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class TreeUserElementEvent extends EventObject
{
	private Object[] elements;
	private String type;

	/**
	 * Constructs new TreeUserElementEvent.
	 *
	 * @param source The object on which the Event initially occurred
	 * @param elements elements that were added or removed from the
	 *        TreeUserElement
	 * @param typeOfElements String representation of the type of elements
	 *
	 * @see com.cosylab.vdct.archiver.ArchiverTreeElement
	 */
	public TreeUserElementEvent(Object source, Object[] elements,
	    String typeOfElements)
	{
		super(source);
		this.elements = elements;
		this.type = typeOfElements;
	}

	/**
	 * Returns the type of the elements
	 *
	 * @return the type
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Returns an array of elements.
	 *
	 * @return added/removed elements
	 */
	public Object[] getElements()
	{
		return elements;
	}
}

/* __oOo__ */
