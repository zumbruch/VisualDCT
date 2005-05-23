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
import com.cosylab.vdct.appplugin.AppTree;
import com.cosylab.vdct.appplugin.AppTreeElement;
import com.cosylab.vdct.appplugin.AppTreeNode;
import com.cosylab.vdct.appplugin.Channel;
import com.cosylab.vdct.appplugin.Group;
import com.cosylab.vdct.appplugin.Property;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;


/**
 * <code>ArchiverTree</code> is an archiver for EPICS records.   This object
 * enables Drag&Drap actions of the ArchiverTreeRecordNodes.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class ArchiverTree extends AppTree
{
	private JMenuItem[] engineItems;
	private JMenuItem[] channelItems;

	/**
	 * Creates a new ArchiverTree object.
	 *
	 * @param arc DOCUMENT ME!
	 */
	public ArchiverTree(AppFrame arc)
	{
		super(arc);
	}

	protected void initialization()
	{
		rootNode = new AppTreeNode(new EngineConfigRoot());

		DefaultTreeModel model = new DefaultTreeModel(rootNode);
		setModel(model);
		defaultEditor.setEditorComponent(new ArchiverEditorComponent());

		// constructs JMenuItems for adding properties to the tree
		engineItems = new JMenuItem[ArchiverEngine.engineConfigProperties.length];

		for (int i = 0; i < engineItems.length; i++) {
			final String name = ArchiverEngine.engineConfigProperties[i];
			final int c = i;
			engineItems[i] = new JMenuItem(name);
			engineItems[i].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{
						boolean hasValue = c == 6 ? false : true;
						Property property = new Property(name, hasValue);

						if (hasValue) {
							property.setValue(ArchiverEngine.defaultPropertyValues[c]);
						}
						
						AppTreeNode node = new AppTreeNode(property);
						rootNode.insert(node, 0);
						getDefaultModel().reload(rootNode);
						appFrame.setIsFileModified(true);
						
						if (property.hasValue()) {
						    startEditingAtPath(constructTreePath(node));
						}
					}
				});
		}

		channelItems = new JMenuItem[ArchiverEngine.channelProperties.length];

		for (int i = 0; i < channelItems.length; i++) {
			final String name = ArchiverEngine.channelProperties[i];
			final int c = i;
			channelItems[i] = new JMenuItem(name);
			channelItems[i].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{
						boolean hasValue = c < 1 ? true : false;
						Property property = new Property(name, hasValue);

						if (hasValue) {
							property.setValue(ArchiverEngine.defaultPropertyValues[6]);
						}
						TreePath path = getSelectionPath();
						AppTreeNode parent = (AppTreeNode)path
							.getLastPathComponent();
						AppTreeNode node = new AppTreeNode(property);
						parent.add(node);
						sortChannelProperties((AppTreeNode)parent);
						getDefaultModel().reload(parent);
						appFrame.setIsFileModified(true);
						
						if (property.hasValue()) {
						    startEditingAtPath(path.pathByAddingChild(node));
						}
					}
				});
		}
	}

	private void sortChannelProperties(AppTreeNode node)
	{
		AppTreeNode[] nodes = new AppTreeNode[node.getChildCount()];

		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = (AppTreeNode)node.getChildAt(i);
		}

		node.removeAllChildren();

		for (int j = 0; j < ArchiverEngine.channelProperties.length; j++) {
			for (int i = 0; i < nodes.length; i++) {
				String name = ((AppTreeElement)nodes[i].getTreeUserElement())
					.getName();

				if (name.equals(ArchiverEngine.channelProperties[j])) {
					node.add(nodes[i]);
				}
			}
		}
	}

	protected JPopupMenu getPopup()
	{
		if (popup == null) {
			super.getPopup();

			addGroup = new JMenuItem("Add Group");
			addGroup.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{
					    AppTreeNode node = new AppTreeNode(new Group("<new group>"));
						rootNode.add(node);
						((DefaultTreeModel)getModel()).reload(rootNode);
						appFrame.setIsFileModified(true);
						startEditingAtPath(constructTreePath(node));
					}
				});
			popup.add(addGroup);

			addProperties = new JMenu("Add Property");
			popup.add(addProperties);
			addProperties.setEnabled(false);

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

							if (!(elem instanceof EngineConfigRoot)) {
								TreeNode parent = node.getParent();
								node.removeFromParent();
								((DefaultTreeModel)getModel()).reload(parent);

								if (elem instanceof Group) {
									for (int i = 0; i < node.getChildCount();
									    i++) {
										nodesList.add((AppTreeNode)node
										    .getChildAt(i));
									}
								} else if (elem instanceof Channel) {
									nodesList.add(node);
								}
							}
						}

						AppTreeNode[] nodes = new AppTreeNode[nodesList
							.size()];
						fireChannelRemoved((AppTreeNode[])nodesList
						    .toArray(nodes));
					}
				});
			remove.setEnabled(false);
			popup.add(remove);
		}

		return popup;
	}

	private JMenuItem addGroup;
	private JMenuItem remove;
	private JMenu addProperties;

	protected void popupInit(TreePath path)
	{
		if (path == null) {
			getPopup().removeAll();

			return;
		}

		if (popup == null) {
			getPopup();
		}

		//		getPopup().removeAll();
		AppTreeNode node = (AppTreeNode)path.getLastPathComponent();
		AppTreeElement elem = node.getTreeUserElement();

		if (!(elem instanceof EngineConfigRoot)) {
			remove.setEnabled(true);
		} else {
			remove.setEnabled(false);
		}

		addProperties.setEnabled(false);
		addProperties.removeAll();

		if (elem instanceof EngineConfigRoot || elem instanceof Channel) {
			addProperties.setEnabled(true);

			JMenuItem[] item = getPropertiesItems(node);

			for (int i = 0; i < item.length; i++) {
				addProperties.add(item[i]);
			}
		}

		getPopup().add(addGroup);
		getPopup().add(addProperties);
		getPopup().add(remove);
	}

	/**
	 * Adds properties to the JMenu. Only those properties are added that are
	 * not attached to the node yet.
	 *
	 * @param node
	 *
	 * @return
	 */
	private JMenuItem[] getPropertiesItems(AppTreeNode node)
	{
		AppTreeElement elem = node.getTreeUserElement();

		ArrayList items = new ArrayList();

		if (elem instanceof EngineConfigRoot) {
			for (int i = 0; i < engineItems.length; i++) {
				boolean exist = false;
				Enumeration children = node.children();

				while (children.hasMoreElements()) {
					AppTreeNode child = (AppTreeNode)children.nextElement();

					if (child.getTreeUserElement().getName().equals(engineItems[i]
					        .getText())) {
						exist = true;

						break;
					}
				}

				if (!exist) {
					items.add(engineItems[i]);
				}
			}
		} else if (elem instanceof Channel) {
			for (int i = 0; i < channelItems.length; i += 3) {
				boolean exist = false;
				Enumeration children = node.children();

				while (children.hasMoreElements()) {
					AppTreeNode child = (AppTreeNode)children.nextElement();

					if (child.getTreeUserElement().getName().equals(channelItems[i]
					        .getText())) {
						exist = true;

						break;
					}
				}

				if (!exist) {
					items.add(channelItems[i]);
				}
			}

			boolean exist = false;

			for (int i = 1; i < channelItems.length - 1; i++) {
				Enumeration children = node.children();

				while (children.hasMoreElements()) {
					AppTreeNode child = (AppTreeNode)children.nextElement();

					if (child.getTreeUserElement().getName().equals(channelItems[i]
					        .getText())) {
						exist = true;

						break;
					}
				}

				if (exist) {
					break;
				}
			}

			if (!exist) {
				items.add(channelItems[1]);
				items.add(channelItems[2]);
			}
		}

		JMenuItem[] array = new JMenuItem[items.size()];

		return (JMenuItem[])items.toArray(array);
	}

	/**
	 * DOCUMENT ME!
	 */
	public void reset()
	{
		stopEditing();
		rootNode = new AppTreeNode(new EngineConfigRoot());
		super.reset();
	}

	/**
	 * Checks if there is another Group with the same name.
	 *
	 * @param name the name to be checked for
	 *
	 * @return true if a group with that name alreasy exist
	 */
	boolean isGroupNameUnique(String name)
	{
		int j = rootNode.getChildCount();

		for (int i = 0; i < j; i++) {
			AppTreeElement elem = ((AppTreeNode)rootNode.getChildAt(i))
				.getTreeUserElement();

			if (elem instanceof Group) {
				if (elem.getName().equals(name)) {
					return false;
				}
			}
		}

		return true;
	}
}

/* __oOo__ */
