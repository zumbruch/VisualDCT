package com.cosylab.vdct.inspector;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import com.cosylab.vdct.dbd.*;

public class InspectorCellEditor implements TableCellEditor, TreeCellEditor {

	/** Event listeners */
	protected EventListenerList listenerList = new EventListenerList();
	protected ChangeEvent changeEvent = null;

	// intelli editor
	protected boolean intelliEditor = false;
	protected InspectorTableModel tableModel;
	protected JComboBox intelliComboBox;
	protected EditorDelegate comboDelegate;
	protected JTextField intelliTextField;
	protected EditorDelegate textfieldDelegate;
	
	protected JComponent editorComponent;
	protected EditorDelegate delegate;
	protected int clickCountToStart = 1;

	protected class EditorDelegate implements ActionListener, ItemListener {

		/** Not implemented. */
		protected Object value;

		/** Not implemented. */
		public Object getCellEditorValue() {
			return null;
		}

		/** Not implemented. */
		public void setValue(Object value) {}

		/** Not implemented. */
		public boolean isCellEditable(EventObject anEvent) {
			return true;
		}
		
		/** Unfortunately, restrictions on API changes force us to 
		  * declare this method package private. 
		  */
		boolean shouldSelectCell(EventObject anEvent) { 
			return true; 
		}


		/** Not implemented. */
		public boolean startCellEditing(EventObject anEvent) {
			return true;
		}

		/** Not implemented. */
		public boolean stopCellEditing() {
			return true;
		}

		/** Not implemented. */
	   	public void cancelCellEditing() {}

		// Implementing ActionListener interface
		public void actionPerformed(ActionEvent e) {
			fireEditingStopped();
		}

