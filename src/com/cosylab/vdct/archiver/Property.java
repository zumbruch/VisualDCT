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
 * <code>Property</code> ...  DOCUMENT ME!
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class Property extends ArchiverTreeElement
{
	private String value;
	private boolean hasValue;

	/**
	 * Creates a new Property object.
	 *
	 * @param name the name of the Property
	 * @param hasValue indicator if the property will display value 
	 * 					(if false, the value can never be set later)
	 */
	public Property(String name, boolean hasValue)
	{
		this(name, null, false);
	}

	/**
	 * Creates a new Property object.
	 *
	 * @param name the name of the Property
	 * @param value the value of the Property
	 */
	public Property(String name, String value)
	{
		this(name, value, true);
	}

	private Property(String name, String value, boolean hasValue)
	{
		super(name);
		this.hasValue = hasValue;
		setValue(value);
	}

	/**
	 * Returns a flag indicating whether this Property displays a value.
	 *
	 * @return true if Property can display a value
	 */
	public boolean hasValue()
	{
		return hasValue;
	}

	/**
	 * Sets the value of the Property (Value can only be set if the value was given when
	 * the Property was constructed or the hasValue parameter was true at construction).
	 *
	 * @param value new Value
	 */
	public void setValue(String value)
	{
		if (hasValue) {
			this.value = value;
		}
	}

	/**
	 * Returns the value of the Property.
	 *
	 * @return value
	 */
	public String getValue()
	{
		return value;
	}

	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return getName() + (hasValue ? ": " + getValue() : "");
	}
}

/* __oOo__ */
