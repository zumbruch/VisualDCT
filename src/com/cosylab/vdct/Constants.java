package com.cosylab.vdct;

import java.awt.Color;

/**
 * Insert the type's description here.
 * Creation date: (21.12.2000 20:45:10)
 * @author: Matej Sekoranja
 */
public class Constants {

    public final static int VDCT_WIDTH = 1000;
    public final static int VDCT_HEIGHT = 770;

    public final static String COPY_SUFFIX = "_copy";
    public final static String MOVE_SUFFIX = "_2";

    public /*final*/
    static char GROUP_SEPARATOR = ':';
    public final static char FIELD_SEPARATOR = '.';
    public final static String HARDWARE_LINK = "#";

    public final static String UNTITLED = "Untitled";
    public final static String MAIN_GROUP = "Main group";

    public final static String NONE = "<none>";
    public final static String CLIPBOARD_NAME = "<clipboard>";

    public final static String CONFIG_DIR = "config/";
    public final static String DTD_DIR = CONFIG_DIR + "dtd/";

    // in future setting will become XML
    //public final static String SETTINGS_FILE = "VisualDCT.xml";
    public final static String SETTINGS_FILE = "VisualDCT.set";

    public final static int MAX_RECORD_NAME_LENGTH = 29;
    public final static int MAX_GROUP_NAME_LENGTH = MAX_RECORD_NAME_LENGTH - 2;

    public final static int UNDO_STEPS_TO_REMEMBER = 25;

    public final static int VIRTUAL_WIDTH = 5000;
    public final static int VIRTUAL_HEIGHT = 5000;

    public final static int GRID_SIZE = 20;

    public static final int RECORD_WIDTH = 160;
    public static final int RECORD_HEIGHT = 45;

    public static final int FIELD_WIDTH = RECORD_WIDTH - 30;
    public static final int FIELD_HEIGHT = 20;

    public static final int GROUP_FIELD_WIDTH = RECORD_WIDTH - 30;
    public static final int GROUP_FIELD_HEIGHT = 20;

    public static final int GROUP_WIDTH = 200;
    public static final int GROUP_HEIGHT = 120;

    public static final int CONNECTOR_WIDTH = 6;
    public static final int CONNECTOR_HEIGHT = 6;
    public static final int LINK_RADIOUS = 5;

    public static final int TAIL_LENGTH = 50 + 3 * LINK_RADIOUS;
    public static final int LINK_LABEL_LENGTH = TAIL_LENGTH - 3 * LINK_RADIOUS;

    public static final String DEFAULT_FONT = "sansserif";

    public final static Color BACKGROUND_COLOR = Color.white;
    public final static Color PICK_COLOR = Color.pink;
    public final static Color FRAME_COLOR = Color.black;
    public final static Color HILITE_COLOR = Color.red;
    public final static Color LINE_COLOR = Color.black;
    public final static Color RECORD_COLOR = Color.white;
    public final static Color SELECTION_COLOR = Color.pink;
    public final static Color LINK_COLOR = Color.white;

    public final static Color GRID_COLOR = Color.lightGray;

}
