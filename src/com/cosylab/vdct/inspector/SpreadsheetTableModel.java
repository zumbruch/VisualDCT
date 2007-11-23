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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.cosylab.vdct.graphics.objects.Record;
import com.cosylab.vdct.graphics.objects.Template;
import com.cosylab.vdct.plugin.debug.PluginDebugManager;
import com.cosylab.vdct.vdb.NameValueInfoProperty;

public class SpreadsheetTableModel extends AbstractTableModel implements PropertyTableModel {
	
	private String dataType = null;
	private Inspectable[] inspectables = null;
	private InspectableProperty[][] fields = null;

	private HashMap colIndicesMap = null;
	private String[] columnNames = null;
    private TableColumn hiddenColumns[] = null; 
    private boolean colAscOrder[] = null; 

	private SpreadsheetRowComparator comparator = null;
	private NameValueInfoProperty emptyProperty = null;

    private SpreadsheetTable table = null;
    private JPopupMenu popUpMenu = null;
    private int popUpMenuX = 0;
    private int popUpMenuY = 0;

    private JCheckBoxMenuItem[] columnHandlers = null;

	private int columnCount = 0;
	private int rowCount = 0;
	private int mode = 0;
	
	private static ArrayList defaultModes = null; 
	
	private static final String hide = "Hide"; 
	private static final String sortAsc = "Sort ascending"; 
	private static final String sortDes = "Sort descending"; 
	private static final String showAll = "Show all"; 
	private static final String hideAll = "Hide all"; 

	private static final String recordType = "R"; 
	private static final String templateType = "T"; 
	private static final String unknownType = "U"; 

	private static final String customView = "Custom"; 
	
	public SpreadsheetTableModel(String dataType, Vector inspectData) throws IllegalArgumentException {

  		if (inspectData == null || inspectData.size() == 0) {
  			throw new IllegalArgumentException("inspectables must not be empty or null");
  		}
		this.dataType = dataType;
  		emptyProperty = new NameValueInfoProperty("", "");
		comparator = new SpreadsheetRowComparator();
		inspectables = new Inspectable[inspectData.size()];
		inspectData.copyInto(inspectables); 
	}
	
	public SpreadsheetTable getTable() {
	    return table;
	}

