package com.cosylab.vdct.events.commands;

import com.cosylab.vdct.events.Command;
import com.cosylab.vdct.VisualDCT;

/**
 * Insert the class' description here.
 * Creation date: (3.2.2001 19:37:21)
 * @author: 
 */
 
public class ShowRenameDialog extends Command {
	private VisualDCT visualDCT;
	private String oldName;
/**
 * Insert the method's description here.
 * Creation date: (29.12.2000 12:37:58)
 */

public ShowRenameDialog(VisualDCT visualDCT) {
	this.visualDCT=visualDCT;
}
/**
 * Insert the method's description here.
 * Creation date: (29.12.2000 12:37:43)
 */
public void execute() {
	visualDCT.showRenameDialog(oldName);
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 10:12:43)
 * @return java.lang.String
 */
public java.lang.String getOldName() {
	return oldName;
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 10:12:43)
 * @param newOldName java.lang.String
 */
public void setOldName(java.lang.String newOldName) {
	oldName = newOldName;
}
}
