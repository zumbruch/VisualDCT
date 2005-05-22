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

import java.io.File;
import java.util.ArrayList;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;


/**
 * <code>Engine</code> ...  DOCUMENT ME!
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public abstract class Engine
{
	protected AppFrame frame;

	/**
	 * TODO DOCUMENT ME!
	 *
	 * @param frame DOCUMENT ME!
	 */
	public Engine(AppFrame frame)
	{
		super();
		this.frame = frame;
		initialize();
	}
	
	protected ArrayList list = new ArrayList();
	
	/**
	 * 
	 * Constructs the TreePath from the TreeNode given as parameter. Path is constructed from 
	 * this node to the root node.
	 * @param last
	 * @return
	 */
	protected TreePath constructTreePath(TreeNode last) {
	    list.clear();
	    return recursiveConstructTreePath(last);
	    
	}
	
	private TreePath recursiveConstructTreePath(TreeNode last) {
	    
	    list.add(last);
	    TreeNode node = last.getParent();
	    if (node == null) {
	        Object[] path = new Object[list.size()];
	        int size = list.size();
	        for (int i = 0; i < size; i++) {
	            path[i] = list.get(size-1-i);
	        }
	        return new TreePath(path);
	    } else {
	        return constructTreePath(node);
	    }
	    
	}

	/**
	 * Initializes the engine.
	 */
	protected abstract void initialize();

	/**
	 * Save data to specified file. Data is supplied by the AppTreeNode root
	 * and its children. Method returns true if saving was successful.
	 *
	 * @param file destination file
	 * @param root source
	 *
	 * @return true if successful
	 */
	public abstract boolean saveToFile(File file, AppTreeNode root);

	/**
	 * Load data from a specified file. Method returns the root element (with
	 * all children) for the tree.
	 *
	 * @param file source file
	 *
	 * @return the root
	 */
	public abstract AppTreeNode openFromFile(File file);
}

/* __oOo__ */