	public void setTable(SpreadsheetTable table) {
	    this.table = table;
	    
		table.getTableHeader().addMouseListener(new MouseAdapter() {
	        public void mouseClicked(MouseEvent event) {
			    if (event.getButton() == MouseEvent.BUTTON1) {
			    	JTableHeader header = (JTableHeader)event.getSource();
			    	TableColumnModel columnModel = header.getColumnModel();
			    	int columnIndex = columnModel.getColumnIndexAtX(event.getX());
			    	int index = columnToDataModelIndex(columnIndex);
			    	if (index >= 0) {
			    		colAscOrder[index] = !colAscOrder[index]; 
			    		comparator.setColumn(index);
			    		comparator.setAscending(colAscOrder[index]);
			    		Arrays.sort(fields, comparator);
			    		fireTableDataChanged(); 
			    	}
			    	
	            } else if (event.getButton() == MouseEvent.BUTTON3) {
			        popUpMenuX = event.getX();
			        popUpMenuY = event.getY();
					popUpMenu.show(getTable(), popUpMenuX, popUpMenuY);
			    }
	        }

	        // The first empty column must stay there. If it is about to be moved, stop it. 
			public void mousePressed(MouseEvent event) {
	        	JTableHeader header = getTable().getTableHeader();
	        	if (header.getDraggedColumn().getModelIndex() == 0) {
	        		header.setDraggedColumn(null);
	        	}
	        }
	        
	        // Put the empty column back if it was put aside during move. 
			public void mouseReleased(MouseEvent event) {
	        	JTable table = getTable();
	        	int viewIndex = table.convertColumnIndexToView(0);
	        	if (viewIndex > 0) {
	        		table.moveColumn(viewIndex, 0);
	        	}
	        }
	    });
	    
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
			    if (event.getButton() == MouseEvent.BUTTON3) {
			        popUpMenuX = event.getX();
			        popUpMenuY = event.getY();
					popUpMenu.show(getTable(), popUpMenuX, popUpMenuY);
			    }
	        }
	    });

        rowCount = inspectables.length;
        loadView();
	}

	private void refreshProperties() {
        columnCount = 1;
        Vector colStrings = new Vector();
        colStrings.add("");
  		colIndicesMap = new HashMap();
        InspectableProperty[][] properties = new InspectableProperty[rowCount][];
        
		for (int i = 0; i < rowCount; i++) {
			properties[i] = inspectables[i].getProperties(mode, true);
			
			for (int j = 0; j < properties[i].length; j++) {
				InspectableProperty property = properties[i][j];
				if (property instanceof CreatorProperty) {
					continue;
				}
				String name = properties[i][j].getName();
				if (colIndicesMap.get(name) == null) {
					colIndicesMap.put(name, Integer.valueOf(columnCount));
					colStrings.add(name);
					columnCount++;
				}
			}
		}
		columnNames = new String[columnCount];
		colStrings.copyInto(columnNames);
	    hiddenColumns = new TableColumn[columnCount]; 
        colAscOrder = new boolean[columnCount]; 
    	for (int j = 0; j < columnCount; j++) {
    	    hiddenColumns[j] = null;
    	    colAscOrder[j] = false;
    	}

		createFields(properties);
		createPopupMenu();
      	fireTableStructureChanged();

	    TableColumnModel columnModel = table.getTableHeader().getColumnModel();
    	for (int j = 0; j < columnCount; j++) {
    	    columnModel.getColumn(j).setIdentifier(columnNames[j]);
    	}
	}

	private void createFields(InspectableProperty[][] properties) {

		fields = new InspectableProperty[rowCount][];
		for (int i = 0; i < rowCount; i++) {
			fields[i] = new InspectableProperty[columnCount];

			CreatorProperty creatorProperty = null;
			for (int j = 0; j < properties[i].length; j++) {
				if (properties[i][j] instanceof CreatorProperty) {
					creatorProperty = (CreatorProperty)properties[i][j];
					break;
				}
			}

			for (int j = 0; j < fields[i].length; j++) {
				fields[i][j] = (j == 0) ? emptyProperty : creatorProperty;
			}
			for (int j = 0; j < properties[i].length; j++) {
				if (properties[i][j] instanceof CreatorProperty) {
					continue;
				}
				String name = properties[i][j].getName();
				// map must contain the key 
				fields[i][((Integer)colIndicesMap.get(name)).intValue()] = properties[i][j];
			}
		}
	}

    private void createPopupMenu() {
    	popUpMenu = new JPopupMenu();

		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String action = event.getActionCommand();
				Object source = event.getSource();
				
				if (source instanceof JCheckBoxMenuItem) {
				    updateColumnState((JCheckBoxMenuItem)source);
				} else if (action.equals(hide) || action.equals(sortAsc) || action.equals(sortDes)) {
			    	TableColumnModel columnModel = table.getColumnModel();
			    	int columnIndex = columnModel.getColumnIndexAtX(popUpMenuX);
			    	int index = columnToDataModelIndex(columnIndex);
			    	if (index >= 0) {
			    		if (action.equals(hide)) {
			    		columnHandlers[index].setSelected(false);
			    		updateColumnState(columnHandlers[index]);
			    		} else {
				    		colAscOrder[index] = action.equals(sortAsc);
				    		comparator.setColumn(index);
				    		comparator.setAscending(colAscOrder[index]);
				    		Arrays.sort(fields, comparator);
				    		fireTableDataChanged(); 
			    		}
			    	}
			    	
				} else if (action.equals(hideAll) || action.equals(showAll)) {
				    boolean state = action.equals(showAll);
				    // do this on all but the first two
				    for (int i = 2; i < columnHandlers.length; i++) {
				    	columnHandlers[i].setSelected(state);
				    	updateColumnState(columnHandlers[i]);
				    }
				} else {
					ArrayList list = getModeNames();
					for (int i = 0; i < list.size(); i++) {
						if (action.equals(list.get(i).toString())) {
							// direct mapping
							mode = i;
							refreshProperties();
							table.resizeColumns();
							break;
						}
					}
                }
    		}
		};

		JMenuItem menuItem = new JMenuItem(hide);
    	menuItem.addActionListener(listener);
    	popUpMenu.add(menuItem);

    	popUpMenu.add(new JSeparator());

    	menuItem = new JMenuItem(sortAsc);
    	menuItem.addActionListener(listener);
    	popUpMenu.add(menuItem);

    	menuItem = new JMenuItem(sortDes);
    	menuItem.addActionListener(listener);
    	popUpMenu.add(menuItem);
    	
    	popUpMenu.add(new JSeparator());
    	
    	menuItem = new JMenuItem(hideAll);
    	menuItem.addActionListener(listener);
    	popUpMenu.add(menuItem);

    	menuItem = new JMenuItem(showAll);
    	menuItem.addActionListener(listener);
    	popUpMenu.add(menuItem);

    	ArrayList list = getModeNames();
       	popUpMenu.add(new JSeparator());

       	for (int i = 0; i < list.size(); i++) {
       		menuItem = new JMenuItem(list.get(i).toString());
       		menuItem.addActionListener(listener);
       		popUpMenu.add(menuItem);
       	}
    	
    	popUpMenu.add(new JSeparator());

        columnHandlers = new JCheckBoxMenuItem[columnCount];
    	
    	for (int j = 0; j < columnCount; j++) {
   			JCheckBoxMenuItem checkBoxItem = new JCheckBoxMenuItem(columnNames[j]);
   			columnHandlers[j] = checkBoxItem;
   			checkBoxItem.setSelected(true);
   			checkBoxItem.addActionListener(listener);
   			
   	    	// add check boxes for all but the first two: empty column and name
   			if (j >= 2) {
   			    popUpMenu.add(checkBoxItem);
   			}
    	}
    }

	private void updateColumnState(JCheckBoxMenuItem checkBox) {
		TableColumnModel columnModel = table.getTableHeader().getColumnModel();
		String action = checkBox.getActionCommand();

		int index = nameToDataModelIndex(action);
		if (index < 0) {
		    return;
		}

		if (checkBox.isSelected() && hiddenColumns[index] != null) {
			columnModel.addColumn(hiddenColumns[index]);
			hiddenColumns[index] = null;
			index++;
			while ((index < columnCount) && (hiddenColumns[index] != null)) {
				index++; 
			}
			if (index < columnCount) {
				index = dataToColumnModelIndex(index);
				if (index >= 0) {
				    columnModel.moveColumn(columnModel.getColumnCount() - 1, index);
				}
			}
		} else if (!checkBox.isSelected() && hiddenColumns[index] == null) {
			hiddenColumns[index] = columnModel.getColumn(nameToColumnModelIndex(action));
			columnModel.removeColumn(hiddenColumns[index]);
		}
	} 
    
    /** Returns the index of the column in the column model or -1 if this data column is not in the column model.
     */
    private int dataToColumnModelIndex(int index) {
    	
    	int columnIndex = -1;
    	try {
    	    columnIndex = table.getTableHeader().getColumnModel().getColumnIndex(columnNames[index]);
    	} catch(IllegalArgumentException exception) {
    		// nothing
    	}
    	return columnIndex;
    }

    /** Returns the index of the column in the column model or -1 if this data column is not in the column model.
     */
    private int nameToColumnModelIndex(String name) {
    	
    	int columnIndex = -1;
    	try {
    	    columnIndex = table.getTableHeader().getColumnModel().getColumnIndex(name);
    	} catch(IllegalArgumentException exception) {
    		// nothing
    	}
    	return columnIndex;
    }
    
    private int columnToDataModelIndex(int index) {
    	return table.getTableHeader().getColumnModel().getColumn(index).getModelIndex();
    }

    /** Returns the index of the named data column or -1 if there is none.
     */
    private int nameToDataModelIndex(String name) {
    	Integer index = (Integer)colIndicesMap.get(name);
        return (index != null) ? index.intValue() : -1;
    }

	public Class getColumnClass(int column) {
		return String.class;
	}

	public String getColumnName(int column) {
		return columnNames[column];
	}

	public int getColumnCount() {
		return columnCount;
	}

	public int getRowCount() {
		return rowCount;
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		return fields[rowIndex][columnIndex].getValue();
	}

	public void setValueAt(Object aValue, int row, int column) {
		
		InspectableProperty property = fields[row][column];

		if (property instanceof CreatorProperty) {
			CreatorProperty creatorProperty = (CreatorProperty)property; 
			creatorProperty.setName(columnNames[column]);
			creatorProperty.setValue(aValue.toString());
			fields[row][column] = creatorProperty.getCreatedProperty(); 
		} else {
			property.setValue(aValue.toString());
		}
		fireTableRowsUpdated(row, row);
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// no editing in debug
		if (PluginDebugManager.isDebugState()) {
			return false;
		}
		return fields[rowIndex][columnIndex].isEditable();
	}

	public InspectableProperty getPropertyAt(int row, int column) {
		return fields[row][column];
	}
	
	public int getPropertyDisplayTypeAt(int row, int column) {
	    if (column > 0) {
	    	return PropertyTableModel.DISP_VALUE;
	    }
	    return PropertyTableModel.DISP_NONE;
	}

	private String getTypeString(Inspectable inspectable) {
		if (inspectable instanceof Record) {
			return recordType;
		} else if (inspectable instanceof Template) {
			return templateType;
		}
		return unknownType;
	}
	
	private void loadView() {
		
		String typeSign = getTypeString(inspectables[0]);
		SpreadsheetTableViewRecord record = SpreadsheetTableViewData.getInstance().get(typeSign + dataType);

        // If there is no record, use default.
		if (record == null) {
	        refreshProperties();
			return;
		}
		String modeName = record.getModeName();
		
		ArrayList list = getModeNames();
		for (mode = 0; mode < list.size(); mode++) {
			if (modeName.equals(list.get(mode).toString())) {
				break;
			}
		}

        // If this is not custom view, just use this mode.
		if (mode < list.size()) {
	        refreshProperties();
			return;
		}
		mode = 0;
		
        // Otherwise, create a default model, store its columns, remove them, then add a subset in custom order.
		refreshProperties();
        TableColumnModel columnModel = table.getTableHeader().getColumnModel();

        // Remove all the columns except the first, which is empty.
        for (int j = 1; j < columnCount; j++) {
        	//test
        	/*
        	columnHandlers[j].setSelected(false);
        	updateColumnState(columnHandlers[j]);
        	*/
        	TableColumn column = columnModel.getColumn(1);
        	hiddenColumns[j] = column; 
        	columnModel.removeColumn(column);
        	columnHandlers[j].setSelected(false);
		}
        
		String[] columns = record.getColumns();
		
        for (int j = 0; j < columns.length; j++) {
	    	Integer integer = (Integer)colIndicesMap.get(columns[j]);
		    if (integer != null) {
		    	int i = integer.intValue();
		    	columnModel.addColumn(hiddenColumns[i]);
		    	hiddenColumns[i] = null;
	        	columnHandlers[i].setSelected(true);
		    }
        }
	}
	
	public void saveView() {
		String typeSign = getTypeString(inspectables[0]);
		
		// Determine if the current view is default.
		boolean defaultView = true;
        for (int j = 0; j < columnCount; j++) {
        	if (table.convertColumnIndexToView(j) != j) {
        		defaultView = false;
        		break;
        	}
        }

        // If the first default view, save nothing and remove the data.
        if (defaultView && mode == 0) {
        	SpreadsheetTableViewData.getInstance().remove(typeSign + dataType);
        	return;
        }
        
        // If other default view, save just the name of the view. Otherwise, save the current visible columns. 
        String modeName = null;
        String[] columns = null;
        if (defaultView) {
        	modeName = getModeNames().get(mode).toString();
        	columns = new String[0]; 
        } else {
        	modeName = customView;
            TableColumnModel columnModel = table.getTableHeader().getColumnModel();
            int columnCount = columnModel.getColumnCount();

            // Store all but the first which is empty.
            columns = new String[columnCount - 1]; 
            for (int j = 0; j < columnCount - 1; j++) {
            	columns[j] = (String)columnModel.getColumn(j + 1).getIdentifier();
    		}
        }
        SpreadsheetTableViewRecord record = new SpreadsheetTableViewRecord(typeSign, dataType, modeName, columns); 
        SpreadsheetTableViewData.getInstance().add(record);
	}
	
	private ArrayList getModeNames() {
		
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
}
