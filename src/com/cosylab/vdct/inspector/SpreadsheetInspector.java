package com.cosylab.vdct.inspector;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.cosylab.vdct.Console;
import com.cosylab.vdct.graphics.ViewState;
import com.cosylab.vdct.graphics.objects.Record;

public class SpreadsheetInspector extends JDialog implements ActionListener, PropertyChangeListener {

	private SpreadsheetTableModel tableModel = null;
	
    private JOptionPane optionPane;
    private String btnString1 = "Enter";
    private String btnString2 = "Cancel";
    
	public SpreadsheetInspector(Frame owner, boolean modal) {
		super(owner, modal);

        setTitle("Spreadsheet");

    	tableModel = new SpreadsheetTableModel(getSelectedRecords());
        //String[] columnNames = {"Records"};
        /*
        Object[][] data = {
        	    {"Mary", "Campione",
        	     "Snowboarding", new Integer(5), new Boolean(false)},
        	    {"Alison", "Huml",
        	     "Rowing", new Integer(3), new Boolean(true)},
        	    {"Kathy", "Walrath",
        	     "Knitting", new Integer(2), new Boolean(false)},
        	    {"Sharon", "Zakhour",
        	     "Speed reading", new Integer(20), new Boolean(true)},
        	    {"Philip", "Milne",
        	     "Pool", new Integer(10), new Boolean(false)}
        	};

        JTable table = new JTable(data, columnNames);
        */
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        Object[] options = {btnString1, btnString2};
        
        optionPane = new JOptionPane(scrollPane,
                                    JOptionPane.QUESTION_MESSAGE,
                                    JOptionPane.YES_NO_OPTION,
                                    null,
                                    options,
                                    options[0]);

        setContentPane(optionPane);	

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                    optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
            }
        });

        optionPane.addPropertyChangeListener(this);
        pack();
     }

	private Vector getSelectedRecords() {
		Console.getInstance().print("displaying:");
    	
    	Vector vector = ViewState.getInstance().getSelectedObjects();
    	Vector records = new Vector();

    	Enumeration en = vector.elements();
    	Object obj = null;
    	while (en.hasMoreElements()) {
    		obj = en.nextElement();
    		if (obj instanceof Record) {
    			records.add(obj);
        		Console.getInstance().println(obj.toString());
    		}
    	}
    	return records;
	}
	
    public void actionPerformed(ActionEvent e) {
        optionPane.setValue(btnString1);
    }
	
    public void propertyChange(PropertyChangeEvent e) {
    	String prop = e.getPropertyName();

    	if (isVisible() && (e.getSource() == optionPane)
    			&& (JOptionPane.VALUE_PROPERTY.equals(prop) ||
    					JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
    		Object value = optionPane.getValue();

    		if (value == JOptionPane.UNINITIALIZED_VALUE) {
    			return;
    		}
    		optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
 			dispose();
    	}
    }	
	
	public static void main(String args[]) {
        new SpreadsheetInspector(new JFrame(), true).setVisible(true);
    }
}
