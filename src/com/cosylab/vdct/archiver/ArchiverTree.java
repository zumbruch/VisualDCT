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

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.AbstractCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
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
public class ArchiverTree extends JTree
{
	private ArchiverEngineConfigRoot root;
	private JPopupMenu popup;
	private ArchiverTreeNode rootNode;
	private CellRenderer cellRenderer;
	private ArchiverTreeRecordNode[] draggedValues;

	/**
	 * Creates a new ArchiverTree object.
	 */
	public ArchiverTree()
	{
		super();
		initialize();
	}

	private void initialize()
	{
		new DropTarget(this, new DropTargetTreeHandler());
		initializeAsDragSource();

		root = new ArchiverEngineConfigRoot();
		rootNode = new ArchiverTreeNode(root);

		root.addTreeUserElementListener(new TreeUserElementListener() {
				public void elementsAdded(TreeUserElementEvent e)
				{
					if (!e.getType().equals(ArchiverEngineConfigRoot.GROUPS)) {
						return;
					}

					Object[] groups = e.getElements();

					for (int i = 0; i < groups.length; i++) {
						ArchiverTreeNode node = new ArchiverTreeNode((TreeUserElement)groups[i]);
						((ArchiverTreeGroup)groups[i]).setTreeNode(node);
						rootNode.add(node);
					}

					((DefaultTreeModel)getModel()).reload(rootNode);
					expandRow(0);
				}

				public void elementsRemoved(TreeUserElementEvent e)
				{
					if (!e.getType().equals(ArchiverEngineConfigRoot.GROUPS)) {
						return;
					}

					Object[] groups = e.getElements();

					for (int i = 0; i < groups.length; i++) {
						rootNode.remove(((ArchiverTreeGroup)groups[i]).getNode());
					}
				}
			});

		DefaultTreeModel model = new DefaultTreeModel(rootNode);
		setModel(model);
		addMouseListener(new TreeMouseHandler());
		cellRenderer = new CellRenderer();
		this.setCellRenderer(cellRenderer);
		this.setEditable(true);
		this.setCellEditor(new CellEditor(this, cellRenderer,
		        new DefaultEditor()));
	}

	/**
	 * Returns DefaultTreeModel of this tree.
	 *
	 * @return the model
	 */
	DefaultTreeModel getDefaultModel()
	{
		return (DefaultTreeModel)getModel();
	}

