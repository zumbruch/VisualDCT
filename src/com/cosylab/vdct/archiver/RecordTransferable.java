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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;


/**
 * <code>RecordTrensferable</code> is an implementation of
 * <code>Transferable</code> interface for transfering
 * <code>ArchiveTreeChannelNodes</code>. <code>RecordTransferable</code>
 * supports only one <code>DataFlavor</code> with class representation of
 * <code>ArchiverTreeChannelNode</code> which is described as "EPICS Channel".
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class RecordTransferable implements Transferable
{
	/** DOCUMENT ME! */
	public static DataFlavor[] flavors = new DataFlavor[]{
			new DataFlavor(ArchiverTreeChannelNode.class, "EPICS Channel")
		};
	private ArchiverTreeChannelNode[] channels;

	/**
	 * Creates a new RecordTransferable object.
	 *
	 * @param channels an array of ArchiverTreeRecordNodes to be transferred
	 */
	public RecordTransferable(ArchiverTreeChannelNode[] channels)
	{
		this.channels = channels;

		//	    recordsList = new ArrayList(Arrays.asList(records));
	}

	/* (non-Javadoc)
	 * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
	 */
	public DataFlavor[] getTransferDataFlavors()
	{
		return flavors;
	}

	/* (non-Javadoc)
	 * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
	 */
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		return Arrays.asList(flavors).contains(flavor);
	}

	/* (non-Javadoc)
	 * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
	 */
	public Object getTransferData(DataFlavor flavor)
		throws UnsupportedFlavorException, IOException
	{
		if (isDataFlavorSupported(flavor)) {
			return channels;
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}
}

/* __oOo__ */
