package com.cosylab.vdct.events.commands;

import com.cosylab.vdct.events.Command;
import com.cosylab.vdct.VisualDCT;

/**
 * Insert the type's description here.
 * Creation date: (29.12.2000 12:37:43)
 * @author: 
 */
public class SetWorkspaceFile extends Command {
	private VisualDCT visualDCT;
	private String fileName = null;
/**
 * Insert the method's description here.
 * Creation date: (29.12.2000 12:37:58)
 */

public SetWorkspaceFile(VisualDCT visualDCT) {
	this.visualDCT=visualDCT;
}
/**
 * Insert the method's description here.
 * Creation date: (29.12.2000 12:37:43)
 */
public void execute() {
	visualDCT.setFileInTitle(fileName);
}
/**
 * Insert the method's description here.
 * Creation date: (29.12.2000 12:39:32)
 * @param fileName java.lang.String
 */
public void setFile(String fileName) {
	this.fileName=fileName;
}
}
