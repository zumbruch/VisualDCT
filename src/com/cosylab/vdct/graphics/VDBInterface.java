package com.cosylab.vdct.graphics;

/**
 * Insert the type's description here.
 * Creation date: (4.2.2001 15:36:01)
 * @author: 
 */
public interface VDBInterface {
/**
 * Returns error message or null if OK
 * Creation date: (3.2.2001 22:11:01)
 * @return java.lang.String
 * @param name java.lang.String
 */
java.lang.String checkGroupName(String name, boolean relative);
/**
 * Returns error message or null if OK
 * Creation date: (3.2.2001 22:11:01)
 * @return java.lang.String
 * @param name java.lang.String
 */
java.lang.String checkRecordName(String name, boolean relative);
/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 23:27:30)
 * @param name java.lang.String
 * @param type java.lang.String
 * @param relative boolean
 */
void createRecord(String name, String type, boolean relative);
}
