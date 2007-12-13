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
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellEditor;

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
	
	private Vector types = null;	
	private Vector instances = null;
	private SpreadsheetTable[] tables = null;
	private JLabel helpLabel = null;
	
	private JMenuItem undoItem = null;
	private JMenuItem redoItem = null;
	
    private CustomSplitDialog splitDialog = null;
	
	private final static String undoString = "UndoAction";
	private final static String redoString = "RedoAction";
	
	private final static String entryHelp =
		"To edit a cell, double click it or select it and write input.\n" +
		"Press and hold left mouse button to select a block.\n" + 
		"Press and hold left mouse button over a block to drag it into another application.\n" + 
		"To select the whole row, select the row start.\n" + 
		"Blocks can be copied(Ctrl+C)/pasted(Ctrl+V) or dragged/dropped from/to Excel.\n" +
		"A paste/drop of data at invalid location can be undone(Ctr+Z).";

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
	        if (types.size() == 0) {
	        	helpLabel.setText("No objects to display.");
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
    	helpLabel.setText(text);
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
        tabbedPane.setPreferredSize(new Dimension(1000, 600));

    	JButton button = new JButton(); 
    	button.setMnemonic('O');
    	button.setText("OK");
    	button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	setVisible(false);
            }
        });

		helpLabel = new JLabel();
		helpLabel.setHorizontalAlignment(SwingConstants.LEFT);
		helpLabel.setHorizontalTextPosition(SwingConstants.CENTER);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBackground(getBackground());
        textArea.setFont(helpLabel.getFont());
        textArea.append(entryHelp);
		
		JPanel panel = new JPanel(new GridBagLayout());
    	
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridwidth = 2;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(4, 4, 4, 4);
		panel.add(tabbedPane, constraints);

		constraints = new GridBagConstraints();
		constraints.gridwidth = 2;
		constraints.gridy = 1;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(4, 4, 4, 4);
		panel.add(textArea, constraints);
		
		constraints = new GridBagConstraints();
		constraints.gridy = 2;
		constraints.weightx = 1.0;
		constraints.insets = new Insets(0, 4, 4, 4);
		panel.add(helpLabel, constraints);

		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.insets = new Insets(0, 0, 4, 4);
		panel.add(button, constraints);

		setTitle("Spreadsheet");
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		getContentPane().add(panel);
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

    	tables = new SpreadsheetTable[types.size()];
        Enumeration typesEn = types.elements();
    	Enumeration instancesEn = instances.elements();
    	String type = null;
    	Vector vector = null;
    	int i = 0;
    	
    	while (typesEn.hasMoreElements() && instancesEn.hasMoreElements()) {
    		type = (String)typesEn.nextElement();
    		vector = (Vector)instancesEn.nextElement();

            JScrollPane scrollPane = new JScrollPane();
    		tables[i] = getTable(type, vector, scrollPane);
            scrollPane.setViewportView(tables[i]);
            tabbedPane.addTab(type, scrollPane);
            i++;
    	}
    }
    
    private SpreadsheetTable getTable(String type, Vector data, JScrollPane scrollPane) {

    	SpreadsheetTable table = new SpreadsheetTable(data);
		SpreadsheetTableModel tableModel = new SpreadsheetTableModel(this, type, data);
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
	
		Vector candidates = ViewState.getInstance().getSelectedObjects();
		Vector inspectables = getSpreadsheetData(candidates);
    	if (inspectables.size() == 0) {
    		candidates = DataProvider.getInstance().getInspectable(); 
    		inspectables = getSpreadsheetData(candidates);
    	}
    	
    	Vector records = getRecords(inspectables);
    	Vector templates = getTemplates(inspectables);

    	types = new Vector();
    	
		for (int i = 0; i < records.size(); i++) {
			types.add(((Record)((Vector)records.get(i)).get(0)).getType());
		}
		for (int i = 0; i < templates.size(); i++) {
			types.add(((Template)((Vector)templates.get(i)).get(0)).getTemplateData().getTemplate().getId());
		}
    	
    	instances = new Vector();
    	instances.addAll(records);
    	instances.addAll(templates);
	}
	
	private Vector getRecords(Vector candidates) {

    	Vector records = new Vector();

    	HashMap typeToVector = new HashMap();    	
    	
		for (int i = 0; i < candidates.size(); i++) {
    		Inspectable inspectable = (Inspectable)candidates.get(i);
    		if (!(inspectable instanceof Record)) {
    			continue;
    		}
    		String type = ((Record)inspectable).getType();

    		Vector vector = (Vector)typeToVector.get(type);
    		if (vector == null) {
    			vector = new Vector();
    		    records.add(vector);
    			typeToVector.put(type, vector);
    		}
    		vector.add(inspectable);
		}
		return records;
	}

	private Vector getTemplates(Vector candidates) {

    	Vector templates = new Vector();
		for (int i = 0; i < candidates.size(); i++) {
    		Inspectable inspectable = (Inspectable)candidates.get(i);
    		if (!(inspectable instanceof Template)) {
    			continue;
    		}
    		VDBTemplate template = ((Template)inspectable).getTemplateData().getTemplate();
    		
    		// templates must be checked by == and not by hash code or equals() method
    		Vector vector = null;
    		int j = 0;
    		for (j = 0; j < templates.size(); j++) {
    			vector = (Vector)templates.get(j);
    			if (template == ((Template)vector.get(0)).getTemplateData().getTemplate()) {
    				break;
    			}
    		}
    		if (j == templates.size()) {
    			vector = new Vector(); 
    			templates.add(vector);
    		}
    		vector.add(inspectable);
		}
		return templates;
	}
	
	private Vector getSpreadsheetData(Vector candidates) {

		Enumeration enumeration = candidates.elements();
    	Vector data = new Vector();
    	
    	Object object = null;
    	Inspectable inspectable = null;
    	while (enumeration.hasMoreElements()) {
    		object = enumeration.nextElement();
    		if (!(object instanceof Inspectable)) {
    			continue;
    		}
    		inspectable = (Inspectable)object;
    		
    		if (inspectable instanceof Template || inspectable instanceof Record) {
        		data.add(object);
    		}
    	}
    	return data;
	}
}
