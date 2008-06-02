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

import javax.swing.JInternalFrame;
import javax.swing.WindowConstants;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import com.cosylab.vdct.db.DbDescriptor;
import com.cosylab.vdct.events.MouseEventManager;
import com.cosylab.vdct.rdb.RdbDataId;

/**
 * @author ssah
 *
 */
public class WorkspaceInternalFrame extends JInternalFrame implements InternalFrameListener {
	
    DbDescriptor id = null; 
    protected PanelDecorator contentPanel = null;

    public WorkspaceInternalFrame(DbDescriptor id) {
        super("Internal frame", true, true, true, true);
        this.id = id;
        contentPanel = new PanelDecorator();
        // First register the component, then create the DrawingSurface which adds listeners to it.
        MouseEventManager.getInstance().registerSubscreiber(
        		"WorkspaceInternalFrame:" + id.toString(), contentPanel);
        DrawingSurface drawSurface = DrawingSurfaceManager.getInstance().addDrawingSurface(id);
        contentPanel.setComponent(drawSurface);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        addInternalFrameListener(this);
        
        setContentPane(contentPanel);
        setSize(256, 256);
        setLocation(32, 32);
    }
    
	/* (non-Javadoc)
	 * @see javax.swing.event.InternalFrameListener#internalFrameActivated(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameActivated(InternalFrameEvent e) {
		DrawingSurfaceManager.getInstance().setFocusedDrawingSurface(id);
		System.out.println(id + "internalFrameActivated");
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.InternalFrameListener#internalFrameClosed(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameClosed(InternalFrameEvent e) {
		DrawingSurfaceManager.getInstance().removeDrawingSurface(id);
		System.out.println(id + "internalFrameClosed");
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.InternalFrameListener#internalFrameClosing(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameClosing(InternalFrameEvent e) {
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.InternalFrameListener#internalFrameDeactivated(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameDeactivated(InternalFrameEvent e) {
		DrawingSurfaceManager.getInstance().setFocusedDrawingSurface(null);
		System.out.println(id + "internalFrameDeactivated");
}

	/* (non-Javadoc)
	 * @see javax.swing.event.InternalFrameListener#internalFrameDeiconified(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameDeiconified(InternalFrameEvent e) {
		DrawingSurfaceManager.getInstance().setFocusedDrawingSurface(id);
		System.out.println(id + "internalFrameDeiconified");
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.InternalFrameListener#internalFrameIconified(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameIconified(InternalFrameEvent e) {
		DrawingSurfaceManager.getInstance().setFocusedDrawingSurface(null);
		System.out.println(id + "internalFrameIconified");
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.InternalFrameListener#internalFrameOpened(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameOpened(InternalFrameEvent e) {
	}
}
