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

import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import com.cosylab.vdct.dbd.DBDData;
import com.cosylab.vdct.events.CommandManager;
import com.cosylab.vdct.events.commands.GetGUIInterface;
import com.cosylab.vdct.util.UniversalFileFilter;

/**
 * Insert the type's description here.
 * Creation date: (7/17/2002 3:21:44 PM)
 * @author: 
 */
public class DBDDialog extends JDialog {

class CellRenderer extends DefaultTableCellRenderer {
	
	public Component getTableCellRendererComponent(JTable table, Object value,
					  boolean isSelected, boolean hasFocus, int row, int column) {
						  
		Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		java.io.File file = (java.io.File)value;
		((JLabel)component).setText(file.getAbsolutePath());
		if (!file.exists() || file.isDirectory())
			component.setBackground(Color.red);
		else
			component.setBackground(table.getBackground());
		return component;
	}
}

public class TableModel extends AbstractTableModel {

	// table header
	private final String[] columnNames = { "Loaded Database Definition File(s)" };

	// table classes
	private final Class[] columnClasses = { java.io.File.class };
	
/**
 * Insert the method's description here.
 * Creation date: (14.11.1999 15:22:35)
 * @return java.lang.Class
 * @param col int
 */
public Class getColumnClass(int col) {
	return columnClasses[col];
}

/**
 * getColumnCount method comment.
 */
public int getColumnCount() {
	return columnClasses.length;
}

public String getColumnName(int column) {
	return columnNames[column];
}

/**
 * getRowCount method comment.
 * @return the number of rows in the model.
 */

public int getRowCount() {
	return DataProvider.getInstance().getDBDs().size();
}

/**
 * Returns an attribute value for the cell at <I>row</I>
 * and <I>column</I>.
 *
 * @param   row             the row whose value is to be looked up
 * @param   column          the column whose value is to be looked up
 * @return                  the value Object at the specified cell
 */
 
public Object getValueAt(int row, int column) {
	return DataProvider.getInstance().getDBDs().get(row);
}

/**
 * Insert the method's description here.
 * Creation date: (14.11.1999 15:23:25)
 * @return boolean
 * @param row int
 * @param col int
 */
public boolean isCellEditable(int row, int col) {
	return true;
}

/**
 * Sets the object value for the cell at <I>column</I> and
 * <I>row</I>.  <I>aValue</I> is the new value.  This method
 * will generate a tableChanged() notification.
 *
 * @param   aValue          the new value.  This can be null.
 * @param   row             the row whose value is to be looked up
 * @param   column          the column whose value is to be looked up
 * @return                  the value Object at the specified cell
 */

public void setValueAt(Object aValue, int row, int column) {

	java.util.Vector dbds = DataProvider.getInstance().getDBDs();

	// remove it
	if (aValue==null || dbds.contains(aValue))
	{
		dbds.removeElementAt(row);
		//update entire table
		getScrollPaneTable().tableChanged(new TableModelEvent(getScrollPaneTable().getModel()));
		return;
	}


	Object oldValue = dbds.get(row);

	// no change
	if (aValue.equals(oldValue))
		return;
	
	dbds.setElementAt(aValue, row);
	
	File f = (File)aValue;
	if (f.exists())
	{
	    GetGUIInterface cmd = (GetGUIInterface)CommandManager.getInstance().getCommand("GetGUIMenuInterface");
	    try {
  		 	cmd.getGUIMenuInterface().importDBD(f);
	    } catch (java.io.IOException e) {
		    Console.getInstance().println();
		    Console.getInstance().println("o) Failed to load DBD file: '"+f.getAbsolutePath()+"'.");
		    Console.getInstance().println(e);
	    }
	}
	
	// generate notification
	fireTableCellUpdated(row, column);
}
}
	private JButton ivjAddDBDButton = null;
	private JPanel ivjButtonPanel = null;
	private JButton ivjCloseButton = null;
	private JPanel ivjJDialogContentPane = null;
	private JPanel ivjListPanel = null;
	private JButton ivjRemoveDBDButton = null;
	private JScrollPane ivjScrollPane = null;
	private JTable ivjScrollPaneTable = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == DBDDialog.this.getCloseButton()) 
				connEtoM1(e);
			if (e.getSource() == DBDDialog.this.getRemoveDBDButton()) 
				connEtoC1(e);
			if (e.getSource() == DBDDialog.this.getAddDBDButton()) 
				connEtoC2(e);
		};
	};
