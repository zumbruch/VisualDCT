/*
 * Copyright (c) 2004 by Cosylab d.o.o.
 *
 * The full license specifying the redistribution, modification, usage and other
 * rights and obligations is included with the distribution of this project in
 * the file license.html. If the license is not included you may find a copy at
 * http://www.cosylab.com/legal/abeans_license.htm or may write to Cosylab, d.o.o.
 *
 * THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND, NOT EVEN THE
 * IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR OF THIS SOFTWARE, ASSUMES
 * _NO_ RESPONSIBILITY FOR ANY CONSEQUENCE RESULTING FROM THE USE, MODIFICATION,
 * OR REDISTRIBUTION OF THIS SOFTWARE.
 */

package com.cosylab.vdct.archiver;

import java.io.Serializable;


/**
 * <code>ArchiverTreeRecord</code> is a tree wrapper for <code>Record</code>.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class ArchiverTreeRecordNode extends ArchiverTreeNode
	implements Serializable
{
	private Record record;

	/**
	 * Creates a new ArchiverTreeRecordNode object.
	 *
	 * @param record <code>Record</code> which is wrapped in the node
	 */
	public ArchiverTreeRecordNode(Record record)
	{
		super(record);
		this.record = record;
	}

	/**
	 * Returns the <code>Record</code> of this node.
	 *
	 * @return the records
	 */
	public Record getRecord()
	{
		return record;
	}
}

/* __oOo__ */
