package com.cosylab.vdct.events.commands;

import com.cosylab.vdct.events.Command;
import com.cosylab.vdct.VisualDCT;

/**
 * Insert the type's description here.
 * Creation date: (29.12.2000 12:37:43)
 * @author: 
 */
public class SetWorkspaceScale extends Command {
	private VisualDCT visualDCT;
	private double scale;
/**
 * Insert the method's description here.
 * Creation date: (29.12.2000 12:37:58)
 */

public SetWorkspaceScale(VisualDCT visualDCT) {
	this.visualDCT=visualDCT;
}
/**
 * Insert the method's description here.
 * Creation date: (29.12.2000 12:37:43)
 */
public void execute() {
	visualDCT.setScale(scale);
}
/**
 * Insert the method's description here.
 * Creation date: (29.12.2000 12:39:32)
 * @param scale double
 */
public void setScale(double scale) {
	this.scale=scale;
}
}