	private JPopupMenu getPopup()
	{
		if (popup == null) {
			popup = new JPopupMenu();

			JMenuItem item = new JMenuItem("AddGroup");
			item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{
						root.addArchiverTreeGroup(new ArchiverTreeGroup(
						        "<new group>"));
					}
				});
			popup.add(item);
		}

		return popup;
	}

	private void initializeAsDragSource()
	{
		DragSource dragSource = DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer(this,
		    DnDConstants.ACTION_COPY_OR_MOVE,
		    new DragGestureListener() {
				/*
				 *  (non-Javadoc)
				 * @see java.awt.dnd.DragGestureListener#dragGestureRecognized(java.awt.dnd.DragGestureEvent)
				 */
				public void dragGestureRecognized(DragGestureEvent event)
				{
					final TreePath[] paths = getSelectionModel()
						.getSelectionPaths();
					ArrayList values = new ArrayList();

					for (int i = 0; i < paths.length; i++) {
						int count = paths[i].getPathCount();

						for (int j = 0; j < count; j++) {
							Object ob = paths[i].getPathComponent(j);

							if (ob instanceof ArchiverTreeRecordNode) {
								values.add(ob);
							}
						}
					}

					draggedValues = new ArchiverTreeRecordNode[values.size()];
					values.toArray(draggedValues);

					Transferable transferable = new RecordTransferable(draggedValues);
					event.startDrag(new Cursor(Cursor.MOVE_CURSOR),
					    transferable,
					    new DragSourceAdapter() {
							/*
							 *  (non-Javadoc)
							 * @see java.awt.dnd.DragSourceListener#dragDropEnd(java.awt.dnd.DragSourceDropEvent)
							 */
							public void dragDropEnd(DragSourceDropEvent dsde)
							{
								if (dsde.getDropSuccess()) {
									int action = dsde.getDropAction();

									if (action == DnDConstants.ACTION_MOVE) {
										for (int i = 0;
										    i < draggedValues.length; i++) {
											((DefaultTreeModel)getModel())
											.removeNodeFromParent((ArchiverTreeNode)draggedValues[i]);
										}
									}
								}
							}
						});
				}
			});
	}

	/**
	 * Returns all selected ArchiverTreeRecordNodes.
	 *
	 * @return array of ArchiverTreeRecordNode.
	 */
	public ArchiverTreeRecordNode[] getSelectionRecords()
	{
		ArrayList nodes = new ArrayList();
		TreePath[] paths = getSelectionPaths();

		for (int i = 0; i < paths.length; i++) {
			ArchiverTreeNode lastNode = (ArchiverTreeNode)paths[i]
				.getLastPathComponent();

			if (lastNode instanceof ArchiverTreeRecordNode) {
				nodes.add(lastNode);
			}
		}

		ArchiverTreeRecordNode[] recordNode = new ArchiverTreeRecordNode[nodes
			.size()];

		return (ArchiverTreeRecordNode[])nodes.toArray(recordNode);
	}

	/**
	 * Adds ArchiverTreeRecordNodes as records to the selected path.
	 *
	 * @param records nodes to be added
	 *
	 * @return true if nodes were added, false if the selected path was not an
	 *         ArchiverTreeNode containing an ArchiverTreeGroup
	 */
	boolean addRecords(ArchiverTreeRecordNode[] records)
	{
		ArchiverTreeNode group = findClosestGroupNode(getSelectionPath());

		if (group == null) {
			JOptionPane.showInternalMessageDialog(this,
			    "Selection must be positioned inside a group",
			    "Invalid selection", JOptionPane.WARNING_MESSAGE);

			return false;
		}

		addElements(records, group);

		return true;
	}

	/**
	 * Adds ArchiverTreeRecordNodes to the parent node.
	 *
	 * @param children nodes to be added
	 * @param parent parent node for the children
	 */
	void addElements(ArchiverTreeRecordNode[] children, ArchiverTreeNode parent)
	{
		for (int j = 0; j < children.length; j++) {
			parent.add(children[j]);
		}

		((DefaultTreeModel)getModel()).reload(parent);
	}

	/**
	 * Finds the node in the path, which holds an ArchiverTreeGroup as a user
	 * object.
	 *
	 * @param path TreePath to be searched
	 *
	 * @return node of the ArchiverTreeGroup
	 */
	private ArchiverTreeNode findClosestGroupNode(TreePath path)
	{
		ArchiverTreeNode node;
		Object object;

		for (int i = 0; i < path.getPathCount(); i++) {
			node = (ArchiverTreeNode)path.getPathComponent(i);
			object = node.getUserObject();

			if (object instanceof ArchiverTreeGroup) {
				return node;
			}
		}

		return null;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args)
	{
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(500, 500);
		f.setContentPane(new ArchiverTree());
		f.show();
	}

	/**
	 * <code>TreeMouseHandler</code> handles mouse events on the tree.
	 *
	 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
	 * @version $Id$
	 *
	 * @since VERSION
	 */
	private class TreeMouseHandler extends MouseAdapter
	{
		/*
		 *  (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		public void mouseReleased(MouseEvent e)
		{
			if ((e.getClickCount() == 2) && SwingUtilities.isLeftMouseButton(e)) {
				ArchiverTree.this.startEditingAtPath(getPathForLocation(
				        e.getX(), e.getY()));
			} else if (e.getClickCount() == 1) {
				boolean ctrlDown = (e.getModifiersEx()
					& InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK;

				if (SwingUtilities.isLeftMouseButton(e)) {
					// clear selection check, if CTRL is not down
					if (!ctrlDown) {
						TreePath p = getPathForLocation(e.getX(), e.getY());

						if (p == null) {
							getSelectionModel().clearSelection();
						}
					}
				} else if (SwingUtilities.isRightMouseButton(e)) {
					TreePath p = getPathForLocation(e.getX(), e.getY());

					if (p != null) {
						// reset/update selection, select currently clicked
						if (!getSelectionModel().isPathSelected(p)) {
							if (!ctrlDown) {
								getSelectionModel().clearSelection();
							}

							getSelectionModel().addSelectionPath(p);
						}
					} else {
						// clear selection
						getSelectionModel().clearSelection();
					}

					getPopup().show(ArchiverTree.this, e.getX(), e.getY());
				}
			}
		}
	}

	/**
	 * <code>DropTargetTreeHandler</code> handles DnD-Drop events on the tree.
	 *
	 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
	 * @version $Id$
	 *
	 * @since VERSION
	 */
	private class DropTargetTreeHandler extends DropTargetAdapter
	{
		private boolean isDragAcceptable(DropTargetDragEvent dtde)
		{
			DataFlavor[] f = dtde.getCurrentDataFlavors();

			boolean accept = false;

			for (int i = 0; i < f.length; i++) {
				if (Arrays.asList(RecordTransferable.flavors).contains(f[i])) {
					accept = true;

					break;
				}
			}

			return (accept
			& (dtde.getDropAction() & DnDConstants.ACTION_MOVE) != 0);
		}

		private boolean isDropAcceptable(DropTargetDropEvent dtde)
		{
			DataFlavor[] f = dtde.getCurrentDataFlavors();
			boolean accept = false;

			for (int i = 0; i < f.length; i++) {
				if (Arrays.asList(RecordTransferable.flavors).contains(f[i])) {
					accept = true;

					break;
				}
			}

			return (accept
			& (dtde.getDropAction() & DnDConstants.ACTION_MOVE) != 0);
		}

		/* (non-Javadoc)
		 * @see java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent)
		 */
		public void dragEnter(DropTargetDragEvent dtde)
		{
			if (!isDragAcceptable(dtde)) {
				dtde.rejectDrag();

				return;
			}
		}

		/* (non-Javadoc)
		 * @see java.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.DropTargetDragEvent)
		 */
		public void dropActionChanged(DropTargetDragEvent dtde)
		{
			if (!isDragAcceptable(dtde)) {
				dtde.rejectDrag();

				return;
			}
		}

		/* (non-Javadoc)
		 * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
		 */
		public void drop(DropTargetDropEvent dtde)
		{
			if (!isDropAcceptable(dtde)) {
				dtde.rejectDrop();

				return;
			}

			dtde.acceptDrop(DnDConstants.ACTION_MOVE);

			Transferable transferable = dtde.getTransferable();

			DataFlavor[] flavors = transferable.getTransferDataFlavors();

			Point point = dtde.getLocation();
			ArchiverTreeNode groupNode = findClosestGroupNode(getClosestPathForLocation(
				        (int)(point.getX() + 0.5), (int)(point.getY() + 0.5)));

			if (groupNode != null) {
				for (int i = 0; i < flavors.length; i++) {
					DataFlavor df = flavors[i];

					try {
						if (df.equals(RecordTransferable.flavors[0])) {
						    addElements((ArchiverTreeRecordNode[])transferable
							    .getTransferData(df), groupNode);
						}
					} catch (UnsupportedFlavorException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				JOptionPane.showMessageDialog(ArchiverTree.this,
				    "This drop is not allowed!", "Invalid drop",
				    JOptionPane.WARNING_MESSAGE);

				return;
			}

			dtde.dropComplete(true);
		}
	}

	/**
	 * <code>CellUtilities</code>' methods provide icons for the nodes.
	 *
	 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
	 * @version $Id$
	 *
	 * @since VERSION
	 */
	private static class CellUtilities
	{
		static Icon getIcon(ArchiverTreeNode node)
		{
			Object object = node.getUserObject();
			Class type = object.getClass();

			if (Record.class.isAssignableFrom(type)) {
				return loadIcon("images/record.gif");
			} else if (ArchiverTreeGroup.class.isAssignableFrom(type)) {
				return loadIcon("images/boxn.gif");
			} else if (ArchiverEngineConfigRoot.class.isAssignableFrom(type)) {
				return loadIcon("images/page.gif");
			}

			return loadIcon("images/Tree16.gif");
		}

		private static Icon loadIcon(String resource)
		{
			return new ImageIcon(ArchiverTree.class.getClassLoader()
			    .getResource(resource));
		}
	}

	/**
	 * <code>CellRenderer</code> is a cell renderer for the tree. It sets the
	 * icon for the node and provides correct name fot it.
	 *
	 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
	 * @version $Id$
	 *
	 * @since VERSION
	 */
	private class CellRenderer extends DefaultTreeCellRenderer
		implements TreeCellRenderer
	{
		/* (non-Javadoc)
		 * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
		 */
		public Component getTreeCellRendererComponent(JTree tree, Object value,
		    boolean selected, boolean expanded, boolean leaf, int row,
		    boolean hasFocus)
		{
			super.getTreeCellRendererComponent(tree, value, selected, expanded,
			    leaf, row, hasFocus);

			if (!(value instanceof ArchiverTreeNode)) {
				return this;
			}

			ArchiverTreeNode node = (ArchiverTreeNode)value;
			this.setText(node.getTreeUserElement().getName()
			    + (node.hasValue() ? (": " + node.getValue()) : ""));
			this.setIcon(CellUtilities.getIcon((ArchiverTreeNode)value));

			return this;
		}
	}

	/**
	 * <code>DefaultEditor</code> is a default editor for tree cell editing. It
	 * provides an empty EditorComponent and should be used together with
	 * CellEditor.
	 *
	 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
	 * @version $Id$
	 *
	 * @since VERSION
	 */
	private class DefaultEditor extends AbstractCellEditor
		implements TreeCellEditor
	{
		private EditorComponent editor;
		private Object value;

		/**
		 * Creates a new DefaultEditor object.
		 */
		public DefaultEditor()
		{
			editor = new EditorComponent();
		}

		/* (non-Javadoc)
		 * @see javax.swing.tree.TreeCellEditor#getTreeCellEditorComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int)
		 */
		public Component getTreeCellEditorComponent(JTree tree, Object value,
		    boolean isSelected, boolean expanded, boolean leaf, int row)
		{
			this.value = value;
		    return editor;
		}

		/* (non-Javadoc)
		 * @see javax.swing.CellEditor#getCellEditorValue()
		 */
		public Object getCellEditorValue()
		{
			// should never be invoked if there wasn't any interference
			return value.toString();
		}
	}

	/**
	 * <code>CellEditor</code> is a cell editor for the tree. It enables the
	 * use of  EditorComponent.
	 *
	 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
	 * @version $Id$
	 *
	 * @since VERSION
	 */
	private class CellEditor extends DefaultTreeCellEditor
	{
		/**
		 * Creates a new CellEditor object.
		 *
		 * @param tree DOCUMENT ME!
		 * @param renderer DOCUMENT ME!
		 * @param editor DOCUMENT ME!
		 */
		public CellEditor(JTree tree, DefaultTreeCellRenderer renderer,
		    TreeCellEditor editor)
		{
			super(tree, renderer, editor);
		}

		/*
		 *  (non-Javadoc)
		 * @see javax.swing.tree.TreeCellEditor#getTreeCellEditorComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int)
		 */
		public Component getTreeCellEditorComponent(JTree tree,
		    final Object value, boolean isSelected, boolean expanded,
		    boolean leaf, int row)
		{
			setTree(tree);
			lastRow = row;
			determineOffset(tree, value, isSelected, expanded, leaf, row);

			if (editingComponent != null) {
				editingContainer.remove(editingComponent);
			}

			editingIcon = CellUtilities.getIcon((ArchiverTreeNode)value);

			editingComponent = realEditor.getTreeCellEditorComponent(tree,
				    value, isSelected, expanded, leaf, row);

			((EditorComponent)editingComponent).setup((ArchiverTreeNode)value,
			    this);

			TreePath newPath = tree.getPathForRow(row);

			canEdit = ((lastPath != null) && (newPath != null)
				&& lastPath.equals(newPath));

			Font font = getFont();

			if (font == null) {
				if (renderer != null) {
					font = renderer.getFont();
				}

				if (font == null) {
					font = tree.getFont();
				}
			}

			editingContainer.setFont(font);
			prepareForEditing();

			return editingContainer;
		}

		/*
		 *  (non-Javadoc)
		 * @see javax.swing.CellEditor#getCellEditorValue()
		 */
		public Object getCellEditorValue()
		{
			try {
				return ((ArchiverTreeNode)lastPath.getLastPathComponent())
				.getUserObject();
			} catch (Exception e) {
				//ignore ClassCastException - shouldn't happened if no 3rd party
			    //objects interfered with the tree
			}
					
			return super.getCellEditorValue();
		}
	}
}

/* __oOo__ */
