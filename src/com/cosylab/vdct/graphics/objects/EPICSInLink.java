package com.cosylab.vdct.graphics.objects;

import java.util.*;
import java.awt.*;
import com.cosylab.vdct.Constants;

/**
 * Insert the type's description here.
 * Creation date: (29.1.2001 21:34:08)
 * @author: Matej Sekoranja
 */
public class EPICSInLink extends EPICSLinkOut {
/**
 * EPICSInLink constructor comment.
 * @param parent com.cosylab.vdct.graphics.objects.ContainerObject
 * @param fieldData com.cosylab.vdct.vdb.VDBFieldData
 */
public EPICSInLink(ContainerObject parent, com.cosylab.vdct.vdb.VDBFieldData fieldData) {
	super(parent, fieldData);
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 22:10:37)
 * @param g java.awt.Graphics
 * @param hilited boolean
 */
protected void draw(Graphics g, boolean hilited) {
	super.draw(g, hilited);
	
	com.cosylab.vdct.graphics.ViewState view = com.cosylab.vdct.graphics.ViewState.getInstance();

	boolean rightSide = isRight();
	int arrowLength = 2*r;
	
	int rrx;
	if (rightSide)
		rrx = getRx()+getRwidth()-view.getRx();
	else 
		rrx = getRx()-view.getRx()-arrowLength;

	int rry = getRy()+getRheight()/2-view.getRy();
	
	if (!hilited) g.setColor(Constants.FRAME_COLOR);
	else g.setColor((this==view.getHilitedObject()) ? 
					Constants.HILITE_COLOR : Constants.FRAME_COLOR);

	if (inlink!=null) {

		// draw arrow
		g.drawLine(rrx, rry-r, rrx+arrowLength, rry-r);
		g.drawLine(rrx, rry+r, rrx+arrowLength, rry+r);
		
		int dr=r; 
		if (rightSide) {
			dr=-dr;
			rrx+=arrowLength;
		}
		g.drawLine(rrx, rry-r, rrx+dr, rry);
		g.drawLine(rrx, rry+r, rrx+dr, rry);

		if (font2!=null) {
			g.setFont(font2);
			rry += realHalfHeight;
			if (rightSide)
				rrx += rtailLen-2*labelLen+realLabelLen;
			else
				rrx += arrowLength-rtailLen+labelLen-realLabelLen;
			g.drawString(label2, rrx, rry);
		}
		
		//if (inlink.getLayerID().equals(getLayerID()))
			g.setColor(getColor());
		LinkDrawer.drawLink(g, this, inlink, getQueueCount(), rightSide);
	} else {
		// draw cross
		if (!rightSide) rrx+=arrowLength;
		g.drawLine(rrx-r, rry-r, rrx+r, rry+r);
		g.drawLine(rrx+r, rry-r, rrx-r, rry+r);
	}

}
}
