package com.cosylab.vdct.inspector;

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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.cosylab.vdct.graphics.DrawingSurface;
import com.cosylab.vdct.graphics.objects.Record;
import com.cosylab.vdct.graphics.objects.Template;
import com.cosylab.vdct.graphics.objects.VisibleObject;
import com.cosylab.vdct.plugin.debug.PluginDebugManager;
import com.cosylab.vdct.undo.DeleteAction;
import com.cosylab.vdct.undo.UndoManager;
import com.cosylab.vdct.vdb.CommentProperty;

public class SpreadsheetTableModel extends AbstractTableModel implements PropertyTableModel {

    // TODO mark
	//class Dummy {
	
    private SpreadsheetTable table = null;
	private SpreadsheetRowComparator comparator = null;
	
    private Vector recentSplitData = null;
	
	private int modelRowCount = 0;
    private boolean showAllRows = true;
    private Color defaultBackground = null;
    private Color background = null;

	private boolean[] propertiesRowVisibilities = null;
	private boolean[] propertiesColumnVisibilities = null;
    private SplitData[] propertiesColumnSplitData = null;
    
    private int propertiesToModelColumnIndex[] = null;
    private int modelToPropertiesColumnIndex[] = null;
    
    private int modelColumnCount = 0;
	private InspectableProperty[][] model = null;
	private String[] modelColumnNames = null;

	// The current sorted state.
    private int sortedPropertiesColumn = -1;
    private int sortedSplitIndex = 0;
    private boolean sortedOrderAsc = true;     

    // The maximum number of recent entries for splitting columns.
    private static final int recentSplitDataMaxCount = 8;

	private void viewModelInit() {
		comparator = new SpreadsheetRowComparator(this);
        recentSplitData = new Vector();
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

	public void refreshOnColumnDragEnd() {
		saveView();
		loadView();
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

	public boolean isSplit(int column) {
		return propertiesColumnSplitData[modelToPropertiesColumnIndex[column]] != null;		
	}
	
	/* Returns -1 if the column at the index should not be dragged. 
	 */
	public int validateDraggedColumnIndex(int columnIndex) {

		// Change the dragged column to first of a split group. 
		int firstOfSplits = propertiesToModelColumnIndex[modelToPropertiesColumnIndex[columnIndex]];
		
		// The first empty column must not be dragged. 
		return (firstOfSplits > 0) ? firstOfSplits : -1;
	}

	public void setColumnVisibility(boolean visible, int column) {
		int propertiesColumn = modelToPropertiesColumnIndex[column];
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
		int propertiesColumnIndex = modelToPropertiesColumnIndex[column]; 
		if (splitData != null) {
			splitData.setName(getColumnId(propertiesColumnIndex));
		}
		propertiesColumnSplitData[propertiesColumnIndex] = splitData;
		refreshModel();
		// refresh
		saveView();
		loadView();
	}
	
	public void setShowAllRows(boolean state) {
		showAllRows = state;
		refreshModel();
		fireTableDataChanged();
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
	}
	
	public void setRowVisibility(boolean visible, int[] rows) {
		int propertyIndex = 0;
		for (int r = 0; r < rows.length; r++) {
			propertyIndex = modelToPropertiesRowIndex(rows[r]);
			propertiesRowVisibilities[propertyIndex] = visible;
			((SpreadsheetRowVisible)getPropertyAtO(propertyIndex, 0)).setVisible(visible);
		}
		refreshModel();
		fireTableDataChanged();
	}
	
	public void deleteRows(int[] rows) {
		
		int[] propertiesRows = new int[rows.length];
		for (int r = 0; r < rows.length; r++) {
			propertiesRows[r] = modelToPropertiesRowIndex(rows[r]);
		}
		deleteRowsO(propertiesRows);
	}

	public void splitColumnByRecentList(int recentIndex, int column) {
		// Add the used item to the top of the list.
		SplitData data = (SplitData)recentSplitData.remove(recentIndex);
		recentSplitData.add(0, data);

		int propertiesColumnIndex = modelToPropertiesColumnIndex[column]; 
		String name = getColumnId(propertiesColumnIndex);
		SplitData split = new SplitData(name, data.getDelimiterTypeString(), data.getPattern());
		splitColumn(split, column);
	}
	
	public void setColumnOrder(String modeName) {
		
		int newMode = getModeIndex(modeName);
		if (newMode != -1) {
			saveSplitData();
			saveViewData();

			setMode(newMode);

			loadSplitData();
			loadViewRowStateData();
			clearColumnModel();
			createDefaultColumnModel();
			loadViewColumnHiddenStateData();
		}
	}

    public void switchRowHiddenState(int row) {
    	int propertyIndex = modelToPropertiesRowIndex(row);
    	boolean state = !propertiesRowVisibilities[propertyIndex];
    	propertiesRowVisibilities[propertyIndex] = state;
    	((SpreadsheetRowVisible)getPropertyAtO(propertyIndex, 0)).setVisible(state);
    	fireTableCellUpdated(row, 0);
    }
    
    public boolean isShowAllRows() {
        return showAllRows;
    }

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
				setValueAtO(aValue, propertiesRow, propertiesColumn);
				propertyGroup.setOwner(((CreatorProperty)baseProperty).getCreatedProperty());
			}
			property.setValue(aValue.toString());
		} else {
			/* If this isn't a split part, just call the base function and update the model data structure if a new
			 * property has been created by creator property.
			 */
			setValueAtO(aValue, propertiesRow, propertiesColumn);
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
	    if (column > 0) {
	    	return PropertyTableModel.HEADERDISP_TEXT;
	    }
	    return showAllRows ? PropertyTableModel.HEADERDISP_EYE : PropertyTableModel.HEADERDISP_NONE;
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
		
		saveViewO();		
		saveSplitData();
		saveViewData();
	}

