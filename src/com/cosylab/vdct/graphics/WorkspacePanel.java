package com.cosylab.vdct.graphics;

/**
 * Copyright (c) 2002, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
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

import java.awt.event.*;
import java.awt.Graphics;
import javax.swing.event.*;

import java.awt.*;
import javax.swing.*;


import com.cosylab.vdct.events.*;
import com.cosylab.vdct.events.commands.*;

/**
 * Insert the type's description here.
 * Creation date: (10.12.2000 12:31:58)
 * @author Matej Sekoranja
 */
 
public class WorkspacePanel extends PanelDecorator implements ComponentListener {
/**
 * VisualAge support
 * Creation date: (10.12.2000 12:34:33)
 */
public WorkspacePanel() {
	initialize();
}
/**
 * WorkspacePanel constructor comment.
 * @param component com.cosylab.vdct.graphics.VisualComponent
 */
public WorkspacePanel(VisualComponent component) {
	super(component);
}
	/**
	 * Invoked when the component has been made invisible.
	 */
public void componentHidden(ComponentEvent e) {}
	/**
	 * Invoked when the component's position changes.
	 */
public void componentMoved(ComponentEvent e) {}
	/**
	 * Invoked when the component's size changes.
	 */
public void componentResized(ComponentEvent e) {
	getComponent().resize(0, 0, getWidth(), getHeight());
}
	/**
	 * Invoked when the component has been made visible.
	 */
public void componentShown(ComponentEvent e) {
}
/**
 * Insert the method's description here.
 * Creation date: (11.12.2000 15:44:25)
 */
protected void initialize() {
	addComponentListener(this);
	MouseEventManager.getInstance().registerSubscreiber("WorkspacePanel", this);
	CommandManager.getInstance().addCommand("RepaintWorkspace", new RepaintCommand(this));
	CommandManager.getInstance().addCommand("NullCommand", new NullCommand(this));
}

/**
 * Insert the method's description here.
 * Creation date: (10.12.2000 14:19:55)
 * @param g java.awt.Graphics
 */

protected void paintComponent(Graphics g) {
	getComponent().draw(g);
/*
	javax.swing.JLabel l = new javax.swing.JLabel(){
	    public void paint(Graphics g) {
			Shape clip = g.getClip();
			g.translate(getX(),getY());
			g.setClip(0, 0, getWidth(), getHeight());
			super.paint(g);
			g.translate(-getX(),-getY());
			g.setClip(clip);
	    }
	};

	String html="<html>Cosylab is one of the few if not the only company that is specialized in <B>software and hardware development in the field of control systems			for particle accelerators and other large experimental physics facilities</B>. Our team, consisting mainly of excellent young graduates of physics,			electronic engineering, mathematics and computer sciences led by Mark Plesko, has a <B>combined experience of over 40 man-years</B> in this field			with a long record of successful projects.			<br><br>		We are dedicated to provide high quality products and state of the art technology to our customers. Combining research-level know-how,			a professional business approach and our proverbial flexibility, we meet all customers’ demands in terms of system performance, quality,			cost and time-to-market.";	
	l.setVerticalAlignment(JLabel.TOP);
	l.setText(html);
	l.setForeground(java.awt.Color.white);

	int x = 10; int y = 10;
	int w = 100; int h = 500;
	l.setBounds(x,y,w,h);
	l.paint(g);

	System.out.println(l.getLocation().toString());
	g.setColor(java.awt.Color.white);
	g.drawRect(x-1, y-1, w+2, h+2);
*/
}
}
