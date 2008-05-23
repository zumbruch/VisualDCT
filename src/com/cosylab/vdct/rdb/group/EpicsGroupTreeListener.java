package com.cosylab.vdct.rdb.group;

// EpicsLogicTree handles the underlying tree selections,
// checks for selecting a "leaf" etc.
public interface EpicsGroupTreeListener
{
    // Called whenever an ioc has been selected
    public void iocSelected (String ioc_id);
    
    // Called whenever a group id has been selected
    public void groupSelected (String ioc_id, String group_id);
};
