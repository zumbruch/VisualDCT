package com.cosylab.vdct.vdb;

import com.cosylab.vdct.util.StringUtils;

/**
 * Insert the type's description here.
 * Creation date: (26.1.2001 15:51:20)
 * @author: 
 */
public class CommentProperty implements com.cosylab.vdct.inspector.InspectableProperty {
	VDBRecordData record;
	private static String helpString = "\""+com.cosylab.vdct.db.DBConstants.commentString+"\" will be added automatically";
/**
 * CommentProperty constructor comment.
 */
public CommentProperty(VDBRecordData record) {
	this.record=record;
}
/**
 * Insert the method's description here.
 * Creation date: (24/8/99 15:29:04)
 * @return java.lang.String
 * @param str java.lang.String
 */
private String addCommentChars(String str) {
	if (str==null || str.length()==0) return str;

	final char eofChar = '\n';
	final String space = " ";
	StringBuffer output = new StringBuffer("");
	int pos;
	
	str = StringUtils.removeBegining(str.trim(), com.cosylab.vdct.db.DBConstants.commentString).trim();
	
	pos = str.indexOf(eofChar);
	while (pos>=0) {
		output.append(com.cosylab.vdct.db.DBConstants.commentString).append(space).append(StringUtils.removeBegining(str.substring(0, pos).trim(), com.cosylab.vdct.db.DBConstants.commentString).trim()).append(eofChar);
		str = str.substring(pos+1);
		pos = str.indexOf(eofChar);
	}

	str = com.cosylab.vdct.db.DBConstants.commentString+space+StringUtils.removeBegining(str.trim(), com.cosylab.vdct.db.DBConstants.commentString).trim();
	output.append(str);
	
	return output.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 15:51:20)
 * @return boolean
 */
public boolean allowsOtherValues() {
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 15:51:20)
 * @return java.lang.String
 */
public String getHelp() {
	return helpString;
}
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 15:51:20)
 * @return java.lang.String
 */
public String getName() {
	return record.getName()+".comment";
}
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 15:51:20)
 * @return java.lang.String[]
 */
public java.lang.String[] getSelectableValues() {
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 15:51:20)
 * @return java.lang.String
 */
public String getValue() {
	return removeCommentChars(record.getComment());
}
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 15:51:20)
 * @return boolean
 */
public boolean isEditable() {
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 15:51:20)
 * @return boolean
 */
public boolean isSepatator() {
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (24/8/99 15:29:04)
 * @return java.lang.String
 * @param str java.lang.String
 */
private String removeCommentChars(String str) {
	if (str==null || str.length()==0) return str;

	final char eofChar = '\n';
	StringBuffer output = new StringBuffer("");
	int pos;
	
	str = StringUtils.removeBegining(str.trim(), com.cosylab.vdct.db.DBConstants.commentString).trim();
	
	pos = str.indexOf(eofChar);
	while (pos>=0) {
		output.append(StringUtils.removeBegining(str.substring(0, pos).trim(), com.cosylab.vdct.db.DBConstants.commentString).trim()).append(eofChar);
		str = str.substring(pos+1);
		pos = str.indexOf(eofChar);
	}

	str = StringUtils.removeBegining(str.trim(), com.cosylab.vdct.db.DBConstants.commentString).trim();
	output.append(str);
	
	return output.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 15:51:20)
 * @param value java.lang.String
 */
public void setValue(String value) {
	String newValue = addCommentChars(value);

	if ((record.getComment()==null) || !record.getComment().equals(newValue))
		com.cosylab.vdct.undo.UndoManager.getInstance().addAction(
			new com.cosylab.vdct.undo.CommentChangeAction(this, record.getComment(), newValue)
		);

	record.setComment(newValue);
}
}
