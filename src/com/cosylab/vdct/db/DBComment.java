package com.cosylab.vdct.db;

/**
 * Insert the type's description here.
 * Creation date: (23/8/99 14:06:31)
 * @author: Matej Sekoranja
 */
public class DBComment {
	protected String comment;
/**
 * Insert the method's description here.
 * Creation date: (23/8/99 14:08:34)
 * @return java.lang.String
 */
public String getComment() {
	return comment;
}
/**
 * Insert the method's description here.
 * Creation date: (23/8/99 14:03:42)
 * @param comment java.lang.String
 */
public void setComment(String comment) {
	comment = comment.trim();
	if (!comment.equals("")) this.comment=comment;
}
}
