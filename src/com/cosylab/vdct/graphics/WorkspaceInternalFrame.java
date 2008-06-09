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

package com.cosylab.vdct.graphics;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.WindowConstants;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import com.cosylab.vdct.db.DbDescriptor;
import com.cosylab.vdct.events.CommandManager;
import com.cosylab.vdct.events.MouseEventManager;
import com.cosylab.vdct.events.commands.SetWorkspaceFile;

/**
 * @author ssah
 *
 */
public class WorkspaceInternalFrame extends JInternalFrame
implements InternalFrameInterface, InternalFrameListener {

	DbDescriptor id = null; 
	protected PanelDecorator contentPanel = null;
	protected DrawingSurfaceManagerInterface drawingSurfaceManager = null;

	public WorkspaceInternalFrame(DbDescriptor id, DrawingSurfaceManagerInterface drawingSurfaceManager) {
		super(id.getFileName(), true, true, true, true);
		this.id = id;
		this.drawingSurfaceManager = drawingSurfaceManager;
		contentPanel = new PanelDecorator();
		/* First register the component, then create the drawing surface which adds listeners to
		 * it.
		 */
		MouseEventManager.getInstance().registerSubscriber(
				"WorkspaceInternalFrame:" + id.toString(), contentPanel);
		contentPanel.setComponent(drawingSurfaceManager.addDrawingSurface(id, this));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		addInternalFrameListener(this);

		setFrameTitle(id.getFileName());

		setContentPane(contentPanel);
		setSize(256, 256);
		setLocation(32, 32);
	}

	public JComponent getDisplayingComponent() {
		return contentPanel;
	}

	public void setFrameTitle(String title) {
		setTitle(title);
		if (isSelected()) {
			sendActiveGroupNotification();
		}
	}

	public void internalFrameActivated(InternalFrameEvent e) {
		drawingSurfaceManager.setFocusedDrawingSurface(id);
		sendActiveGroupNotification();
		System.out.println(id + "internalFrameActivated");
	}

	public void internalFrameDeactivated(InternalFrameEvent e) {
		drawingSurfaceManager.setFocusedDrawingSurface(null);
		System.out.println(id + "internalFrameDeactivated");
	}

	public void internalFrameClosed(InternalFrameEvent e) {
		drawingSurfaceManager.removeDrawingSurface(id);
		System.out.println(id + "internalFrameClosed");
	}

	public void internalFrameClosing(InternalFrameEvent e) {
	}
	public void internalFrameDeiconified(InternalFrameEvent e) {
	}
	public void internalFrameIconified(InternalFrameEvent e) {
	}
	public void internalFrameOpened(InternalFrameEvent e) {
	}

	private void sendActiveGroupNotification() {
		SetWorkspaceFile command =
			(SetWorkspaceFile)CommandManager.getInstance().getCommand("SetFile");
		command.setFile(title);
		command.execute();
	}
}
