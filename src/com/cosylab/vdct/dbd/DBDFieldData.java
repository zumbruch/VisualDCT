package com.cosylab.vdct.dbd;

/**
 * This type was created in VisualAge.
 */
public class DBDFieldData {

	private static String nullString = "";
	
	protected String name;	
	protected int GUI_type = DBDConstants.GUI_UNDEFINED;
	protected int field_type = DBDConstants.NOT_DEFINED;
	protected String init_value = nullString;
	protected String prompt_value = nullString; 	

	// for integer fields
	protected int base_type = DBDConstants.DECIMAL;

	// for DBF_STRINGS fields
	protected int size_value;

	// for DBF_MENU fields
	protected String menu_name;
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @return int
 */
public int getBase_type() {
	return base_type;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @return int
 */
public int getField_type() {
	return field_type;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @return int
 */
public int getGUI_type() {
	return GUI_type;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @return java.lang.String
 */
public java.lang.String getInit_value() {
	return init_value;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @return java.lang.String
 */
public java.lang.String getMenu_name() {
	return menu_name;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @return java.lang.String
 */
public java.lang.String getPrompt_value() {
	return prompt_value;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @return int
 */
public int getSize_value() {
	return size_value;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @param newBase_type int
 */
public void setBase_type(int newBase_type) {
	base_type = newBase_type;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @param newField_type int
 */
public void setField_type(int newField_type) {
	field_type = newField_type;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @param newGUI_type int
 */
public void setGUI_type(int newGUI_type) {
	GUI_type = newGUI_type;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @param newInit_value java.lang.String
 */
public void setInit_value(java.lang.String newInit_value) {
	init_value = newInit_value;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @param newMenu_name java.lang.String
 */
public void setMenu_name(java.lang.String newMenu_name) {
	menu_name = newMenu_name;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	name = newName;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:01)
 * @param newPrompt_value java.lang.String
 */
public void setPrompt_value(java.lang.String newPrompt_value) {
	prompt_value = newPrompt_value;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:25:02)
 * @param newSize_value int
 */
public void setSize_value(int newSize_value) {
	size_value = newSize_value;
}
}
