package com.cosylab.vdct.db;

/**
 * Insert the type's description here.
 * Creation date: (23.4.2001 17:29:39)
 * @author: 
 */
public class DBConnectorData {
	protected String connectorID;
	protected String targetID;
	protected int x;
	protected int y;
	protected java.awt.Color color;
	protected String description;
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 17:31:15)
 * @param id java.lang.String
 * @param x int
 * @param y int
 * @param color java.awt.Color
 * @param description java.lang.String
 */
public DBConnectorData(String id, String targetID, int x, int y, java.awt.Color color, String description) {
	this.connectorID = id;
	this.targetID=targetID;
	this.x=x; this.y=y;
	this.color=color;
	this.description=description;
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 17:30:42)
 * @return java.awt.Color
 */
public java.awt.Color getColor() {
	return color;
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 17:30:42)
 * @return java.lang.String
 */
public java.lang.String getConnectorID() {
	return connectorID;
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 17:30:42)
 * @return java.lang.String
 */
public java.lang.String getDescription() {
	return description;
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 21:10:55)
 * @return java.lang.String
 */
public java.lang.String getTargetID() {
	return targetID;
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 17:30:42)
 * @return int
 */
public int getX() {
	return x;
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 17:30:42)
 * @return int
 */
public int getY() {
	return y;
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 17:30:42)
 * @param newColor java.awt.Color
 */
public void setColor(java.awt.Color newColor) {
	color = newColor;
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 17:30:42)
 * @param newConnectorID java.lang.String
 */
public void setConnectorID(java.lang.String newConnectorID) {
	connectorID = newConnectorID;
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 17:30:42)
 * @param newDescription java.lang.String
 */
public void setDescription(java.lang.String newDescription) {
	description = newDescription;
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 21:10:55)
 * @param newTargetID java.lang.String
 */
public void setTargetID(java.lang.String newTargetID) {
	targetID = newTargetID;
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 17:30:42)
 * @param newX int
 */
public void setX(int newX) {
	x = newX;
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 17:30:42)
 * @param newY int
 */
public void setY(int newY) {
	y = newY;
}
}
