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

package com.cosylab.vdct.events.commands;

import javax.swing.JOptionPane;

import com.cosylab.vdct.VisualDCT;
import com.cosylab.vdct.events.Command;
import com.cosylab.vdct.graphics.DrawingSurface;
import com.cosylab.vdct.graphics.DsManager;

/**
 * @author ssah
 *
 */
public class ShowModifiedDialog extends Command {

	private VisualDCT visualDCT = null;
	private Object dsId = null;
	private int selection = JOptionPane.DEFAULT_OPTION;

	public ShowModifiedDialog(VisualDCT visualDCT) {
		this.visualDCT = visualDCT;
	}
	
	public void execute() {
		DrawingSurface drawingSurface = DsManager.getDrawingSurface(dsId);
		if (drawingSurface != null) {
			if (drawingSurface.isModified()) {
				selection = JOptionPane.showConfirmDialog(visualDCT, "The file has been"
						+ " modified. Save changes?", drawingSurface.getTitle(),
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
				if (selection == JOptionPane.YES_OPTION) {
					if (!visualDCT.saveMenuItem_ActionPerformed()) {
						selection = JOptionPane.CANCEL_OPTION;				
					}
				}
			} else {
				selection = JOptionPane.YES_OPTION;
			}
		} else {
			selection = JOptionPane.CANCEL_OPTION;
		}
	}

	public int getSelection() {
		return selection;
	}

	public void setDsId(Object dsId) {
		this.dsId = dsId;
	}
}
