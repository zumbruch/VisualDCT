package com.cosylab.vdct.events.commands;

import com.cosylab.vdct.events.Command;
import com.cosylab.vdct.VisualDCT;

/**
 * Insert the class' description here.
 * Creation date: (3.2.2001 19:37:21)
 * @author: 
 */
 
public class ShowNewDialog extends Command {
	private VisualDCT visualDCT;
/**
 * Insert the method's description here.
 * Creation date: (29.12.2000 12:37:58)
 */

public ShowNewDialog(VisualDCT visualDCT) {
	this.visualDCT=visualDCT;
}
/**
 * Insert the method's description here.
 * Creation date: (29.12.2000 12:37:43)
 */
public void execute() {
	visualDCT.showNewDialog();
}
}
