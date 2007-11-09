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
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import com.cosylab.vdct.Console;
import com.cosylab.vdct.DataProvider;
import com.cosylab.vdct.graphics.ViewState;
import com.cosylab.vdct.graphics.objects.Record;
import com.cosylab.vdct.graphics.objects.Template;

/**
 * @author ssah
 *
 */
public class SpreadsheetInspector extends JDialog implements HelpDisplayer {

    private JTabbedPane tabbedPane;
	
	private Vector types = null;	
	private Vector instances = null;	

    /** Creates new form SettingsDialog */
    public SpreadsheetInspector(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

		loadData();
		createGUI();
    }

    public void displayHelp(String text) {
		// TODO: convert this to display help in a label 
    	Console.getInstance().println(text);
    }
    
	/** This method is called from within the constructor to
     * initialize the form.
     */
    private void createGUI() {

        setTitle("Spreadsheet");

        tabbedPane = new javax.swing.JTabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        
        tabbedPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setAutoscrolls(true);

    	Enumeration typesEn = types.elements();
    	Enumeration instancesEn = instances.elements();
    	String type = null;
    	Vector vector = null;
    	
    	while (typesEn.hasMoreElements() && instancesEn.hasMoreElements()) {
    		type = (String)typesEn.nextElement();
    		vector = (Vector)instancesEn.nextElement();

    		SpreadsheetTableModel tableModel = new SpreadsheetTableModel(vector);
        	JTable table = getTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            tabbedPane.addTab(type, scrollPane);
    	}

        getContentPane().add(tabbedPane, java.awt.BorderLayout.CENTER);
        pack();
    }

    private JTable getTable(SpreadsheetTableModel tableModel) {

    	JTable table = new javax.swing.JTable(tableModel);
    	table.setName("ScrollPaneTable");
    	
    	table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    	table.setBackground(new Color(204,204,204));
    	table.setShowVerticalLines(true);
    	table.setGridColor(Color.black);
    	table.setBounds(0, 0, 200, 200);
    	table.setRowHeight(17);

        // not yet tested	
    	// enable clipboard actions
    	new InspectorTableClipboardAdapter(table);
    	table.setRowSelectionAllowed(true);
    	// note: selection is possible only on name column
    	table.setColumnSelectionAllowed(false);

    	table.setDefaultRenderer(String.class, new InspectorTableCellRenderer(
    			table, tableModel));
    	table.setDefaultEditor(String.class,
    		new InspectorCellEditor(tableModel, this));

    	// not yet known why necessary
    	/*
    	table.addMouseListener(new MouseAdapter() {
    		public void mouseReleased(MouseEvent evt) {
    			java.awt.Point pnt = evt.getPoint();
    			int popupAtRow = table.rowAtPoint(pnt);
    			int popupAtCol = -1;
    			if ((popupAtRow != -1) && 
    					((popupAtCol=table.columnAtPoint(pnt)) != -1)) {
    				mouseEvent(evt, popupAtRow, popupAtCol); 
    			}
    		}
    	});
    	*/

    	return table;
    }
    
	private void loadData() {
		Console.getInstance().print("displaying:");
		
    	Vector candidates = ViewState.getInstance().getSelectedObjects();
    	if (candidates.size() == 0) {
    		candidates = DataProvider.getInstance().getInspectable(); 
    	}
    	
    	/* Store the types in vector and instances in a vector of vectors. This
    	 * implementation uses n^2 time since the number of types is expected to
    	 * be small.
    	 */
    	Enumeration enumeration = candidates.elements();
    	Vector inspectables = new Vector();
    	Vector inspectablesTypes = new Vector();
    	
    	Object object = null;
    	Inspectable inspectable = null;
    	String type = null;
    	Record record = null;
    	Template template = null;
    	while (enumeration.hasMoreElements()) {
    		object = enumeration.nextElement();
    		if (!(object instanceof Inspectable)) {
    			continue;
    		}
    		inspectable = (Inspectable)object;
    		
    		if (inspectable instanceof Template) {
    			template = (Template)inspectable;
    			type = template.getTemplateData().getTemplate().getId();
    		} else if (inspectable instanceof Record) {
    			record = (Record)inspectable;
    			type = record.getRecordData().getType();
    		}
    		inspectables.add(object);
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
    	
    	typesEnumer = instances.elements();
    	while (typesEnumer.hasMoreElements()) {

    		enumeration = ((Vector)typesEnumer.nextElement()).elements();
        	while (enumeration.hasMoreElements()) {
        		object = enumeration.nextElement().toString();
        		Console.getInstance().println(object.toString());
        	}
    		Console.getInstance().println();
    	}
	}
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new SpreadsheetInspector(new javax.swing.JFrame(), true).setVisible(true);
    }
}
