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

import javax.swing.tree.DefaultMutableTreeNode;


/**
 * <code>ArchiverTreeGroup</code> is an extension of
 * <code>ArchiverTreeElement</code> representing the group that can be added
 * in the <code>ArchiverTree</code>.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class Group extends ArchiverTreeElement
{
	private DefaultMutableTreeNode node;

	/**
	 * Creates a new ArchiverTreeGroup object.
	 *
	 * @param name the name of the group
	 */
	public Group(String name)
	{
		super(name);
	}

	/**
	 * Sets the <code>ArchiverTreeNode</code> that this group belongs too.
	 *
	 * @param node node wrapper for the group
	 */
	public void setTreeNode(ArchiverTreeNode node)
	{
		this.node = node;
	}

	/**
	 * Returns the <code>ArchiverTreeNode</code> of this group.
	 *
	 * @return node of the group
	 */
	public DefaultMutableTreeNode getNode()
	{
		return node;
	}
}

/* __oOo__ */