		// Implementing ItemListener interface
		public void itemStateChanged(ItemEvent e) {
			fireEditingStopped();
		}
	}

/**
 * Insert the method's description here.
 * Creation date: (10.1.2001 16:03:29)
 */
public InspectorCellEditor(InspectorTableModel tableModel) {
	intelliEditor = true;
	this.tableModel=tableModel;

	// create all components
	// combo box
		intelliComboBox = new JComboBox();
		intelliComboBox.setBorder(null);
		intelliComboBox.setEditor(new BorderlessComboBoxEditor());
		comboDelegate = new EditorDelegate() {
			
			public void setValue(Object value) {
				intelliComboBox.setSelectedItem(value);
			}

			public Object getCellEditorValue() {
				return intelliComboBox.getSelectedItem();
			}
				
			boolean shouldSelectCell(EventObject anEvent) { 
				if (anEvent instanceof MouseEvent) { 
					MouseEvent e = (MouseEvent)anEvent;
					return e.getID() != MouseEvent.MOUSE_DRAGGED;
				}
				return true;
			}
			
		};
		intelliComboBox.addActionListener(comboDelegate);

	// textfield
		intelliTextField = new JTextField();
		intelliTextField.setBorder(null);
		textfieldDelegate = new EditorDelegate() {
			
			public void setValue(Object value) {
				intelliTextField.setText((value != null) ? value.toString() : "");
			}

			public Object getCellEditorValue() {
				return intelliTextField.getText();
			}
			
		};
		intelliTextField.addActionListener(textfieldDelegate);

}
	/**
	 * Constructs a InspectorCellEditor object that uses a check box.
	 *
	 * @param checkBox javax.swing.JCheckBox
	 */
	public InspectorCellEditor(final JCheckBox checkBox) {
		editorComponent = checkBox;
		delegate = new EditorDelegate() {
			
			public void setValue(Object value) { 
				boolean selected = false; 
				if (value instanceof Boolean)
		    			selected = ((Boolean)value).booleanValue();
				else if (value instanceof String)
			    		selected = value.equals("true");
				checkBox.setSelected(selected);
			}

			public Object getCellEditorValue() {
				return new Boolean(checkBox.isSelected());
			}
		};
		checkBox.addActionListener(delegate);
	}
	/**
	 * Constructs a InspectorCellEditor object that uses a combo box.
	 *
	 * @param comboBox javax.swing.JComboBox
	 */
	public InspectorCellEditor(final JComboBox comboBox) {
		comboBox.setEditor(new BorderlessComboBoxEditor());
		editorComponent = comboBox;
		delegate = new EditorDelegate() {
			
			public void setValue(Object value) {
				comboBox.setSelectedItem(value);
			}

			public Object getCellEditorValue() {
				return comboBox.getSelectedItem();
			}
				
			boolean shouldSelectCell(EventObject anEvent) { 
				if (anEvent instanceof MouseEvent) { 
					MouseEvent e = (MouseEvent)anEvent;
					return e.getID() != MouseEvent.MOUSE_DRAGGED;
				}
				return true;
			}
			
		};
		comboBox.addActionListener(delegate);
	}
	/**
	 * Constructs a InspectorCellEditor that uses a text field.
	 *
	 * @param textField javax.swing.JTextField
	 */
	public InspectorCellEditor(final JTextField textField) {
		editorComponent = textField;
		delegate = new EditorDelegate() {
			
			public void setValue(Object value) {
				textField.setText((value != null) ? value.toString() : "");
			}

			public Object getCellEditorValue() {
				return textField.getText();
			}
			
		};
		textField.addActionListener(delegate);
	}
	// implements javax.swing.CellEditor
	public void addCellEditorListener(CellEditorListener l) {
		listenerList.add(CellEditorListener.class, l);
	}
	// implements javax.swing.CellEditor
	public void cancelCellEditing() {
		fireEditingCanceled();
		InspectorManager.getInstance().getActiveInspector().setHelp("");
	}
	/*
	 * Notify all listeners that have registered interest for
	 * notification on this event type.  The event instance 
	 * is lazily created using the parameters passed into 
	 * the fire method.
	 * @see EventListenerList
	 */
	protected void fireEditingCanceled() {
		textfieldDelegate.cancelCellEditing();
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length-2; i>=0; i-=2) {
	       if (listeners[i]==CellEditorListener.class) {
			// Lazily create the event:
			if (changeEvent == null)
		    	changeEvent = new ChangeEvent(this);
			((CellEditorListener)listeners[i+1]).editingCanceled(changeEvent);
	       }	       
		}
	}
	/*
	 * Notify all listeners that have registered interest for
	 * notification on this event type.  The event instance 
	 * is lazily created using the parameters passed into 
	 * the fire method.
	 * @see EventListenerList
	 */
	protected void fireEditingStopped() {
		textfieldDelegate.stopCellEditing();
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length-2; i>=0; i-=2) {
		   if (listeners[i]==CellEditorListener.class) {
			// Lazily create the event:
			if (changeEvent == null)
		    	changeEvent = new ChangeEvent(this);
			((CellEditorListener)listeners[i+1]).editingStopped(changeEvent);
	   	   }	       
		}
	}
	// implements javax.swing.CellEditor
	public Object getCellEditorValue() {
		return delegate.getCellEditorValue();
	}
	/**
	 *  ClickCountToStart controls the number of clicks required to start
	 *  editing.
	 */
	public int getClickCountToStart() {
		return clickCountToStart;
	}
	/**
	 * Returns the a reference to the editor component.
	 *
	 * @return the editor Component
	 */
	public Component getComponent() {
		return editorComponent;
	}
	// implements javax.swing.table.TableCellEditor
	public Component getTableCellEditorComponent(JTable table, Object value,
						 boolean isSelected, int row, int column) {
		if (intelliEditor) setAppropriateComponent4Table(table, row, column);
		delegate.setValue(value);
		return editorComponent;
	}
	// implements javax.swing.tree.TreeCellEditor
	public Component getTreeCellEditorComponent(JTree tree, Object value,
						boolean isSelected,
						boolean expanded,
						boolean leaf, int row) {
		String stringValue = tree.convertValueToText(value, isSelected,
					    expanded, leaf, row, false);

		// intelliEditor not supported
		delegate.setValue(stringValue);
		return editorComponent;
	}
	// implements javax.swing.CellEditor
	public boolean isCellEditable(EventObject anEvent) {
		if (anEvent instanceof MouseEvent) { 
			return ((MouseEvent)anEvent).getClickCount() >= clickCountToStart;
		}
		return true;
	}
	// implements javax.swing.CellEditor
	public void removeCellEditorListener(CellEditorListener l) {
		listenerList.remove(CellEditorListener.class, l);
	}
/**
 * Insert the method's description here.
 * Creation date: (10.1.2001 16:17:33)
 * @param row int
 * @param column int
 */
private void setAppropriateComponent4Table(JTable table, int row, int column) {
	
	InspectableProperty property = tableModel.getPropertyAt(row);
	String[] choices = property.getSelectableValues();
	
	if (choices!=null) {
			editorComponent = intelliComboBox;
			delegate = comboDelegate;
			if (intelliComboBox.getItemCount()>0)
				intelliComboBox.removeAllItems();

			for (int i=0; i<choices.length; i++)
				intelliComboBox.addItem(choices[i]);
		
			intelliComboBox.setEditable(property.allowsOtherValues());
			intelliComboBox.setSelectedItem(property.getValue());
	}
	else {
			editorComponent = intelliTextField;
			delegate = textfieldDelegate;
			
			intelliTextField.setText(property.getValue());
	}

	InspectorManager.getInstance().getActiveInspector().setHelp(property.getHelp());
	editorComponent.setFont(table.getFont());
}
	/**
	 * Specifies the number of clicks needed to start editing.
	 *
	 * @param count  an int specifying the number of clicks needed to start editing
	 * @see #getClickCountToStart
	 */
	public void setClickCountToStart(int count) {
		clickCountToStart = count;
	}
	// implements javax.swing.CellEditor
	public boolean shouldSelectCell(EventObject anEvent) { 
		return delegate.shouldSelectCell(anEvent); 
	}
	// implements javax.swing.CellEditor
	public boolean stopCellEditing() {
		fireEditingStopped();
		InspectorManager.getInstance().getActiveInspector().setHelp("");
		return true;
	}
}
