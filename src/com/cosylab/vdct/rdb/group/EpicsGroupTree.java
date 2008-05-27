package com.cosylab.vdct.rdb.group;

import java.awt.Dimension;
import java.util.Vector;

import javax.swing.JTree;
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
    
    public EpicsGroupTree()
    {
        listeners = new Vector();
        
        // TODO:REM
        /*
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
        */
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
        // TODO:REM
        /*
        if (! (ioc instanceof IOCNode))
        {
            System.err.println ("EpicsGroupTree: invalid IOC " + ioc_id);
            return;
        }
        */
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
};

