/**
 * Copyright (c) 2007, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
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
package com.cosylab.vdct.inspector;

import java.awt.Dimension;
import java.awt.Event;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellEditor;

import com.cosylab.vdct.Constants;
import com.cosylab.vdct.DataProvider;
import com.cosylab.vdct.events.CommandManager;
import com.cosylab.vdct.events.commands.GetGUIInterface;
import com.cosylab.vdct.graphics.ViewState;
import com.cosylab.vdct.graphics.objects.Record;
import com.cosylab.vdct.graphics.objects.Template;
import com.cosylab.vdct.vdb.VDBTemplate;

/**
 * @author ssah
 *
 */
public class SpreadsheetInspector extends JDialog implements HelpDisplayer, ChangeListener, ActionListener {

    private JTabbedPane tabbedPane = null;
	
	private HashMap inspectables = null;
	private HashMap selectedInspectables = null;
	
	private SpreadsheetTable[] tables = null;
	private JLabel hintLabel = null;
	
	private JMenuItem undoItem = null;
	private JMenuItem redoItem = null;
	
    private CustomSplitDialog splitDialog = null;
    private CommentDialog commentDialog = null;
	
	private final static String undoString = "UndoAction";
	private final static String redoString = "RedoAction";

	private final static String helpTitle = "Help";
	private final static String hintTitle = "Hint";
	
	private final static String helpTask1 = "Edit a cell:";
	private final static String helpAction1 = "Left double click";
				
	private final static String helpTask2 = "Select a block:";
	private final static String helpAction2 = "Hold left button";
				
	private final static String helpTask3 = "Drag and drop:";
	private final static String helpAction3 = "Hold left button over selection";
				
	private final static String helpTask4 = "Hide/show a line:";
	private final static String helpAction4 = "Right click on the row start";
				
	private final static String helpTask5 = "Copy selection:";
	private final static String helpAction5 = "Ctrl+C";
				
	private final static String helpTask6 = "Paste data:";
	private final static String helpAction6 = "Ctrl+V";
				
	private final static String helpTask7 = "Undo an action:";
	private final static String helpAction7 = "Ctrl+Z";
				
	private final static String helpTask8 = "Redo an action:";
	private final static String helpAction8 = "Ctrl+Y";

	public SpreadsheetInspector(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
		createStaticGUI();
    }

	/* (non-Javadoc)
	 * @see java.awt.Dialog#setVisible(boolean)
	 */
	public void setVisible(boolean b) {
		if (b == isVisible()) {
			return;
		}
		
		if (b) {
			loadData();
	        if (inspectables.isEmpty()) {
	        	hintLabel.setText("No objects to display.");
	        }
			createDynamicGUI();
		} else {
			closeEditors();
	    	for (int i = 0; i < tables.length; i++) {
	    		((SpreadsheetTableModel)tables[i].getModel()).saveView();
	    	}
        }
		super.setVisible(b);
	}
	
	public void displayHelp(String text) {
    	hintLabel.setText((text != null && text.length() > 0) ? text : " ");
    }
	
	public JMenuItem getUndoItem() {
		return undoItem;
	}

	public JMenuItem getRedoItem() {
		return redoItem;
	}

	/** This method is called from within the constructor to
     * initialize the form.
     */
    private void createStaticGUI() {

    	createMenuBar();
    	
        tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
        tabbedPane.setAutoscrolls(true);
        tabbedPane.addChangeListener(this);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setPreferredSize(new Dimension(Constants.VDCT_WIDTH - 16, Constants.VDCT_HEIGHT - 64));

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(4, 4, 4, 4);
		contentPanel.add(tabbedPane, constraints);
		
        JPanel helpAndButtonPanel = createHelpAndButtonPanel();
		constraints = new GridBagConstraints();
		constraints.gridy = 1;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(4, 4, 4, 4);
		contentPanel.add(helpAndButtonPanel, constraints);
		
		setTitle("Spreadsheet");
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		getContentPane().add(contentPanel);
		pack();
    }

