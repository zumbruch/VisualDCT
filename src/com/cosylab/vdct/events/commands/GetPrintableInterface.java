package com.cosylab.vdct.events.commands;

import com.cosylab.vdct.events.Command;
import java.awt.print.*;

/**
 * Insert the type's description here.
 * Creation date: (3.2.2001 22:05:51)
 * @author: 
 */
public class GetPrintableInterface extends Command {
	Pageable pageable = null;
/**
 * Insert the method's description here.
 * Creation date: (12.5.2001 17:41:08)
 * @param printable java.awt.print.Printable
 */
public GetPrintableInterface(Pageable intf) {
	this.pageable=intf;
}
/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 22:05:51)
 */
public void execute() {}
/**
 * Insert the method's description here.
 * Creation date: (13.5.2001 16:34:21)
 * @return java.awt.print.Pageable
 */
public java.awt.print.Pageable getPageable() {
	return pageable;
}
/**
 * Insert the method's description here.
 * Creation date: (13.5.2001 16:34:21)
 * @param newPageable java.awt.print.Pageable
 */
public void setPageable(java.awt.print.Pageable newPageable) {
	pageable = newPageable;
}
}
