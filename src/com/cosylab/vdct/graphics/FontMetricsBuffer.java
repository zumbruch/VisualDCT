package com.cosylab.vdct.graphics;

import java.awt.*;
import java.util.*;

/**
 * Font metrics buffer (using Flyweight/Singleton pattern)
 * Creation date: (25.12.2000 11:51:01)
 * @author: Matej Sekoranja
 */

public class FontMetricsBuffer {

	private class FontData {
		private Font font;
		private FontMetrics fontMetrics;
		
		public FontData(Font font, FontMetrics fontMetrics) {
			this.font=font;
			this.fontMetrics=fontMetrics;
		}
		
		public Font getFont() { return font; }
		public FontMetrics getFontMetrics() { return fontMetrics; }
		
	}
	private final static int MIN_SIZE = 3;
	private final static int MAX_SIZE = 72;
	private Hashtable fonts;
	private Graphics graphics;
	private static FontMetricsBuffer instance = null;
/**
 * FontMetricsBuffer constructor comment.
 * @param g java.awt.Graphics
 */
protected FontMetricsBuffer(Graphics g) {
	this.graphics = g;
	fonts = new Hashtable();
}
/**
 * Insert the method's description here.
 * Creation date: (25.12.2000 12:28:19)
 * @param g java.awt.Graphics
 */
public static void createInstance(Graphics g) {
	if (g!=null) 
		if (instance==null) instance = new FontMetricsBuffer(g);
		else instance.setGraphics(g);
}
/**
 * Insert the method's description here.
 * Creation date: (25.12.2000 12:47:03)
 * @return java.awt.Font
 * @param fontName java.lang.String
 * @param style int
 * @param str java.lang.String
 * @param maxWidth int
 * @param maxHeight int
 */
public Font getAppropriateFont(String fontName, int style, String str, int maxWidth, int maxHeight) {
  if (graphics==null) return null;
  int size = MIN_SIZE;
  FontData fl = null;
  FontData fd = getFontData(fontName, size, style);
  while ((size<=MAX_SIZE) &&
	  	 (fd.getFontMetrics().getHeight() < maxHeight) &&
	  	 (fd.getFontMetrics().stringWidth(str) < maxWidth)) {
 	size++;
 	fl = fd;
	fd = getFontData(fontName, size, style);
  }
  if (fl==null) return null;
  else return fl.getFont();
}
/**
 * Insert the method's description here.
 * Creation date: (25.12.2000 12:34:08)
 * @return java.awt.Font
 * @param name java.lang.String
 * @param size int
 * @param style int
 */
public Font getFont(String name, int size, int style) {
	String id = getID(name, size, style);
	FontData fd = (FontData)(fonts.get(id));
	if (fd==null) {
		Font font = new Font(name, style, size);
		if (font==null) return null;
 		fd = new FontData(font, graphics.getFontMetrics(font));
		fonts.put(id, fd);
	}
	return fd.getFont();
}
/**
 * Insert the method's description here.
 * Creation date: (25.12.2000 12:34:08)
 * @return com.cosylab.vdct.graphics.FontData
 * @param name java.lang.String
 * @param size int
 * @param style int
 */
private FontData getFontData(String name, int size, int style) {
	if (graphics==null) return null;
	String id = getID(name, size, style);
	FontData fd = (FontData)(fonts.get(id));
	if (fd==null) {
		Font font = new Font(name, style, size);
		if (font==null) return null;
 		fd = new FontData(font, graphics.getFontMetrics(font));
		fonts.put(id, fd);
	}
	return fd;
}
/**
 * Insert the method's description here.
 * Creation date: (25.12.2000 12:32:57)
 * @return java.awt.FontMetrics
 */
public FontMetrics getFontMetrics(Font font) {
	if (graphics==null) return null;
	String id = getID(font);
	FontData fd = (FontData)(fonts.get(id));
	if (fd==null) {
 		fd = new FontData(font, graphics.getFontMetrics(font));
		fonts.put(id, fd);
	}
	return fd.getFontMetrics();
}
/**
 * Insert the method's description here.
 * Creation date: (25.12.2000 12:35:28)
 * @return java.lang.String
 * @param font java.awt.Font
 */
private String getID(Font font) {
	return getID(font.getName(), font.getSize(), font.getStyle());
}
/**
 * Insert the method's description here.
 * Creation date: (25.12.2000 12:36:00)
 * @return java.lang.String
 * @param name java.lang.String
 * @param size int
 * @param style int
 */
private String getID(String name, int size, int style) {
	return name+"_"+size+"_"+style;
}
/**
 * Must be initialized with createInstance(java.awt.Graphics)
 * Creation date: (25.12.2000 11:54:36)
 * @return com.cosylab.vdct.graphics.FontMetricsBuffer
 */
public static FontMetricsBuffer getInstance() {
	if (instance==null) instance = new FontMetricsBuffer(null);
	return instance;
}
/**
 * Insert the method's description here.
 * Creation date: (26.12.2000 22:31:20)
 * @param g java.awt.Graphics
 */
private void setGraphics(Graphics g) {
	graphics = g;
}
}
