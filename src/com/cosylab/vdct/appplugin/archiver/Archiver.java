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

package com.cosylab.vdct.appplugin.archiver;

import com.cosylab.vdct.appplugin.AppFrame;
import com.cosylab.vdct.appplugin.AppList;
import com.cosylab.vdct.appplugin.AppTree;
import com.cosylab.vdct.appplugin.AppTreeNode;
import com.cosylab.vdct.appplugin.Channel;
import com.cosylab.vdct.appplugin.Engine;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;


/**
 * <code>Archiver</code> is the frame of the Archiver application.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class Archiver extends AppFrame
{
	/**
	 * Creates a new ArchiverDialog object.
	 */
	public Archiver()
	{
		super();
	}

	/*
	 *  (non-Javadoc)
	 * @see com.cosylab.vdct.appplugin.AppFrame#initialization()
	 */
	protected void nameInitialization()
	{
		this.setTitle("Archiver");
	}

	/*
	 *  (non-Javadoc)
	 * @see com.cosylab.vdct.appplugin.AppFrame#getTree()
	 */
	public AppTree getTree()
	{
		if (tree == null) {
			tree = new ArchiverTree(this);
		}

		return tree;
	}

	/*
	 *  (non-Javadoc)
	 * @see com.cosylab.vdct.appplugin.AppFrame#getList()
	 */
	public AppList getList()
	{
		if (list == null) {
			list = new AppList();
		}

		return list;
	}

	/*
	 *  (non-Javadoc)
	 * @see com.cosylab.vdct.appplugin.AppFrame#openFile()
	 */
	public synchronized String openFile()
	{
		String file = super.openFile();

		if (file != null) {
			tree.getRoot().getTreeUserElement().setName(file);
			tree.getDefaultModel().reload();
		}

		return file;
	}

	/*
	 *  (non-Javadoc)
	 * @see com.cosylab.vdct.appplugin.AppFrame#fileApproved(java.io.File)
	 */
	protected boolean fileApproved(File f)
	{
		if (!f.getAbsolutePath().toLowerCase().endsWith(".xml")) {
			JOptionPane.showMessageDialog(this, "Cannot open file " + f + ".",
			    "File invalid", JOptionPane.WARNING_MESSAGE);

			return false;
		}

		return true;
	}

	/*
	 *  (non-Javadoc)
	 * @see com.cosylab.vdct.appplugin.AppFrame#addExtension(java.io.File)
	 */
	protected File addExtension(File f)
	{
		String name = f.getAbsolutePath();

		if (!name.endsWith(".xml")) {
			f = new File(name + ".xml");
		}

		return f;
	}

	/*
	 *  (non-Javadoc)
	 * @see com.cosylab.vdct.appplugin.AppFrame#saveFile(java.io.File)
	 */
	public synchronized boolean saveFile(File file, AppTreeNode source, boolean changeTitle)
	{
		if (super.saveFile(file, source, changeTitle)) {
			tree.getRoot().getTreeUserElement().setName(file.getName());

			return true;
		}

		return false;
	}

	/*
	 *  (non-Javadoc)
	 * @see com.cosylab.vdct.appplugin.AppFrame#getFileChooser()
	 */
	protected JFileChooser getFileChooser()
	{
		if (fileChooser == null) {
			super.getFileChooser().setFileFilter(new FileFilter() {
					public boolean accept(File f)
					{
						return f.isDirectory()
						|| f.getName().toLowerCase().endsWith(".xml");
					}

					public String getDescription()
					{
						return "XML files";
					}
				});
			fileChooser.setMultiSelectionEnabled(false);
		}

		return fileChooser;
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.appplugin.AppFrame#getEngine()
	 */
	public Engine getEngine()
	{
		if (engine == null) {
			engine = new ArchiverEngine(this);
		}

		return engine;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args)
	{
		Archiver dialog = new Archiver();

		for (int i = 0; i < 15; i++) {
			Channel ch = new Channel(i + " channel");
			dialog.addChannel(ch);
		}

		//		Vector v = DataProvider.getInstance().getInspectable();
		//		for (int i = 0; i < v.size(); i++) {
		//		    System.out.println(v.getClass());
		//		}
		dialog.setVisible(true);
	}
}

/* __oOo__ */
