package com.cosylab.vdct.rdb.group;

import javax.swing.JDialog;

import com.cosylab.vdct.rdb.DataMapper;

// $Id$
//
// kasemir@lanl.gov

public class group
{
    //private SQLTableGUI gui = null;
	
	static public void usage ()
    {
        System.err.println ("USAGE: group [-t]");
        System.exit(0);
    }
    
    public group(DataMapper mapper, JDialog guiContext) {
    	main(new String[0], mapper, guiContext);
    }
    
    private void main (String args[], DataMapper mapper, JDialog guiContext)
    {
        /*
        boolean use_tree = false;
        
        if (args.length > 0)
        {
            if (args[0].equals("-t"))
                use_tree = true;
            else
                usage();
        }
        Connection c = SQLHelper.create(mapper).getConnection();

        if (use_tree)
        {

            OkCancelDlg dlg = new EpicsGroupDlg(null);
            dlg.run();
            System.exit(0);
        }
        else
        {
            Vector headers = new Vector();
            headers.add(new String("Group Name"));
            headers.add(new String("Description"));
            headers.add(new String("IOC"));
            

            PreparedStatement select_all, select, update, insert, delete;
            try
            {
                select_all = c.prepareStatement(
                    "SELECT epics_grp_id, epics_grp_desc, ioc_dvc_id" +
                    " FROM epics_grp");
                select = c.prepareStatement(
                    "SELECT epics_grp_id FROM epics_grp WHERE epics_grp_id=?");
                update = c.prepareStatement(
                    "UPDATE epics_grp SET epics_grp_desc=?, ioc_dvc_id=?" +
                    " WHERE epics_grp_id=?");
                insert = c.prepareStatement(
                    "INSERT INTO epics_grp" +
                    "(epics_grp_id,epics_grp_desc,ioc_dvc_id)" +
                    " VALUES (?,?,?)");
                delete = c.prepareStatement(
                    "DELETE FROM epics_grp WHERE epics_grp_id=?");
                /*
                SQLTableModel model =
                    new SQLTableModel101(headers,
                                         select_all,
                                         select,
                                         update,
                                         insert,
                                         delete);
                //gui = new SQLTableGUI();
                //gui.makeGUI(model, "EPICS Record Groups", guiContext);
                //   gui.setColumnEditor(3, new MenuFieldEditor(
                //    "SELECT dvc_id FROM ioc_dvc ORDER BY dvc_id"));
                //gui.execute();
            }
            catch (SQLException e)
            {
                System.err.println ("SQL Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }
        */
    }
};





