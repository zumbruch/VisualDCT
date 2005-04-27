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

package com.cosylab.vdct.appplugin.alarmhandler;

import com.cosylab.vdct.appplugin.EditorComponent;

import java.awt.Dimension;
import java.awt.FontMetrics;

import sun.awt.font.FontDesignMetrics;


/**
 * <code>AHEditorComponent</code> ...  DOCUMENT ME!
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class AHEditorComponent extends EditorComponent
{
	private Dimension dimen = new Dimension();
	private FontMetrics fm;

	/**
	 * TODO DOCUMENT ME!
	 */
	public AHEditorComponent()
	{
		super();
		fm = new FontDesignMetrics(valueField.getFont());
	}

	protected Dimension getDimension()
	{
	    String text = valueField.getText();
	    if (text != null) {
	        int i = fm.stringWidth(text) + 10;
	        if (i < 100) {
	            i = 100;
	        }
	        dimen.width = i;
	    } else {
	        dimen.width = 100;
	    }
	    
		dimen.height = 18;
		

		return dimen;
	}
	
}

/* __oOo__ */
