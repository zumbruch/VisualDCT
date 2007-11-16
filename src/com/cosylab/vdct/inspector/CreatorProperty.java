/**
 * 
 */
package com.cosylab.vdct.inspector;

import com.cosylab.vdct.vdb.CreatorPropertyListener;
import com.cosylab.vdct.vdb.NameValueInfoProperty;

/**
 * @author ssah
 *
 */
public class CreatorProperty extends NameValueInfoProperty {
	
	private CreatorPropertyListener listener = null;

	public CreatorProperty(CreatorPropertyListener listener) {
		super("", "");
		this.listener = listener;
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.vdb.NameValueInfoProperty#isEditable()
	 */
	public boolean isEditable() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.vdb.NameValueInfoProperty#setValue(java.lang.String)
	 */
	public void setValue(String value) {
		if (value.equals("")) {
			return;
		}
		listener.addProperty(name, value);
		super.setValue(value);
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
