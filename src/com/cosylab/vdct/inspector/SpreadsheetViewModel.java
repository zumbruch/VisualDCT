/**
 * Copyright (c) 2008, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
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

import java.util.Set;
import java.util.Vector;

/**
 * @author ssah
 *
 */
public class SpreadsheetViewModel extends SpreadsheetTableModel {

    private boolean[] rowVisibilities = null; 
    private boolean[] columnVisibilities = null;
    
    private int[] baseToOrderedRow = null;
    private int[] orderedToBaseRow = null;
    private int[] baseToOrderedColumn = null;
    private int[] orderedToBaseColumn = null;

    private int[] allToVisibleRow = null;
    private int[] visibleToAllRow = null;
    private int[] allToVisibleColumn = null;
    private int[] visibleToAllColumn = null;
    
    private boolean showAllRows = false;
	
	public SpreadsheetViewModel(String dataType, Vector displayData,
			Vector loadedData) throws IllegalArgumentException {
		super(dataType, displayData, loadedData);
	}
	
	private void refreshAll() {
		int rowCount = super.getRowCount();
		int columnCount = super.getColumnCount();

	    rowVisibilities = new boolean[rowCount];
	    columnVisibilities = new boolean[columnCount];
	    
	    for (int r = 0; r < rowVisibilities.length; r++) {
	    	rowVisibilities[r] = true;
			((SpreadsheetRowVisible)super.getPropertyAt(r, 0)).setVisible(true);
	    }

	    for (int c = 0; c < columnVisibilities.length; c++) {
	    	columnVisibilities[c] = true; 
	    }
		
		baseToOrderedRow = new int[rowCount];
	    orderedToBaseRow = new int[rowCount];
	    baseToOrderedColumn = new int[columnCount];
	    orderedToBaseColumn = new int[columnCount];
	    
	    allToVisibleRow = new int[rowCount];
	    allToVisibleColumn = new int[columnCount];

		setId(baseToOrderedRow);
		refreshOrderedToBaseRow();
		refreshAllToVisibleRow();
		refreshVisibleToAllRow();

		setIdBaseToOrderedColumn();
		refreshOrderedToBaseColumn();
		refreshAllToVisibleColumn();
		refreshVisibleToAllColumn();

		loadViewState();
	}
	
	private void setId(int[] order) {
	    for (int i = 0; i < order.length; i++) {
	    	order[i] = i; 
		}
	}
	
	private void refreshBaseToOrderedRow() {
	    for (int r = 0; r < baseToOrderedRow.length; r++) {
	        baseToOrderedRow[baseToOrderedRow[r]] = r;
		}
	}
	
	private void refreshOrderedToBaseRow() {
	    for (int r = 0; r < orderedToBaseRow.length; r++) {
	    	orderedToBaseRow[baseToOrderedRow[r]] = r; 
		}
	}

	private void refreshAllToVisibleRow() {
		int h = 0;
	    for (int r = 0; r < orderedToBaseRow.length; r++) {
	    	int baseR = orderedToBaseRow[r];
	    	if (rowVisibilities[baseR] || showAllRows) {
	    		allToVisibleRow[r] = h;
	    		h++;
	    	} else {
	    		allToVisibleRow[r] = -1;
	    	}
		}
	}
	
	private void refreshVisibleToAllRow() {
		int maxVisible = 0;
	    for (int r = 0; r < allToVisibleRow.length; r++) {
	    	maxVisible = Math.max(maxVisible, allToVisibleRow[r]); 
		}
	    visibleToAllRow = new int[maxVisible + 1];
	    for (int r = 0; r < allToVisibleRow.length; r++) {
	    	if (allToVisibleRow[r] >= 0) {
	    	    visibleToAllRow[allToVisibleRow[r]] = r;
	    	}
		}
	}

	private void setIdBaseToOrderedColumn() {
	    for (int c = 0; c < baseToOrderedColumn.length; c++) {
	        baseToOrderedColumn[c] = c; 
		}
	}
	
	private void refreshBaseToOrderedColumn() {
	    for (int c = 0; c < baseToOrderedColumn.length; c++) {
	        baseToOrderedColumn[baseToOrderedColumn[c]] = c;
		}
	}
	
	private void refreshOrderedToBaseColumn() {
	    for (int c = 0; c < orderedToBaseColumn.length; c++) {
	    	orderedToBaseColumn[baseToOrderedColumn[c]] = c; 
		}
	}

