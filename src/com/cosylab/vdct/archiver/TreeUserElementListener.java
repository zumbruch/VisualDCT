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

import java.util.EventListener;


/**
 * <code>TreeUserElementListener</code> should be implemented by those classes
 * that needs to be notified of the change in the in the
 * <code>TreeUserElement</code>.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public interface TreeUserElementListener extends EventListener
{
	/**
	 * This method is invoked when new elements are added to
	 * <code>TreeUserElement</code>. New elements could be either new
	 * properties, group, etc.
	 *
	 * @param event
	 */
	public void elementsAdded(TreeUserElementEvent event);

	/**
	 * Method is invoked when elements are removed from the TreeUserElement.
	 *
	 * @param event
	 */
	public void elementsRemoved(TreeUserElementEvent event);
}

/* __oOo__ */
