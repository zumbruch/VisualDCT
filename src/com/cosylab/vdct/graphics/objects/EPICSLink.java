package com.cosylab.vdct.graphics.objects;

import com.cosylab.vdct.Constants;

/**
 * Insert the type's description here.
 * Creation date: (29.1.2001 21:23:04)
 * @author: Matej Sekoranja 
 */
public abstract class EPICSLink extends Field implements Descriptable, Linkable, Rotatable {
	protected int r = Constants.LINK_RADIOUS;
	protected boolean disconnected = false;
	protected int rtailLen = Constants.TAIL_LENGTH;
	private boolean right = true;
/**
 * EPICSLink constructor comment.
 * @param parent com.cosylab.vdct.graphics.objects.ContainerObject
 * @param fieldData com.cosylab.vdct.vdb.VDBFieldData
 */
public EPICSLink(ContainerObject parent, com.cosylab.vdct.vdb.VDBFieldData fieldData) {
	super(parent, fieldData);
	setColor(Constants.FRAME_COLOR);
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 19:09:33)
 */
public void destroy() {
	if (!isDestroyed()) {
		super.destroy();
		disconnected = true;
		((Hub)getParent()).removeLink(this);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 21:23:04)
 */
public void disconnect(Linkable disconnector) {
	disconnected=true;
}
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 12:07:15)
 * @return java.lang.String
 */
public String getDescription() {
	return getFieldData().getFullName();
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 20:36:36)
 * @return java.lang.String
 */
public String getID() {
	return fieldData.getFullName();
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 21:23:04)
 * @return java.lang.String
 */
public String getLayerID() {
	return getParent().getParent().toString();
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 21:23:04)
 * @return boolean
 */
public boolean isConnectable() {
	return !disconnected;
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 21:23:04)
 * @return boolean
 */
public boolean isDisconnected() {
	return disconnected;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 16:58:58)
 * @return boolean
 */
public boolean isRight() {
	return right;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 16:58:58)
 */
public void rotate() { right=!right; }
/**
 * Insert the method's description here.
 * Creation date: (24.4.2001 17:40:45)
 * @param description java.lang.String
 */
public void setDescription(String description) {}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 21:23:04)
 * @param id java.lang.String
 */
public void setLayerID(String id) {
	// not needed, id is retrieved dynamicaly via parent	
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 16:58:58)
 * @param state boolean
 */
public void setRight(boolean state) { right=state; }
/**
 * Insert the method's description here.
 * Creation date: (31.1.2001 18:49:28)
 */
public void validate() {
	super.validate();
	r = (int)(getRscale()*Constants.LINK_RADIOUS);
	rtailLen = (int)(getRscale()*Constants.TAIL_LENGTH);
}
}
