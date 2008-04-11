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

import java.awt.Color;
import java.util.Arrays;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.cosylab.vdct.plugin.debug.PluginDebugManager;
import com.cosylab.vdct.undo.UndoManager;
import com.cosylab.vdct.vdb.CommentProperty;

/**
 * @author ssah
 *
 */
public class SpreadsheetSplitViewModel extends SpreadsheetViewModel {

	private SpreadsheetTable table = null;
	private SpreadsheetRowComparator comparator = null;
	
    private Vector recentSplitData = null;
	private int modelRowCount = 0;
    private Color defaultBackground = null;
    private Color background = null;
    private SplitData[] propertiesColumnSplitData = null;
    private int propertiesToModelColumnIndex[] = null;
    private int modelToPropertiesColumnIndex[] = null;
    
    private int modelColumnCount = 0;
	private InspectableProperty[][] model = null;
	private String[] modelColumnNames = null;

	// The current sorted state.
    private int sortedSplitIndex = 0;

    // The maximum number of recent entries for splitting columns.
    private static final int recentSplitDataMaxCount = 8;

    private boolean showAllRows = true;

	private boolean[] propertiesRowVisibilities = null;
	private boolean[] propertiesColumnVisibilities = null;
    
	// The current sorted state.
    private int sortedPropertiesColumn = -1;
    private boolean sortedOrderAsc = true;     
	
