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

public class SpreadsheetTableModel extends AbstractTableModel implements PropertyTableModel, ActionListener {

    SpreadsheetInspector inspector = null;
	
	// Created on construction.
	private String dataType = null;
	private Inspectable[] inspectables = null;
	private SpreadsheetRowComparator comparator = null;
	private NameValueInfoProperty emptyProperty = null;
    private Vector recentSplitData = null;
	private HashMap nameToRecentSplitDataIndex = null;
	
	private int modelRowCount = 0;
    private SpreadsheetTable table = null;
    
	// The following refresh on mode change. 
	private int mode = -1;
	private int propertiesColumnCount = 0;
	
	private InspectableProperty[][] properties = null;
	private HashMap nameToPropertiesColumnIndex = null;
	private String[] propertiesColumnNames = null;
	private boolean[] propertiesColumnVisibilities = null;
    private SplitData[] propertiesColumnSplitData = null;
    private JCheckBoxMenuItem[] propertiesColumnMenuItem = null;

    private JPopupMenu popUpMenu = null;
    private JMenuItem hideItem = null; 
    private JComponent hideSeparator = null; 
	private JMenu splitMenu = null; 
    private JMenuItem joinItem = null;
    private JComponent splitSeparator = null; 
	private JMenuItem sortAscItem = null; 
	private JMenuItem sortDesItem = null; 
    private JComponent sortSeparator = null; 
    private JMenuItem extendCountersItem = null;
    
	// The following refresh on column number change.
    private int propertiesToModelColumnIndex[] = null;
    private int modelToPropertiesColumnIndex[] = null;

    private int modelColumnCount = 0;
	private InspectableProperty[][] model = null;
    // These are the size of the number of columns.
	private String[] modelColumnNames = null;

    // The current sorted state.
    private int sortedModelColumn = -1;
    private boolean sortedOrderAsc = true;     
    
    // The number of starting columns that should not be hidden or split. Currently this is only the name.
    private static final int solidPropertiesColumnCount = 1;    

    // The number of items in visibility submenus.
    private static final int visibilityMenuItemCount = 16;    

    // The maximum number of recent entries for splitting columns.
    private static final int recentSplitDataMaxCount = 8;    
    
    private int popUpColumn = 0;

	private static ArrayList defaultModes = null; 
	
	private static final String hide = "Hide"; 
	private static final String sortAsc = "Sort ascending"; 
	private static final String sortDes = "Sort descending"; 
	private static final String showAll = "Show all"; 
	private static final String hideAll = "Hide all"; 
	private static final String visibility = "Visibility"; 
	private static final String extendCounters = "Increase counters"; 
	
	private static final String split = "Split by"; 
	private static final String whitespaces = "Whitespaces";
	private static final String customPattern = "Custom...";
	private static final String join = "Join"; 

	private static final String recordType = "R"; 
	private static final String templateType = "T"; 
	private static final String unknownType = "U"; 

	public SpreadsheetTableModel(SpreadsheetInspector inspector, String dataType, Vector inspectData) throws
	        IllegalArgumentException {

  		if (inspectData == null || inspectData.size() == 0) {
  			throw new IllegalArgumentException("inspectables must not be empty or null");
  		}
  		this.inspector = inspector; 
  		
		this.dataType = dataType;
  		emptyProperty = new NameValueInfoProperty("", "");
		comparator = new SpreadsheetRowComparator(this);
		inspectables = new Inspectable[inspectData.size()];
		inspectData.copyInto(inspectables); 
        recentSplitData = new Vector();
    	nameToRecentSplitDataIndex = new HashMap();
	}

