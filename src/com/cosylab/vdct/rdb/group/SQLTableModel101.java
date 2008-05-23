package com.cosylab.vdct.rdb.group;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JOptionPane;

// A SQLTableModel for a table/view
// that can be described with few prepared statement,
// suitable for editing a single RDB table.
// The first column is "fixed",
// usually a name/primary index
// that cannot be edited without copy/remove.
public class SQLTableModel101 extends SQLTableModel
{
    private Vector headers;
    private PreparedStatement select_all, select, update, insert, delete;

    // headers: defines the headers.
    //          First column is used for id/primary key!
    //          headers.size() determines column count!
    // The SQL statements have to fit this pattern
    // because the id (first column) and remaining cols
    // are provided as parameters:
    //   select_all: SELECT <all cols>...
    //   select:     SELECT <all cols> WHERE id=?
    //   update:     UPDATE <all cols> WHERE id=?
    //   insert:     INSERT ... VALUES (id, <all other cols>)
    //   delete:     DELETE ... WHERE id=?
    public SQLTableModel101(Vector headers,
                            PreparedStatement select_all,
                            PreparedStatement select,
                            PreparedStatement update,
                            PreparedStatement insert,
                            PreparedStatement delete)
    {
        this.headers = headers;
        this.select_all = select_all;
        this.select = select;
        this.update = update;
        this.insert = insert;
        this.delete = delete;
    }
    
    private boolean edited;
    private Vector deletes; // Boolean 
    private Vector rows;  // String fields from DB
    
    // Addition
    public boolean wasEdited()
    {   return edited; }
    
    public void load()
    {
        int i;
        deletes = new Vector();
        rows = new Vector();
        try
        {
            ResultSet rs = select_all.executeQuery();
            while (rs.next())
            {
                Vector row = new Vector();
                int fieldcount = headers.size();
                for (i=0; i<fieldcount; ++i)
                    row.add(rs.getString(i+1));
                deletes.add(new Boolean(false));
                rows.add(row);
            }
            rs.close();
            edited = false;
        }
        catch (SQLException e)
        {
            System.err.println ("SQL Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public boolean save()
    {
        int i;
        for (i=0; i<getRowCount(); ++i)
        {
            Vector row = (Vector)rows.get(i);
            if (((Boolean)deletes.get(i)).booleanValue())
            {
                if (! deleteDBRow(i, row))
                    return false;
            }
            else
            {
                if (! updateDBRow(i, row))
                    return false;
            }
        }
        try
        {
            SQLHelper.get().getConnection().commit();
        }
        catch (SQLException e)
        {
            String info = "SQL Exception:" + e.getMessage();
            System.err.println(info);
            JOptionPane.showMessageDialog(null, info, "Update Error",
                                          JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean updateDBRow(int i, Vector row)
    {
        String id = (String)row.get(0);
        int fieldcount = headers.size();
        int col;

        try
        {
            select.setString(1, id);
            ResultSet rs = select.executeQuery();
            if (rs.next())
            {
                for (col=1; col<fieldcount; ++col)
                    update.setString(col, (String)row.get(col));
                update.setString(fieldcount, id);
                update.executeUpdate();
            }
            else
            {
                insert.setString(1, id);
                for (col=1; col<fieldcount; ++col)
                    insert.setString(col+1, (String)row.get(col));
                insert.executeUpdate();
            }
            rs.close();
        }
        catch (SQLException e)
        {
            String info = "SQL Update (row " +
                Integer.toString(i) + ") " + e.getMessage();
            info = info + "\nPlease check the above SQL error,\n" +
                "maybe a required field was left empty" +
                " or set to an invalid value?";
            System.err.println(info);
            JOptionPane.showMessageDialog(null, info, "Save Error",
                                          JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
    private boolean deleteDBRow(int i, Vector row)
    {
        String id = (String)row.get(0);
        try
        {
            delete.setString(1, id);
            return 1 == delete.executeUpdate();
        }
        catch (SQLException e)
        {
            String info = "SQL Delete (row " +
                Integer.toString(i) + ") " + e.getMessage();
            info = info + "\nPlease check the above SQL error,\n" +
                "maybe a required field was left empty" +
                " or set to an invalid value?";
            System.err.println(info);
            JOptionPane.showMessageDialog(null, info, "Save Error",
                                          JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public int getRowCount()
    {   return rows.size();    }
    
    public int getColumnCount()
    {   return 1 + headers.size();  }
    
    public String getColumnName(int column)
    {
        if (column==0)
            return "Delete";
      return headers.get(column-1).toString();
    }
    
    public Class getColumnClass(int column)
    {
        if (column == 0)
            return Boolean.class;
        return String.class;
    }
    
    public Object getValueAt(int row, int column)
    {
        if (column==0)
            return (Boolean) deletes.get(row);
        else
            return (String) ((Vector)rows.get(row)).get(column-1);
    }
    
    public void setValueAt(Object value, int row, int column)
    {
        if (column==0)
            deletes.set(row, value);
        else
        {
            Vector rowvec = (Vector)rows.get(row);
            String v;
            if (value==null)
                v = "";
            else
                if (value instanceof String)
                    v = (String)value;
                else
                    v = value.toString();
            
            String old = (String)rowvec.get(column-1);
            if (old!=null && old.equals(v))
                return; // no change, don't set "edited"
            rowvec.set(column-1, v);
        }
        edited = true;
    }
    
    public boolean isCellEditable(int row, int column)
    {
        if (column==0)
            return true;
        String value = (String)getValueAt(row, column);
        if (value==null  ||  value.trim().length()<=0)
            return true;
        return column >= 2;
    }
    
    public void addRecord()
    {
        int i;
        Vector rowvec = new Vector();
        int fieldcount = headers.size();
        for (i=0; i<fieldcount; ++i)
            rowvec.add(new String());
        deletes.add(new Boolean(false));
        rows.add(rowvec);
        edited = true;
        fireTableDataChanged();
    }
};



