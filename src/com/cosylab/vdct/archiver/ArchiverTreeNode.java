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
 * <code>ArchiverTreeNode</code> ...  DOCUMENT ME!
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class ArchiverTreeNode extends DefaultMutableTreeNode
{
	/**
	 * Creates a new ArchiverTreeNode object.
	 */
	public ArchiverTreeNode()
	{
		this(null);
	}

	/**
	 * Creates a new ArchiverTreeNode object and allows children for this node.
	 *
	 * @param userObject <code>TreeUserElement</code> which is wrapped in this
	 *        node
	 */
	public ArchiverTreeNode(ArchiverTreeElement userObject)
	{
		super(userObject);
	}

	
	/**
	 * Returns the <code>TreeUserElement</code> that is wrapped in this node.
	 *
	 * @return TreeUserElement
	 */
	public ArchiverTreeElement getArchiverTreeUserElement()
	{
		return (ArchiverTreeElement)userObject;
	}
}

/* __oOo__ */
