package com.cosylab.vdct.events.commands;

import com.cosylab.vdct.events.Command;
import com.cosylab.vdct.VisualDCT;

/**
 * Insert the type's description here.
 * Creation date: (29.12.2000 12:37:43)
 * @author: 
 */
public class SetWorkspaceGroup extends Command {
	private VisualDCT visualDCT;
	private String group = null;
/**
 * Insert the method's description here.
 * Creation date: (29.12.2000 12:37:58)
 */

public SetWorkspaceGroup(VisualDCT visualDCT) {
	this.visualDCT=visualDCT;
}
/**
 * Insert the method's description here.
 * Creation date: (29.12.2000 12:37:43)
 */
public void execute() {
	visualDCT.setCurrentGroup(group);
}
/**
 * Insert the method's description here.
 * Creation date: (29.12.2000 12:39:32)
 * @param group java.lang.String
 */
public void setGroup(String group) {
	this.group=group;
}
}
