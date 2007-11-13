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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellEditor;

import com.cosylab.vdct.DataProvider;
import com.cosylab.vdct.graphics.ViewState;
import com.cosylab.vdct.graphics.objects.Record;
import com.cosylab.vdct.graphics.objects.Template;

/**
 * @author ssah
 *
 */
public class SpreadsheetInspector extends JDialog
        implements HelpDisplayer, ChangeListener {

    private JTabbedPane tabbedPane;
	
	private Vector types = null;	
	private Vector instances = null;
	private JTable[] tables = null;
	private JLabel helpLabel = null; 

	public SpreadsheetInspector(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

		loadData();
		createGUI();
    }

    public void displayHelp(String text) {
    	helpLabel.setText(text);
    }
    
	/** This method is called from within the constructor to
     * initialize the form.
     */
    private void createGUI() {
    	
     	createTabbedPane();
    	JButton button = new JButton(); 
    	button.setMnemonic('O');
    	button.setText("OK");
    	button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	closeEditors();
                dispose();
            }
        });

		helpLabel = new JLabel();
		helpLabel.setHorizontalAlignment(SwingConstants.LEFT);
		helpLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        if (types.size() == 0) {
        	helpLabel.setText("No objects to display.");
        }

		
    	JPanel panel = new JPanel(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridwidth = 2;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(4, 4, 4, 4);
		panel.add(tabbedPane, constraints);

		constraints = new GridBagConstraints();
		constraints.gridy = 1;
		constraints.weightx = 1.0;
		constraints.insets = new Insets(0, 4, 4, 4);
		panel.add(helpLabel, constraints);

		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.insets = new Insets(0, 0, 4, 4);
		panel.add(button, constraints);

		setTitle("Spreadsheet");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().add(panel);
		pack();
    }

    private void createTabbedPane() {
        tabbedPane = new JTabbedPane();

    	tables = new JTable[types.size()];
        Enumeration typesEn = types.elements();
    	Enumeration instancesEn = instances.elements();
    	String type = null;
    	Vector vector = null;
    	int i = 0;
    	
    	while (typesEn.hasMoreElements() && instancesEn.hasMoreElements()) {
    		type = (String)typesEn.nextElement();
    		vector = (Vector)instancesEn.nextElement();
    		SpreadsheetTableModel tableModel = new SpreadsheetTableModel(vector);
        	tables[i] = getTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(tables[i]);
            tabbedPane.addTab(type, scrollPane);
            i++;
    	}

        tabbedPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setAutoscrolls(true);
        tabbedPane.addChangeListener(this);
        tabbedPane.setPreferredSize(new Dimension(1000, 600));
    }
    
    private JTable getTable(SpreadsheetTableModel tableModel) {

    	JTable table = new javax.swing.JTable(tableModel);
    	table.setName("ScrollPaneTable");
    	
    	table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    	table.setBackground(new Color(204, 204, 204));
    	table.setShowVerticalLines(true);
    	table.setGridColor(Color.black);
    	table.setBounds(0, 0, 200, 200);
    	table.setRowHeight(17);

        // not yet tested	
    	// enable clipboard actions
    	new InspectorTableClipboardAdapter(table);
    	table.setRowSelectionAllowed(true);
    	table.setColumnSelectionAllowed(false);

    	table.setDefaultRenderer(String.class, new InspectorTableCellRenderer(
    			table, tableModel));
    	table.setDefaultEditor(String.class,
    		new InspectorCellEditor(tableModel, this));

    	return table;
    }

    /* Saves the content of open cells when tabs are changed.
    */
    public void stateChanged(ChangeEvent event) {
    	closeEditors();
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
    	
    	/* Store the types in vector and instances in a vector of vectors. This
    	 * implementation uses n^2 time since the number of types is expected to
    	 * be small.
    	 */
    	Enumeration enumeration = inspectables.elements();
    	Vector inspectablesTypes = new Vector();
    	
    	Inspectable inspectable = null;
    	String type = null;
    	Record record = null;
    	Template template = null;
    	while (enumeration.hasMoreElements()) {
    		inspectable = (Inspectable)enumeration.nextElement();
    		if (inspectable instanceof Template) {
    			template = (Template)inspectable;
    			type = template.getTemplateData().getTemplate().getId();
    		} else if (inspectable instanceof Record) {
    			record = (Record)inspectable;
    			type = record.getRecordData().getType();
    		}
    		inspectablesTypes.add(type);
    	}

    	types = new Vector(new HashSet(inspectablesTypes));
    	instances = new Vector();
    	
    	enumeration = types.elements();
    	while (enumeration.hasMoreElements()) {
    		enumeration.nextElement();
    		instances.add(new Vector());
    	}
    		
    	enumeration = inspectables.elements();
    	Enumeration typesEnumer = inspectablesTypes.elements();
    	while (enumeration.hasMoreElements() && typesEnumer.hasMoreElements()) {
    		inspectable = (Inspectable)enumeration.nextElement();
    		type = (String)typesEnumer.nextElement();

    		((Vector)instances.get(types.indexOf(type))).add(inspectable);
    	}
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

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new SpreadsheetInspector(new javax.swing.JFrame(), true).setVisible(true);
    }
}
