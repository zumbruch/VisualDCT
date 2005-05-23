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

package com.cosylab.vdct.appplugin.alarmhandler;

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
 * <code>Test</code> ...  DOCUMENT ME!
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class AlHandler extends AppFrame
{
	private FileFilter configFilter;
	private FileFilter includeFilter;
	static final int CONFIG = 1;
	static final int INCLUDE = 2;

	/**
	 * TODO DOCUMENT ME!
	 */
	public AlHandler()
	{
		super();
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.appplugin.AppFrame#nameInitialization()
	 */
	protected void nameInitialization()
	{
		this.setTitle("Alarm Handler");
	}

	/*
	 *  (non-Javadoc)
	 * @see com.cosylab.vdct.appplugin.AppFrame#getTree()
	 */
	public AppTree getTree()
	{
		if (tree == null) {
			tree = new AHTree(this);
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
	 * @see com.cosylab.vdct.appplugin.AppFrame#fileApproved(java.io.File)
	 */
	protected boolean fileApproved(File f)
	{
		if (!(f.getAbsolutePath().toLowerCase().endsWith(".alhconfig")
		    || f.getAbsolutePath().toLowerCase().endsWith(".alhinclude"))) {
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

		if (!(name.toLowerCase().endsWith(".alhconfig")
		    || name.toLowerCase().endsWith(".alhinclude"))) {
			FileFilter filter = getFileChooser().getFileFilter();

			if (filter == configFilter) {
				f = new File(name + ".alhConfig");
			} else {
				f = new File(name + ".alhInclude");
			}
		}

		return f;
	}

	/*
	 *  (non-Javadoc)
	 * @see com.cosylab.vdct.appplugin.AppFrame#getFileChooser()
	 */
	protected JFileChooser getFileChooser()
	{
		if (fileChooser == null) {
			configFilter = new FileFilter() {
						public boolean accept(File f)
						{
							return f.isDirectory()
							|| f.getName().toLowerCase().endsWith(".alhconfig");
						}

						public String getDescription()
						{
							return "AlarmHandler config files (*.alhConfig)";
						}
					};

			includeFilter = new FileFilter() {
						public boolean accept(File f)
						{
							return f.isDirectory()
							|| f.getName().toLowerCase().endsWith(".alhinclude");
						}

						public String getDescription()
						{
							return "AlarmHandler included files (*.alhInclude)";
						}
					};

			super.getFileChooser().addChoosableFileFilter(configFilter);
			super.getFileChooser().addChoosableFileFilter(includeFilter);

			fileChooser.setMultiSelectionEnabled(false);
		}

		return fileChooser;
	}

	void setSelectedFileChooserFilter(int filter)
	{
		if (filter == CONFIG) {
			getFileChooser().setFileFilter(configFilter);
		} else if (filter == INCLUDE) {
			getFileChooser().setFileFilter(includeFilter);
		}
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.appplugin.AppFrame#getEngine()
	 */
	public Engine getEngine()
	{
		if (engine == null) {
			engine = new AHEngine(this);
		}

		return engine;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param f DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public synchronized boolean openFile(File f)
	{
		tree.reset();

		if (!fileApproved(f)) {
			return false;
		}

		AppTreeNode node = getEngine().openFromFile(f);

		if (node != null) {
			tree.setRoot(node);
			tree.getDefaultModel().reload();
			currentFile = f;

			return true;
		}

		return false;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args)
	{
		AppFrame test = new AlHandler();

		for (int i = 0; i < 9; i++) {
			Channel ch = new Channel("rf" + i);
			test.addChannel(ch);
		}

		test.setVisible(true);
	}
}

/* __oOo__ */
