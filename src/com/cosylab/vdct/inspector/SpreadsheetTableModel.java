package com.cosylab.vdct.inspector;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class SpreadsheetTableModel extends AbstractTableModel {
	
	private Vector records = null;
	
	public SpreadsheetTableModel(Vector records) {
		super();
		this.records = records;
	}

	public int getColumnCount() {
		return 1;
	}

	public int getRowCount() {
		return records.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return records.elementAt(rowIndex);
	}

}
