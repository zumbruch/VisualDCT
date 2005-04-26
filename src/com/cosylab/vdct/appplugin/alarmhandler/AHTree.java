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
	 * TODO DOCUMENT ME!
	 *
	 * @param handler DOCUMENT ME!
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
							element = new Group("<new group>");
						} else if (c == 2) {
							element = new Include("<file name>");
						} else if (c == 3 || c == 4 || c == 5 || c == 9) {
							element = new Property(name, false);
						} else if (c == 21) {
							element = new Property(name, false);
						} else if (c == 22) {
							element = new Property("$GUIDANCE", "<URL>");
						} else {
							element = new Property(name, true);
						}

						AppTreeNode parent = (AppTreeNode)getSelectionPath()
							.getLastPathComponent();
						parent.add(new AppTreeNode(element));
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

					File f = appFrame.saveFileAs(export);

					AppTreeNode parent = (AppTreeNode)export.getParent();

					//TODO is it absolute path or should the include file be in the same directory
					if (f != null) {
						parent.add(new AppTreeNode(new Include(f.getName())));
						export.removeFromParent();
						getDefaultModel().reload(parent);
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
					AppTreeNode imp = (AppTreeNode)path
						.getLastPathComponent();
					AppTreeElement element = imp.getTreeUserElement();
					
					if (!(element instanceof Include)) {
						return;
					}

					//TODO how is with absoulte path
					File f = appFrame.getCurrentFile();

					if (f == null) {
						int i = JOptionPane.showConfirmDialog(appFrame,
							    "Current data should be saved first. \n Would you like to save now?",
							    "Caution", JOptionPane.YES_NO_OPTION,
							    JOptionPane.WARNING_MESSAGE);

						if (i == JOptionPane.YES_OPTION) {
							appFrame.saveFileAs(getRoot());

							return;
						} else {
							return;
						}
					}

					File parentFile = f.getParentFile();
					File file = new File(parentFile, element.getName());
					
					final AppTreeNode load = appFrame.getEngine().openFromFile(file);
					
					if (load == null) {
						return;
					}
					
					String name = load.getTreeUserElement().getName();
					AppTreeNode parent = (AppTreeNode)imp.getParent();
			
					if (parent.getTreeUserElement().getName().equals(name)) {
					    while(!load.isLeaf()) {
					        parent.add((MutableTreeNode) load.getFirstChild());
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
					AppTreeNode imp = (AppTreeNode)path
						.getLastPathComponent();
					AppTreeElement element = imp.getTreeUserElement();

					if (!(element instanceof Include)) {
						return;
					}

					//TODO how is with absoulte path
					File f = appFrame.getCurrentFile();

					if (f == null) {
						int i = JOptionPane.showConfirmDialog(appFrame,
							    "Current data should be saved first. \n Would you like to save now?",
							    "Caution", JOptionPane.YES_NO_OPTION,
							    JOptionPane.WARNING_MESSAGE);

						if (i == JOptionPane.YES_OPTION) {
							appFrame.saveFileAs(getRoot());

							return;
						} else {
							return;
						}
					}

					File parentFile = f.getParentFile();
					File file = new File(parentFile, element.getName());

					if (handler.openFile(file)) {
					    DefaultListModel model = appFrame.getList().getDefaultModel();
					    DefaultListModel dest = handler.getList().getDefaultModel();
					    for (int i = 0; i < model.size(); i++){
					        dest.addElement(model.get(i));
					    }
					    handler.setExitOnClose(false);
						handler.show();
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
		JMenuItem[] items = null;

		if (element instanceof Include || element instanceof Text) {
			items = new JMenuItem[0];
		} else if (element instanceof Group) {
			items = new JMenuItem[22];
			System.arraycopy(popupItems, 0, items, 0, 22);
		} else if (element instanceof Property) {
			String name = element.getName();

			if (name.equals("$GUIDANCE") && !((Property)element).hasValue()) {
				items = new JMenuItem[]{ propertyItems[0] };
			} else if (name.equals("$HEARTBEATPV")) {
				items = new JMenuItem[]{ propertyItems[1], propertyItems[2] };
			} else if (name.equals("$ACKPV")) {
				items = new JMenuItem[]{ propertyItems[4] };
			} else if (name.equals("$FORCEPV") || name.equals("$FORCEPV CALC")) {
				items = new JMenuItem[]{
						propertyItems[5], propertyItems[6], propertyItems[7]
					};
			} else if (name.equals("$SEVRCOMMAND")) {
				items = new JMenuItem[]{ propertyItems[8], propertyItems[9] };
			} else if (name.equals("$STATCOMMAND")) {
				items = new JMenuItem[]{ propertyItems[10], propertyItems[11] };
			} else if (name.equals("$ALARMCOUNTFILTER")) {
				items = new JMenuItem[]{ propertyItems[12], propertyItems[13] };
			} else {
				items = new JMenuItem[0];
			}
		} else if (element instanceof Channel) {
			items = new JMenuItem[popupItems.length - 1];
			System.arraycopy(popupItems, 0, items, 1, 20);
			items[0] = propertyItems[3];
		}

		return items;
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
