/**
 * 
 */
package com.cosylab.vdct.inspector;

import com.cosylab.vdct.vdb.NameValueInfoProperty;

/**
 * @author ssah
 *
 */
public class EmptyProperty extends NameValueInfoProperty {

	public EmptyProperty() {
		super("", "");
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.vdb.NameValueInfoProperty#isEditable()
	 */
	public boolean isEditable() {
		return false;
	}

}