	private void refreshAllToVisibleColumn() {
		int h = 0;
	    for (int c = 0; c < orderedToBaseColumn.length; c++) {
	    	int baseR = orderedToBaseColumn[c];
	    	if (columnVisibilities[baseR]) {
	    		allToVisibleColumn[c] = h;
	    		h++;
	    	} else {
	    		allToVisibleColumn[c] = -1;
	    	}
		}
	}
	
	private void refreshVisibleToAllColumn() {
		int maxVisible = 0;
	    for (int c = 0; c < allToVisibleColumn.length; c++) {
	    	maxVisible = Math.max(maxVisible, allToVisibleColumn[c]); 
		}
	    visibleToAllColumn = new int[maxVisible + 1];
	    for (int c = 0; c < allToVisibleColumn.length; c++) {
	    	if (allToVisibleColumn[c] >= 0) {
	    	    visibleToAllColumn[allToVisibleColumn[c]] = c;
	    	}
		}
	}
	
	protected final int visibleToBaseRow(int row) {
		// TODO: remove
		try {
		return orderedToBaseRow[visibleToAllRow[row]];
		} catch(Exception e) {
		}
		return 0;
	}

	protected final int visibleToBaseColumn(int column) {
		// TODO: remove
		try {
		return orderedToBaseColumn[visibleToAllColumn[column]];
		} catch (Exception we) {
			
		}
		return 0;
	}
	
	protected final int baseToVisibleRow(int row) {
		return allToVisibleRow[baseToOrderedRow[row]];
	}

	protected final int baseToVisibleColumn(int column) {
		return allToVisibleColumn[baseToOrderedColumn[column]];
	}
	
	private void loadViewState() {
		loadRowsVisibilityAndOrder();
		loadColumnsVisibilityAndOrder();
	}
	
	private void saveViewState() {
	    saveRowsVisibilityAndOrder();
	    saveColumnsVisibilityAndOrder();
	}
	
	private void loadRowsVisibilityAndOrder() {
	}

	private void loadColumnsVisibilityAndOrder() {

		SpreadsheetTableViewRecord record = getViewRecord();
        SpreadsheetColumnData[] columns = record.getColumns();
		if (columns != null && columns.length > 0) {

		    // Clear data, except for the first empty column.  
	        baseToOrderedColumn[0] = 0;
	        columnVisibilities[0] = true;
			for (int c = 1; c < baseToOrderedColumn.length; c++) {
		        baseToOrderedColumn[c] = -1;
		        columnVisibilities[c] = false;
			}
			
	    	// The first empty column is always there, so loaded columns have positions starting at 1.
			int orderedIndex = 1;
			for (int c = 0; c < columns.length; c++) {
            	int index = getPropertiesColumnIndex(columns[c].getColumnName());
            	if (index >= 0) {
            		baseToOrderedColumn[index] = orderedIndex;
            		columnVisibilities[index] = !columns[c].isHidden();
            		orderedIndex++;
            	}
            }
			
		    // If not all column positions loaded, assume the rest follow in the base order.
			for (int c = 0; c < baseToOrderedColumn.length; c++) {
				if (baseToOrderedColumn[c] == -1) {
    		        baseToOrderedColumn[c] = orderedIndex;
    		        orderedIndex++;
				}
			}
			refreshOrderedToBaseColumn();
			refreshAllToVisibleColumn();
			refreshVisibleToAllColumn();
		}
	}

	private void saveRowsVisibilityAndOrder() {

		SpreadsheetTableViewRecord record = getViewRecord();
		
        // If there are hidden rows that don't appear in this view, they have to be saved, so no default. 
		boolean defaultRowVisibility = true;
        if (hiddenRowsVector.size() > 0) {
            defaultRowVisibility = false;
        } else {
        	for (int r = 0; r < viewRowCount; i++) {
        		if (!propertiesRowVisibilities[i]) {
        			defaultRowVisibility = false;
        			break;
        		}
        	}
        }
        if (!defaultRowVisibility) {
        	int namesIndex = super.getNamesColumn();
        	if (namesIndex >= 0) {
        		for (int i = 0; i < viewRowCount; i++) {
        			if (!propertiesRowVisibilities[i]) {
        				hiddenRowsVector.add(super.getPropertyAt(i, namesIndex).getValue());
        			}
        		}
            }
        	rows = new String[hiddenRowsVector.size()]; 
            hiddenRowsVector.copyInto(rows);
    		record.setHiddenRows(rows);
        } else {
    		record.setHiddenRows(null);
        }
	}

