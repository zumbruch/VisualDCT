package com.cosylab.vdct.vdb;

/**
 * Insert the type's description here.
 * Creation date: (3.2.2001 21:58:24)
 * @author: matej Sekoranja
 */
public interface NameValidator {
/**
 * Return error message or null if OK
 * Creation date: (3.2.2001 22:03:24)
 * @return java.lang.String
 * @param name java.lang.String
 * @param relative boolean
 */
String checkGroupName(String name, boolean relative);
/**
 * Return error message or null if OK
 * Creation date: (3.2.2001 22:02:52)
 * @return java.lang.String
 * @param name java.lang.String
 * @param relative boolean
 */
String checkRecordName(String name, boolean relative);
}
