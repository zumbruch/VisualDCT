package com.cosylab.vdct.graphics.printing;

import java.awt.*;
import java.awt.print.*;

/**
 * Insert the type's description here.
 * Creation date: (12.5.2001 15:24:22)
 * @author: Matej Sekoranja
 */

public class PagePreview extends javax.swing.JPanel {
	protected int width;
	protected int height;
	protected Image source;
	protected Image image;
/**
 * Insert the method's description here.
 * Creation date: (12.5.2001 16:53:36)
 * @param w int
 * @param h int
 * @param source java.awt.Image
 */
public PagePreview(int w, int h, Image source) {
	width = w;
	height = h;
	this.source = source;
	image = source.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	image.flush();
	setBackground(Color.white);
	setBorder(new javax.swing.border.MatteBorder(1, 1, 2, 2, Color.black));
}
/**
 * Insert the method's description here.
 * Creation date: (12.5.2001 16:43:10)
 */
public Dimension getMainimumSize()
{
	return getPreferredSize();
}
/**
 * Insert the method's description here.
 * Creation date: (12.5.2001 16:43:10)
 */
public Dimension getMaximumSize()
{
	return getPreferredSize();
}
/**
 * Insert the method's description here.
 * Creation date: (12.5.2001 16:43:10)
 */
public Dimension getPreferredSize()
{
	Insets ins = getInsets();
	return new Dimension(width+ins.left+ins.right,
						 height+ins.top+ins.bottom);
}
/**
 * Insert the method's description here.
 * Creation date: (12.5.2001 16:55:17)
 * @param g java.awt.Graphics
 */
public void paint(Graphics g) {
	g.setColor(getBackground());
	g.fillRect(0, 0, getWidth(), getHeight());
	g.drawImage(image, 0, 0, this);
	paintBorder(g);
}
/**
 * Insert the method's description here.
 * Creation date: (12.5.2001 16:52:04)
 * @param w int
 * @param h int
 */
public void setScaledSize(int w, int h) {
	width = w;
	height = h;
	image = source.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	repaint();
}
}
