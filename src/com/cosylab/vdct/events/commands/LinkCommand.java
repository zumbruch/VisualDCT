package com.cosylab.vdct.events.commands;

import com.cosylab.vdct.events.Command;
import com.cosylab.vdct.graphics.DrawingSurface;
import com.cosylab.vdct.vdb.VDBFieldData;
import com.cosylab.vdct.graphics.objects.Record;

/**
 * Insert the class' description here.
 * Creation date: (3.2.2001 13:28:11)
 * @author: Matej Sekoranja
 */
 
public class LinkCommand extends com.cosylab.vdct.events.Command {
	private DrawingSurface drawingSurface;
	private VDBFieldData field = null;
	private Record record = null;
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 22:43:26)
 * @param drawingSurface com.cosylab.vdct.graphics.DrawingSurface 
 */
public LinkCommand(DrawingSurface drawingSurface) {
	this.drawingSurface=drawingSurface;
}
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 22:42:23)
 */
public void execute() {
	drawingSurface.linkCommand(record, field);
}
/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 13:29:40)
 * @param record com.cosylab.vdct.graphics.objects.Record
 * @param field com.cosylab.vdct.vdb.VDBFieldData
 */
public void setData(Record record, VDBFieldData field) {
	this.field=field;
	this.record=record;
}
}