/**
 * DBDDialog constructor comment.
 */
public DBDDialog() {
	super();
	initialize();
}
/**
 * DBDDialog constructor comment.
 * @param owner java.awt.Frame
 */
public DBDDialog(java.awt.Frame owner) {
	super(owner);
	initialize();
}
/**
 * DBDDialog constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 */
public DBDDialog(java.awt.Frame owner, String title) {
	super(owner, title);
}
/**
 * DBDDialog constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 * @param modal boolean
 */
public DBDDialog(java.awt.Frame owner, String title, boolean modal) {
	super(owner, title, modal);
}
/**
 * DBDDialog constructor comment.
 * @param owner java.awt.Frame
 * @param modal boolean
 */
public DBDDialog(java.awt.Frame owner, boolean modal) {
	super(owner, modal);
}
/**
 * Comment
 */
public void addDBDButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	JFileChooser chooser = ((VisualDCT)getParent()).getfileChooser();
	UniversalFileFilter filter = new UniversalFileFilter(
		new String("dbd"), "DBD File");
	chooser.resetChoosableFileFilters();
	chooser.addChoosableFileFilter(filter);
	chooser.setDialogTitle("Import DBD");
	chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	int retval = chooser.showOpenDialog(this);

	if(retval == JFileChooser.APPROVE_OPTION) {
	    java.io.File theFile = chooser.getSelectedFile();
	    if(theFile != null) {
		    
		    // check if already exists
		    java.util.Vector dbds = DataProvider.getInstance().getDBDs();
		    if (dbds.contains(theFile))
			{
			    Console.getInstance().println();
			    Console.getInstance().println("o) Failed to import DBD file: '"+theFile.getAbsolutePath()+"' is already loaded.");
			    Console.getInstance().println();
				return;
			}
			
		    GetGUIInterface cmd = (GetGUIInterface)CommandManager.getInstance().getCommand("GetGUIMenuInterface");
		    try {
	  		 	cmd.getGUIMenuInterface().importDBD(theFile);
				getScrollPaneTable().tableChanged(new TableModelEvent(getScrollPaneTable().getModel()));
		    } catch (java.io.IOException e) {
			    Console.getInstance().println();
			    Console.getInstance().println("o) Failed to import DBD file: '"+theFile.getAbsolutePath()+"'.");
			    Console.getInstance().println(e);
		    }
		}
	}
}
/**
 * connEtoC1:  (RemoveDBDButton.action.actionPerformed(java.awt.event.ActionEvent) --> DBDDialog.removeDBDButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.removeDBDButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (AddDBDButton.action.actionPerformed(java.awt.event.ActionEvent) --> DBDDialog.addDBDButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.addDBDButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM1:  (CloseButton.action.actionPerformed(java.awt.event.ActionEvent) --> DBDDialog.dispose()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.awt.event.ActionEvent arg1) {
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
 * Return the AddDBDButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getAddDBDButton() {
	if (ivjAddDBDButton == null) {
		try {
			ivjAddDBDButton = new javax.swing.JButton();
			ivjAddDBDButton.setName("AddDBDButton");
			ivjAddDBDButton.setText("Add...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddDBDButton;
}
/**
 * Return the ButtonPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getButtonPanel() {
	if (ivjButtonPanel == null) {
		try {
			ivjButtonPanel = new javax.swing.JPanel();
			ivjButtonPanel.setName("ButtonPanel");
			ivjButtonPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsAddDBDButton = new java.awt.GridBagConstraints();
			constraintsAddDBDButton.gridx = 0; constraintsAddDBDButton.gridy = 0;
			constraintsAddDBDButton.anchor = java.awt.GridBagConstraints.EAST;
			constraintsAddDBDButton.weightx = 1.0;
			constraintsAddDBDButton.ipadx = 16;
			constraintsAddDBDButton.insets = new java.awt.Insets(4, 4, 10, 4);
			getButtonPanel().add(getAddDBDButton(), constraintsAddDBDButton);

			java.awt.GridBagConstraints constraintsRemoveDBDButton = new java.awt.GridBagConstraints();
			constraintsRemoveDBDButton.gridx = 1; constraintsRemoveDBDButton.gridy = 0;
			constraintsRemoveDBDButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRemoveDBDButton.weightx = 1.0;
			constraintsRemoveDBDButton.insets = new java.awt.Insets(4, 4, 10, 4);
			getButtonPanel().add(getRemoveDBDButton(), constraintsRemoveDBDButton);

			java.awt.GridBagConstraints constraintsCloseButton = new java.awt.GridBagConstraints();
			constraintsCloseButton.gridx = 2; constraintsCloseButton.gridy = 0;
			constraintsCloseButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCloseButton.weightx = 1.0;
			constraintsCloseButton.ipadx = 10;
			constraintsCloseButton.insets = new java.awt.Insets(4, 4, 10, 4);
			getButtonPanel().add(getCloseButton(), constraintsCloseButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonPanel;
}
/**
 * Return the CloseButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getCloseButton() {
	if (ivjCloseButton == null) {
		try {
			ivjCloseButton = new javax.swing.JButton();
			ivjCloseButton.setName("CloseButton");
			ivjCloseButton.setAlignmentX(java.awt.Component.RIGHT_ALIGNMENT);
			ivjCloseButton.setText("Close");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCloseButton;
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
			getJDialogContentPane().add(getButtonPanel(), "South");
			getJDialogContentPane().add(getListPanel(), "Center");
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
 * Return the ListPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getListPanel() {
	if (ivjListPanel == null) {
		try {
			ivjListPanel = new javax.swing.JPanel();
			ivjListPanel.setName("ListPanel");
			ivjListPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsScrollPane = new java.awt.GridBagConstraints();
			constraintsScrollPane.gridx = 1; constraintsScrollPane.gridy = 1;
			constraintsScrollPane.fill = java.awt.GridBagConstraints.BOTH;
			constraintsScrollPane.weightx = 1.0;
			constraintsScrollPane.weighty = 1.0;
			constraintsScrollPane.insets = new java.awt.Insets(10, 10, 10, 10);
			getListPanel().add(getScrollPane(), constraintsScrollPane);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjListPanel;
}
/**
 * Return the RemoveDBDButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getRemoveDBDButton() {
	if (ivjRemoveDBDButton == null) {
		try {
			ivjRemoveDBDButton = new javax.swing.JButton();
			ivjRemoveDBDButton.setName("RemoveDBDButton");
			ivjRemoveDBDButton.setText("Remove");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRemoveDBDButton;
}
/**
 * Return the ScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getScrollPane() {
	if (ivjScrollPane == null) {
		try {
			ivjScrollPane = new javax.swing.JScrollPane();
			ivjScrollPane.setName("ScrollPane");
			ivjScrollPane.setAutoscrolls(true);
			ivjScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			getScrollPane().setViewportView(getScrollPaneTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScrollPane;
}
/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new javax.swing.JTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
			getScrollPane().setColumnHeaderView(ivjScrollPaneTable.getTableHeader());
			getScrollPane().getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
			ivjScrollPaneTable.setBounds(0, 0, 200, 200);
			// user code begin {1}
			ivjScrollPaneTable.setDefaultRenderer(java.io.File.class, new DBDDialog.CellRenderer());
			ivjScrollPaneTable.setModel(new DBDDialog.TableModel());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable;
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
	// user code end
	getCloseButton().addActionListener(ivjEventHandler);
	getRemoveDBDButton().addActionListener(ivjEventHandler);
	getAddDBDButton().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DBDDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setSize(605, 256);
		setTitle("Import DBD...");
		setContentPane(getJDialogContentPane());
		initConnections();
		setModal(true);
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
		DBDDialog aDBDDialog;
		aDBDDialog = new DBDDialog();
		aDBDDialog.setModal(true);
		aDBDDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aDBDDialog.show();
		java.awt.Insets insets = aDBDDialog.getInsets();
		aDBDDialog.setSize(aDBDDialog.getWidth() + insets.left + insets.right, aDBDDialog.getHeight() + insets.top + insets.bottom);
		aDBDDialog.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JDialog");
		exception.printStackTrace(System.out);
	}
}
/**
 * Comment
 */
public void removeDBDButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

	java.util.Vector dbds = DataProvider.getInstance().getDBDs();

	int[] selected = getScrollPaneTable().getSelectedRows();
	Object[] objs = new Object[selected.length];
	for (int i=0; i < selected.length; i++)
		objs[i] = dbds.get(selected[i]);
		
	for (int i=0; i < objs.length; i++)
		dbds.removeElement(objs[i]);
		
	getScrollPaneTable().tableChanged(new TableModelEvent(getScrollPaneTable().getModel()));

}
}