    private void createMenuBar() {

    	JMenuItem exitItem = new JMenuItem();
		exitItem.setMnemonic('L');
		exitItem.setText("Close");
		exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.CTRL_MASK));
		exitItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	setVisible(false);
            }
        });

    	JMenu fileMenu = new JMenu();
		fileMenu.setMnemonic('F');
		fileMenu.setText("File");
		fileMenu.add(exitItem);
		
		undoItem = new JMenuItem();
		undoItem.setText("Undo");
		undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK));
		undoItem.setEnabled(false);
		undoItem.addActionListener(this);

		redoItem = new JMenuItem();
		redoItem.setText("Redo");
		redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK));
		redoItem.setEnabled(false);
		redoItem.addActionListener(this);

    	JMenu editMenu = new JMenu();
    	editMenu.setMnemonic('E');
    	editMenu.setText("Edit");
    	editMenu.add(undoItem);
    	editMenu.add(redoItem);
    	
		JMenuBar bar = new JMenuBar();
		bar.add(fileMenu);
		bar.add(editMenu);
    	
    	setJMenuBar(bar);
    }

    private JPanel createHelpAndButtonPanel() {
    	JPanel helpAndButtonPanel = new JPanel(new GridBagLayout());

    	JPanel helpPanel = new JPanel(new GridBagLayout());
    	
    	JPanel hintPanel = new JPanel(new GridBagLayout());
    	hintPanel.setBorder(new TitledBorder(hintTitle));
		hintLabel = new JLabel(" ");
    	GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
    	constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(0, 4, 8, 4);
		hintPanel.add(hintLabel, constraints);

    	constraints = new GridBagConstraints();
    	constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
		helpPanel.add(hintPanel, constraints);

    	JPanel helpTextPanel = new JPanel(new GridBagLayout());
    	helpTextPanel.setBorder(new TitledBorder(helpTitle));
    	
    	JPanel helpPanel1 = createHelpPanel1();
    	constraints = new GridBagConstraints();
		constraints.weightx = .5;
		constraints.insets = new Insets(0, 4, 8, 4);
		helpTextPanel.add(helpPanel1, constraints);

    	JPanel helpPanel2 = createHelpPanel2();
    	constraints = new GridBagConstraints();
		constraints.gridx = 1;
    	constraints.weightx = .5;
		constraints.insets = new Insets(0, 4, 8, 4);
		helpTextPanel.add(helpPanel2, constraints);

    	constraints = new GridBagConstraints();
    	constraints.gridy = 1;
    	constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
		helpPanel.add(helpTextPanel, constraints);

    	constraints = new GridBagConstraints();
    	constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
		helpAndButtonPanel.add(helpPanel, constraints);
		
    	JPanel buttonPanel = createButtonPanel();
    	constraints = new GridBagConstraints();
		constraints.gridx = 1;
        constraints.anchor = GridBagConstraints.SOUTHEAST;
		helpAndButtonPanel.add(buttonPanel, constraints);

		return helpAndButtonPanel;
    }
    
    private JPanel createHelpPanel1() {
    	
    	JPanel helpPanel1 = new JPanel(new GridBagLayout());

    	JLabel helpTask1Label = new JLabel(helpTask1);
    	GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(0, 4, 0, 4);
		helpPanel1.add(helpTask1Label, constraints);

    	JLabel helpAction1Label = new JLabel(helpAction1);
    	constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 4, 0, 4);
		helpPanel1.add(helpAction1Label, constraints);

    	JLabel helpTask2Label = new JLabel(helpTask2);
    	constraints = new GridBagConstraints();
		constraints.gridy = 1;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(0, 4, 0, 4);
		helpPanel1.add(helpTask2Label, constraints);

    	JLabel helpAction2Label = new JLabel(helpAction2);
    	constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 4, 0, 4);
		helpPanel1.add(helpAction2Label, constraints);

    	JLabel helpTask3Label = new JLabel(helpTask3);
    	constraints = new GridBagConstraints();
		constraints.gridy = 2;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(0, 4, 0, 4);
		helpPanel1.add(helpTask3Label, constraints);

    	JLabel helpAction3Label = new JLabel(helpAction3);
    	constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 4, 0, 4);
		helpPanel1.add(helpAction3Label, constraints);

    	JLabel helpTask4Label = new JLabel(helpTask4);
    	constraints = new GridBagConstraints();
		constraints.gridy = 3;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(0, 4, 0, 4);
		helpPanel1.add(helpTask4Label, constraints);

    	JLabel helpAction4Label = new JLabel(helpAction4);
    	constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 4, 0, 4);
		helpPanel1.add(helpAction4Label, constraints);
		
		return helpPanel1;
    }

    private JPanel createHelpPanel2() {
    	
    	JPanel helpPanel2 = new JPanel(new GridBagLayout());

    	JLabel helpTask5Label = new JLabel(helpTask5);
    	GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(0, 4, 0, 4);
		helpPanel2.add(helpTask5Label, constraints);

    	JLabel helpAction5Label = new JLabel(helpAction5);
    	constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 4, 0, 4);
		helpPanel2.add(helpAction5Label, constraints);

    	JLabel helpTask6Label = new JLabel(helpTask6);
    	constraints = new GridBagConstraints();
		constraints.gridy = 1;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(0, 4, 0, 4);
		helpPanel2.add(helpTask6Label, constraints);

    	JLabel helpAction6Label = new JLabel(helpAction6);
    	constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 4, 0, 4);
		helpPanel2.add(helpAction6Label, constraints);

    	JLabel helpTask7Label = new JLabel(helpTask7);
    	constraints = new GridBagConstraints();
		constraints.gridy = 2;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(0, 4, 0, 4);
		helpPanel2.add(helpTask7Label, constraints);

    	JLabel helpAction7Label = new JLabel(helpAction7);
    	constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 4, 0, 4);
		helpPanel2.add(helpAction7Label, constraints);

    	JLabel helpTask8Label = new JLabel(helpTask8);
    	constraints = new GridBagConstraints();
		constraints.gridy = 3;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(0, 4, 0, 4);
		helpPanel2.add(helpTask8Label, constraints);

    	JLabel helpAction8Label = new JLabel(helpAction8);
    	constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 4, 0, 4);
		helpPanel2.add(helpAction8Label, constraints);
		
		return helpPanel2;
    }

    private JPanel createButtonPanel() {
    	
    	JPanel buttonPanel = new JPanel(new GridBagLayout());
    	JButton button = new JButton(); 
    	button.setMnemonic('C');
    	button.setText("Close");
    	button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	setVisible(false);
            }
        });
    	GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(4, 16, 4, 4);
		buttonPanel.add(button, constraints);
		return buttonPanel;
    }
    
    private void createDynamicGUI() {
    	createTables();
    	resizeTablesColumns();
    }
    
    private void refreshTables() {
    	for (int i = 0; i < tables.length; i++) {
    		tables[i].refresh();
    	}
    }
    
    private void createTables() {
        tabbedPane.removeAll();

        /*
        createTables: hashtable table is selected if not empty, otherwise all;
        for every key: get all vector, display vector; if display vector nonempty:
        get the typeName using first element, and create model with it;
        */
        HashMap displayData = selectedInspectables.isEmpty() ? inspectables : selectedInspectables;
        
    	Object key = null;
    	String type = null;
    	Vector displayObjects = null;
    	Vector loadedObjects = null;
    	JTable table = null;
    	Vector tableVector = new Vector();
        Iterator iterator = displayData.keySet().iterator();
        while(iterator.hasNext()) {
            key = iterator.next();
            displayObjects = (Vector)displayData.get(key);
            loadedObjects = (Vector)inspectables.get(key);
            if (!displayObjects.isEmpty()) {
            	type = getTypeName(displayObjects.get(0));
            	
                JScrollPane scrollPane = new JScrollPane();
                table = getTable(type, displayObjects, loadedObjects, scrollPane);
                scrollPane.setViewportView(table);
                tabbedPane.addTab(type, scrollPane);
                tableVector.add(table);
            }
        }
        
        tables = new SpreadsheetTable[tableVector.size()];
        tableVector.copyInto(tables);
    }
    
    private SpreadsheetTable getTable(String type, Vector displayData, Vector loadedData, JScrollPane scrollPane) {

    	SpreadsheetTable table = new SpreadsheetTable(displayData);
		SpreadsheetTableModel tableModel = new SpreadsheetTableModel(this, type, displayData, loadedData);
    	table.setModel(tableModel);

        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK, false);
		table.registerKeyboardAction(this, undoString, keyStroke, JComponent.WHEN_FOCUSED);
        keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK, false);
        table.registerKeyboardAction(this, redoString, keyStroke, JComponent.WHEN_FOCUSED);
    	
    	table.setTransferHandler(new InspectorTableClipboardAdapter(table));
    	table.setDragEnabled(true);
    	
    	InspectorCellEditor editor = new InspectorCellEditor(tableModel, this);
    	editor.setClickCountToStart(2);

    	table.setDefaultRenderer(String.class, new InspectorTableCellRenderer(table, tableModel));
    	table.setDefaultEditor(String.class, editor);

    	tableModel.setTable(table, scrollPane);
    	
    	return table;
    }

    /**
     * Sets the widths of all columns so that the values text of all fields can
     * be displayed. 
     */
    public void resizeTablesColumns() {
    	for (int i = 0; i < tables.length; i++) {
    		tables[i].resizeColumns();
    	}
    }
    
    /* Saves the content of open cells when tabs are changed.
    */
    public void stateChanged(ChangeEvent event) {
    	if (isVisible()) {
            closeEditors();
    	}
    } 

    public void actionPerformed(ActionEvent event) {
    	if (event.getActionCommand().equals(undoString) || event.getSource() == undoItem) {
        	closeEditors();
        	GetGUIInterface com = (GetGUIInterface)CommandManager.getInstance().getCommand("GetGUIMenuInterface");
            com.getGUIMenuInterface().undo();
            refreshTables();
    	} else if (event.getActionCommand().equals(redoString) || event.getSource() == redoItem) {
        	closeEditors();
        	GetGUIInterface com = (GetGUIInterface)CommandManager.getInstance().getCommand("GetGUIMenuInterface");
            com.getGUIMenuInterface().redo();
            refreshTables();
    	}  
    }
    
    public CustomSplitDialog getCustomSplitDialog() {
        if (splitDialog == null) {
            splitDialog = new CustomSplitDialog(this);
        }
        return splitDialog;
    }

    public CommentDialog getCommentDialog() {
        if (commentDialog == null) {
        	commentDialog = new CommentDialog(this);
        }
        return commentDialog;
    }

    private void closeEditors() {
    	for (int i = 0; i < tables.length; i++) {

    		TableCellEditor editor = tables[i].getCellEditor();
    		if (editor != null) {
    			editor.stopCellEditing();
    			tables[i].editingStopped(null);
    		}
    	}
    }

	private void loadData() {
		
		inspectables = new HashMap();
		selectedInspectables = new HashMap();
		
		fillTable(inspectables, DataProvider.getInstance().getInspectable());
		fillTable(selectedInspectables, ViewState.getInstance().getSelectedObjects());
	}
	
	private void fillTable(HashMap map, Vector objects) {

		Enumeration enumeration = objects.elements();
    	
    	Object object = null;
    	Object key = null;
    	Vector vector = null;
    	while (enumeration.hasMoreElements()) {
    		object = enumeration.nextElement();
    		key = getTypeKey(object);
    		if (key != null) {
    			vector = (Vector)map.get(key); 
    			if (vector == null) {
    				vector = new Vector();
        			map.put(key, vector);
    			}
    			vector.add(object);
    		}
    	}
	}
    
    public static Object getTypeKey(Object object) {
    	
    	if (object instanceof Record) {
    		return ((Record)object).getType();
    	} else if (object instanceof Template) {
    		return ((Template)object).getTemplateData().getTemplate();
    	}
    	return null;
    }

    public static String getTypeName(Object object) {
    	
    	Object key = getTypeKey(object);
    	if (object instanceof Record) {
    		return key.toString();
    	} else if (object instanceof Template) {
    		return ((VDBTemplate)(key)).getId();
    	}
    	return null;
    }
}
