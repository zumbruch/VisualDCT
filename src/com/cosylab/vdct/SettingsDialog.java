package com.cosylab.vdct;

/**
 * Copyright (c) 2002, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution. 
 * Neither the name of the Cosylab, Ltd., Control System Laboratory nor the names
 * of its contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


/**
 * Insert the type's description here.
 * Creation date: (23.5.2001 15:31:19)
 * @author 
 */
public class SettingsDialog extends javax.swing.JDialog {
	private javax.swing.JButton ivjJButton2 = null;
	private javax.swing.JPanel ivjJDialogContentPane = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JCheckBox ivjGroupingCheckBox = null;
	private javax.swing.JPanel ivjGroupingPanel = null;
	private javax.swing.JLabel ivjGroupingSeparatorLabel = null;
	private javax.swing.JTextField ivjGroupingSeparatorTextField = null;
	private javax.swing.JButton ivjOKButton = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JLabel ivjWarningLabel = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.KeyListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == SettingsDialog.this.getJButton2()) 
				connEtoM2(e);
			if (e.getSource() == SettingsDialog.this.getGroupingCheckBox()) 
				connEtoC1(e);
			if (e.getSource() == SettingsDialog.this.getOKButton()) 
				connEtoC2(e);
		};
		public void keyPressed(java.awt.event.KeyEvent e) {};
		public void keyReleased(java.awt.event.KeyEvent e) {};
		public void keyTyped(java.awt.event.KeyEvent e) {
			if (e.getSource() == SettingsDialog.this.getGroupingSeparatorTextField()) 
				connEtoC3(e);
		};
	};
/**
 * SettingsDialog constructor comment.
 */
public SettingsDialog() {
	super();
	initialize();
}
/**
 * SettingsDialog constructor comment.
 * @param owner java.awt.Frame
 */
public SettingsDialog(java.awt.Frame owner) {
	super(owner);
	initialize();
}
/**
 * SettingsDialog constructor comment.
 * @param owner java.awt.Frame
 * @param modal boolean
 */
public SettingsDialog(java.awt.Frame owner, boolean modal) {
	super(owner, modal);
	initialize();
}
/**
 * connEtoC1:  (GroupingCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> SettingsDialog.groupingCheckBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.groupingCheckBox_ActionPerformed(null);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (OKButton.action.actionPerformed(java.awt.event.ActionEvent) --> SettingsDialog.oKButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.oKButton_ActionPerformed(null);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (GroupingSeparatorTextField.key.keyTyped(java.awt.event.KeyEvent) --> SettingsDialog.groupingSeparatorTextField_KeyTyped(Ljava.awt.event.KeyEvent;)V)
 * @param arg1 java.awt.event.KeyEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.KeyEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.groupingSeparatorTextField_KeyTyped(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM2:  (JButton2.action.actionPerformed(java.awt.event.ActionEvent) --> SettingsDialog.dispose()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.dispose();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP1SetTarget:  (GroupingCheckBox.selected <--> GroupingSeparatorLabel.enabled)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getGroupingSeparatorLabel().setEnabled(getGroupingCheckBox().isSelected());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP2SetTarget:  (GroupingCheckBox.selected <--> GroupingSeparatorTextField.enabled)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		getGroupingSeparatorTextField().setEnabled(getGroupingCheckBox().isSelected());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the GroupingCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getGroupingCheckBox() {
	if (ivjGroupingCheckBox == null) {
		try {
			ivjGroupingCheckBox = new javax.swing.JCheckBox();
			ivjGroupingCheckBox.setName("GroupingCheckBox");
			ivjGroupingCheckBox.setSelected(true);
			ivjGroupingCheckBox.setText("Enable Grouping");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGroupingCheckBox;
}
/**
 * Return the GroupingPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getGroupingPanel() {
	if (ivjGroupingPanel == null) {
		try {
			ivjGroupingPanel = new javax.swing.JPanel();
			ivjGroupingPanel.setName("GroupingPanel");
			ivjGroupingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(" Grouping "));
			ivjGroupingPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsGroupingCheckBox = new java.awt.GridBagConstraints();
			constraintsGroupingCheckBox.gridx = 0; constraintsGroupingCheckBox.gridy = 0;
			constraintsGroupingCheckBox.insets = new java.awt.Insets(4, 14, 4, 4);
			getGroupingPanel().add(getGroupingCheckBox(), constraintsGroupingCheckBox);

			java.awt.GridBagConstraints constraintsGroupingSeparatorLabel = new java.awt.GridBagConstraints();
			constraintsGroupingSeparatorLabel.gridx = 0; constraintsGroupingSeparatorLabel.gridy = 1;
			constraintsGroupingSeparatorLabel.insets = new java.awt.Insets(4, 14, 4, 4);
			getGroupingPanel().add(getGroupingSeparatorLabel(), constraintsGroupingSeparatorLabel);

			java.awt.GridBagConstraints constraintsGroupingSeparatorTextField = new java.awt.GridBagConstraints();
			constraintsGroupingSeparatorTextField.gridx = 1; constraintsGroupingSeparatorTextField.gridy = 1;
			constraintsGroupingSeparatorTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGroupingSeparatorTextField.weightx = 1.0;
			constraintsGroupingSeparatorTextField.ipadx = 3;
			constraintsGroupingSeparatorTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getGroupingPanel().add(getGroupingSeparatorTextField(), constraintsGroupingSeparatorTextField);

			java.awt.GridBagConstraints constraintsWarningLabel = new java.awt.GridBagConstraints();
			constraintsWarningLabel.gridx = 0; constraintsWarningLabel.gridy = 2;
			constraintsWarningLabel.gridwidth = 2;
			constraintsWarningLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsWarningLabel.insets = new java.awt.Insets(14, 14, 4, 14);
			getGroupingPanel().add(getWarningLabel(), constraintsWarningLabel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGroupingPanel;
}
/**
 * Return the GroupingSeparatorLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getGroupingSeparatorLabel() {
	if (ivjGroupingSeparatorLabel == null) {
		try {
			ivjGroupingSeparatorLabel = new javax.swing.JLabel();
			ivjGroupingSeparatorLabel.setName("GroupingSeparatorLabel");
			ivjGroupingSeparatorLabel.setText("Grouping separator:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGroupingSeparatorLabel;
}
/**
 * Return the GroupingSeparatorTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getGroupingSeparatorTextField() {
	if (ivjGroupingSeparatorTextField == null) {
		try {
			ivjGroupingSeparatorTextField = new javax.swing.JTextField();
			ivjGroupingSeparatorTextField.setName("GroupingSeparatorTextField");
			ivjGroupingSeparatorTextField.setOpaque(false);
			ivjGroupingSeparatorTextField.setBorder(javax.swing.BorderFactory.createEmptyBorder());
			ivjGroupingSeparatorTextField.setText(":");
			ivjGroupingSeparatorTextField.setColumns(1);
			ivjGroupingSeparatorTextField.setMargin(new java.awt.Insets(0, 2, 0, 1));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGroupingSeparatorTextField;
}
/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButton2() {
	if (ivjJButton2 == null) {
		try {
			ivjJButton2 = new javax.swing.JButton();
			ivjJButton2.setName("JButton2");
			ivjJButton2.setPreferredSize(new java.awt.Dimension(75, 25));
			ivjJButton2.setText("Cancel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButton2;
}
/**
 * Return the JDialogContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJDialogContentPane() {
	if (ivjJDialogContentPane == null) {
		try {
			ivjJDialogContentPane = new javax.swing.JPanel();
			ivjJDialogContentPane.setName("JDialogContentPane");
			ivjJDialogContentPane.setLayout(new java.awt.BorderLayout());
			getJDialogContentPane().add(getJPanel1(), "South");
			getJDialogContentPane().add(getGroupingPanel(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialogContentPane;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsOKButton = new java.awt.GridBagConstraints();
			constraintsOKButton.gridx = 0; constraintsOKButton.gridy = 0;
			constraintsOKButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getOKButton(), constraintsOKButton);

			java.awt.GridBagConstraints constraintsJButton2 = new java.awt.GridBagConstraints();
			constraintsJButton2.gridx = 1; constraintsJButton2.gridy = 0;
			constraintsJButton2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJButton2(), constraintsJButton2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}
/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getOKButton() {
	if (ivjOKButton == null) {
		try {
			ivjOKButton = new javax.swing.JButton();
			ivjOKButton.setName("OKButton");
			ivjOKButton.setPreferredSize(new java.awt.Dimension(75, 25));
			ivjOKButton.setText("OK");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOKButton;
}
/**
 * Return the WarningLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getWarningLabel() {
	if (ivjWarningLabel == null) {
		try {
			ivjWarningLabel = new javax.swing.JLabel();
			ivjWarningLabel.setName("WarningLabel");
			ivjWarningLabel.setFont(new java.awt.Font("Arial", 1, 10));
			ivjWarningLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
			ivjWarningLabel.setText("Warning: Changing separator char will not reflect changes on preexistings names!");
			ivjWarningLabel.setForeground(java.awt.Color.red);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjWarningLabel;
}
/**
 * Comment
 */