	protected void sortModelRows() {
		if (sortedPropertiesColumn >= 0) {
			comparator.setColumn(propertiesToModelColumnIndex[sortedPropertiesColumn] + sortedSplitIndex);
			comparator.setAscending(sortedOrderAsc);
			Arrays.sort(model, comparator);
			fireTableDataChanged();
		}
	}
	

    protected int modelToPropertiesRowIndex(int modelRow) {
    	int propertiesNamesColumnIndex = getNamesColumn();
    	if (propertiesNamesColumnIndex == -1) {
    		return -1;
    	}
    	String rowName = model[modelRow][propertiesToModelColumnIndex[propertiesNamesColumnIndex]].getValue();
    	return nameToPropertiesRowIndex(rowName);
    }

	private void refreshModel() {

		updateSplitData();
        
		// Create a translation table from visible to all rows.
		modelRowCount = 0; 
		for (int i = 0; i < propertiesRowCount; i++) {
			if (showAllRows || propertiesRowVisibilities[i]) {
				modelRowCount++;
			}
		}
		
		int[] modelToPropertiesRowIndex = new int[modelRowCount];
		int modelRow = 0; 
		for (int i = 0; i < propertiesRowCount; i++) {
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
		for (int j = 0; j < propertiesColumnCount; j++) {

		    String name = getColumnId(j);
			SplitData split = propertiesColumnSplitData[j]; 
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
					SplitPropertyGroup group = new SplitPropertyGroup(getPropertyAtO(propertyRow, j), split);
					for (int p = 0; p < parts; p++) {
						model[i][modelJ + p] = group.getPart(p);
					}
				}
			} else {
		    	modelColumnNames[modelJ] = name;
				for (int i = 0; i < modelRowCount; i++) {
					model[i][modelJ] = getPropertyAtO(modelToPropertiesRowIndex[i], j);
				}
            }
		}
		sortModelRows();
	}

	private void updateSplitData() {

		int propertiesColumnCount = getColumnCountO();
		int propertiesRowCount = getRowCountO();
		
		// Calculate the required parts to which the a specific column must be split into.
		for (int j = 0; j < propertiesColumnCount; j++) {
			SplitData split = propertiesColumnSplitData[j];
			if (split != null) {
				int maxParts = 1;
				for (int i = 0; i < propertiesRowCount; i++) {
					String value = getPropertyAtO(i, j).getValue();
					int parts = SplitPropertyGroup.getPartsCount(value, split);
					maxParts = Math.max(maxParts, parts);
				}
				split.setParts(maxParts);
			}
		}
		
		// Create split translation tables.
		modelColumnCount = 0;
		for (int j = 0; j < propertiesColumnCount; j++) {
			modelColumnCount += (propertiesColumnSplitData[j] != null) ? propertiesColumnSplitData[j].getParts() : 1;
		}
		
		propertiesToModelColumnIndex = new int[propertiesColumnCount];
		modelToPropertiesColumnIndex = new int[modelColumnCount];
		
		int col = 0;
		for (int j = 0; j < propertiesColumnCount; j++) {
		    propertiesToModelColumnIndex[j] = col;
		    int parts = (propertiesColumnSplitData[j] != null) ? propertiesColumnSplitData[j].getParts() : 1;
		    for (int p = 0; p < parts; p++) {
		    	modelToPropertiesColumnIndex[col] = j;
		    	col++;
		    }
		}
	}
	
	private void updateColumnVisibility() {
		TableColumnModel columnModel = table.getTableHeader().getColumnModel();
		
		int lastIndex = -1;
		for (int j = 0; j < modelColumnCount; j++) {
			
			int index = table.convertColumnIndexToView(j);
			TableColumn column = index >= 0 ? columnModel.getColumn(index) : null;
			
			boolean visible = propertiesColumnVisibilities[modelToPropertiesColumnIndex[j]];
			boolean first = propertiesToModelColumnIndex[modelToPropertiesColumnIndex[j]] == j;
			
			if (column != null && !visible) {
				columnModel.removeColumn(column);
			}
			if (column == null && visible) {
				column = createColumn(j, first, true);
	    		// Set the identifier to the first of the split columns only. This is used when saving the order.
				columnModel.addColumn(column);
				/* If a column is shown, place it after the column where it would appear if the columns were not
				 * reordered. 
				 */
				columnModel.moveColumn(columnModel.getColumnCount() - 1, lastIndex + 1);
				lastIndex++;
			}
			if (index >= 0) {
				lastIndex = index;
			}
		}
	}
	
	private void clearColumnModel() {
		for (int j = 0; j < getColumnCountO(); j++) {
    	    propertiesColumnVisibilities[j] = false;
    	}
		
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
    	int colStart = propertiesToModelColumnIndex[propIndex];
    	int viewIndex = 0;
    	for (int p = 0; p < parts; p++) {
	    	// Set the identifier to the first of the split columns only. This is used when saving the order.
        	viewIndex = table.convertColumnIndexToView(colStart + p);
    		columnModel.removeColumn(columnModel.getColumn(viewIndex));
    	}
    	propertiesColumnVisibilities[propIndex] = false;
	}
	
	private void createDefaultColumnModel() {
		for (int j = 0; j < getColumnCountO(); j++) {
			addColumns(j, true);
		}
	} 
	
	private void addColumns(int propIndex, boolean defaultWidth) {

        TableColumnModel columnModel = table.getTableHeader().getColumnModel();
        
		int parts = 1;
		if (propertiesColumnSplitData[propIndex] != null) {
			parts = propertiesColumnSplitData[propIndex].getParts();
		}
    	int colStart = propertiesToModelColumnIndex[propIndex];
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
		String name = getColumnId(modelToPropertiesColumnIndex[colIndex]);
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

		loadViewO();
		
		String typeSign = getModelTypeSign();
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
    	for (int j = 0; j < getColumnCountO(); j++) {
    		propertiesColumnSplitData[j] = null;
    	}
    	SplitData[] splitColumns = record.getSplitColumns();
    	if (splitColumns != null) {
    		for (int j = 0; j < splitColumns.length; j++) {
    			int index = nameToPropertiesColumnIndex(splitColumns[j].getName());
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
        for (int i = 0; i < getRowCountO(); i++) {
        	propertiesRowVisibilities[i] = true;
			((SpreadsheetRowVisible)getPropertyAtO(i, 0)).setVisible(true);
        }
		String[] rows = record.getHiddenRows();
		if (rows != null && rows.length > 0) {
            for (int i = 0; i < rows.length; i++) {
            	int propertiesIndex = nameToPropertiesRowIndex(rows[i]);
            	if (propertiesIndex >= 0) {
            		propertiesRowVisibilities[propertiesIndex] = false;
        			((SpreadsheetRowVisible)getPropertyAtO(propertiesIndex, 0)).setVisible(false);
            	}
            }
    	}
		refreshModel();

    	SpreadsheetRowOrder rowOrder = record.getRowOrder();
        if (rowOrder != null) {
        	String orderedColumnName = rowOrder.getColumnName();
        	int index = nameToPropertiesColumnIndex(orderedColumnName);
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
	}
	
	protected void loadViewColumnHiddenStateData() {

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
	}

	protected void saveSplitData() {
		SpreadsheetTableViewRecord record = getViewRecord();
		
		boolean defaultNoColumnSplit = true;
		int propertiesColumnCount = getColumnCountO();
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

        // Stored hidden rows must be preserved if they exist in the database and don't appear in the new view.
		Vector hiddenRowsVector = new Vector();
		String[] rows = record.getHiddenRows();
		if (rows != null && rows.length > 0) {
			for (int i = 0; i < rows.length; i++) {
				int propertiesIndex = nameToPropertiesRowIndex(rows[i]);
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
        	String name = getColumnId(sortedPropertiesColumn);
        	record.setRowOrder(new SpreadsheetRowOrder(name, sortedSplitIndex, sortedOrderAsc));
        } else {
        	record.setRowOrder(null);
        }
		
		boolean defaultColumnVisibilityAndOrder = true;
		int prevColumnViewPosition = -1;
		int propertiesRowCount = getRowCountO();
		int propertiesColumnCount = getColumnCountO();
        for (int j = 0; j < propertiesColumnCount; j++) {
        	int columnViewPosition = table.convertColumnIndexToView(propertiesToModelColumnIndex[j]);
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
        		    columnStrings.add(new SpreadsheetColumnData(getColumnId(j), true, true, 0));
            	}
            }
            
            SpreadsheetColumnData[] columns = new SpreadsheetColumnData[columnStrings.size()]; 
            columnStrings.copyInto(columns);
    		record.setColumns(columns);
        } else {
    		record.setColumns(null);
        }
        
		boolean defaultRowVisibility = true;
        // If there are hidden rows that don't appear in this view, they have to be saved, so no default. 
        if (hiddenRowsVector.size() > 0) {
            defaultRowVisibility = false;
        } else {
        	for (int i = 0; i < propertiesRowCount; i++) {
        		if (!propertiesRowVisibilities[i]) {
        			defaultRowVisibility = false;
        			break;
        		}
        	}
        }
        if (!defaultRowVisibility) {
        	int namesIndex = getNamesColumn();
        	if (namesIndex >= 0) {
        		for (int i = 0; i < propertiesRowCount; i++) {
        			if (!propertiesRowVisibilities[i]) {
        				hiddenRowsVector.add(getPropertyAtO(i, namesIndex).getValue());
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

	protected void setMode(int mode) {
		setModeO(mode);
		refreshModel();
	}
	//}
	// TODO mark
	
	protected String dataType = null;
	private Inspectable[] inspectables = null;
	private Set loadedInspectablesNames = null;
	
	private int mode = -1;
	private int propertiesRowCount = 0;
	private int propertiesColumnCount = 0;
	
	private InspectableProperty[][] properties = null;
	private HashMap nameToPropertiesColumnIndex = null;
	private HashMap nameToPropertiesRowIndex = null;
	private String[] propertiesColumnNames = null;

    // The name of the column that contains the row names.  
    protected static final String propertiesNamesColumn = "Name";
    
    // The name of the column that contains the comments.  
    protected static final String propertiesCommentsColumn = "Comment";
    
    // The number of starting columns that should not be hidden or split. Currently this is the name.
    private static final int solidPropertiesColumnCount = 1;    

	private static ArrayList defaultModes = null; 
	
	private static final String recordType = "R"; 
	private static final String templateType = "T"; 
	private static final String unknownType = "U";
    
	public SpreadsheetTableModel(String dataType, Vector displayData, Vector loadedData)
			throws IllegalArgumentException {
			
  		if (displayData == null || displayData.size() == 0) {
  			throw new IllegalArgumentException("inspectables must not be empty or null");
  		}
		  		
		this.dataType = dataType;
		inspectables = new Inspectable[displayData.size()];
		displayData.copyInto(inspectables); 
        propertiesRowCount = inspectables.length;
        
    	// Store all names of the loaded objects. 
        loadedInspectablesNames = new HashSet();
        Iterator iterator = loadedData.iterator();
        while (iterator.hasNext()) {
        	loadedInspectablesNames.add(((Inspectable)iterator.next()).getName());
        }
        viewModelInit();
	}

    public int getColumnCountO() {
		return propertiesColumnCount;
	}

	public int getRowCountO() {
		return propertiesRowCount;
	}
	
	public Object getValueAtO(int rowIndex, int columnIndex) {
	    String value = properties[rowIndex][columnIndex].getValue();
		// TODO: The only properties with null values are CommentProperty. This could maybe be fixed.
	    return (value != null) ? value : ""; 
	}

	public InspectableProperty getPropertyAtO(int row, int column) {
		return properties[row][column];
	}
	
	public void setValueAtO(Object aValue, int row, int column) {
		
		InspectableProperty property = properties[row][column];

		int propertiesNamesColumnIndex = getNamesColumn();
		// If a name change, the whole table is going to be refreshed.
		if (column == propertiesNamesColumnIndex) {
			properties[row][column].setValue(aValue.toString());
			return;
			// TODO: fast renaming
			//nameToPropertiesRowIndex.remove(property.getValue());
		}
		/* Check if the property is a creator property that creates a new property on the inspectable object.
		 */
		if (property instanceof CreatorProperty) {
			CreatorProperty creatorProperty = (CreatorProperty)property; 

			creatorProperty.setName(propertiesColumnNames[column]);
			creatorProperty.setValue(aValue.toString());
			property = creatorProperty.getCreatedProperty();
			
			properties[row][column] = property; 
			
		} else {
			properties[row][column].setValue(aValue.toString());
		}
		
		// TODO: fast renaming
		/*
		if (column == propertiesNamesColumnIndex) {
   			nameToPropertiesRowIndex.put(properties[row][column].getValue(), new Integer(row));
		}
		*/
		fireTableRowsUpdated(row, row);
	}
	
	public int getHeaderDisplayTypeO(int column) {
	    return (column > 0) ? PropertyTableModel.HEADERDISP_TEXT : PropertyTableModel.HEADERDISP_NONE;
	}
	
	public int getPropertyDisplayTypeAt(int row, int column) {
	    return (column > 0) ? PropertyTableModel.DISP_VALUE : PropertyTableModel.DISP_VISIBILITY;
	}
	
	protected String getColumnId(int column) {
		return propertiesColumnNames[column]; 
	}
	
	public void deleteRowsO(int[] rows) {
		try	{
			UndoManager.getInstance().startMacroAction();

			Inspectable inspectable = null;
			for (int r = 0; r < rows.length; r++) {
				inspectable = inspectables[rows[r]];
				if (inspectable instanceof VisibleObject) {
					VisibleObject visible = (VisibleObject)inspectable;
					visible.destroy();
					UndoManager.getInstance().addAction(new DeleteAction(visible));
				}
			}
		} catch (Exception exception) {
			// Nothing.
		} finally {
			UndoManager.getInstance().stopMacroAction();
		}
		DrawingSurface.getInstance().repaint();
	}
	
	protected int getNamesColumn() {
	    return nameToPropertiesColumnIndex(propertiesNamesColumn);
	}
	
	protected Set getLoadedInspectablesNames() {
	    return loadedInspectablesNames;
	}
	
	// TODO: mark
	
	/* Returns -1 if none for this modeName.
	 */
	protected int getModeIndex(String modeName) {
    	ArrayList list = getModeNames();
    	for (int m = 0; m < list.size(); m++) {
    		if (modeName.equals(list.get(m).toString())) {
    			return m;
    		}
    	}
    	return -1;
	}

	protected void setModeO(int mode) {
   		this.mode = mode;
   		refreshProperties();
	}

	protected void loadViewO() {
		
		String typeSign = getModelTypeSign();
		SpreadsheetTableViewRecord record = SpreadsheetTableViewData.getInstance().get(typeSign + dataType);

		// If no record, make default model.
		if (record == null) {
			mode = 0;
	        refreshProperties();
			return;
		}
		
		loadTableModelData();
	}
	
	protected void saveViewO() {
		saveTableModelData();
	}
	
	public Inspectable getLastInspectable() {
		return inspectables[propertiesRowCount - 1];
	}
	
    /* Returns the number of starting columns that should not be hidden or split.
     */
	public int getSolidColumnsCount() {
		return solidPropertiesColumnCount;
	}
	
	public int getPropertiesColumnCount() {
		return propertiesColumnCount;
	}

	public String getPropertiesColumnNames(int column) {
		return propertiesColumnNames[column];
	}
	
	public int getPropertiesColumnIndex(String name) {
		return nameToPropertiesColumnIndex(name);
	}
	
    
	public Class getColumnClass(int column) {
		return String.class;
	}
	
	public ArrayList getModeNames() {

		ArrayList list = inspectables[0].getModeNames();
		if (list == null) {
			if (defaultModes == null) {
				defaultModes = new ArrayList();
				defaultModes.add("Default view");
			}
			list = defaultModes;
		}
		return list;
	}
	
	protected String getTypeString(Inspectable inspectable) {
		if (inspectable instanceof Record) {
			return recordType;
		} else if (inspectable instanceof Template) {
			return templateType;
		}
		return unknownType;
	}
	
	protected String getModelTypeSign() {
		return getTypeString(inspectables[0]);
	}
	
    /** Returns the index of the named data row or -1 if there is none.
     */
    private int nameToPropertiesRowIndex(String name) {
    	Integer index = (Integer)nameToPropertiesRowIndex.get(name);
        return (index != null) ? index.intValue() : -1;
    }
    /** Returns the index of the named data column or -1 if there is none.
     */
    private int nameToPropertiesColumnIndex(String name) {
    	Integer index = (Integer)nameToPropertiesColumnIndex.get(name);
        return (index != null) ? index.intValue() : -1;
    }
    
	private void refreshProperties() {

        // Get the properties of all inspectable objects and store the first creator property of each.
		InspectableProperty[][] inspectableProperties = new InspectableProperty[propertiesRowCount][];
        InspectableProperty[] creatorProperties = new InspectableProperty[propertiesRowCount];
        for (int i = 0; i < propertiesRowCount; i++) {
        	inspectableProperties[i] = inspectables[i].getProperties(mode, true);
			for (int j = 0; j < inspectableProperties[i].length; j++) {
				InspectableProperty property = inspectableProperties[i][j];
				if (property instanceof CreatorProperty && creatorProperties[i] == null) {
					creatorProperties[i] = property;
					break;
				}
			}
        }
			
		// Count the number of columns required by matching the properties by their names.
  		nameToPropertiesColumnIndex = new HashMap();
        Vector propStrings = new Vector();
        // Add the starting fixed columns.
        propStrings.add("");
        propStrings.add(propertiesNamesColumn);
		nameToPropertiesColumnIndex.put(propertiesNamesColumn, new Integer(1));
        propStrings.add(propertiesCommentsColumn);
		nameToPropertiesColumnIndex.put(propertiesCommentsColumn, new Integer(2));
        propertiesColumnCount = 3;
        for (int i = 0; i < propertiesRowCount; i++) {
			for (int j = 0; j < inspectableProperties[i].length; j++) {
				InspectableProperty property = inspectableProperties[i][j];
				if (!(property instanceof CreatorProperty)) {
					String name = property.getName();
					if (nameToPropertiesColumnIndex.get(name) == null) {
						nameToPropertiesColumnIndex.put(name, Integer.valueOf(String.valueOf(propertiesColumnCount)));
						propStrings.add(name);
						propertiesColumnCount++;
					}
				}
			}
		}
		propertiesColumnNames = new String[propertiesColumnCount];
		propStrings.copyInto(propertiesColumnNames);

		// Copy the properties into a rectangle shaped table. Empty spaces are filled with creator properties.
		properties = new InspectableProperty[propertiesRowCount][];
		for (int i = 0; i < propertiesRowCount; i++) {
			properties[i] = new InspectableProperty[propertiesColumnCount];

			SpreadsheetRowVisible visibilityProperty = new SpreadsheetRowVisible(true);
			for (int j = 0; j < propertiesColumnCount; j++) {
				if (j == 0) {
				    properties[i][j] = visibilityProperty;
				} else if (propertiesColumnNames[j].equals(propertiesCommentsColumn)) {
				    properties[i][j] = inspectables[i].getCommentProperty();
				} else {
				    properties[i][j] = creatorProperties[i];
				}
			}
			
			for (int j = 0; j < inspectableProperties[i].length; j++) {
				InspectableProperty property = inspectableProperties[i][j];
				if (!(property instanceof CreatorProperty)) {
					int col = ((Integer)nameToPropertiesColumnIndex.get(property.getName())).intValue();
					properties[i][col] = property; 
				}
			}
		}

  		// Create a name to row translation table.
		nameToPropertiesRowIndex = new HashMap();
		int nameColumn = nameToPropertiesColumnIndex(propertiesNamesColumn);
		if (nameColumn >= 0) {
			for (int i = 0; i < propertiesRowCount; i++) {
				nameToPropertiesRowIndex.put(properties[i][nameColumn].getValue(), new Integer(i));
			}
		}

	    // TODO: move to someplace else
		// Prepare space for properties column data.
		int propertiesColumnCount = getColumnCountO();
		int propertiesRowCount = getRowCountO();
		propertiesColumnVisibilities = new boolean[propertiesColumnCount]; 
		propertiesColumnSplitData = new SplitData[propertiesColumnCount];
		for (int j = 0; j < propertiesColumnCount; j++) {
			propertiesColumnVisibilities[j] = false;
		    propertiesColumnSplitData[j] = null;
		}

		// Prepare space for row data.
		propertiesRowVisibilities = new boolean[propertiesRowCount]; 
		for (int i = 0; i < propertiesRowCount; i++) {
			propertiesRowVisibilities[i] = true;
		}
	}

	protected SpreadsheetTableViewRecord getViewRecord() {
		String typeSign = getTypeString(inspectables[0]);
		SpreadsheetTableViewRecord record = SpreadsheetTableViewData.getInstance().get(typeSign + dataType);
		if (record == null) {
			record = new SpreadsheetTableViewRecord(typeSign, dataType);
		 	SpreadsheetTableViewData.getInstance().add(record);
		}
		return record;
	}
	
	protected void loadTableModelData() {
		SpreadsheetTableViewRecord record = getViewRecord();
		
        int newMode = -1;
        String modeName = record.getModeName();
        if (modeName != null) {
            newMode = getModeIndex(modeName);
        }
        
        // If no mode yet, and no mode loaded, assume mode 0.
        if (newMode == -1 && mode == -1) {
			setMode(0);
        } else if (newMode != -1 && newMode != mode) {
			setMode(newMode);
		}
	}

	protected void saveTableModelData() {
		SpreadsheetTableViewRecord record = getViewRecord();
		
		boolean defaultMode = mode == 0;
        if (!defaultMode) {
        	record.setModeName(getModeNames().get(mode).toString());
        } else {
        	record.setModeName(null);
        }
	}
}
