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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
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
import com.cosylab.vdct.graphics.popup.PopUpMenu;
import com.cosylab.vdct.plugin.debug.PluginDebugManager;
import com.cosylab.vdct.vdb.NameValueInfoProperty;

public class SpreadsheetTableModel extends AbstractTableModel implements PropertyTableModel {
	
	// Created on construction.
	private String dataType = null;
	private Inspectable[] inspectables = null;
	private SpreadsheetRowComparator comparator = null;
	private NameValueInfoProperty emptyProperty = null;

	
	private int rowCount = 0;
    private SpreadsheetTable table = null;
	
    
	// The following refresh on mode change. 
	private int mode = -1;
	private int propertiesCount = 0;
	private InspectableProperty[][] properties = null;
	private HashMap propNamesIndicesMap = null;
    // These are the size of the number of properties.
	private String[] propertyNames = null;
	private boolean[] hiddenProperties = null;
    private boolean[] splitProperties = null;
    private JCheckBoxMenuItem[] propMenuItem = null;

    private JPopupMenu popUpMenu = null;
    private JMenuItem hideItem = null; 
    private JComponent hideSeparator = null; 
	private JMenuItem splitItem = null; 
    private JMenuItem joinItem = null;
    private JComponent splitSeparator = null; 
	private JMenuItem sortAscItem = null; 
	private JMenuItem sortDesItem = null; 
    private JComponent sortSeparator = null; 
    
    
	// The following refresh on column number change.
    // Translation tables between properties and columns. 
    private int propToColumnIndex[] = null;
    private int columnToPropIndex[] = null;

    private int columnCount = 0;
	private InspectableProperty[][] fields = null;
    // These are the size of the number of columns.
	private String[] columnNames = null;
    private boolean[] colAscOrder = null; 

    
    // The number of starting columns that should not be hidden or split. Currently this is only the name.
    private static final int persistantColumns = 1;    

    // The number of items in visibility submenus.
    private static final int visibilityMenuItemCount = 16;    

    private int popUpColumn = 0;

	private static ArrayList defaultModes = null; 
	
	private static final String hide = "Hide"; 
	private static final String sortAsc = "Sort ascending"; 
	private static final String sortDes = "Sort descending"; 
	private static final String showAll = "Show all"; 
	private static final String hideAll = "Hide all"; 
	private static final String visibility = "Visibility"; 
	