public void groupingCheckBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	boolean status = getGroupingCheckBox().isSelected();
	getGroupingSeparatorLabel().setEnabled(status);
	getGroupingSeparatorTextField().setEnabled(status);
	return;
}
/**
 * Comment
 */
public void groupingSeparatorTextField_KeyTyped(java.awt.event.KeyEvent keyEvent) {
	if ((keyEvent.getKeyChar()!=java.awt.event.KeyEvent.VK_BACK_SPACE && getGroupingSeparatorTextField().getText().length()>=1) &&
		((getGroupingSeparatorTextField().getSelectedText()==null) || (getGroupingSeparatorTextField().getSelectedText().length()==0)))
		keyEvent.setKeyChar('\0');
	return;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	getGroupingCheckBox().setSelected(Constants.GROUP_SEPARATOR!='\0');
	if (Constants.GROUP_SEPARATOR!='\0')
		getGroupingSeparatorTextField().setText(String.valueOf(Constants.GROUP_SEPARATOR));
	else
		getGroupingSeparatorTextField().setText("");
	// user code end
	getJButton2().addActionListener(ivjEventHandler);
	getGroupingCheckBox().addActionListener(ivjEventHandler);
	getOKButton().addActionListener(ivjEventHandler);
	getGroupingSeparatorTextField().addKeyListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("SettingsDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setSize(475, 197);
		setTitle("Visual DCT Settings");
		setContentPane(getJDialogContentPane());
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
		SettingsDialog aSettingsDialog;
		aSettingsDialog = new SettingsDialog();
		aSettingsDialog.setModal(true);
		aSettingsDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aSettingsDialog.show();
		java.awt.Insets insets = aSettingsDialog.getInsets();
		aSettingsDialog.setSize(aSettingsDialog.getWidth() + insets.left + insets.right, aSettingsDialog.getHeight() + insets.top + insets.bottom);
		aSettingsDialog.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JDialog");
		exception.printStackTrace(System.out);
	}
}
/**
 * Comment
 */
public void oKButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	if (getGroupingCheckBox().isSelected() &&
		getGroupingSeparatorTextField().getText().length()>0)
		Constants.GROUP_SEPARATOR = getGroupingSeparatorTextField().getText().charAt(0);
	else
		Constants.GROUP_SEPARATOR = '\0';
	dispose();
}
}
