package com.cosylab.vdct.events.commands;

import com.cosylab.vdct.events.Command;
import com.cosylab.vdct.VisualDCT;

/**
 * Insert the type's description here.
 * Creation date: (29.12.2000 12:37:43)
 * @author: 
 */
public class SetRedoMenuItemState extends Command {
	private VisualDCT visualDCT;
	private boolean state;
/**
 * Insert the method's description here.
 * Creation date: (29.12.2000 12:37:58)
 */

public SetRedoMenuItemState(VisualDCT visualDCT) {
	this.visualDCT=visualDCT;
}
/**
 * Insert the method's description here.
 * Creation date: (29.12.2000 12:37:43)
 */
public void execute() {
	visualDCT.getRedoMenuItem().setEnabled(state);
	visualDCT.getRedoButton().setEnabled(state);
}
/**
 * Insert the method's description here.
 * Creation date: (22.4.2001 18:04:29)
 * @param newState boolean
 */
public void setState(boolean newState) {
	state = newState;
}
}