	private void saveColumnsVisibilityAndOrder() {
		
		// Default view is all columns in the starting order and all visible.
		boolean defaultColumnVisibilityAndOrder = baseToOrderedColumn.length == visibleToAllColumn.length;
        for (int c = 0; c < baseToOrderedColumn.length; c++) {
        	if (baseToOrderedColumn[c] != c) {
        		defaultColumnVisibilityAndOrder = false;
        		break;
        	}
        }

		SpreadsheetTableViewRecord record = getViewRecord();
        
        if (!defaultColumnVisibilityAndOrder) {
        	Vector columnData = new Vector();
        	// Save ordered, exclude the first.
            for (int c = 1; c < orderedToBaseColumn.length; c++) {
            	int baseIndex = orderedToBaseColumn[c];
            	String name = getPropertiesColumnNames(baseIndex);
            	columnData.add(new SpreadsheetColumnData(name, !columnVisibilities[baseIndex], true, 0));
            }
            SpreadsheetColumnData[] columns = new SpreadsheetColumnData[columnData.size()]; 
            columnData.copyInto(columns);
    		record.setColumns(columns);
        } else {
    		record.setColumns(null);
        }
	}
	
    public int getPropertyRow(int row) {
    	return visibleToBaseRow(row);
    }

    public int getPropertyColumn(int column) {
    	return visibleToBaseColumn(column);
    }
    
    public boolean isRowVisible(int row) {
		return rowVisibilities[visibleToBaseRow(row)];
    }

    public boolean isShowAllRows() {
        return showAllRows;
    }
    
    public void setShowAllRows(boolean showAllRows) {
    	this.showAllRows = showAllRows;
		refreshAllToVisibleRow();
		refreshVisibleToAllRow();
    }
    
	public void setRowsVisibility(int[] rows, boolean visible) {
		for (int r = 0; r < rows.length; r++) {
			int baseIndex = visibleToBaseRow(rows[r]);
			rowVisibilities[baseIndex] = visible;
			((SpreadsheetRowVisible)super.getPropertyAt(baseIndex, 0)).setVisible(visible);
		}
		refreshAllToVisibleRow();
		refreshVisibleToAllRow();
	}

	public void setColumnsVisibility(int[] columns, boolean visible) {
		for (int c = 0; c < columns.length; c++) {
			columnVisibilities[visibleToBaseColumn(columns[c])] = visible;
		}
		refreshAllToVisibleColumn();
		refreshVisibleToAllColumn();
	}

	
	public void setPropertyColumnsVisibility(int[] columns, boolean visible) {
		for (int c = 0; c < columns.length; c++) {
			columnVisibilities[columns[c]] = visible;
		}
		refreshAllToVisibleColumn();
		refreshVisibleToAllColumn();
	}
	
	public void repositionColumn(int startIndex, int destIndex) {

		/* Translate the indices to all columns, then perform the shift and insertion on the ordered array.
		 * Finally recreate the base and visibility arrays.  
		 */
		startIndex = visibleToAllColumn[startIndex];
		destIndex = visibleToAllColumn[destIndex];
		
		int baseMovedIndex = orderedToBaseColumn[startIndex]; 
		if (startIndex < destIndex) {
			for (int i = startIndex; i < destIndex; i++) {
				orderedToBaseColumn[i] = orderedToBaseColumn[i + 1]; 
			}
		} else {
			for (int i = startIndex; i > destIndex; i--) {
				orderedToBaseColumn[i] = orderedToBaseColumn[i - 1]; 
			}
		}
		orderedToBaseColumn[destIndex] = baseMovedIndex;
		
		refreshBaseToOrderedColumn();
		refreshAllToVisibleColumn();
		refreshVisibleToAllColumn();
	}

	public void sortRows(int column, boolean ascending) {
        column = visibleToBaseColumn(column);
		baseToOrderedRow = PropertyComparator.getOrder(getProperties(), true, column, ascending);

		refreshOrderedToBaseRow();
		refreshAllToVisibleRow();
		refreshVisibleToAllRow();
	}

	public void sortColumns(int row, boolean ascending) {
		row = visibleToBaseRow(row);
		baseToOrderedColumn = PropertyComparator.getOrder(getProperties(), false, row, ascending);

		refreshOrderedToBaseColumn();
		refreshAllToVisibleColumn();
		refreshVisibleToAllColumn();
	}
	
