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
 * <code>ArchiverEngineConfigRoot</code> is the root element of for the
 * <code>ArhiverTree</code>. It holds information about all
 * <code>ArchiverTreeGroup</code> which paths are connected to this root.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class ArchiverEngineConfigRoot extends ArchiverTreeElement
{
	protected static final String GROUPS = "Groups";
	private ArrayList groups = new ArrayList();

	/**
	 * Creates a new ArchiverEngineConfigRoot object.
	 */
	public ArchiverEngineConfigRoot()
	{
		super();
	}

	/**
	 * Adds <code>ArchiverTreeGroup</code> to this root.
	 *
	 * @param group group to be added
	 */
	public void addArchiverTreeGroup(ArchiverTreeGroup group)
	{
		ArrayList gr = new ArrayList();
		gr.add(group);
		addArchiverTreeGroups(gr);
	}

	/**
	 * Removes <code>ArchiverTreeGroup</code> from the root.
	 *
	 * @param group group to be removed
	 */
	public void removeArchiverTreeGroup(ArchiverTreeGroup group)
	{
		ArrayList gr = new ArrayList();
		gr.remove(group);
		removeArchiverTreeGroups(gr);
	}

	/**
	 * Adds list of <code>ArchiverTreeGroup</code>s.
	 *
	 * @param addedGroups groups to be added
	 */
	public void addArchiverTreeGroups(ArrayList addedGroups)
	{
		groups.addAll(addedGroups);
		fireTreeUserElementEvent(addedGroups, ADDED, GROUPS);
	}

	/**
	 * Returns all <code>ArchiverTreeGroup</code> attached to this root.
	 *
	 * @return Collection of groups
	 */
	public Collection getArchiverTreeGroups()
	{
		return groups;
	}

	/**
	 * Removes a list of <code>ArchiverTreeGroup</code>s.
	 *
	 * @param removedGroups groups to be removed
	 */
	public void removeArchiverTreeGroups(ArrayList removedGroups)
	{
		groups.removeAll(removedGroups);
		fireTreeUserElementEvent(removedGroups, REMOVED, GROUPS);
	}

	/*
	 *  (non-Javadoc)
	 * @see com.cosylab.vdct.archiver.TreeUserElement#getName()
	 */
	public String getName()
	{
		return "EngineConfig";
	}

	/*
	 *  (non-Javadoc)
	 * @see com.cosylab.vdct.archiver.TreeUserElement#setName(java.lang.String)
	 */
	public void setName(String name)
	{
		//do nothig
	}
}

/* __oOo__ */
