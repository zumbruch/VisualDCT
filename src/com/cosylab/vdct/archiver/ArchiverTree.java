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

import java.awt.Color;
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
import java.util.Enumeration;

import javax.swing.AbstractCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
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
public class ArchiverTree extends JTree
{
	private JPopupMenu popup;
	private ArchiverTreeNode rootNode;
	private CellRenderer cellRenderer;
	private ArchiverTreeChannelNode[] draggedValues;
	private ArrayList treeListeners = new ArrayList();
	private JMenuItem[] engineItems;
	private JMenuItem[] channelItems;

	private Archiver archiver;
	/**
	 * Creates a new ArchiverTree object.
	 */
	public ArchiverTree(Archiver arc)
	{
		super();
		initialize();
		this.archiver = arc;
	}

	private void initialize()
	{
		new DropTarget(this, new DropTargetTreeHandler());
		initializeAsDragSource();

		rootNode = new ArchiverTreeNode(new EngineConfigRoot());
		
		DefaultTreeModel model = new DefaultTreeModel(rootNode);
		setModel(model);
		addMouseListener(new TreeMouseHandler());
		cellRenderer = new CellRenderer();
		this.setCellRenderer(cellRenderer);
		this.setEditable(true);
		this.setCellEditor(new CellEditor(this, cellRenderer,
		        new DefaultEditor()));
				
				
		// constructs JMenuItems for adding properties to the tree
		engineItems = new JMenuItem[Engine.engineConfigProperties.length];
		for (int i = 0; i < engineItems.length; i++) {
		    final String name = Engine.engineConfigProperties[i];
		    final int c = i;
		    engineItems[i] = new JMenuItem(name);
		    engineItems[i].addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    Property property = new Property(name, (c==6 ? false : true));
                    rootNode.insert(new ArchiverTreeNode(property), 0);
                    getDefaultModel().reload(rootNode);
                }
		        
		    });
		}
		
		channelItems = new JMenuItem[Engine.channelProperties.length];
		for (int i = 0; i < channelItems.length; i++) {
		    final String name = Engine.channelProperties[i];
		    final int c = i;
		    channelItems[i] = new JMenuItem(name);
		    channelItems[i].addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    Property property = new Property(name, (c < 1 ? true : false));
                    ArchiverTreeNode parent = (ArchiverTreeNode) getSelectionPath().getLastPathComponent();
                    parent.add(new ArchiverTreeNode(property));
                    sortChannelProperties((ArchiverTreeChannelNode) parent);
                    getDefaultModel().reload(parent);
                }
		        
		    });
		}
		
			
	}
	
	private void sortChannelProperties(ArchiverTreeChannelNode node) {
	    ArchiverTreeNode[] nodes = new ArchiverTreeNode[node.getChildCount()];
	    for (int i = 0; i < nodes.length; i++) {
	        nodes[i] = (ArchiverTreeNode) node.getChildAt(i);
	    }
	    
	    node.removeAllChildren();
	    
	    for (int j = 0; j < Engine.channelProperties.length; j++) {
		    for (int i = 0; i < nodes.length; i++) {
		        String name = ((ArchiverTreeElement)nodes[i].getArchiverTreeUserElement()).getName();
		        if (name.equals(Engine.channelProperties[j])) {
		            node.add(nodes[i]);
		        }
		    }
	    }
	}
	
	/**
	 * 
	 * Checks if there is another Group with the same name.
	 * @param name the name to be checked for
	 * @return true if a group with that name alreasy exist
	 */
	boolean isGroupNameUnique(String name) {
	    
	    int j = rootNode.getChildCount();
	    for (int i = 0; i < j; i++) {
	        ArchiverTreeElement elem = ((ArchiverTreeNode) rootNode.getChildAt(i)).getArchiverTreeUserElement();
	        if (elem instanceof Group) {
	            if (elem.getName().equals(name)) {
	                return false;
	            }
	        }
	    }
	    return true;
	    
	}

	/**
	 * Returns DefaultTreeModel of this tree.
	 *
	 * @return the model
	 */
	public DefaultTreeModel getDefaultModel()
	{
		return (DefaultTreeModel)getModel();
	}
	
	
	/**
	 * 
	 * Sets the root for this tree.
	 * @param root
	 */
	public void setRoot(ArchiverTreeNode root)
	{
		this.rootNode = root;
		getDefaultModel().setRoot(root);
	}
	
	public ArchiverTreeNode getRoot() {
	    return rootNode;
	}
	
	
	public void reset()
	{
	    stopEditing();
	    
		rootNode = new ArchiverTreeNode(new EngineConfigRoot());
		getDefaultModel().setRoot(rootNode);
		getDefaultModel().reload();
	}
	
	protected void fireChannelRemoved(ArchiverTreeChannelNode[] channel) {
	    
	    ChannelRemovedEvent evt = new ChannelRemovedEvent(this, channel);
	    for (int i = 0; i < treeListeners.size(); i++) {
	        ((TreeListener)treeListeners.get(i)).channelRemoved(evt);
	    }
	    
	}
	
	public void addTreeListener(TreeListener listener) {
	    treeListeners.add(listener);
	}
	
	public void removeTreeListener(TreeListener listener) {
	    treeListeners.remove(listener);
	}
	
	private JPopupMenu getPopup()
	{
		if (popup == null) {
			popup = new JPopupMenu();

			JMenuItem addGRoup = new JMenuItem("Add Group");
			addGRoup.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{
						rootNode.add(new ArchiverTreeNode(
						        new Group("<new group>")));
						((DefaultTreeModel)getModel()).reload(rootNode);
					}
				});
			popup.add(addGRoup);

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
					        ArchiverTreeNode node = (ArchiverTreeNode)paths[j].getLastPathComponent();
					        ArchiverTreeElement elem = node.getArchiverTreeUserElement();

							if (! (elem instanceof EngineConfigRoot)) {
								TreeNode parent = node.getParent();
								node.removeFromParent();
								((DefaultTreeModel)getModel()).reload(parent);
								if (elem instanceof Group) {
								    for (int i = 0; i < node.getChildCount(); i++) {
								        nodesList.add((ArchiverTreeChannelNode)node.getChildAt(i));
								    }
								} else if (elem instanceof Channel) {
								    nodesList.add(node);
								}
							} 		
					    }
					    ArchiverTreeChannelNode[] nodes = new ArchiverTreeChannelNode[nodesList.size()];
					    fireChannelRemoved((ArchiverTreeChannelNode[]) nodesList.toArray(nodes));
										
					}
				});
			remove.setEnabled(false);
			popup.add(remove);
			
			
		}

		return popup;
	}
	
	private JMenuItem remove;
	private JMenu addProperties;
	
	private void popupInit(TreePath path) {
	    
	    if (path == null) {
	        return;
	    }
	    
	    if (popup == null) {
	        getPopup();
	    }
	    
	    ArchiverTreeNode node = (ArchiverTreeNode) path.getLastPathComponent();
	    ArchiverTreeElement elem = node.getArchiverTreeUserElement();
	    
	    if (!(elem instanceof EngineConfigRoot)) {
	        remove.setEnabled(true);
	    } else {
	        remove.setEnabled(false);
	    }
	    addProperties.setEnabled(false);
	    addProperties.removeAll();
	    
	    if (elem instanceof EngineConfigRoot || elem instanceof Channel) {
	        addProperties.setEnabled(true);
	        JMenuItem[] item = getPropertisItems(node);
	        for (int i = 0; i < item.length; i++) {
	            addProperties.add(item[i]);
	        }
	    }
	    
	}
	
	
	/**
	 * Adds properties to the JMenu. Only those properties are added that are not attached to the
	 * node yet.
	 * 
	 * @param node
	 * @return
	 */
	private JMenuItem[] getPropertisItems(ArchiverTreeNode node) {
	    
	    ArchiverTreeElement elem = node.getArchiverTreeUserElement();
	    
	    ArrayList items = new ArrayList();
	    
	    
	    if (elem instanceof EngineConfigRoot) {
	        for (int i = 0; i < engineItems.length; i++) {
	            boolean exist = false;
	            Enumeration children = node.children();
		        while (children.hasMoreElements()) {
		            ArchiverTreeNode child = (ArchiverTreeNode) children.nextElement();
		            if (child.getArchiverTreeUserElement().getName().equals(engineItems[i].getText())) {
		                exist = true;
		                break;
		            }
		        }
		        if (!exist) {
		            items.add(engineItems[i]);
		        }
	        }
	    } else if (elem instanceof Channel) {
	        for (int i = 0; i < channelItems.length; i+=3) {
	            boolean exist = false;
	            Enumeration children = node.children();
		        while (children.hasMoreElements()) {
		            ArchiverTreeNode child = (ArchiverTreeNode) children.nextElement();
		            if (child.getArchiverTreeUserElement().getName().equals(channelItems[i].getText())) {
		                exist = true;
		                break;
		            }
		        }
		        if (!exist) {
		            items.add(channelItems[i]);
		        }
	        }
	        
	        boolean exist = false;
	        for (int i = 1; i < channelItems.length-1; i++) {
	            Enumeration children = node.children();
		        while (children.hasMoreElements()) {
		            ArchiverTreeNode child = (ArchiverTreeNode) children.nextElement();
		            if (child.getArchiverTreeUserElement().getName().equals(channelItems[i].getText())) {
		                exist = true;
		                break;
		            }
		        }
		        if (exist) {
		            break;
		        }
	        }
	        if (!exist) {
	            items.add(channelItems[channelItems.length - 2]);
	            items.add(channelItems[channelItems.length - 1]);
	        }
	        
	        
	    } 
	    JMenuItem[] array = new JMenuItem[items.size()];
	    return (JMenuItem[]) items.toArray(array);
	    
	    
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
					if (paths != null) {
						for (int i = 0; i < paths.length; i++) {
							int count = paths[i].getPathCount();
	
							for (int j = 0; j < count; j++) {
								Object ob = paths[i].getPathComponent(j);
	
								if (ob != null
								    && ob instanceof ArchiverTreeChannelNode) {
									values.add(ob);
								}
							}
						}
					}

					draggedValues = new ArchiverTreeChannelNode[values.size()];
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
	public ArchiverTreeChannelNode[] getSelectionRecords()
	{
		ArrayList nodes = new ArrayList();
		TreePath[] paths = getSelectionPaths();
		if (paths == null) {
		    return null;
		}
		for (int i = 0; i < paths.length; i++) {
			ArchiverTreeNode lastNode = (ArchiverTreeNode)paths[i]
				.getLastPathComponent();

			if (lastNode instanceof ArchiverTreeChannelNode) {
				nodes.add(lastNode);
			}
		}

		ArchiverTreeChannelNode[] recordNode = new ArchiverTreeChannelNode[nodes
			.size()];

		return (ArchiverTreeChannelNode[])nodes.toArray(recordNode);
	}

	/**
	 * Adds ArchiverTreeRecordNodes as records to the selected path.
	 *
	 * @param records nodes to be added
	 *
	 * @return true if nodes were added, false if the selected path was not an
	 *         ArchiverTreeNode containing an ArchiverTreeGroup
	 */
	boolean addRecords(ArchiverTreeChannelNode[] records)
	{
		ArchiverTreeNode group = findClosestGroupNode(getSelectionPath());

		if (group == null) {
			JOptionPane.showMessageDialog(archiver,
			    "En element inside a group has to be selected \n in order to add a channel.",
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
	void addElements(ArchiverTreeChannelNode[] children, ArchiverTreeNode parent)
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

		if (path != null) {
			for (int i = 0; i < path.getPathCount(); i++) {
				node = (ArchiverTreeNode)path.getPathComponent(i);
				object = node.getUserObject();

				if (object instanceof Group) {
					return node;
				}
			}
		}

		return null;
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
		    if ((e.getClickCount() == 3) && SwingUtilities.isLeftMouseButton(e)) {
		        stopEditing();
		    }
		    
		    if ((e.getClickCount() == 2) && SwingUtilities.isLeftMouseButton(e)) {
		    	TreePath path = getPathForLocation(e.getX(), e.getY());
		    	if (path != null) {
			    	ArchiverTreeNode node = (ArchiverTreeNode) path.getLastPathComponent();
			    	
			    	if (node.getArchiverTreeUserElement().isEditable()) {
			    	    ArchiverTree.this.startEditingAtPath(path);
			    	}
		    	}

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
					
					popupInit(p);
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
							addElements((ArchiverTreeChannelNode[])transferable
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

	
	private static class CellUtilities
	{
		static Icon getIcon(ArchiverTreeNode node)
		{
			Object object = node.getUserObject();
			Class type = object.getClass();

			if (Channel.class.isAssignableFrom(type)) {
				return loadIcon("images/record.gif");
			} else if (Group.class.isAssignableFrom(type)) {
				return loadIcon("images/boxn.gif");
			} else if (EngineConfigRoot.class.isAssignableFrom(type)) {
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
			this.setBackground(new Color(255,255,225));
			if (value instanceof ArchiverTreeNode) {
				ArchiverTreeNode node = (ArchiverTreeNode)value;
				this.setText(node.getArchiverTreeUserElement().toString());
				this.setIcon(CellUtilities.getIcon((ArchiverTreeNode)value));
			}

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
			return value;
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
		    if (!((ArchiverTreeNode)value).getArchiverTreeUserElement().isEditable()) {
		        return renderer.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, true);
		    }
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
			    this, ArchiverTree.this);

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