	/* (non-Javadoc)
	 * @see com.cosylab.vdct.inspector.SpreadsheetTableModel#deleteRows(int[])
	 */
	public void deleteRows(int[] rows) {
	    for (int r = 0; r < rows.length; r++) {
	        rows[r] = visibleToBaseRow(rows[r]);
		}
		super.deleteRows(rows);
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.inspector.SpreadsheetTableModel#getColumnClass(int)
	 */
	public Class getColumnClass(int column) {
		return super.getColumnClass(visibleToBaseColumn(column));
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.inspector.SpreadsheetTableModel#getRowCount()
	 */
	public int getRowCount() {
		return visibleToAllRow.length;
	}
	
	/* (non-Javadoc)
	 * @see com.cosylab.vdct.inspector.SpreadsheetTableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return visibleToAllColumn.length;
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.inspector.SpreadsheetTableModel#getColumnId(int)
	 */
	protected String getColumnId(int column) {
		return super.getColumnId(visibleToBaseColumn(column));
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.inspector.SpreadsheetTableModel#getHeaderDisplayType(int)
	 */
	public int getHeaderDisplayType(int column) {
	    if (column > 0) {
	    	return PropertyTableModel.HEADERDISP_TEXT;
	    }
	    return showAllRows ? PropertyTableModel.HEADERDISP_EYE : PropertyTableModel.HEADERDISP_NONE;
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.inspector.SpreadsheetTableModel#getMultilineString(int, int)
	 */
	public String getMultilineString(int row, int column) {
		return super.getMultilineString(visibleToBaseRow(row), visibleToBaseColumn(column));
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.inspector.SpreadsheetTableModel#getNamesColumn()
	 */
	protected int getNamesColumn() {
		return baseToVisibleColumn(super.getNamesColumn());
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.inspector.SpreadsheetTableModel#getPropertyAt(int, int)
	 */
	public InspectableProperty getPropertyAt(int row, int column) {
		return super.getPropertyAt(visibleToBaseRow(row), visibleToBaseColumn(column));
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.inspector.SpreadsheetTableModel#getPropertyDisplayTypeAt(int, int)
	 */
	public int getPropertyDisplayTypeAt(int row, int column) {
		return super.getPropertyDisplayTypeAt(visibleToBaseRow(row), visibleToBaseColumn(column));
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.inspector.SpreadsheetTableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		return super.getValueAt(visibleToBaseRow(rowIndex), visibleToBaseColumn(columnIndex));
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.inspector.SpreadsheetTableModel#loadView()
	 */
	protected void loadView() {
		super.loadView();
		loadViewState();
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.inspector.SpreadsheetTableModel#saveView()
	 */
	protected void saveView() {
		super.saveView();
		saveViewState();
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.inspector.SpreadsheetTableModel#setValueAt(java.lang.Object, int, int)
	 */
	public void setValueAt(Object value, int row, int column) {
		super.setValueAt(value, visibleToBaseRow(row), visibleToBaseColumn(column));
	}

	protected void setRowOrder(int[] sortOrder) {
		
		// Expand sortOrder permutation to all rows, leaving hidden where they are.
		int[] allSortOrder = new int[baseToOrderedRow.length]; 
		setId(allSortOrder);
	    for (int r = 0; r < sortOrder.length; r++) {
	    	allSortOrder[visibleToAllRow[r]] = visibleToAllRow[sortOrder[r]];
	    }
	    
		// New orderToBase order is first the sortOrder, and then the previous orderToBase.
	    for (int r = 0; r < baseToOrderedRow.length; r++) {
	    	allSortOrder[r] = orderedToBaseRow[allSortOrder[r]];
		}
	    orderedToBaseRow = allSortOrder;
	    refreshBaseToOrderedRow();
	}
	
	// TODO: remove
	private void displayOrder(String name, int[] order) {
		System.out.print(name + "[");
	    for (int r = 0; r < order.length; r++) {
			System.out.print(order[r] + " ");
		}
		System.out.println("]");
	}

	protected void setColumnOrder(int[] sortOrder) {
		// TODO: copy above
	    for (int c = 0; c < sortOrder.length; c++) {
	    	orderedToBaseColumn[visibleToAllColumn[c]] = visibleToAllColumn[sortOrder[c]];
		}
		refreshBaseToOrderedColumn();
	}
	
	/* (non-Javadoc)
	 * @see com.cosylab.vdct.inspector.SpreadsheetTableModel#getLoadedInspectablesNames()
	 */
	protected Set getLoadedInspectablesNames() {
		// TODO should be handled by persistence, or at least moved to this level
		return super.getLoadedInspectablesNames();
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.inspector.SpreadsheetTableModel#refreshProperties()
	 */
	protected void refreshProperties() {
		super.refreshProperties();
		// TODO move to a single refresh function stack
		refreshAll();
	}
}
