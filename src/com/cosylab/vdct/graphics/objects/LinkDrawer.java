package com.cosylab.vdct.graphics.objects;

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

import java.awt.*;
import com.cosylab.vdct.Constants;
import com.cosylab.vdct.graphics.*;

/**
 * !!! support for shortened tail drawing 
 * Insert the type's description here.
 * Creation date: (30.1.2001 14:35:04)
 * @author Matej Sekoranja
 */
 
public final class LinkDrawer {
	private final static int tailLenOfR = 6;
	private final static int maxWidth = Constants.FIELD_WIDTH*4/5;
	private final static int maxHeight = Constants.FIELD_HEIGHT;
	private final static int LARGE_RECT = Constants.LINK_RADIOUS;
	private final static int SMALL_RECT = (int)(0.7*LARGE_RECT);
	private final static String maxLenStr = "012345678901234";
	private static double lastScale = 0.0;
	private static Font font = null;
	private static FontMetrics fontMetrics = null;
	private static int dy;
/**
 * Creation date: (30.1.2001 14:43:02)
 * @param g java.awt.Graphics
 * @param com.cosylab.vdct.graphics.objects.OutLink out
 * @param com.cosylab.vdct.graphics.objects.InLink in
 * @param isRight boolean
 */
public static void drawInIntergroupLink(Graphics g, OutLink out, InLink in, boolean isRight) {
	if (!out.getLayerID().equals(in.getLayerID())) {
		
		ViewState view = ViewState.getInstance();
		double scale = view.getScale();
		int x1 = (int)(scale*in.getInX())-view.getRx();
		int y1 = (int)(scale*in.getInY())-view.getRy();

		if (in instanceof MultiInLink && ((MultiInLink)in).getLinkCount()>1)
			drawIntergroupLink(g, view, x1, y1, null, isRight);
		else
			drawIntergroupLink(g, view, x1, y1, out, isRight);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 12:30:16)
 * @param x1 int
 * @param y1 int
 * @param descPoint com.cosylab.vdct.graphics.objects.Linkable
 * @param isRight boolean
 */
private static void drawIntergroupLink(Graphics g, ViewState view, int x1, int y1, Linkable descPoint, boolean isRight) {

	int r = (int)(LARGE_RECT*view.getScale());

	int dx = tailLenOfR*r;
	if (!isRight) dx=-dx;
	g.drawLine(x1, y1, x1+dx, y1);
	x1+=dx;
		
	g.drawRect(x1-r, y1-r, 2*r, 2*r);

	String label = null;
	validateFont(view);
	if (font!=null) {
		g.setFont(font);
		Linkable target = null;
		if (descPoint==null)
		{
			label = "<more>";
		}

		// bug: what about connectors (they are both)! +
		// InLink/OutLink cannot identify the right direction
		//    there can be INP in the subgroup	or hypergroup!
		else if (descPoint instanceof InLink)
		{
			target = EPICSLinkOut.getEndPoint(descPoint);
			// any special threatment ??
			if (target instanceof Descriptable)
				label = ((Descriptable)target).getDescription();
		}
		else if (descPoint instanceof OutLink)
		{
			target = EPICSLinkOut.getStartPoint(descPoint);
			// any special threatment ??
			if (target instanceof Descriptable)
				label = ((Descriptable)target).getDescription();
		}
			
		if (label!=null) {
			if (isRight) dx = 3*r; 
			else dx = -(fontMetrics.stringWidth(label)+2*r);
			g.drawString(label, x1+dx, y1+dy);
		}
	}

	r=(int)(SMALL_RECT*view.getScale());
	g.drawRect(x1-r, y1-r, 2*r, 2*r);

}
/**
 * This method was created in VisualAge.
 * @param g java.awt.Graphics
 * @param com.cosylab.vdct.graphics.objects.OutLink out
 * @param com.cosylab.vdct.graphics.objects.InLink in
 * @param firstHorizontal boolean
 */
public static void drawKneeLine(Graphics g, OutLink out, InLink in, boolean firstHorizontal) {
	ViewState view = ViewState.getInstance();
	double scale = view.getScale();
	int x1 = (int)(scale*out.getOutX())-view.getRx();
	int y1 = (int)(scale*out.getOutY())-view.getRy();
	int x2 = (int)(scale*in.getInX())-view.getRx();
	int y2 = (int)(scale*in.getInY())-view.getRy();


	// inivsible mode
	if (out.getMode() == OutLink.INVISIBLE_MODE)
	{
		int s = (int)(scale*Constants.INVISIBLE_CROSS_SIZE);	

		if (firstHorizontal) {
			g.drawLine(x1-s, y1, x1+s, y1);
			g.drawLine(x1, y1-s, x1, y1+s);

			g.drawLine(x2, y2-s, x2, y2+s);
			g.drawLine(x2-s, y2, x2+s, y2);
		}
		else {
			g.drawLine(x1, y1-s, x1, y1+s);
			g.drawLine(x1-s, y1, x1+s, y1);

			g.drawLine(x2-s, y2, x2+s, y2);
			g.drawLine(x2, y2-s, x2, y2+s);
		}
		return;
	}

	// only test here
	else if (out.getMode() == OutLink.EXTERNAL_INPUT_MODE)
	{
		// horizontal
		
		int s = (int)(scale*Constants.LINK_STUB_SIZE/2.0);
		
		if (x2<x1) s = -s;
		
		g.drawLine(x1, y1-s, x1, y1+s);

		g.drawLine(x1, y1-s, x1+s, y1-s);
		g.drawLine(x1, y1+s, x1+s, y1+s);

		g.drawLine(x1+s, y1-s, x1+2*s, y1);
		g.drawLine(x1+s, y1+s, x1+2*s, y1);

		return;
	}
	else if (out.getMode() == OutLink.EXTERNAL_OUTPUT_MODE)
	{
		// horizontal
		
		int s = (int)(scale*Constants.LINK_STUB_SIZE/2.0);
		
		if (x2<x1) s = -s;
		
		g.drawLine(x1, y1-s, x1, y1+s);

		g.drawLine(x1, y1-s, x1+2*s, y1-s);
		g.drawLine(x1, y1+s, x1+2*s, y1+s);

		g.drawLine(x1+2*s, y1-s, x1+s, y1);
		g.drawLine(x1+2*s, y1+s, x1+s, y1);

		return;
	}
/*
	else if (out.getMode() == ?)
	{
		// horizontal
		
		int s = (int)(scale*Constants.LINK_STUB_SIZE/2.0);
		
		if (x2<x1) s = -s;
		
		g.drawLine(x1, y1-s, x1, y1+s);

		g.drawLine(x1, y1-s, x1+2*s, y1-s);
		g.drawLine(x1, y1+s, x1+2*s, y1+s);

		g.drawLine(x1+s, y1-s, x1+s, y1);
		g.drawLine(x1+s, y1+s, x1+s, y1);

		return;
	}
*/	
	if (firstHorizontal) {
		g.drawLine(x1, y1, x2, y1);
		g.drawLine(x2, y1, x2, y2);
	}
	else {
		g.drawLine(x1, y1, x1, y2);
		g.drawLine(x1, y2, x2, y2);
	}

}
/**
 * Creation date: (30.1.2001 14:43:02)
 * @param g java.awt.Graphics
 * @param com.cosylab.vdct.graphics.objects.OutLink out
 * @param com.cosylab.vdct.graphics.objects.InLink in
 * @param count int
 * @param isRight boolean
 */
public static void drawLink(Graphics g, OutLink out, InLink in, int count, boolean isRight) {
	if (out.getLayerID().equals(in.getLayerID()))
		drawKneeLine(g, out, in, ((count%2)==0));
	else {
		
		ViewState view = ViewState.getInstance();
		double scale = view.getScale();
		int x1 = (int)(scale*out.getOutX())-view.getRx();
		int y1 = (int)(scale*out.getOutY())-view.getRy();
		int r = (int)(LARGE_RECT*scale);

		drawIntergroupLink(g, view, x1, y1, in, isRight);

	}
}
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 10:26:24)
 */
private static void validateFont(ViewState view) {
  if (lastScale!=view.getScale()) {
	  double nscale = view.getScale();
	  int w = (int)(maxWidth*nscale);
	  if (w<10)
	  {
		/*if (font==null) - perf. */ lastScale = nscale;
	  	font = null;
	  	return;
	  }
  	  lastScale = nscale;
	  font = FontMetricsBuffer.getInstance().getAppropriateFont(
		  			Constants.DEFAULT_FONT, Font.PLAIN, 
 		 			maxLenStr, 
  					w, 
  					(int)(maxHeight*lastScale));

	  if (font!=null) {
		  fontMetrics = FontMetricsBuffer.getInstance().getFontMetrics(font);
		  dy = fontMetrics.getAscent()-fontMetrics.getHeight()/2;
	  }
  }
}
}
