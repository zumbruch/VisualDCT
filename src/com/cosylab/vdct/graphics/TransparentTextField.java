package com.cosylab.vdct.graphics;

/**
 * Insert the type's description here.
 * Creation date: (2.5.2001 8:25:44)
 * @author: 
 */
public class TransparentTextField extends javax.swing.JTextField {
	private com.cosylab.vdct.graphics.objects.VisibleObject owner = null;
	private int offsetX;
	private int offsetY;
/**
 * TransparentTextField constructor comment.
 */
public TransparentTextField() {
	super();
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (2.5.2001 9:47:36)
 * @param owner com.cosylab.vdct.graphics.objects.VisibleObject
 */
public TransparentTextField(com.cosylab.vdct.graphics.objects.VisibleObject owner, int offsetX, int offsetY)
{
	super();
	setOwner(owner);
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (2.5.2001 9:51:02)
 * @return int
 */
public int getOffsetX() {
	return offsetX;
}
/**
 * Insert the method's description here.
 * Creation date: (2.5.2001 9:51:02)
 * @return int
 */
public int getOffsetY() {
	return offsetY;
}
/**
 * Insert the method's description here.
 * Creation date: (2.5.2001 9:47:11)
 * @return com.cosylab.vdct.graphics.objects.VisibleObject
 */
public com.cosylab.vdct.graphics.objects.VisibleObject getOwner() {
	return owner;
}
/**
 * Insert the method's description here.
 * Creation date: (2.5.2001 9:48:17)
 */
public int getX() {
	ViewState view = ViewState.getInstance();
	return (int)((owner.getX()+offsetX)*view.getScale()-view.getRx());
}
/**
 * Insert the method's description here.
 * Creation date: (2.5.2001 9:48:53)
 * @return int
 */
public int getY() {
	ViewState view = ViewState.getInstance();
	return (int)((owner.getY()+offsetY)*view.getScale()-view.getRy());
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	com.cosylab.vdct.Console.getInstance().println(exception);
	exception.printStackTrace(System.out);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("TransparentTextField");
		setOpaque(false);
		setSize(100, 20);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		TransparentTextField aTransparentTextField;
		aTransparentTextField = new TransparentTextField();
		frame.setContentPane(aTransparentTextField);
		frame.setSize(aTransparentTextField.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cosylab.vdct.graphics.TransparentTextField");
		exception.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2.5.2001 9:51:02)
 * @param newOffsetX int
 */
public void setOffsetX(int newOffsetX) {
	offsetX = newOffsetX;
}
/**
 * Insert the method's description here.
 * Creation date: (2.5.2001 9:51:02)
 * @param newOffsetY int
 */
public void setOffsetY(int newOffsetY) {
	offsetY = newOffsetY;
}
/**
 * Insert the method's description here.
 * Creation date: (2.5.2001 9:47:11)
 * @param newOwner com.cosylab.vdct.graphics.objects.VisibleObject
 */
public void setOwner(com.cosylab.vdct.graphics.objects.VisibleObject newOwner) {
	owner = newOwner;
}
}