	private static final String split = "Split"; 
	private static final String join = "Join"; 

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
		comparator = new SpreadsheetRowComparator(this);
		inspectables = new Inspectable[inspectData.size()];
		inspectData.copyInto(inspectables); 
	}

	private void refreshProperties() {
        propertiesCount = 1;
        Vector propStrings = new Vector();
        propStrings.add("");
  		propNamesIndicesMap = new HashMap();
        properties = new InspectableProperty[rowCount][];
        
		for (int i = 0; i < rowCount; i++) {
			properties[i] = inspectables[i].getProperties(mode, true);
			
			for (int j = 0; j < properties[i].length; j++) {
				InspectableProperty property = properties[i][j];
				if (property instanceof CreatorProperty) {
					continue;
				}
				String name = properties[i][j].getName();
				if (propNamesIndicesMap.get(name) == null) {
					propNamesIndicesMap.put(name, Integer.valueOf(String.valueOf(propertiesCount)));
					propStrings.add(name);
					propertiesCount++;
				}
			}
		}
		propertyNames = new String[propertiesCount];
		propStrings.copyInto(propertyNames);
	    hiddenProperties = new boolean[propertiesCount]; 
		splitProperties = new boolean[propertiesCount];
		propToColumnIndex = new int[propertiesCount];
		createPopupMenu();

		refreshColumns();
	}
	
	private void refreshColumns() {
		
		createSplitTranslationTables();

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
				fields[i][j] = (j == 0 || creatorProperty == null) ? emptyProperty : creatorProperty;
			}
			for (int j = 0; j < properties[i].length; j++) {
				if (properties[i][j] instanceof CreatorProperty) {
					continue;
				}
				// map must contain the key 
				int propIndex = ((Integer)propNamesIndicesMap.get(properties[i][j].getName())).intValue();
				int col = propToColumnIndex[propIndex];

				int parts = splitProperties[propIndex] ? 3 : 1;
			    for (int p = 0; p < parts; p++) {
					fields[i][col + p] = properties[i][j];
			    }
			}
		}

        colAscOrder = new boolean[columnCount]; 
		for (int j = 0; j < columnCount; j++) {
			colAscOrder[j] = false;
		}
		
    	columnNames = new String[columnCount];
		for (int j = 0; j < propertiesCount; j++) {
		    String name = propertyNames[j];
		    int parts = splitProperties[j] ? 3 : 1;
		    int col = propToColumnIndex[j];
		    if (parts == 1) {
		    	columnNames[col] = name;
		    } else {
	    		columnNames[col] = "<" + name + " 1";
		    	for (int p = 1; p < parts - 1; p++) {
		    		columnNames[col + p] = name + " " + (p + 1);
		    	}
	    		columnNames[col + parts - 1] = name + " " + parts + ">";
		    }
		}
	}
	
	private void createSplitTranslationTables() {
		
		columnCount = 0;
		for (int j = 0; j < propertiesCount; j++) {
			columnCount += splitProperties[j] ? 3 : 1;
		}
		columnToPropIndex = new int[columnCount];
		
		int col = 0;
		for (int j = 0; j < propertiesCount; j++) {
		    propToColumnIndex[j] = col;
		    int parts = splitProperties[j] ? 3 : 1;
		    for (int p = 0; p < parts; p++) {
		    	columnToPropIndex[col] = j;
		    	col++;
		    }
		}
	}
	
	private void updateHiddenColumns() {
		TableColumnModel columnModel = table.getTableHeader().getColumnModel();
		
		int lastIndex = -1;
		for (int j = 0; j < columnCount; j++) {
			
			int index = table.convertColumnIndexToView(j);
			TableColumn column = index >= 0 ? columnModel.getColumn(index) : null;
			
			boolean hidden = hiddenProperties[columnToPropIndex[j]];
			boolean first = propToColumnIndex[columnToPropIndex[j]] == j;
			
			if (column != null && hidden) {
				columnModel.removeColumn(column);
			}
			if (column == null && !hidden) {
		    	// Set the identifier to the first of the split columns only. This is used when saving the order.
				columnModel.addColumn(createColumn(j, first));
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
			    	
			    	int index = getTable().convertColumnIndexToModel(columnIndex);
			    	if (index >= 0) {
			    		colAscOrder[index] = !colAscOrder[index]; 
			    		comparator.setColumn(index);
			    		comparator.setAscending(colAscOrder[index]);
			    		Arrays.sort(fields, comparator);
			    		fireTableDataChanged(); 
			    	}
			    	
	            } else if (event.getButton() == MouseEvent.BUTTON3) {
	            	displayPopupMenu(event.getX(), event.getY());
			    }
	        }

			public void mousePressed(MouseEvent event) {
	        	JTableHeader header = getTable().getTableHeader();
	        	int colIndex = header.getDraggedColumn().getModelIndex(); 
	        	if (colIndex > 0) {
        	        // Change the dragged column to first of a split group. 
                    int firstOfSplits = propToColumnIndex[columnToPropIndex[colIndex]];
                    int viewIndex = getTable().convertColumnIndexToView(firstOfSplits);
	        	    header.setDraggedColumn(header.getColumnModel().getColumn(viewIndex));
	        	} else {
        	        // The first empty column must stay there. If it is about to be moved, stop it. 
	        		header.setDraggedColumn(null);
	        	}
	        }
	        
			public void mouseReleased(MouseEvent event) {
			    if (event.getButton() == MouseEvent.BUTTON1) {
			    	JTable table = getTable();
			    	int viewIndex = table.convertColumnIndexToView(0);
			        // Put the empty column back if it was dragged away. 
			    	if (viewIndex > 0) {
			    		table.moveColumn(viewIndex, 0);
			    	}
			    	// Validate the positions of columns.
			    	saveView();
			    	loadView();
			    }

	        }
		});
	    
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
			    if (event.getButton() == MouseEvent.BUTTON3) {
			    	displayPopupMenu(event.getX(), event.getY());
			    }
	        }
		});

        rowCount = inspectables.length;
        loadView();
	}

    private void createPopupMenu() {
    	popUpMenu = new JPopupMenu();

		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String action = event.getActionCommand();
				Object source = event.getSource();
				
				if (source instanceof JCheckBoxMenuItem) {
					hiddenProperties[nameToPropIndex(action)] = !((JCheckBoxMenuItem)source).isSelected();
				    updateHiddenColumns();
				} else if (action.equals(hide)) {
			    	int index = table.convertColumnIndexToModel(popUpColumn);
			    	if (index >= 0) {
			    		index = columnToPropIndex[index];
			    		propMenuItem[index].setSelected(false);
			    		hiddenProperties[index] = true;
			    		updateHiddenColumns();
			    	}
				} else if (action.equals(split) || action.equals(join)) {
			    	int index = table.convertColumnIndexToModel(popUpColumn);
			    	if (index >= 0) {
			    		index = columnToPropIndex[index];
			    		splitProperties[index] = action.equals(split);
			    		refreshColumns();
						// refresh
						saveView();
				    	loadView();
			    	}
				} else if (action.equals(sortAsc) || action.equals(sortDes)) {
			    	int index = table.convertColumnIndexToModel(popUpColumn);
			    	if (index >= 0) {
			    		colAscOrder[index] = action.equals(sortAsc);
			    		comparator.setColumn(index);
			    		comparator.setAscending(colAscOrder[index]);
			    		Arrays.sort(fields, comparator);
			    		fireTableDataChanged(); 
			    	}
				} else if (action.equals(hideAll) || action.equals(showAll)) {
				    boolean state = action.equals(showAll);
				    // do this on all but the first two
				    for (int i = persistantColumns + 1; i < propMenuItem.length; i++) {
				    	propMenuItem[i].setSelected(state);
						hiddenProperties[i] = !state;
				    }
				    updateHiddenColumns();
				} else {
					ArrayList list = getModeNames();
					for (int i = 0; i < list.size(); i++) {
						if (action.equals(list.get(i).toString())) {
							// direct mapping
							mode = i;
							saveView();
							// force refresh
							mode = -1;
					    	loadView();
							break;
						}
					}
                }
    		}
		};

		hideItem = new JMenuItem(hide);
		hideItem.addActionListener(listener);
    	popUpMenu.add(hideItem);

	    hideSeparator = new JSeparator(); 
    	popUpMenu.add(hideSeparator);
    	
    	splitItem = new JMenuItem(split);
    	splitItem.addActionListener(listener);
    	popUpMenu.add(splitItem);
    	
    	joinItem = new JMenuItem(join);
    	joinItem.addActionListener(listener);
    	popUpMenu.add(joinItem);
    	
	    splitSeparator = new JSeparator(); 
    	popUpMenu.add(splitSeparator);

		sortAscItem = new JMenuItem(sortAsc);
		sortAscItem.addActionListener(listener);
    	popUpMenu.add(sortAscItem);

    	sortDesItem = new JMenuItem(sortDes);
    	sortDesItem.addActionListener(listener);
    	popUpMenu.add(sortDesItem);

	    sortSeparator = new JSeparator(); 
    	popUpMenu.add(sortSeparator);
    	
    	JMenuItem menuItem = null;
    	ArrayList list = getModeNames();

       	for (int i = 0; i < list.size(); i++) {
       		menuItem = new JMenuItem(list.get(i).toString());
       		menuItem.addActionListener(listener);
       		popUpMenu.add(menuItem);
       	}
    	
    	popUpMenu.add(new JSeparator());

    	JMenuItem hideAllItem = new JMenuItem(hideAll);
    	hideAllItem.addActionListener(listener);
    	popUpMenu.add(hideAllItem);

    	JMenuItem showAllItem = new JMenuItem(showAll);
    	showAllItem.addActionListener(listener);
    	popUpMenu.add(showAllItem);
    	
    	JMenu visibilityMenu = new JMenu(visibility);
    	popUpMenu.add(visibilityMenu);
    	
        propMenuItem = new JCheckBoxMenuItem[propertiesCount];
        JMenu menuToAdd = visibilityMenu;
    	
    	for (int j = 0; j < propertiesCount; j++) {
   			JCheckBoxMenuItem checkBoxItem = new JCheckBoxMenuItem(propertyNames[j]);
   			propMenuItem[j] = checkBoxItem;
   			checkBoxItem.setSelected(true);
   			checkBoxItem.addActionListener(listener);
   			
   	    	// Add check boxes for all but the unhideable and the first.
   			int itemPos = j - (persistantColumns + 1); 
   			if (itemPos >= 0) {
   				menuToAdd = PopUpMenu.addItem(checkBoxItem, menuToAdd, itemPos, visibilityMenuItemCount);
   			}
    	}
    	
    	// If no items to hide/show, the options should be disabled.
    	if (propertiesCount <= persistantColumns + 1) {
    		hideAllItem.setEnabled(false);
    		showAllItem.setEnabled(false);
    		visibilityMenu.setEnabled(false);
    	}
    }
    
    private void displayPopupMenu(int posX, int posY) {
        popUpColumn = table.getColumnModel().getColumnIndexAtX(posX);
        boolean first = popUpColumn == 0;
        boolean persistant = popUpColumn <= persistantColumns;
        boolean splitted = splitProperties[columnToPropIndex[table.convertColumnIndexToModel(popUpColumn)]];

        setJComponentVisible(hideItem, !first && !persistant);
        setJComponentVisible(hideSeparator, !first && !persistant);
        setJComponentVisible(splitItem, !first && !persistant && !splitted);
        setJComponentVisible(joinItem, !first && !persistant && splitted);
        setJComponentVisible(splitSeparator, !first && !persistant);
        setJComponentVisible(sortAscItem, !first);
        setJComponentVisible(sortDesItem, !first);
        setJComponentVisible(sortSeparator, !first);

        popUpMenu.show(table, posX, posY);
    }
    
    private void setJComponentVisible(JComponent component, boolean visible) {
    	if (component.isVisible() != visible) {
    		component.setVisible(visible);
    	}
    }
	
    /** Returns the index of the named data column or -1 if there is none.
     */
    private int nameToPropIndex(String name) {
    	Integer index = (Integer)propNamesIndicesMap.get(name);
        return (index != null) ? index.intValue() : -1;
    }
    
	public Class getColumnClass(int column) {
		return String.class;
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

        int newMode = 0;
        boolean customMode = false;
		if (record != null) {
			String modeName = record.getModeName();
			ArrayList list = getModeNames();
			int m = 0;
			for (m = 0; m < list.size(); m++) {
				if (modeName.equals(list.get(m).toString())) {
					newMode = m;
					break;
				}
			}
			customMode = m == list.size();
		}
		
		if (mode != newMode) {
			mode = newMode;
	        refreshProperties();
		}
		
		for (int j = 0; j < propertiesCount; j++) {
    	    hiddenProperties[j] = true;
        	propMenuItem[j].setSelected(false);
    	}
		
        TableColumnModel columnModel = table.getTableHeader().getColumnModel();
        while (columnModel.getColumnCount() > 0) {
        	columnModel.removeColumn(columnModel.getColumn(0));
        }

    	if (customMode) {
    		String[] columns = record.getColumns();
    		
	    	// add the first empty column
    		addColumns(0);
            for (int j = 0; j < columns.length; j++) {
    	    	Integer integer = (Integer)propNamesIndicesMap.get(columns[j]);
    		    if (integer != null) {
    		    	int index = integer.intValue();
    		    	addColumns(index);
    		    }
            }
    	} else {
    		for (int j = 0; j < propertiesCount; j++) {
		    	addColumns(j);
        	}
    	}
		table.resizeColumns();
	}
	
	private void addColumns(int propIndex) {

        TableColumnModel columnModel = table.getTableHeader().getColumnModel();
		int parts = splitProperties[propIndex] ? 3 : 1;
    	int colStart = propToColumnIndex[propIndex];
    	for (int p = 0; p < parts; p++) {
	    	// Set the identifier to the first of the split columns only. This is used when saving the order.
    		columnModel.addColumn(createColumn(colStart + p, p == 0));
    	}
    	hiddenProperties[propIndex] = false;
    	propMenuItem[propIndex].setSelected(true);
	}
	
	private TableColumn createColumn(int colIndex, boolean identifier) {
		TableColumn column = new TableColumn();
		column.setModelIndex(colIndex);
		column.setIdentifier(identifier ? propertyNames[columnToPropIndex[colIndex]] : null);
		column.setHeaderValue(columnNames[colIndex]);
		return column;
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
        	Vector columnStrings = new Vector();
        	
        	modeName = customView;
            TableColumnModel columnModel = table.getTableHeader().getColumnModel();
            int columnCount = columnModel.getColumnCount();
            // Store the first columns of the split ones, except for the first which is empty.
            for (int j = 1; j < columnCount; j++) {
            	String name = (String)columnModel.getColumn(j).getIdentifier();
            	if (name != null) {
        		    columnStrings.add(name);
            	}
    		}
            columns = new String[columnStrings.size()]; 
            columnStrings.copyInto(columns);
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
