package com.cosylab.vdct.graphics.printing;

import java.awt.print.*;

/**
 * Insert the type's description here.
 * Creation date: (13.5.2001 12:33:52)
 * @author: 
 */
public class Page {
	public final static int TRUE_SCALE = 0; 
	public final static int USER_SCALE = 1;
	public final static int FIT_SCALE = 2;

	private static int printMode = FIT_SCALE;

	private static PageFormat pageFormat = PrinterJob.getPrinterJob().defaultPage();

	private static double userScale = 1.0;

/**
 * Page constructor comment.
 */
public Page() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (13.5.2001 12:35:50)
 * @return java.awt.print.PageFormat
 */
public static java.awt.print.PageFormat getPageFormat() {
	return pageFormat;
}
/**
 * Insert the method's description here.
 * Creation date: (13.5.2001 17:45:23)
 * @return int
 */
public static int getPrintMode() {
	return printMode;
}
/**
 * Insert the method's description here.
 * Creation date: (13.5.2001 17:50:21)
 * @return double
 */
public static double getUserScale() {
	return userScale;
}
/**
 * Insert the method's description here.
 * Creation date: (13.5.2001 12:35:50)
 * @param newPageFormat java.awt.print.PageFormat
 */
public static void setPageFormat(java.awt.print.PageFormat newPageFormat) {
	pageFormat = newPageFormat;
}
/**
 * Insert the method's description here.
 * Creation date: (13.5.2001 17:45:23)
 * @param newPrintMode int
 */
public static void setPrintMode(int newPrintMode) {
	printMode = newPrintMode;
}
/**
 * Insert the method's description here.
 * Creation date: (13.5.2001 17:50:21)
 * @param newUserScale double
 */
public static void setUserScale(double newUserScale) {
	userScale = newUserScale;
}
}