	/**
	 * @param dataType
	 * @param displayData
	 * @param loadedData
	 * @throws IllegalArgumentException
	 */
	public SpreadsheetSplitViewModel(String dataType, Vector displayData,
			Vector loadedData) throws IllegalArgumentException {
		super(dataType, displayData, loadedData);
		comparator = new SpreadsheetRowComparator(this);
        recentSplitData = new Vector();
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.inspector.SpreadsheetViewModel#setShowAllRows(boolean)
	 */
	public void setShowAllRows(boolean showAllRows) {
		super.setShowAllRows(showAllRows);
		refreshColumnModel();
	}
	
	/* (non-Javadoc)
	 * @see com.cosylab.vdct.inspector.SpreadsheetViewModel#setRowsVisibility(int[], boolean)
	 */
	public void setRowsVisibility(int[] rows, boolean visible) {
		super.setRowsVisibility(rows, visible);
		refreshColumnModel();
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.inspector.SpreadsheetViewModel#setColumnsVisibility(int[], boolean)
	 */
	public void setColumnsVisibility(int[] columns, boolean visible) {

		for (int c = 0; c < columns.length; c++) {
			columns[c] = modelToPropertiesColumnIndex[columns[c]];
		}
		super.setColumnsVisibility(columns, visible);
		refreshColumnModel();
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.inspector.SpreadsheetViewModel#setPropertyColumnVisibility(int, boolean)
	 */
	public void setPropertyColumnsVisibility(int[] columns, boolean visible) {
		super.setPropertyColumnsVisibility(columns, visible);
		refreshColumnModel();
	}
	
	/* (non-Javadoc)
	 * @see com.cosylab.vdct.inspector.SpreadsheetViewModel#moveColumn(int, int)
	 */
	public void repositionColumn(int startIndex, int destIndex) {
		super.repositionColumn(modelToPropertiesColumnIndex[startIndex], modelToPropertiesColumnIndex[destIndex]);
		refreshColumnModel();
		// TODO: remove
		/*
		System.out.print("repositionColumn");
        TableColumnModel columnModel = table.getTableHeader().getColumnModel();
		for (int i = 0; i < columnModel.getColumnCount(); i++) {
			System.out.print("[" + i + "->" + columnModel.getColumn(i).getModelIndex() + "]");
		}
		System.out.println("");
		*/
	}

	
	public void sortRows(int modelColumn) {
		int newSortedPropertiesColumn = modelToPropertiesColumnIndex[modelColumn];
		int newSortedSplitIndex = modelColumn - propertiesToModelColumnIndex[newSortedPropertiesColumn];

		if (sortedPropertiesColumn == newSortedPropertiesColumn && sortedSplitIndex == newSortedSplitIndex) {
			sortedOrderAsc = !sortedOrderAsc;
		} else {
			sortedPropertiesColumn = newSortedPropertiesColumn;
			sortedSplitIndex = newSortedSplitIndex;
    		sortedOrderAsc = true;
        }
		int[] order = PropertyComparator.getOrder(model, true, modelColumn, sortedOrderAsc);
        setRowOrder(order);
		refreshColumnModel();
		
		// TODO: remove
		//displayModel();		
		/*
		System.out.print("sortRows");
        TableColumnModel columnModel = table.getTableHeader().getColumnModel();
		for (int i = 0; i < columnModel.getColumnCount(); i++) {
			System.out.print("[" + i + "->" + columnModel.getColumn(i).getModelIndex() + "]");
		}
		System.out.println("");
		*/
	}
	
	/* (non-Javadoc)
	 * @see com.cosylab.vdct.inspector.SpreadsheetViewModel#getColumnClass(int)
	 */
	public Class getColumnClass(int column) {
	   return super.getColumnClass(modelToPropertiesColumnIndex[column]);
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.inspector.SpreadsheetViewModel#getColumnId(int)
	 */
	protected String getColumnId(int column) {
		return super.getColumnId(modelToPropertiesColumnIndex[column]);
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.inspector.SpreadsheetViewModel#getNamesColumn()
	 */
	protected int getNamesColumn() {
		return propertiesToModelColumnIndex[super.getNamesColumn()];
	}

	/* (non-Javadoc)
	 * @see com.cosylab.vdct.inspector.SpreadsheetViewModel#getPropertyDisplayTypeAt(int, int)
	 */
	public int getPropertyDisplayTypeAt(int row, int column) {
		return super.getPropertyDisplayTypeAt(modelToPropertiesRowIndex(row), modelToPropertiesColumnIndex[column]);
	}

	public void setTable(SpreadsheetTable table) {
	    this.table = table;
	}

	public void refresh() {
		saveView();
		refreshProperties();
		refreshModel();
		loadView();
	}

	public SpreadsheetTable getTable() {
	    return table;
	}

	/* Returns the multi-line string associated with the cell at the given position, or null it there is
	 * none. 
	 */
	public String getMultilineString(int row, int column) { 
		// Check if the cell is part of the comment, if so return it.
		if (row >= 0 && column >= 0) {
			InspectableProperty property = model[row][column];
			InspectableProperty baseProperty = property;
			if (baseProperty instanceof SplitPropertyPart) {
			    baseProperty = ((SplitPropertyPart)baseProperty).getOwner().getOwner();
			}
			
			if (baseProperty instanceof CommentProperty) {
				String value = property.getValue();
				return (value == null) ? "" : value;
			}
		}
		return null;
	}
	
	protected final int modelToPropertiesColumn(int column) {
		return super.visibleToBaseColumn(modelToPropertiesColumnIndex[column]);
	}
	protected final int modelToPropertiesRow(int row) {
		// TODO
		return row;//super.visibleToBaseRow(modelToPropertiesRowIndex(row));
	}

	public boolean isSplit(int column) {
		return propertiesColumnSplitData[modelToPropertiesColumn(column)] != null;		
	}

    public int getPropertyRow(int row) {
    	return super.getPropertyRow(modelToPropertiesRow(row));
    }

    public int getPropertyColumn(int column) {
    	return super.getPropertyColumn(modelToPropertiesColumnIndex[column]);
    }
	
	public void setColumnVisibility(boolean visible, int column) {
		int propertiesColumn = modelToPropertiesColumn(column);
		propertiesColumnVisibilities[propertiesColumn] = visible;
		updateColumnVisibility();
	}

	public int getModelToPropertiesColumnIndex(int column) {
		return modelToPropertiesColumnIndex[column];
	}
	
	public Vector getRecentSplitData() {
		return recentSplitData;
	}
	
	public int getModelRowCount() {
		return modelRowCount;
	}
	
	public String getModelValue(int row, int column) {
		return model[row][column].getValue();
	}

	public int getRecentSplitDataMaxCount() {
		return recentSplitDataMaxCount;
	}
	
	public void splitColumn(SplitData splitData, int column) {
		int propertiesColumn = modelToPropertiesColumn(column); 
		if (splitData != null) {
			splitData.setName(getPropertiesColumnNames(propertiesColumn));
		}
		propertiesColumnSplitData[propertiesColumn] = splitData;
		refreshModel();
		// refresh
		saveView();
		loadView();
	}

	public Color getBackground() {
		return background;
	}
	
	public void setBackground(Color background) {
		this.background = background;
	}
	
	public Color getDefaultBackground() {
		return defaultBackground;
	}

	public void setDefaultBackground(Color defaultBackground) {
		this.defaultBackground = defaultBackground;
	}
	
	public void updateSortedColumn(int modelColumn) {
		/*
		int newSortedPropertiesColumn = modelToPropertiesColumnIndex[modelColumn];
		int newSortedSplitIndex = modelColumn - propertiesToModelColumnIndex[newSortedPropertiesColumn];

		if (sortedPropertiesColumn == newSortedPropertiesColumn && sortedSplitIndex == newSortedSplitIndex) {
			sortedOrderAsc = !sortedOrderAsc;
		} else {
			sortedPropertiesColumn = newSortedPropertiesColumn;
			sortedSplitIndex = newSortedSplitIndex;
    		sortedOrderAsc = true;
        }
		sortModelRows();
		*/
	}

	public void splitColumnByRecentList(int recentIndex, int column) {
		// Add the used item to the top of the list.
		SplitData data = (SplitData)recentSplitData.remove(recentIndex);
		recentSplitData.add(0, data);

		String name = super.getColumnId(modelToPropertiesColumnIndex[column]);
		SplitData split = new SplitData(name, data.getDelimiterTypeString(), data.getPattern());
		splitColumn(split, column);
	}
	
	public void setColumnOrder(String modeName) {
		
		int newMode = getColumnOrderIndex(modeName);
		if (newMode != -1) {
			saveSplitData();
			saveViewData();

			setColumnOrderIndex(newMode);

			loadSplitData();
			loadViewRowStateData();
			clearColumnModel();
			createDefaultColumnModel();
			loadViewColumnHiddenStateData();
		}
	}
	
	public void refreshOnColumnDragEnd() {
		saveView();
		loadView();
	}
	
	/* Returns -1 if the column at the index should not be dragged. 
	 */
	public int validateDraggedColumnIndex(int columnIndex) {

		// Change the dragged column to first of a split group. 
		int firstOfSplits = propertiesToModelColumnIndex[modelToPropertiesColumnIndex[columnIndex]];
		
		// The first empty column must not be dragged. 
		return (firstOfSplits > 0) ? firstOfSplits : -1;
	}

	// TODO: remove
	/*
	public void setShowAllRows(boolean state) {
		showAllRows = state;
		refreshModel();
		fireTableDataChanged();
	}
	*/

	/*
	public void setRowVisibility(boolean visible, int[] rows) {
		int propertyIndex = 0;
		for (int r = 0; r < rows.length; r++) {
			propertyIndex = modelToPropertiesRowIndex(rows[r]);
			propertiesRowVisibilities[propertyIndex] = visible;
			((SpreadsheetRowVisible)super.getPropertyAt(propertyIndex, 0)).setVisible(visible);
		}
		refreshModel();
		fireTableDataChanged();
	}
	*/
	
	public void deleteRows(int[] rows) {
		
		int[] propertiesRows = new int[rows.length];
		for (int r = 0; r < rows.length; r++) {
			propertiesRows[r] = modelToPropertiesRowIndex(rows[r]);
		}
		super.deleteRows(propertiesRows);
	}
    /*
    public void switchRowHiddenState(int row) {
    	int propertyIndex = modelToPropertiesRowIndex(row);
    	boolean state = !propertiesRowVisibilities[propertyIndex];
    	propertiesRowVisibilities[propertyIndex] = state;
    	((SpreadsheetRowVisible)super.getPropertyAt(propertyIndex, 0)).setVisible(state);
    	fireTableCellUpdated(row, 0);
    }
    */
    public int getColumnCount() {
		return modelColumnCount;
	}

	public int getRowCount() {
		return modelRowCount;
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
	    String value = model[rowIndex][columnIndex].getValue();
		// TODO: The only properties with null values are CommentProperty. This could maybe be fixed.
	    return (value != null) ? value : ""; 
	}

	public void setValueAt(Object aValue, int row, int column) {

		InspectableProperty property = model[row][column];

		int propertiesRow = modelToPropertiesRowIndex(row);
		int propertiesColumn = modelToPropertiesColumnIndex[column];
		
		if (property instanceof SplitPropertyPart) {
			/* If this split property is part of creator property, all other split parts are empty, so just set
			 * the current value in the base property and set the new created property as the owner of this group.  
			 */
			SplitPropertyGroup propertyGroup = ((SplitPropertyPart)property).getOwner();
			InspectableProperty baseProperty = propertyGroup.getOwner(); 
			
			if (baseProperty instanceof CreatorProperty) {
				super.setValueAt(aValue, propertiesRow, propertiesColumn);
				propertyGroup.setOwner(((CreatorProperty)baseProperty).getCreatedProperty());
			}
			property.setValue(aValue.toString());
		} else {
			/* If this isn't a split part, just call the base function and update the model data structure if a new
			 * property has been created by creator property.
			 */
			super.setValueAt(aValue, propertiesRow, propertiesColumn);
			if (property instanceof CreatorProperty) {
				model[row][column] = ((CreatorProperty)property).getCreatedProperty();
			}
		}
		
		/* TODO: fast renaming
		 * On name change, if the whole table is not refreshed, at least splitting has to be recalculated as the
		 * field values that point to the name change.
		 */
		/*
		if (propertiesColumn == getNamesColumn()) {
   			refreshModel();
		}
		*/
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// no editing in debug
		if (PluginDebugManager.isDebugState()) {
			return false;
		}
		return model[rowIndex][columnIndex].isEditable();
	}

	public InspectableProperty getPropertyAt(int row, int column) {
		return model[row][column];
	}
	
	public int getHeaderDisplayType(int column) {
	    return super.getHeaderDisplayType(modelToPropertiesColumn(column));
	}

	public void extendCounters(int[] rows, int[] columns) {
		
		UndoManager.getInstance().startMacroAction();
		for (int c = 0; c < columns.length; c++) {
			String firstEntry = model[rows[0]][columns[c]].getValue();
			String secondEntry = model[rows[1]][columns[c]].getValue();
			String baseString = SplitData.removeValueAtEnd(firstEntry);
			int firstValue = SplitData.extractValueAtEnd(firstEntry);
			int secondValue = SplitData.extractValueAtEnd(secondEntry);

			// Assume base at 0 and step 1 if values are absent.
			if (firstValue == -1) {
				firstValue = 0;
			}
			if (secondValue == -1 || secondValue == firstValue) {
				secondValue = firstValue + 1;
			}
			
			int value = firstValue;
			int step = secondValue - firstValue;

			for (int r = 0; r < rows.length; r++) {
				model[rows[r]][columns[c]].setValue(baseString + value);
				value += step;
				fireTableCellUpdated(rows[r], columns[c]);
			}
		}
        UndoManager.getInstance().stopMacroAction();
	}
	
	public boolean isModelRowVisible(int row) {
	    return propertiesRowVisibilities[modelToPropertiesRowIndex(row)];
	}

	public void saveView() {
		
		super.saveView();		
		saveSplitData();
		saveViewData();
	}

	protected void sortModelRows() {
		// TODO
		/*
		if (sortedPropertiesColumn >= 0) {
			comparator.setColumn(propertiesToModelColumnIndex[sortedPropertiesColumn] + sortedSplitIndex);
			comparator.setAscending(sortedOrderAsc);
			Arrays.sort(model, comparator);
			fireTableDataChanged();
		}
		*/
	}
	

    protected int modelToPropertiesRowIndex(int modelRow) {
    	return modelRow;
    	// TODO: remove
    	/*
    	int propertiesNamesColumnIndex = super.getNamesColumn();
    	if (propertiesNamesColumnIndex == -1) {
    		return -1;
    	}
    	String rowName = model[modelRow][propertiesToModelColumnIndex[propertiesNamesColumnIndex]].getValue();
    	return getPropertiesRowIndex(rowName);
    	*/
    }

	private void refreshModel() {

		int viewColumnCount = super.getColumnCount();
		int viewRowCount = super.getRowCount();
		updateSplitData();
        
		// Create a translation table from visible to all rows.
		modelRowCount = 0; 
		for (int i = 0; i < viewRowCount; i++) {
			if (showAllRows || propertiesRowVisibilities[i]) {
				modelRowCount++;
			}
		}
		
		int[] modelToPropertiesRowIndex = new int[modelRowCount];
		int modelRow = 0; 
		for (int i = 0; i < viewRowCount; i++) {
			if (showAllRows || propertiesRowVisibilities[i]) {
				modelToPropertiesRowIndex[modelRow] = i;
				modelRow++;
			}
		}
		
		model = new InspectableProperty[modelRowCount][];
		for (int i = 0; i < modelRowCount; i++) {
			model[i] = new InspectableProperty[modelColumnCount];
		}
		
		// Set the model column names and model data according to split columns.
		modelColumnNames = new String[modelColumnCount];
		for (int j = 0; j < viewColumnCount; j++) {

		    String name = super.getColumnId(j);
			SplitData split = propertiesColumnSplitData[super.visibleToBaseColumn(j)]; 
			int modelJ = propertiesToModelColumnIndex[j];

			if (split != null) {

	    		String colName = null;
		    	int parts = split.getParts();
				for (int p = 0; p < parts; p++) {
					colName = "";
					if (p == 0) {
						colName += "<";
					}
					colName += name + "[" + (p + 1) + "]";
					if (p == parts - 1) {
						colName += ">";
					}
			    	modelColumnNames[modelJ + p] = colName;
				}
				
				for (int i = 0; i < modelRowCount; i++) {
					int propertyRow = modelToPropertiesRowIndex[i];
					SplitPropertyGroup group = new SplitPropertyGroup(super.getPropertyAt(propertyRow, j), split);
					for (int p = 0; p < parts; p++) {
						model[i][modelJ + p] = group.getPart(p);
					}
				}
			} else {
		    	modelColumnNames[modelJ] = name;
				for (int i = 0; i < modelRowCount; i++) {
					model[i][modelJ] = super.getPropertyAt(modelToPropertiesRowIndex[i], j);
				}
            }
		}
		sortModelRows();
	}

	private void updateSplitData() {

		int propertiesRowCount = getPropertiesRowCount();
		int propertiesColumnCount = getPropertiesColumnCount();
		
		// Calculate the required parts to which the a specific column must be split into.
		for (int j = 0; j < propertiesColumnCount; j++) {
			SplitData split = propertiesColumnSplitData[j];
			if (split != null) {
				int maxParts = 1;
				for (int i = 0; i < propertiesRowCount; i++) {
					String value = getPropertyValue(i, j);
					int parts = SplitPropertyGroup.getPartsCount(value, split);
					maxParts = Math.max(maxParts, parts);
				}
				split.setParts(maxParts);
			}
		}

		int viewColumnCount = super.getColumnCount();
		
		// Create split translation tables.
		SplitData splitData = null;
		modelColumnCount = 0;
		for (int j = 0; j < viewColumnCount; j++) {
			splitData = propertiesColumnSplitData[super.visibleToBaseColumn(j)];
			modelColumnCount += (splitData != null) ? splitData.getParts() : 1;
		}
		
		propertiesToModelColumnIndex = new int[viewColumnCount];
		modelToPropertiesColumnIndex = new int[modelColumnCount];
		
		int col = 0;
		for (int j = 0; j < viewColumnCount; j++) {
		    propertiesToModelColumnIndex[j] = col;
			splitData = propertiesColumnSplitData[super.visibleToBaseColumn(j)];
		    int parts = (splitData != null) ? splitData.getParts() : 1;
		    for (int p = 0; p < parts; p++) {
		    	modelToPropertiesColumnIndex[col] = j;
		    	col++;
		    }
		}
	}
	
	private void updateColumnVisibility() {
		// TODO
		
		/*
		TableColumnModel columnModel = table.getTableHeader().getColumnModel();
		
		int lastIndex = -1;
		for (int j = 0; j < modelColumnCount; j++) {
			
			int index = table.convertColumnIndexToView(j);
			TableColumn column = index >= 0 ? columnModel.getColumn(index) : null;
			
			boolean visible = propertiesColumnVisibilities[modelToPropertiesColumn(j)];
			boolean first = propertiesToModelColumnIndex[modelToPropertiesColumnIndex[j]] == j;
			
			if (column != null && !visible) {
				columnModel.removeColumn(column);
			}
			if (column == null && visible) {
				column = createColumn(j, first, true);
	    		// Set the identifier to the first of the split columns only. This is used when saving the order.
				columnModel.addColumn(column);
				*/
				/* If a column is shown, place it after the column where it would appear if the columns were not
				 * reordered. 
				 */
		/*
				columnModel.moveColumn(columnModel.getColumnCount() - 1, lastIndex + 1);
				lastIndex++;
			}
			if (index >= 0) {
				lastIndex = index;
			}
		}
        */
	}
	
	private void clearColumnModel() {
		// TODO
		/*
		for (int j = 0; j < getPropertiesColumnCount(); j++) {
    	    propertiesColumnVisibilities[j] = false;
    	}
		*/
        TableColumnModel columnModel = table.getTableHeader().getColumnModel();
        while (columnModel.getColumnCount() > 0) {
        	columnModel.removeColumn(columnModel.getColumn(0));
        }
	}
	
	private void removeColumns(int propIndex) {
        TableColumnModel columnModel = table.getTableHeader().getColumnModel();
        
        int parts = 1;
		if (propertiesColumnSplitData[propIndex] != null) {
			parts = propertiesColumnSplitData[propIndex].getParts();
		}
		
		int colStart = super.baseToVisibleColumn(propIndex);
		if (colStart < 0) {
			return;
		}
		colStart = propertiesToModelColumnIndex[colStart];
    	int tableViewIndex = 0;
    	for (int p = 0; p < parts; p++) {
	    	// Set the identifier to the first of the split columns only. This is used when saving the order.
        	tableViewIndex = table.convertColumnIndexToView(colStart + p);
    		columnModel.removeColumn(columnModel.getColumn(tableViewIndex));
    	}
    	propertiesColumnVisibilities[propIndex] = false;
	}
	
	private void createDefaultColumnModel() {
		// TODO: remove other version
        TableColumnModel columnModel = table.getTableHeader().getColumnModel();
		for (int j = 0; j < modelColumnCount; j++) {
			boolean isSplitFirst = propertiesToModelColumnIndex[modelToPropertiesColumnIndex[j]] == j;
    		TableColumn column = createColumn(j, isSplitFirst, true);
		    columnModel.addColumn(column);
		}
        /*		
		for (int j = 0; j < super.getColumnCount(); j++) {
			addColumns(super.visibleToBaseColumn(j), true);
		}
		*/
	} 
	
	private void addColumns(int propIndex, boolean defaultWidth) {

        TableColumnModel columnModel = table.getTableHeader().getColumnModel();
        
        int parts = 1;
		if (propertiesColumnSplitData[propIndex] != null) {
			parts = propertiesColumnSplitData[propIndex].getParts();
		}
		
		int colStart = super.baseToVisibleColumn(propIndex);
		if (colStart < 0) {
			return;
		}
		colStart = propertiesToModelColumnIndex[colStart];
		    
    	for (int p = 0; p < parts; p++) {
			TableColumn column = createColumn(colStart + p, p == 0, defaultWidth);
    		// Set the identifier to the first of the split columns only. This is used when saving the order.
    		columnModel.addColumn(column);
    	}
    	propertiesColumnVisibilities[propIndex] = true;
	}
	
	private TableColumn createColumn(int colIndex, boolean identifier, boolean defaultWidth) {
		TableColumn column = new SpreadsheetColumn(defaultWidth);
		column.setModelIndex(colIndex);
		column.setHeaderRenderer(table.getDefaultRenderer(String.class));
		String name = super.getColumnId(modelToPropertiesColumnIndex[colIndex]);
		if (name.equals(propertiesCommentsColumn)) {
			column.setCellEditor(new DefaultCellEditor(new JTextField()){
				public boolean isCellEditable(EventObject anEvent) {
					return false;
				}
			});
		}
		column.setIdentifier(identifier ? name : null);
		column.setHeaderValue(modelColumnNames[colIndex]);
		return column;
	}

	
	protected void loadView() {

		super.loadView();
		SpreadsheetTableViewRecord record = SpreadsheetTableViewData.getInstance().get(typeSign + dataType);

		// If no record, make default model.
		if (record == null) {
			refreshModel();
		    background = defaultBackground;
    	    createDefaultColumnModel();
			return;
		}
		
		loadSplitData();
		loadViewData();
	}
	
	protected void loadSplitData() {
		SpreadsheetTableViewRecord record = getViewRecord();

		// Set the split data and refresh the model.
    	for (int j = 0; j < getPropertiesColumnCount(); j++) {
    		propertiesColumnSplitData[j] = null;
    	}
    	SplitData[] splitColumns = record.getSplitColumns();
    	if (splitColumns != null) {
    		for (int j = 0; j < splitColumns.length; j++) {
    			int index = getPropertiesColumnIndex(splitColumns[j].getName());
    			if (index >= 0) {
    				propertiesColumnSplitData[index] = splitColumns[j];
    			}
    		}
    	}
		refreshModel();
    	
    	SplitData[] recentSplits = record.getRecentSplits();
    	if (recentSplits != null) {
    		recentSplitData.clear();
    		for (int j = 0; j < recentSplits.length; j++) {
        		recentSplitData.add(recentSplits[j]);
    		}
    	}
	}
	
	protected void loadViewData() {
		SpreadsheetTableViewRecord record = getViewRecord();

		Integer recBackgroundColor = record.getBackgroundColor(); 
		if (recBackgroundColor != null) {
			background = new Color(recBackgroundColor.intValue());
		}
		
		loadViewRowStateData();
		loadViewColumnOrderStateData();
		loadViewColumnHiddenStateData();
	}

	protected void loadViewRowStateData() {
		SpreadsheetTableViewRecord record = getViewRecord();
		
		Boolean recShowAllRows = record.getShowAllRows();
		if (recShowAllRows != null && showAllRows != recShowAllRows.booleanValue()) {
			showAllRows = recShowAllRows.booleanValue();
		}
		
    	// If no rows, assume all are visible. 
        for (int i = 0; i < super.getRowCount(); i++) {
        	propertiesRowVisibilities[i] = true;
			((SpreadsheetRowVisible)super.getPropertyAt(i, 0)).setVisible(true);
        }
		String[] rows = record.getHiddenRows();
		if (rows != null && rows.length > 0) {
            for (int i = 0; i < rows.length; i++) {
            	int propertiesIndex = getPropertiesRowIndex(rows[i]);
            	if (propertiesIndex >= 0) {
            		propertiesRowVisibilities[propertiesIndex] = false;
        			((SpreadsheetRowVisible)super.getPropertyAt(propertiesIndex, 0)).setVisible(false);
            	}
            }
    	}
		refreshModel();

    	SpreadsheetRowOrder rowOrder = record.getRowOrder();
        if (rowOrder != null) {
        	String orderedColumnName = rowOrder.getColumnName();
        	int index = getPropertiesColumnIndex(orderedColumnName);
        	if (index >= 0) {
        		int splitIndex = rowOrder.getColumnSplitIndex();
        		SplitData splitData = propertiesColumnSplitData[index];
        		if (splitData == null || splitIndex >= splitData.getParts()) {
        			splitIndex = 0;
        		}
        		sortedPropertiesColumn = index;
        		sortedSplitIndex = splitIndex;
        		sortedOrderAsc = rowOrder.isAscending();
        		sortModelRows();
        	}
        }
	}

	protected void loadViewColumnOrderStateData() {
		clearColumnModel();
	    createDefaultColumnModel();

    	// TODO
		/*
		SpreadsheetTableViewRecord record = getViewRecord();
		
		clearColumnModel();
        
    	// If no columns, assume default view.
        SpreadsheetColumnData[] columns = record.getColumns();
		if (columns != null && columns.length > 0) {
	    	// Add the first empty column.
    		addColumns(0, true);
            for (int j = 0; j < columns.length; j++) {
            	int index = getPropertiesColumnIndex(columns[j].getColumnName());
    		    if (index >= 0) {
    		    	addColumns(index, columns[j].isDefaultWidth());
    		    }
            }
    	} else {
    	    createDefaultColumnModel();
    	}
    	*/
	}
	
	protected void loadViewColumnHiddenStateData() {

		// TODO
		/*
		SpreadsheetTableViewRecord record = getViewRecord();
		SpreadsheetColumnData[] columns = record.getColumns();
		if (columns != null) {
			// Remove all hidden from view.
            for (int j = 0; j < columns.length; j++) {
            	int index = getPropertiesColumnIndex(columns[j].getColumnName());
    	    	if (index >= 0 && columns[j].isHidden()) {
    		    	removeColumns(index);
    		    }
            }
    	}
    	*/
	}

	protected void saveSplitData() {
		SpreadsheetTableViewRecord record = getViewRecord();
		
		boolean defaultNoColumnSplit = true;
		int propertiesColumnCount = getPropertiesColumnCount();
        for (int j = 0; j < propertiesColumnCount; j++) {
        	if (propertiesColumnSplitData[j] != null) {
        		defaultNoColumnSplit = false;
        		break;
        	}
        }
        
        if (!defaultNoColumnSplit) {
            SplitData[] splitColumns = null;
        	Vector splitColumnVector = new Vector();
        	for (int j = 0; j < propertiesColumnCount; j++) {
        		if (propertiesColumnSplitData[j] != null) {
            		splitColumnVector.add(propertiesColumnSplitData[j]);
        		}
        	}
        	splitColumns = new SplitData[splitColumnVector.size()]; 
            splitColumnVector.copyInto(splitColumns);
    		record.setSplitColumns(splitColumns);
        } else {
    		record.setSplitColumns(null);
        }

		boolean defaultNoRecentSplits = recentSplitData.isEmpty();
        if (!defaultNoRecentSplits) {
        	SplitData[] recentSplits = new SplitData[recentSplitData.size()]; 
        	recentSplitData.copyInto(recentSplits);
    		record.setRecentSplits(recentSplits);
        } else {
    		record.setRecentSplits(null);
        }
	}
	
	protected void saveViewData() {
		SpreadsheetTableViewRecord record = getViewRecord();
		
		//continue here

        // Stored hidden rows must be preserved if they exist in the database and don't appear in the new view.
		Vector hiddenRowsVector = new Vector();
		String[] rows = record.getHiddenRows();
		if (rows != null && rows.length > 0) {
			for (int i = 0; i < rows.length; i++) {
				int propertiesIndex = getPropertiesRowIndex(rows[i]);
				if (propertiesIndex < 0 && getLoadedInspectablesNames().contains(rows[i])) {
					hiddenRowsVector.add(rows[i]);
				}
			}
		}

		// Determine if the parts of the view are default.
		boolean defaultShowAllRows = showAllRows;
        if (!defaultShowAllRows) {
        	record.setShowAllRows(Boolean.valueOf(showAllRows));
        } else {
        	record.setShowAllRows(null);
        }

		boolean defaultBackgroundColour = background.equals(defaultBackground);
        if (!defaultBackgroundColour) {
        	record.setBackgroundColor(new Integer(background.getRGB()));
        } else {
        	record.setBackgroundColor(null);
        }
        
		boolean defaultNoSortOrder = (sortedPropertiesColumn == -1);
        if (!defaultNoSortOrder) {
        	String name = super.getColumnId(sortedPropertiesColumn);
        	record.setRowOrder(new SpreadsheetRowOrder(name, sortedSplitIndex, sortedOrderAsc));
        } else {
        	record.setRowOrder(null);
        }
		
		// TODO
		/*
		boolean defaultColumnVisibilityAndOrder = true;
		int prevColumnViewPosition = -1;
		int propertiesColumnCount = super.getColumnCount();
		int viewRowCount = super.getRowCount();
		int viewColumnCount = super.getColumnCount();
        for (int j = 0; j < propertiesColumnCount; j++) {
        	int columnViewPosition = table.convertColumnIndexToView(propertiesToModelColumnIndex[super.baseToVisibleColumn(j)]);
        	if (prevColumnViewPosition > columnViewPosition || !propertiesColumnVisibilities[j]) {
        		defaultColumnVisibilityAndOrder = false;
        	}
        	prevColumnViewPosition = columnViewPosition;
        }

        if (!defaultColumnVisibilityAndOrder) {
        	Vector columnStrings = new Vector();

        	TableColumnModel columnModel = table.getTableHeader().getColumnModel();
        	SpreadsheetColumn column = null;
        	String name = null;
            int columnCount = columnModel.getColumnCount();
            // Store the first columns of the split ones, except for the first which is empty.
            for (int j = 1; j < columnCount; j++) {
            	column = (SpreadsheetColumn)columnModel.getColumn(j);
            	name = (String)column.getIdentifier();
            	if (name != null) {
        		    columnStrings.add(new SpreadsheetColumnData(name, false,
        		    		column.isDefaultWidth(), column.getPreferredWidth()));
            	}
    		}
            // Then store all the hidden columns.
            for (int j = 1; j < propertiesColumnCount; j++) {
            	if (!propertiesColumnVisibilities[j]) {
        		    columnStrings.add(new SpreadsheetColumnData(getPropertiesColumnNames(j), true, true, 0));
            	}
            }
            
            SpreadsheetColumnData[] columns = new SpreadsheetColumnData[columnStrings.size()]; 
            columnStrings.copyInto(columns);
    		record.setColumns(columns);
        } else {
    		record.setColumns(null);
        }
        */
        
		int viewRowCount = super.getRowCount();
		boolean defaultRowVisibility = true;
        // If there are hidden rows that don't appear in this view, they have to be saved, so no default. 
        if (hiddenRowsVector.size() > 0) {
            defaultRowVisibility = false;
        } else {
        	for (int i = 0; i < viewRowCount; i++) {
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
	
	public void setPropertiesColumnVisibility(boolean visible, int column) {
		propertiesColumnVisibilities[column] = visible;
		updateColumnVisibility();
	}

	public boolean isPropertiesColumnVisible(int column) {
	    return propertiesColumnVisibilities[column];
	}
	
	public void refreshView() {
	    loadView();
	}

	protected void setColumnOrderIndex(int mode) {
		super.setColumnOrderIndex(mode);
		refreshModel();
	}
	
	protected void refreshProperties() {
		
		super.refreshProperties();

		// Prepare space for properties column data.
		int propertiesColumnCount = getPropertiesColumnCount();
		propertiesColumnSplitData = new SplitData[propertiesColumnCount];
		for (int j = 0; j < propertiesColumnCount; j++) {
		    propertiesColumnSplitData[j] = null;
		}
		
		propertiesColumnVisibilities = new boolean[propertiesColumnCount]; 
		for (int j = 0; j < propertiesColumnCount; j++) {
			propertiesColumnVisibilities[j] = false;
		}

		// Prepare space for row data.
		int viewRowCount = super.getRowCount();
		propertiesRowVisibilities = new boolean[viewRowCount]; 
		for (int i = 0; i < viewRowCount; i++) {
			propertiesRowVisibilities[i] = true;
		}
	}
	
	// TODO: better refresh hierarchy  
	private void refreshColumnModel() {
		refreshModel();
		clearColumnModel();
	    createDefaultColumnModel();
	}
	
	// TODO: remove
	public void displayModel() {
		for (int i = 0; i < super.getPropertiesRowCount(); i++) {
			System.out.print("[" + getPropertyValue(i, 1) + "]");
		}
		System.out.println();
		
		for (int i = 0; i < super.getRowCount(); i++) {
			System.out.print("[" + getPropertyAt(super.visibleToBaseRow(i), 1).getValue() + "]");
		}
		System.out.println();
		
		for (int i = 0; i < getRowCount(); i++) {
			System.out.print("[" + model[i][1].getValue() + "]");
		}
		System.out.println();
	}
}
