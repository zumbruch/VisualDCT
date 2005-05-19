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
import com.cosylab.vdct.appplugin.AppTree;
import com.cosylab.vdct.appplugin.AppTreeChannelNode;
import com.cosylab.vdct.appplugin.AppTreeElement;
import com.cosylab.vdct.appplugin.AppTreeNode;
import com.cosylab.vdct.appplugin.Channel;
import com.cosylab.vdct.appplugin.Group;
import com.cosylab.vdct.appplugin.Property;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.DefaultListModel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;


/**
 * <code>AHTree</code> ...  DOCUMENT ME!
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class AHTree extends AppTree
{
	private JMenuItem[] popupItems;
	private JMenuItem[] propertyItems;
	private JMenuItem[] includeItems;

	/**
	 * Constructs a new Alarm Handler Plugin tree.
	 *
	 * @param handler the Application Frame of this tree
	 */
	public AHTree(AppFrame handler)
	{
		super(handler);
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.appplugin.AppTree#initialization()
	 */
	protected void initialization()
	{
		rootNode = new AppTreeNode(new Group("NULL"));

		DefaultTreeModel model = new DefaultTreeModel(rootNode);
		setModel(model);
		defaultEditor.setEditorComponent(new AHEditorComponent());

		popupItems = new JMenuItem[AHEngine.inputFormat.length - 1];

		for (int i = 1; i <= popupItems.length; i++) {
			final String name = (i == 23) ? "$GUIDANCE - URL"
				: AHEngine.inputFormat[i];
			final int c = i;

			popupItems[i - 1] = new JMenuItem(name);
			popupItems[i - 1].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{
						AppTreeElement element = null;

						if (c == 1) {
							element = new Group("<newGroup>");
						} else if (c == 2) {
							element = new Include("<fileName>");
						} else if (c == 3 || c == 4 || c == 5 || c == 9) {
							element = new Property(name, false);
						} else if (c == 21) {
							element = new Property(name, false);
						} else if (c == 22) {
							element = new Property("$GUIDANCE", "<URL>");
						} else {
							element = new Property(name, true);
							((Property)element).setValue(AHEngine.defaultPropertyValues[c]);
						}

						AppTreeNode parent = (AppTreeNode)getSelectionPath()
							.getLastPathComponent();
						AppTreeNode node = new AppTreeNode(element);

						if (element instanceof Property) {
							boolean added = false;

							for (int i = 0; i < parent.getChildCount(); i++) {
								if (!(((AppTreeNode)parent.getChildAt(i))
								    .getTreeUserElement() instanceof Property)) {
									parent.insert(node, i);
									added = true;

									break;
								}
							}

							if (!added) {
								parent.add(node);
							}
						} else {
							parent.add(node);
						}

						getDefaultModel().reload(parent);
					}
				});
		}

		propertyItems = new JMenuItem[AHEngine.propertyItems.length];

		for (int i = 0; i < propertyItems.length; i++) {
			final String name = AHEngine.propertyItems[i];
			final int c = i;

			propertyItems[i] = new JMenuItem(name);
			propertyItems[i].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{
						AppTreeElement element = null;
						AppTreeNode parent = (AppTreeNode)getSelectionPath()
							.getLastPathComponent();

						if (c == 0) {
							element = new Text("<insert guidance text>");
							parent.add(new AppTreeNode(element));
						} else {
							element = new Property(name, true);

							if (c == 9) {
								((Property)element).setValue(AHEngine.severityCommands[0]);
							} else if (c == 11) {
								((Property)element).setValue(AHEngine.statCommands[0]);
							}

							parent.insert(new AppTreeNode(element), 0);
						}

						sortChildren(parent);
						getDefaultModel().reload(parent);
					}
				});
		}

		includeItems = new JMenuItem[3];
		includeItems[0] = new JMenuItem("Export");
		includeItems[0].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					AppTreeNode export = (AppTreeNode)getSelectionPath()
						.getLastPathComponent();

					if (!(export.getTreeUserElement() instanceof Group)) {
						return;
					}

					((AlHandler)appFrame).setSelectedFileChooserFilter(AlHandler.INCLUDE);

					File f = appFrame.saveFileAs(export);

					AppTreeNode parent = (AppTreeNode)export.getParent();

					//TODO is it absolute path or should the include file be in the same directory
					if (f != null) {
						AppTreeNode inc = new AppTreeNode(new Include(
							        f.getName()));

						if (export.isRoot()) {
							setRoot(inc);
						} else {
							parent.add(inc);
							export.removeFromParent();
							getDefaultModel().reload(parent);
						}
					}
				}
			});

		includeItems[1] = new JMenuItem("Import");
		includeItems[1].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					TreePath path = getSelectionPath();

					if (path == null) {
						return;
					}

					AppTreeNode imp = (AppTreeNode)path.getLastPathComponent();
					AppTreeElement element = imp.getTreeUserElement();

					if (!(element instanceof Include)) {
						return;
					}

					File file = new File(element.getName());

					if (!file.exists()) {
						//TODO how is with absoulte path
						File f = appFrame.getCurrentFile();

						if (f == null) {
							int i = JOptionPane.showConfirmDialog(appFrame,
								    "Current data should be saved first. \n Would you like to save now?",
								    "Caution", JOptionPane.YES_NO_OPTION,
								    JOptionPane.WARNING_MESSAGE);

							if (i == JOptionPane.YES_OPTION) {
								appFrame.saveFileAs(getRoot());

								f = appFrame.getCurrentFile();
							} else {
								return;
							}
						}

						file = new File(f.getParentFile(), file.getName());
					}

					AppTreeNode load = appFrame.getEngine().openFromFile(file);

					if (load == null) {
						return;
					}

					String name = load.getTreeUserElement().getName();
					AppTreeNode parent = (AppTreeNode)imp.getParent();

					if (parent.getTreeUserElement().getName().equals(name)) {
						while (!load.isLeaf()) {
							parent.add((MutableTreeNode)load.getFirstChild());
						}

						imp.removeFromParent();
						getDefaultModel().reload(parent);
					} else {
						JOptionPane.showMessageDialog(appFrame,
						    "Unable to import file. /n Parent name in the included file /n does not match group "
						    + name + ".", "Invalid data",
						    JOptionPane.ERROR_MESSAGE);
					}
				}
			});

		includeItems[2] = new JMenuItem("Open");
		includeItems[2].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					AlHandler handler = new AlHandler();

					TreePath path = getSelectionPath();

					if (path == null) {
						return;
					}

					AppTreeNode imp = (AppTreeNode)path.getLastPathComponent();
					AppTreeElement element = imp.getTreeUserElement();

					if (!(element instanceof Include)) {
						return;
					}

					File file = new File(element.getName());

					if (!file.exists()) {
						//TODO how is with absoulte path
						File f = appFrame.getCurrentFile();

						if (f == null) {
							int i = JOptionPane.showConfirmDialog(appFrame,
								    "File does not exist in the root. \n Current data should be saved first to specify the home directory. \n Would you like to save now?",
								    "Caution", JOptionPane.YES_NO_OPTION,
								    JOptionPane.WARNING_MESSAGE);

							if (i == JOptionPane.YES_OPTION) {
								appFrame.saveFileAs(getRoot());

								f = appFrame.getCurrentFile();
							} else {
								return;
							}
						}

						file = new File(f.getParentFile(), file.getName());
					}

					if (handler.openFile(file)) {
						DefaultListModel model = appFrame.getList()
							.getDefaultModel();
						DefaultListModel dest = handler.getList()
							.getDefaultModel();

						for (int i = 0; i < model.size(); i++) {
							dest.addElement(model.get(i));
						}

						//					    handler.setExitOnClose(false);
						handler.setVisible(true);
					}
				}
			});
	}

	private JMenuItem remove;
	private JMenu addMenu;

	/*
	 *  (non-Javadoc)
	 * @see com.cosylab.vdct.appplugin.AppTree#getPopup()
	 */
	protected JPopupMenu getPopup()
	{
		if (popup == null) {
			super.getPopup();

			addMenu = new JMenu("Add item");
			popup.add(addMenu);
			addMenu.setEnabled(false);

			remove = new JMenuItem("Remove");
			remove.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{
						TreePath[] paths = getSelectionPaths();
						ArrayList nodesList = new ArrayList();

						for (int j = 0; j < paths.length; j++) {
							AppTreeNode node = (AppTreeNode)paths[j]
								.getLastPathComponent();
							AppTreeElement elem = node.getTreeUserElement();

							TreeNode parent = node.getParent();
							node.removeFromParent();
							((DefaultTreeModel)getModel()).reload(parent);

							if (elem instanceof Group) {
								for (int i = 0; i < node.getChildCount();
								    i++) {
									AppTreeNode child = (AppTreeNode)node
										.getChildAt(i);

									if (child instanceof AppTreeChannelNode) {
										nodesList.add(child);
									}
								}
							} else if (elem instanceof Channel) {
								nodesList.add(node);
							}
						}

						AppTreeChannelNode[] nodes = new AppTreeChannelNode[nodesList
							.size()];
						fireChannelRemoved((AppTreeChannelNode[])nodesList
						    .toArray(nodes));
					}
				});
			remove.setEnabled(true);
			popup.add(remove);
		}

		return popup;
	}

	private void removeChannelNodes(AppTreeNode node)
	{
		for (int i = 0; i < node.getChildCount(); i++) {
			AppTreeNode child = (AppTreeNode)node.getChildAt(i);

			if (child instanceof AppTreeChannelNode) {
				fireChannelRemoved(new AppTreeChannelNode[]{
					    (AppTreeChannelNode)child
				    });
			} else if (child.getTreeUserElement() instanceof Group) {
				removeChannelNodes(child);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.appplugin.AppTree#popupInit(javax.swing.tree.TreePath)
	 */
	protected void popupInit(TreePath path)
	{
		if (path == null) {
			getPopup().removeAll();

			return;
		}

		if (popup == null) {
			getPopup();
		}

		getPopup().removeAll();

		AppTreeNode node = (AppTreeNode)path.getLastPathComponent();
		AppTreeElement elem = node.getTreeUserElement();

		remove.setEnabled(true);

		addMenu.setEnabled(true);
		addMenu.removeAll();

		JMenuItem[] item = getAddItems(node);

		if (item.length == 0 || item == null) {
			addMenu.setEnabled(false);
		} else {
			for (int i = 0; i < item.length; i++) {
				addMenu.add(item[i]);
			}
		}

		getPopup().add(addMenu);
		getPopup().add(remove);

		if (elem instanceof Group) {
			getPopup().addSeparator();
			getPopup().add(includeItems[0]);
		} else if (elem instanceof Include) {
			getPopup().addSeparator();
			getPopup().add(includeItems[1]);
			getPopup().add(includeItems[2]);
		}
	}

	private JMenuItem[] getAddItems(AppTreeNode node)
	{
		AppTreeElement element = node.getTreeUserElement();

		ArrayList items = new ArrayList();

		if (element instanceof Include || element instanceof Text) {
			//			return new JMenuItem[0];
		} else if (element instanceof Group) {
			for (int i = 0; i < 21; i++) {
				items.add(popupItems[i]);
			}
		} else if (element instanceof Property) {
			String name = element.getName();

			if (name.equals("$GUIDANCE") && !((Property)element).hasValue()) {
				items.add(propertyItems[0]);
			} else if (name.equals("$HEARTBEATPV")) {
				for (int i = 1; i <= 2; i++) {
					boolean exist = false;
					Enumeration children = node.children();

					while (children.hasMoreElements()) {
						AppTreeNode child = (AppTreeNode)children.nextElement();

						if (child.getTreeUserElement().getName().equals(propertyItems[i]
						        .getText())) {
							exist = true;

							break;
						}
					}

					if (!exist) {
						items.add(propertyItems[i]);
					}
				}
			} else if (name.equals("$ACKPV")) {
				if (node.isLeaf()) {
					items.add(propertyItems[4]);
				}
			} else if (name.equals("$FORCEPV") || name.equals("$FORCEPV CALC")) {
				for (int i = 5; i <= 7; i++) {
					boolean exist = false;
					Enumeration children = node.children();

					while (children.hasMoreElements()) {
						AppTreeNode child = (AppTreeNode)children.nextElement();

						if (child.getTreeUserElement().getName().equals(propertyItems[i]
						        .getText())) {
							exist = true;

							break;
						}
					}

					if (!exist) {
						items.add(propertyItems[i]);
					}
				}
			} else if (name.equals("$SEVRCOMMAND")) {
				for (int i = 8; i <= 9; i++) {
					boolean exist = false;
					Enumeration children = node.children();

					while (children.hasMoreElements()) {
						AppTreeNode child = (AppTreeNode)children.nextElement();

						if (child.getTreeUserElement().getName().equals(propertyItems[i]
						        .getText())) {
							exist = true;

							break;
						}
					}

					if (!exist) {
						items.add(propertyItems[i]);
					}
				}
			} else if (name.equals("$STATCOMMAND")) {
				for (int i = 10; i <= 11; i++) {
					boolean exist = false;
					Enumeration children = node.children();

					while (children.hasMoreElements()) {
						AppTreeNode child = (AppTreeNode)children.nextElement();

						if (child.getTreeUserElement().getName().equals(propertyItems[i]
						        .getText())) {
							exist = true;

							break;
						}
					}

					if (!exist) {
						items.add(propertyItems[i]);
					}
				}
			} else if (name.equals("$ALARMCOUNTFILTER")) {
				for (int i = 12; i <= 13; i++) {
					boolean exist = false;
					Enumeration children = node.children();

					while (children.hasMoreElements()) {
						AppTreeNode child = (AppTreeNode)children.nextElement();

						if (child.getTreeUserElement().getName().equals(propertyItems[i]
						        .getText())) {
							exist = true;

							break;
						}
					}

					if (!exist) {
						items.add(propertyItems[i]);
					}
				}
			} else {
				//				items = new JMenuItem[0];
			}
		} else if (element instanceof Channel) {
			items.add(propertyItems[3]);

			for (int i = 1; i < 21; i++) {
				items.add(popupItems[i]);
			}
		}

		JMenuItem[] it = new JMenuItem[items.size()];

		return (JMenuItem[])items.toArray(it);
	}

	/*
	 *  (non-Javadoc)
	 * @see com.cosylab.vdct.appplugin.AppTree#reset()
	 */
	public void reset()
	{
		stopEditing();
		rootNode = new AppTreeNode(new Group("NULL"));
		super.reset();
	}

	/**
	 * Sort children according to the order in the AHEngine.propertyItems
	 *
	 * @param node
	 */
	private void sortChildren(AppTreeNode node)
	{
		AppTreeNode[] children = new AppTreeNode[node.getChildCount()];

		for (int i = 0; i < children.length; i++) {
			children[i] = (AppTreeNode)node.getChildAt(i);
		}

		for (int j = 0; j < AHEngine.propertyItems.length; j++) {
			for (int i = 0; i < children.length; i++) {
				String name = ((AppTreeElement)children[i].getTreeUserElement())
					.getName();

				if (name.equals(AHEngine.propertyItems[j])) {
					node.add(children[i]);
				}
			}
		}
	}
}

/* __oOo__ */
