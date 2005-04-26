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

package com.cosylab.vdct.appplugin;

import com.cosylab.vdct.appplugin.alarmhandler.Include;
import com.cosylab.vdct.appplugin.alarmhandler.Text;
import com.cosylab.vdct.appplugin.archiver.EngineConfigRoot;

import javax.swing.Icon;
import javax.swing.ImageIcon;


/**
 * <code>CellUtilities</code> ...  DOCUMENT ME!
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class CellUtilities
{
	static Icon getIcon(AppTreeNode node)
	{
		Object object = node.getUserObject();
		Class type = object.getClass();

		if (Channel.class.isAssignableFrom(type)) {
			return loadIcon("images/record.gif");
		} else if (Group.class.isAssignableFrom(type)) {
			return loadIcon("images/boxn.gif");
		} else if (EngineConfigRoot.class.isAssignableFrom(type)) {
			return loadIcon("images/page.gif");
		} else if (Include.class.isAssignableFrom(type)) {
			return loadIcon("images/open.gif");
		} else if (Text.class.isAssignableFrom(type)) {
			return loadIcon("images/textboxn.gif");
		} else if (Property.class.isAssignableFrom(type)) {
			//			return loadIcon("images/port.gif");
			return loadIcon("images/Tree16.gif");
		}

		return loadIcon("images/Tree16.gif");
	}

	private static Icon loadIcon(String resource)
	{
		return new ImageIcon(CellUtilities.class.getClassLoader().getResource(resource));
	}
}

/* __oOo__ */
