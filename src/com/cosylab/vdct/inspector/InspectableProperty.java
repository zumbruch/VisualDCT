package com.cosylab.vdct.inspector;

/**
 * Insert the type's description here.
 * Creation date: (11.1.2001 21:28:00)
 * @author: Matej Sekoranja 
 */
public interface InspectableProperty {
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:34:58)
 * @return boolean
 */
public boolean allowsOtherValues();
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 15:01:28)
 * @return java.lang.String
 */
public String getHelp();
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:54:12)
 * @return java.lang.String
 */
public String getName();
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:36:10)
 * @return java.lang.String[]
 */
public String[] getSelectableValues();
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:29:48)
 * @return java.lang.String
 */
public String getValue();
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:28:51)
 * @return boolean
 */
public boolean isEditable();
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:44:32)
 * @return boolean
 */
public boolean isSepatator();
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:30:04)
 * @param value java.lang.String
 */
public void setValue(String value);
}
