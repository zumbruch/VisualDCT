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
	private boolean hasValue;

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
	public ArchiverTreeNode(TreeUserElement userObject)
	{
		this(userObject, true, false);
	}

	/**
	 * Creates a new ArchiverTreeNode object.
	 *
	 * @param userObject <code>TreeUserElement</code> which is wrapped in this
	 *        node
	 * @param allowsChildren flag indicating if this node is allowed to have
	 *        children
	 * @param hasValue this field indicates if the node has a value which could
	 *        be adjusted     in tree by editing the nodes
	 */
	public ArchiverTreeNode(TreeUserElement userObject, boolean allowsChildren,
	    boolean hasValue)
	{
		super(userObject, allowsChildren);
		this.hasValue = hasValue;
	}

	/**
	 * Returns the <code>TreeUserElement</code> that is wrapped in this node.
	 *
	 * @return TreeUserElement
	 */
	public TreeUserElement getTreeUserElement()
	{
		return (TreeUserElement)userObject;
	}

	/**
	 * Returns wheather this node has a value, adjustable in tree or not.
	 *
	 * @return true if has value
	 */
	public boolean hasValue()
	{
		return hasValue;
	}

	/*
	public String toString() {
	    return getTreeUserElement().getName() + ": " + getValue();
	}*/
	private String value = "";

	//TODO implement an appropriate solution for adjusting values of the object
	//TreeNode should not be storage for properties of the userObject
	//This implementation is only temporarily for test purposes
	public void setValue(String newValue)
	{
		this.value = newValue;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getValue()
	{
		return value;
	}
}

/* __oOo__ */
