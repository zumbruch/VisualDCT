package com.cosylab.vdct;

/**
 * Insert the type's description here.
 * Creation date: (3.5.2001 21:25:30)
 * @author: 
 */
public class Console extends javax.swing.JFrame {

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == Console.this.getClearButton()) 
				connEtoM1(e);
		};
	}
	protected static Console instance = null;
	private javax.swing.JButton ivjClearButton = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JPanel ivjJInternalFrameContentPane = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JTextArea ivjTextPane = null;
/**
 * Console constructor comment.
 */
public Console() {
	super();
	initialize();
}
/**
 * connEtoM1:  (ClearButton.action.actionPerformed(java.awt.event.ActionEvent) --> JTextArea1.text)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getTextPane().setText("");
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (20.07.99 14:37:16)
 */
public void flush() {
	getTextPane().setText("");
}
/**
 * Return the ClearButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getClearButton() {
	if (ivjClearButton == null) {
		try {
			ivjClearButton = new javax.swing.JButton();
			ivjClearButton.setName("ClearButton");
			ivjClearButton.setText("Clear");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjClearButton;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:39:46)
 * @return com.cosylab.vdct.Console
 */
public static Console getInstance() {
	if (instance==null)
		instance = new Console();
	return instance;
}
/**
 * Return the JInternalFrameContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJInternalFrameContentPane() {
	if (ivjJInternalFrameContentPane == null) {
		try {
			ivjJInternalFrameContentPane = new javax.swing.JPanel();
			ivjJInternalFrameContentPane.setName("JInternalFrameContentPane");
			ivjJInternalFrameContentPane.setLayout(new java.awt.BorderLayout());
			getJInternalFrameContentPane().add(getClearButton(), "South");
			getJInternalFrameContentPane().add(getJScrollPane1(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJInternalFrameContentPane;
}
/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			getJScrollPane1().setViewportView(getTextPane());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}
/**
 * Return the JTextArea1 property value.
 * @return javax.swing.JTextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextArea getTextPane() {
	if (ivjTextPane == null) {
		try {
			ivjTextPane = new javax.swing.JTextArea();
			ivjTextPane.setName("TextPane");
			ivjTextPane.setBackground(new java.awt.Color(255,255,225));
			ivjTextPane.setBounds(0, 0, 160, 120);
			ivjTextPane.setMargin(new java.awt.Insets(10, 10, 10, 10));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTextPane;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getClearButton().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		setLocation(100, 100);
		// user code end
		setName("Console");
		setTitle("VisualDCT Console");
		setSize(764, 270);
		setResizable(true);
		setContentPane(getJInternalFrameContentPane());
		initConnections();
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
		Console aConsole;
		aConsole = new Console();
		aConsole.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aConsole.show();
		java.awt.Insets insets = aConsole.getInsets();
		aConsole.setSize(aConsole.getWidth() + insets.left + insets.right, aConsole.getHeight() + insets.top + insets.bottom);
		aConsole.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JFrame");
		exception.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (20.07.99 14:15:05)
 * @param text java.lang.String
 */
public void print(String text) {
	getTextPane().append(text);
	if (!isVisible()) setVisible(true);
}
/**
 * Insert the method's description here.
 * Creation date: (20.07.99 14:22:21)
 */
public void println() {
	getTextPane().append("\n");
	if (!isVisible()) setVisible(true);
}
/**
 * Insert the method's description here.
 * Creation date: (20.07.99 14:15:05)
 * @param text java.lang.String
 */
public void println(String text) {
	getTextPane().append(text+"\n");
	if (!isVisible()) setVisible(true);
}
/**
 * Insert the method's description here.
 * Creation date: (20.07.99 14:24:41)
 * @param thr java.lang.Throwable
 */
public void println(Throwable thr) {
	getTextPane().append(thr.toString()+"\n\n");
	
	// !!! some debug info in development phase
	System.err.println();
	thr.printStackTrace();
	System.err.println();
	
	if (!isVisible()) setVisible(true);
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 21:19:09)
 * @param string java.lang.String
 */
public void silent(String string) {
	getTextPane().append(string);
}
}
