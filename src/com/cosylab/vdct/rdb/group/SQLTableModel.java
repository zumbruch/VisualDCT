package com.cosylab.vdct.rdb.group;

import javax.swing.table.AbstractTableModel;

// SQLTableModel
// is used by SQLTableModelGUI.
//
// We assume that the first column is always a
// Boolean delete yes/no column.
//
// Rest (header, is editable, ...) is handled
// via the usual AbstractTableModel API
public abstract class SQLTableModel extends AbstractTableModel
{
    // GUI calls this to verify save-before-quit
    abstract public boolean wasEdited();
    // GUI calls these to load/save/add a new record.
    abstract public void load();
    abstract public boolean save();
    abstract public void addRecord();
};