	private void refreshProperties() {

        // Get the properties of all inspectable objects and store the first creator property of each.
		InspectableProperty[][] inspectableProperties = new InspectableProperty[modelRowCount][];
        InspectableProperty[] creatorProperties = new InspectableProperty[modelRowCount];
        for (int i = 0; i < modelRowCount; i++) {
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
        // Add an empty first one.
        propertiesColumnCount = 1;
        propStrings.add("");
        for (int i = 0; i < modelRowCount; i++) {
			for (int j = 0; j < inspectableProperties[i].length; j++) {
				InspectableProperty property = inspectableProperties[i][j];
				if (property instanceof CreatorProperty) {
					continue;
				}
				String name = property.getName();
				if (nameToPropertiesColumnIndex.get(name) == null) {
					nameToPropertiesColumnIndex.put(name, Integer.valueOf(String.valueOf(propertiesColumnCount)));
					propStrings.add(name);
					propertiesColumnCount++;
				}
			}
		}
		propertiesColumnNames = new String[propertiesColumnCount];
		propStrings.copyInto(propertiesColumnNames);

		// Copy the properties into a rectangle shaped table. Empty spaces are filled with creator properties.
		properties = new InspectableProperty[modelRowCount][];
		for (int i = 0; i < modelRowCount; i++) {
			properties[i] = new InspectableProperty[propertiesColumnCount];

			for (int j = 0; j < propertiesColumnCount; j++) {
				properties[i][j] = (j == 0) ? emptyProperty : creatorProperties[i];
			}
			
			for (int j = 0; j < inspectableProperties[i].length; j++) {
				InspectableProperty property = inspectableProperties[i][j];
				if (!(property instanceof CreatorProperty)) {
					int col = ((Integer)nameToPropertiesColumnIndex.get(property.getName())).intValue();
					properties[i][col] = property; 
				}
			}
		}

	    // Prepare space for properties column data.
		propertiesColumnVisibilities = new boolean[propertiesColumnCount]; 
		propertiesColumnSplitData = new SplitData[propertiesColumnCount];
		for (int j = 0; j < propertiesColumnCount; j++) {
			propertiesColumnVisibilities[j] = false;
		    propertiesColumnSplitData[j] = null;
		}
		createPopupMenu();

		refreshModel();
	}
	
	private void refreshModel() {
		
		// Calculate the required parts to which the a specific column must be split into.
		for (int j = 0; j < propertiesColumnCount; j++) {
			SplitData split = propertiesColumnSplitData[j];
			if (split != null) {
				int maxParts = 1;
				for (int i = 0; i < modelRowCount; i++) {
					String value = properties[i][j].getValue();
					int parts = SplitPropertyGroup.getPartsCount(value, split);
					maxParts = Math.max(maxParts, parts);
				}
				split.setParts(maxParts);
			}
		}
		
		createSplitTranslationTables();

		model = new InspectableProperty[modelRowCount][];
		for (int i = 0; i < modelRowCount; i++) {
			model[i] = new InspectableProperty[modelColumnCount];
		}
		
		// Set the model column names and model data according to split columns.
		modelColumnNames = new String[modelColumnCount];
		for (int j = 0; j < propertiesColumnCount; j++) {

		    String name = propertiesColumnNames[j];
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
					SplitPropertyGroup group = new SplitPropertyGroup(properties[i][j], split);
					for (int p = 0; p < parts; p++) {
						model[i][modelJ + p] = group.getPart(p);
					}
				}
			} else {
		    	modelColumnNames[modelJ] = name;
				for (int i = 0; i < modelRowCount; i++) {
					model[i][modelJ] = properties[i][j];
				}
            }
		}
	}
	
	private void createSplitTranslationTables() {
		
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
			
			private int draggedColumnModelIndex = -1;
			private int draggedColumnViewIndex = -1;
			
	        public void mouseClicked(MouseEvent event) {
			    if (event.getButton() == MouseEvent.BUTTON1) {
			    	
			    	JTable table = getTable();
			        int columnIndex = table.getColumnModel().getColumnIndexAtX(event.getX());
			    	int index = table.convertColumnIndexToModel(columnIndex);
			    	if (index >= 0) {
			    		if (sortedModelColumn == index) {
			    			sortedOrderAsc = !sortedOrderAsc; 
			    		} else {
				    		sortedModelColumn = index;
				    		sortedOrderAsc = true;
			    		}
				    	sortModelRows();
			    	}
	            } else if (event.getButton() == MouseEvent.BUTTON3) {
	            	displayPopupMenu(event.getX(), event.getY());
			    }
	        }

			public void mousePressed(MouseEvent event) {
	        	JTableHeader header = getTable().getTableHeader();
	        	TableColumn draggedColumn = header.getDraggedColumn(); 
	        	if (draggedColumn != null) {
	        		int colIndex = draggedColumn.getModelIndex();
	        		if (colIndex > 0) {
	        			// Change the dragged column to first of a split group. 
	        			int firstOfSplits = propertiesToModelColumnIndex[modelToPropertiesColumnIndex[colIndex]];
	        			int viewIndex = getTable().convertColumnIndexToView(firstOfSplits);
	        			header.setDraggedColumn(header.getColumnModel().getColumn(viewIndex));

	        			draggedColumnModelIndex = firstOfSplits;
		        		draggedColumnViewIndex = viewIndex;
	        		} else {
	        			// The first empty column must stay there. If it is about to be moved, stop it. 
	        			header.setDraggedColumn(null);
	        		}
	        	}
	        }
	        
			public void mouseReleased(MouseEvent event) {
			    if (event.getButton() == MouseEvent.BUTTON1) {
			    	JTable table = getTable();
			        // Put the empty column back if it was dragged away. 
			    	int viewIndex = table.convertColumnIndexToView(0);
			    	if (viewIndex > 0) {
			    		table.moveColumn(viewIndex, 0);
			    	}
			    	// Validate the positions of columns if there was a drag.
			    	if (draggedColumnModelIndex >= 0) {
			    		if (getTable().convertColumnIndexToView(draggedColumnModelIndex) != draggedColumnViewIndex) {
			    			saveView();
			    			loadView();
			    		}
			    		draggedColumnModelIndex = -1;
			    	}
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

        modelRowCount = inspectables.length;
        loadView();
	}

    private void createPopupMenu() {
    	popUpMenu = new JPopupMenu();

		hideItem = new JMenuItem(hide);
		hideItem.addActionListener(this);
    	popUpMenu.add(hideItem);

	    hideSeparator = new JSeparator(); 
    	popUpMenu.add(hideSeparator);
    	
    	splitMenu = new JMenu(split);
    	popUpMenu.add(splitMenu);
    	updateSplitMenu();
    	
    	joinItem = new JMenuItem(join);
    	joinItem.addActionListener(this);
    	popUpMenu.add(joinItem);
    	
	    splitSeparator = new JSeparator(); 
    	popUpMenu.add(splitSeparator);

		sortAscItem = new JMenuItem(sortAsc);
		sortAscItem.addActionListener(this);
    	popUpMenu.add(sortAscItem);

    	sortDesItem = new JMenuItem(sortDes);
    	sortDesItem.addActionListener(this);
    	popUpMenu.add(sortDesItem);

	    sortSeparator = new JSeparator(); 
    	popUpMenu.add(sortSeparator);
    	
    	ArrayList list = getModeNames();

       	for (int i = 0; i < list.size(); i++) {
       		JMenuItem menuItem = new JMenuItem(list.get(i).toString());
       		menuItem.addActionListener(this);
       		popUpMenu.add(menuItem);
       	}
    	
    	popUpMenu.add(new JSeparator());

    	JMenuItem hideAllItem = new JMenuItem(hideAll);
    	hideAllItem.addActionListener(this);
    	popUpMenu.add(hideAllItem);

    	JMenuItem showAllItem = new JMenuItem(showAll);
    	showAllItem.addActionListener(this);
    	popUpMenu.add(showAllItem);
    	
    	JMenu visibilityMenu = new JMenu(visibility);
    	popUpMenu.add(visibilityMenu);
    	
        propertiesColumnMenuItem = new JCheckBoxMenuItem[propertiesColumnCount];
        JMenu menuToAdd = visibilityMenu;
    	
    	for (int j = 0; j < propertiesColumnCount; j++) {
   			JCheckBoxMenuItem checkBoxItem = new JCheckBoxMenuItem(propertiesColumnNames[j]);
   			propertiesColumnMenuItem[j] = checkBoxItem;
   			checkBoxItem.setSelected(true);
   			checkBoxItem.addActionListener(this);
   			
   	    	// Add check boxes for all but the unhideable and the first.
   			int itemPos = j - (solidPropertiesColumnCount + 1); 
   			if (itemPos >= 0) {
   				menuToAdd = PopUpMenu.addItem(checkBoxItem, menuToAdd, itemPos, visibilityMenuItemCount);
   			}
    	}
    	
    	// If no items to hide/show, the options should be disabled.
    	if (propertiesColumnCount <= solidPropertiesColumnCount + 1) {
    		hideAllItem.setEnabled(false);
    		showAllItem.setEnabled(false);
    		visibilityMenu.setEnabled(false);
    	}

    	popUpMenu.addSeparator();
    	
    	extendCountersItem = new JMenuItem(extendCounters);
    	extendCountersItem.addActionListener(this);
    	popUpMenu.add(extendCountersItem);
    }

	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		Object source = event.getSource();
		
		if (source instanceof JCheckBoxMenuItem) {
			propertiesColumnVisibilities[nameToPropertiesColumnIndex(action)] = ((JCheckBoxMenuItem)source).isSelected();
		    updateColumnVisibility();
		} else if (action.equals(hide)) {
	    	int index = table.convertColumnIndexToModel(popUpColumn);
	    	if (index >= 0) {
	    		index = modelToPropertiesColumnIndex[index];
	    		propertiesColumnMenuItem[index].setSelected(false);
	    		propertiesColumnVisibilities[index] = false;
	    		updateColumnVisibility();
	    	}
		} else if (action.equals(whitespaces) || action.equals(customPattern)) {

			int index = table.convertColumnIndexToModel(popUpColumn);
			if (index >= 0) {
				SplitData split = null; 
				if (action.equals(customPattern)) {
					CustomSplitDialog dialog = inspector.getCustomSplitDialog();
					dialog.setLocationRelativeTo(inspector);
					
					if (recentSplitData.size() > 0) {
						split = (SplitData)recentSplitData.get(0);
					} else {
						split = SplitData.getWhitespaceSplitData();
					}
					dialog.setSplitData(split);
					// Add the data from the first row of the splitting column as starting test example. 
                    dialog.setTestExample(model[0][index].getValue());
					dialog.setVisible(true);
					split = dialog.getSplitData();
					if (split != null) {
						recentSplitData.add(0, split);
						while (recentSplitData.size() > recentSplitDataMaxCount) {
							recentSplitData.remove(recentSplitData.size() - 1);
						}
						updateSplitMenu();
					}
					
				} else {
				    split = SplitData.getWhitespaceSplitData();
				}
				if (split != null) {
					int propertiesColumnIndex = modelToPropertiesColumnIndex[index]; 
					split.setName(propertiesColumnNames[propertiesColumnIndex]);
					propertiesColumnSplitData[propertiesColumnIndex] = split;
					refreshModel();
					// refresh
					saveView();
					loadView();
				}
			}
		} else if (action.equals(join)) {
			int index = table.convertColumnIndexToModel(popUpColumn);
			if (index >= 0) {
				propertiesColumnSplitData[modelToPropertiesColumnIndex[index]] = null;
				refreshModel();
				// refresh
				saveView();
				loadView();
	    	}
		} else if (action.equals(sortAsc) || action.equals(sortDes)) {
	    	int index = table.convertColumnIndexToModel(popUpColumn);
	    	if (index >= 0) {
	    		if (sortedModelColumn == index) {
	    			sortedOrderAsc = !sortedOrderAsc; 
	    		} else {
		    		sortedModelColumn = index;
		    		sortedOrderAsc = true;
	    		}
		    	sortModelRows();
	    	}
		} else if (action.equals(hideAll) || action.equals(showAll)) {
		    boolean state = action.equals(showAll);
		    // do this on all but the first two
		    for (int i = solidPropertiesColumnCount + 1; i < propertiesColumnMenuItem.length; i++) {
		    	propertiesColumnMenuItem[i].setSelected(state);
				propertiesColumnVisibilities[i] = state;
		    }
		    updateColumnVisibility();
		} else if (action.equals(extendCounters)) {
			extendCounters(table.getSelectedRows(), table.getSelectedColumns());
		} else {
			int index = table.convertColumnIndexToModel(popUpColumn);
			if (index >= 0) {
				Integer recentIndexObject = (Integer)nameToRecentSplitDataIndex.get(action);
				if (recentIndexObject != null) {
					int recentIndex = recentIndexObject.intValue();
					// Add the used item to the top of the list.
					SplitData data = (SplitData)recentSplitData.remove(recentIndex);
					recentSplitData.add(0, data);
					updateSplitMenu();
					propertiesColumnSplitData[modelToPropertiesColumnIndex[index]] = data;
					refreshModel();
					// refresh
					saveView();
					loadView();
					return;
				}
			}
			
			ArrayList list = getModeNames();
			for (int i = 0; i < list.size(); i++) {
				if (action.equals(list.get(i).toString())) {
					// direct mapping
					mode = i;
					refreshProperties();
					clearColumnModel();
                    createDefaultColumnModel();
                    table.resizeColumns();
					break;
				}
			}
        }
	}
	
	private void sortModelRows() {
		if (sortedModelColumn >= 0) {
			comparator.setColumn(sortedModelColumn);
			comparator.setAscending(sortedOrderAsc);
			Arrays.sort(model, comparator);
			fireTableDataChanged();
		}
	}

	private void updateSplitMenu() {
    	splitMenu.removeAll();
    	JMenuItem menuItem = new JMenuItem(whitespaces);
    	menuItem.addActionListener(this);
    	splitMenu.add(menuItem);
    	splitMenu.addSeparator();

    	nameToRecentSplitDataIndex.clear();
    	
    	for (int i = 0; i < recentSplitData.size(); i++) {
    		String name = recentSplitData.get(i).toString();
        	menuItem = new JMenuItem(name);
        	menuItem.addActionListener(this);
        	splitMenu.add(menuItem);
        	nameToRecentSplitDataIndex.put(name, new Integer(i));
    	}
    	
    	if (recentSplitData.size() > 0) {
        	splitMenu.addSeparator();
    	}

    	menuItem = new JMenuItem(customPattern);
    	menuItem.addActionListener(this);
    	splitMenu.add(menuItem);
    }

    private void displayPopupMenu(int posX, int posY) {
        popUpColumn = table.getColumnModel().getColumnIndexAtX(posX);
        int colIndex = table.convertColumnIndexToModel(popUpColumn);
        boolean first = colIndex == 0;
        boolean persistant = colIndex <= solidPropertiesColumnCount;
        boolean splitted = propertiesColumnSplitData[modelToPropertiesColumnIndex[colIndex]] != null;
        
        setJComponentVisible(hideItem, !first && !persistant);
        setJComponentVisible(hideSeparator, !first && !persistant);
        setJComponentVisible(splitMenu, !first && !persistant && !splitted);
        setJComponentVisible(joinItem, !first && !persistant && splitted);
        setJComponentVisible(splitSeparator, !first && !persistant);
        setJComponentVisible(sortAscItem, !first);
        setJComponentVisible(sortDesItem, !first);
        setJComponentVisible(sortSeparator, !first);
        
        extendCountersItem.setEnabled(table.getSelectedColumnCount() >= 1 && table.getSelectedRowCount() >= 2);
        
        popUpMenu.show(table, posX, posY);
    }
    
    private void setJComponentVisible(JComponent component, boolean visible) {
    	if (component.isVisible() != visible) {
    		component.setVisible(visible);
    	}
    }
	
    /** Returns the index of the named data column or -1 if there is none.
     */
    private int nameToPropertiesColumnIndex(String name) {
    	Integer index = (Integer)nameToPropertiesColumnIndex.get(name);
        return (index != null) ? index.intValue() : -1;
    }
    
	public Class getColumnClass(int column) {
		return String.class;
	}

	public int getColumnCount() {
		return modelColumnCount;
	}

	public int getRowCount() {
		return modelRowCount;
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		return model[rowIndex][columnIndex].getValue(); 
	}

	public void setValueAt(Object aValue, int row, int column) {
		
		InspectableProperty property = model[row][column];
		
		/* Check if the property is a creator property that creates a new property on the inspectable object. If the
		 * current property is a split part, check if the owner is a creator property. If it is, get the newly created
		 * property and fit it into the data structure.
		 */
		SplitPropertyGroup propertyGroup = null;
		if (property instanceof SplitPropertyPart) {
			propertyGroup = ((SplitPropertyPart)property).getOwner();
			property = propertyGroup.getOwner();
		}
		if (property instanceof CreatorProperty) {
			CreatorProperty creatorProperty = (CreatorProperty)property; 

			int propertiesColumn = modelToPropertiesColumnIndex[column]; 
			creatorProperty.setName(propertiesColumnNames[propertiesColumn]);
			creatorProperty.setValue(aValue.toString());
			property = creatorProperty.getCreatedProperty();
			
			if (propertyGroup != null) {
				propertyGroup.setOwner(property);
			} else {
				model[row][column] = property; 
			}
			properties[row][propertiesColumn] = property; 
			
		} else {
			model[row][column].setValue(aValue.toString());
		}
		fireTableRowsUpdated(row, row);
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
	
	private void extendCounters(int[] rows, int[] viewColumns) {
		for (int c = 0; c < viewColumns.length; c++) {
			int modelColumn = table.convertColumnIndexToModel(viewColumns[c]);
			
			String firstEntry = model[rows[0]][modelColumn].getValue();
			String secondEntry = model[rows[1]][modelColumn].getValue();
			String baseString = SplitData.removeValueAtEnd(firstEntry);
			int firstValue = SplitData.extractValueAtEnd(firstEntry);
			int secondValue = SplitData.extractValueAtEnd(secondEntry);
			// Assume base at 0 and step 1 if values are absent.
			int value = 0;
			int step = 1;
			if (firstValue != -1) {
				value = firstValue;
			}
			if (secondValue != -1) {
				step = secondValue - firstValue;
			}
			for (int r = 0; r < rows.length; r++) {
				model[rows[r]][modelColumn].setValue(baseString + value);
				value += step;
				fireTableCellUpdated(rows[r], modelColumn);
			}
		}
	}
	
	private void loadView() {
		
		String typeSign = getTypeString(inspectables[0]);
		SpreadsheetTableViewRecord record = SpreadsheetTableViewData.getInstance().get(typeSign + dataType);

        int newMode = 0;
		if (record != null) {
			String modeName = record.getModeName();
			if (modeName != null) {
				ArrayList list = getModeNames();
				for (int m = 0; m < list.size(); m++) {
					if (modeName.equals(list.get(m).toString())) {
						newMode = m;
						break;
					}
				}
			}
		}
		
		if (mode != newMode) {
			mode = newMode;
	        refreshProperties();
		}

		clearColumnModel();
		
		// If no record, make default model.
		if (record == null) {
    	    createDefaultColumnModel();
    		table.resizeColumns();
			return;
		}
		
		// Set the split data and refresh the model.
    	for (int j = 0; j < propertiesColumnCount; j++) {
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
    		refreshModel();
    	}
    	
    	SplitData[] recentSplits = record.getRecentSplits();
    	if (recentSplits != null) {
    		recentSplitData.clear();
    		for (int j = 0; j < recentSplits.length; j++) {
        		recentSplitData.add(recentSplits[j]);
    		}
    		updateSplitMenu();
    	}

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
        		
        		sortedModelColumn = propertiesToModelColumnIndex[index] + splitIndex;
        		sortedOrderAsc = rowOrder.isAscending();
        		sortModelRows();
        	}
        }
    	
    	// If no columns, assume default view. 
		String[] columns = record.getColumns();
		if (columns != null && columns.length > 0) {
	    	// Add the first empty column.
    		addColumns(0);
            for (int j = 0; j < columns.length; j++) {
    	    	Integer integer = (Integer)nameToPropertiesColumnIndex.get(columns[j]);
    		    if (integer != null) {
    		    	int index = integer.intValue();
    		    	addColumns(index);
    		    }
            }
    	} else {
    	    createDefaultColumnModel();
    	}
		table.resizeColumns();
	}
	
	private void clearColumnModel() {
		for (int j = 0; j < propertiesColumnCount; j++) {
    	    propertiesColumnVisibilities[j] = false;
        	propertiesColumnMenuItem[j].setSelected(false);
    	}
		
        TableColumnModel columnModel = table.getTableHeader().getColumnModel();
        while (columnModel.getColumnCount() > 0) {
        	columnModel.removeColumn(columnModel.getColumn(0));
        }
	}
	
	private void createDefaultColumnModel() {
		for (int j = 0; j < propertiesColumnCount; j++) {
			addColumns(j);
		}
	} 
	
	private void addColumns(int propIndex) {

        TableColumnModel columnModel = table.getTableHeader().getColumnModel();
        
		int parts = 1;
		if (propertiesColumnSplitData[propIndex] != null) {
			parts = propertiesColumnSplitData[propIndex].getParts();
		}
    	int colStart = propertiesToModelColumnIndex[propIndex];
    	for (int p = 0; p < parts; p++) {
	    	// Set the identifier to the first of the split columns only. This is used when saving the order.
    		columnModel.addColumn(createColumn(colStart + p, p == 0));
    	}
    	propertiesColumnVisibilities[propIndex] = true;
    	propertiesColumnMenuItem[propIndex].setSelected(true);
	}
	
	private TableColumn createColumn(int colIndex, boolean identifier) {
		TableColumn column = new TableColumn();
		column.setModelIndex(colIndex);
		column.setIdentifier(identifier ? propertiesColumnNames[modelToPropertiesColumnIndex[colIndex]] : null);
		column.setHeaderValue(modelColumnNames[colIndex]);
		return column;
	}
	
	public void saveView() {
		String typeSign = getTypeString(inspectables[0]);
		
		// Determine if the current column visibility/order and splitting is default.
		boolean defaultNoSortOrder = (sortedModelColumn == -1);
		boolean defaultColumnVisibilityAndOrder = true;
		boolean defaultNoColumnSplit = true;
		boolean defaultNoRecentSplits = recentSplitData.isEmpty();
		int prevColumnViewPosition = -1; 
        for (int j = 0; j < propertiesColumnCount; j++) {
        	int columnViewPosition = table.convertColumnIndexToView(propertiesToModelColumnIndex[j]);
        	if (prevColumnViewPosition > columnViewPosition || !propertiesColumnVisibilities[j]) {
        		defaultColumnVisibilityAndOrder = false;
        	}
        	prevColumnViewPosition = columnViewPosition;
        	
        	if (propertiesColumnSplitData[j] != null) {
        		defaultNoColumnSplit = false;
        	}
        }
        
        boolean allDefault = defaultNoSortOrder && defaultColumnVisibilityAndOrder && defaultNoColumnSplit
        	&& defaultNoRecentSplits; 

        // If the first mode and default, save nothing and remove the data.
        if (allDefault && mode == 0) {
        	SpreadsheetTableViewData.getInstance().remove(typeSign + dataType);
        	return;
        }

        // If other modes, save an entry with all data that is not default. 
        String modeName = getModeNames().get(mode).toString();
        SpreadsheetTableViewRecord viewRecord = new SpreadsheetTableViewRecord(typeSign, dataType, modeName);
        
        if (!defaultNoSortOrder) {
        	int sortedPropertiesColumn = modelToPropertiesColumnIndex[sortedModelColumn];
        	int splitIndex = sortedModelColumn - propertiesToModelColumnIndex[sortedPropertiesColumn];
        	String name = propertiesColumnNames[sortedPropertiesColumn];
        	viewRecord.setRowOrder(new SpreadsheetRowOrder(name, splitIndex, sortedOrderAsc));
        }
        
        if (!defaultColumnVisibilityAndOrder) {
            String[] columns = null;
        	Vector columnStrings = new Vector();

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
    		viewRecord.setColumns(columns);
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
    		viewRecord.setSplitColumns(splitColumns);
        }

        if (!defaultNoRecentSplits) {
        	SplitData[] recentSplits = new SplitData[recentSplitData.size()]; 
        	recentSplitData.copyInto(recentSplits);
    		viewRecord.setRecentSplits(recentSplits);
        }
        
	 	SpreadsheetTableViewData.getInstance().add(viewRecord);
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
