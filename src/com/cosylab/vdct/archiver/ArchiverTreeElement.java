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

import java.util.ArrayList;
import java.util.Collection;


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
public abstract class ArchiverTreeElement implements TreeUserElement
{
	protected static final String ADDED = "Elements added";
	protected static final String REMOVED = "Elements removed";

	/** DOCUMENT ME! */
	public static final String PROPERTIES = "Properties";
	protected String name = "";
	private ArrayList properties = new ArrayList();
	private ArrayList elementListeners = new ArrayList();

	/*
	 *  (non-Javadoc)
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

	/**
	 * Returns all properties of this element.
	 *
	 * @return Collection of properties.
	 */
	public Collection getProperties()
	{
		return properties;
	}

	/**
	 * Removes properties from this element.
	 *
	 * @param removedProperties ArrayList of properties to be removed
	 */
	public void removeProperties(ArrayList removedProperties)
	{
		properties.removeAll(removedProperties);
		fireTreeUserElementEvent(removedProperties, REMOVED, PROPERTIES);
	}

	/**
	 * Adds properties to this element.
	 *
	 * @param addedProperties properties to be added
	 */
	public void addProperties(ArrayList addedProperties)
	{
		properties.addAll(addedProperties);
		fireTreeUserElementEvent(addedProperties, ADDED, PROPERTIES);
	}

	/**
	 * Triggers <code>TreeUserElementEvent</code> and notifies the listeners.
	 *
	 * @param elements list of elements that were added/removed
	 * @param action tha action taken (added/removed)
	 * @param typeOfElements String representing the type of elements
	 */
	protected void fireTreeUserElementEvent(ArrayList elements, String action,
	    String typeOfElements)
	{
		TreeUserElementEvent event = new TreeUserElementEvent(this,
			    elements.toArray(), typeOfElements);

		if (action.equals(ADDED)) {
			for (int i = 0; i < elementListeners.size(); i++) {
				((TreeUserElementListener)elementListeners.get(i))
				.elementsAdded(event);
			}
		} else if (action.equals(REMOVED)) {
			for (int i = 0; i < elementListeners.size(); i++) {
				((TreeUserElementListener)elementListeners.get(i))
				.elementsRemoved(event);
			}
		}
	}

	/**
	 * Adds <code>TreeUserElementListener</code>.
	 *
	 * @param listener new listener
	 */
	public void addTreeUserElementListener(TreeUserElementListener listener)
	{
		elementListeners.add(listener);
	}

	/**
	 * Removes <code>TreeUserElementListener</code>
	 *
	 * @param listener listener to be removed
	 */
	public void removeTreeUserElementListener(TreeUserElementListener listener)
	{
		elementListeners.remove(listener);
	}

	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return getName();
	}
}

/* __oOo__ */
