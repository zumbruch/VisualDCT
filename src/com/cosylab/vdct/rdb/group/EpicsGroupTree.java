package com.cosylab.vdct.rdb.group;

import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

// Tree Display for IOCs and Epics Groups
public class EpicsGroupTree
    extends JTree
    implements TreeSelectionListener
{
    public static final boolean debug=false;
    private Vector listeners;
    private PreparedStatement select_groups;
    
    public EpicsGroupTree()
    {
        listeners = new Vector();
        Connection c = SQLHelper.get().getConnection();
        try
        {
            select_groups =
                c.prepareStatement(
                    "SELECT epics_grp_id FROM epics_grp WHERE ioc_dvc_id=?");
        }
        catch (SQLException e)
        {
            System.err.println("SQL Exception " + e.getMessage());
            System.err.println("This is fatal in EpicsGroupTree(), exiting");
            System.exit(1);
        }
        setModel(new GroupTreeModel());
        getSelectionModel().setSelectionMode(
            TreeSelectionModel.SINGLE_TREE_SELECTION);
        addTreeSelectionListener(this);
        setPreferredSize(new Dimension(500, 500));
    }

    public void addListener(EpicsGroupTreeListener l)
    {   listeners.add(l);  }

    public void removeListener(EpicsGroupTreeListener l)
    {   listeners.remove(l);  }

    // interface TreeSelectionListener
    public void valueChanged(TreeSelectionEvent e)
    {   // Idea: Path = root, ioc, group
        // (group is not hierarchical)
        EpicsGroupTreeListener lsnr;
        TreeModel model = getModel();

        TreePath p = e.getNewLeadSelectionPath();
        if (p == null)
            return;
        int i, len = p.getPathCount();
        if (len < 2) // only root selected
            return;

        Object ioc = p.getPathComponent(1);
        String ioc_id = ioc.toString();
        if (! (ioc instanceof IOCNode))
        {
            System.err.println ("EpicsGroupTree: invalid IOC " + ioc_id);
            return;
        }
        if (debug)
            System.out.println("Selected IOC  : " + ioc_id);
        if (len == 2) // only ioc selected
        {
            for (i=0; i<listeners.size(); ++i)
            {
                lsnr =(EpicsGroupTreeListener)listeners.get(i);
                lsnr.iocSelected(ioc_id);
            }
            return;
        }
            
        // IOC plus group selected?
        Object last = p.getPathComponent(len-1);
        if (! model.isLeaf(last))
            return;
        String group_id = last.toString();
        if (debug)
            System.out.println("Selected Group: " + group_id);
        for (i=0; i<listeners.size(); ++i)
        {
            lsnr =(EpicsGroupTreeListener)listeners.get(i);
            lsnr.groupSelected(ioc_id, group_id);
        }
    }

    // Implements TreeModel in order to support
    // late evaluation:
    // "Children" are only fetched when asked for.
    // If this had been based on the DefaultTreeModel,
    // the whole group database would have to be read
    // into the model from the very beginning,
    // which could be a huge amount of data.

    // Root is just a Vector (of IOCNodes).
    // Currently only difference to Vector: toString representation
    // Default was toString of all the elements
    class RootNode extends Vector
    {
        public String toString()
        {   return "IOCs/Groups"; }
    };
    
    // IOCNode is the one level below root for the IOCs.
    // Children of each IOC are groups
    class IOCNode
    {
        private Vector groups;
        private String ioc_id;
            
        public IOCNode(String id)
        {
            ioc_id = id;
            groups = null;
        }

        private void load()
        {
            groups = new Vector();
            try
            {
                select_groups.setString(1, ioc_id);
                ResultSet rs = select_groups.executeQuery();
                while (rs.next())
                    groups.add(rs.getString(1));
            }
            catch (SQLException e)
            {
                System.err.println("SQL Exception: " + e.getMessage());
            }
        }
        
        public String toString()
        {   return ioc_id; }
            
        public int getChildCount()
        {
            if (groups==null)
                load();
            return (groups!=null) ? groups.size() : 0;
        }
            
        public Object getChild(int index)
        {
            if (groups==null)
                load();
            return (groups!=null) ? groups.get(index) : null;
        }
            
        public int getIndexOfChild(Object child)
        {
            if (groups==null)
                load();
            return (groups!=null) ? groups.indexOf(child) : 0;
        }
    };

    class GroupTreeModel implements TreeModel
    {
        private Vector tree_model_listeners;
        private RootNode root; // IOCNode Vector
        
        public GroupTreeModel()
        {
            tree_model_listeners = new Vector();
            root = new RootNode();
            try
            {   // Initialize Model: get all IOCs
                Connection c = SQLHelper.get().getConnection();
                Statement s = c.createStatement();
                ResultSet rs =
                    s.executeQuery("SELECT ioc_dvc_id" +
                                   " FROM epics_grp GROUP BY ioc_dvc_id");
                while (rs.next())
                    root.add(new IOCNode(rs.getString(1)));
            }
            catch (SQLException e)
            {
                System.err.println ("SQL Exception: " + e.getMessage());
                System.err.println("This is fatal in EpicsGroupTree(), exiting");
                System.exit(1);
            }
        }
    
        public Object getRoot()
        {   return root;   }
        
        public int getChildCount(Object parent)
        {
            if (parent == root)
                return root.size();
            if (parent instanceof IOCNode)
                return ((IOCNode)parent).getChildCount();
            return 0;
        }
        
        public Object getChild(Object parent, int index)
        {
            if (debug)
                System.out.println ("getChild(" + parent.toString() +
                                    ", " + index + ")");
            if (parent == root)
                return root.get(index);
            else if (parent instanceof IOCNode)
                return ((IOCNode)parent).getChild(index);
            return null;
        }
        
        public int getIndexOfChild(Object parent, Object child)
        {
            if (debug)
                System.out.println ("getIndexOfChild(" + parent.toString() +
                                    ", " + child.toString() + ")");
            if (parent == root)
                return root.indexOf(child);
            else if (parent instanceof IOCNode)
                return ((IOCNode)parent).getIndexOfChild(child);
            return 0;
        }
        
        public boolean isLeaf(Object node)
        {   return node!=root && !(node instanceof IOCNode);  }

        public void valueForPathChanged(TreePath path, Object newValue)
        {
            System.out.println("valueForPathChanged:");
            System.out.println("Path: " + path.toString());
            System.out.println("Value: " + newValue.toString());
        }
        
        public void addTreeModelListener(TreeModelListener l)
        {   tree_model_listeners.add(l);  }
        
        public void removeTreeModelListener(TreeModelListener l) 
        {   tree_model_listeners.remove(l);  }
    };
};

