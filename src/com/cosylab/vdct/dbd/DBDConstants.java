package com.cosylab.vdct.dbd;

/**
 * This type was created in VisualAge.
 */
public interface DBDConstants {
	public static final int NOT_DEFINED	= -1;
	public static final int DBF_STRING 	=  0;
	public static final int DBF_CHAR	=  1;
	public static final int DBF_UCHAR	=  2;
	public static final int DBF_SHORT	=  3;
	public static final int DBF_USHORT	=  4;
	public static final int DBF_LONG	=  5;
	public static final int DBF_ULONG	=  6;
	public static final int DBF_FLOAT	=  7;
	public static final int DBF_DOUBLE	=  8;
	public static final int DBF_ENUM	=  9;
	public static final int DBF_MENU	= 10;
	public static final int DBF_DEVICE	= 11;
	public static final int DBF_INLINK	= 12;
	public static final int DBF_OUTLINK	= 13;
	public static final int DBF_FWDLINK	= 14;
	public static final int DBF_NOACCESS	= 15;


	
	public static final int DECIMAL	= 0;
	public static final int HEX		= 1;
	

	
	// lower number means higer pos. in property-window
	public static final int GUI_UNDEFINED = Integer.MAX_VALUE;
	
	public static final int GUI_COMMON	 =  0;
	public static final int GUI_LINKS	 =  1;
	public static final int GUI_INPUTS	 =  2;
	public static final int GUI_OUTPUT	 =  3;
	public static final int GUI_SCAN	 =  4;
	public static final int GUI_ALARMS 	 =  5;
	public static final int GUI_DISPLAY  =  6;
	public static final int GUI_BITS1	 =  7;
	public static final int GUI_BITS2 	 =  8;
	public static final int GUI_CALC	 =  9;
	public static final int GUI_CLOCK	 = 10;
	public static final int GUI_COMPRESS = 11;
	public static final int GUI_CONVERT  = 12;
	public static final int GUI_HIST	 = 13;
	public static final int GUI_MBB		 = 14;
	public static final int GUI_MOTOR	 = 15;
	public static final int GUI_PID		 = 16;
	public static final int GUI_PULSE	 = 17;
	public static final int GUI_SELECT	 = 18;
	public static final int GUI_SEQ1	 = 19;
	public static final int GUI_SEQ2	 = 20;
	public static final int GUI_SEQ3	 = 21;
	public static final int GUI_SUB	 	 = 22;
	public static final int GUI_TIMER	 = 23;
	public static final int GUI_WAVE	 = 24;

	public static final char quoteChar = '"';
}
