/**
 * Copyright (c) 2008, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution. 
 * Neither the name of the Cosylab, Ltd., Control System Laboratory nor the names
 * of its contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.cosylab.vdct.rdb;

import java.awt.Frame;

import com.cosylab.vdct.db.DBData;

/**
 * @author ssah
 *
 */
public class Irmis implements RdbInterface {

	private DataMapper mapper = null;
	private ConnectionDialog dialog = null;

	/**
	 * 
	 */
	public Irmis() {
		super();
		try {
		    mapper = new DataMapper();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.rdb.RdbInterface#connect(java.awt.Frame)
	 */
	public void connect(Frame guiContext) {
		if (dialog == null) {
			dialog = new ConnectionDialog(guiContext, mapper);
		}
		dialog.setVisible(true);
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.rdb.RdbInterface#getDbGroupName()
	 */
	public String getDbGroupName(Frame guiContext) {
		if (dialog == null) {
			dialog = new ConnectionDialog(guiContext, mapper);
		}
		dialog.setVisible(true);
		
		// TODO:REPLACE
		//return dialog.getGroupName();
		return "AirC";
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.rdb.RdbInterface#loadDbGroup(java.lang.String)
	 */
	public DBData loadDbGroup(Frame guiContext) {
		if (dialog == null) {
			dialog = new ConnectionDialog(guiContext, mapper);
		}
		//dialog.setSaveMode(false);
		dialog.setVisible(true);
		
		// TODO:REPLACE
		//return dialog.getData();
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.rdb.RdbInterface#saveDbGroup(java.lang.String)
	 */
	public void saveDbGroup(String name, Frame guiContext) {
		if (dialog == null) {
			dialog = new ConnectionDialog(guiContext, mapper);
		}
		//dialog.setSaveMode(true);
		dialog.setVisible(true);
	}
}
