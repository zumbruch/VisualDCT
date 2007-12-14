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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.cosylab.vdct.graphics.ColorChooser;//
import com.cosylab.vdct.graphics.objects.Record;
import com.cosylab.vdct.graphics.objects.Template;
import com.cosylab.vdct.graphics.popup.PopUpMenu;
import com.cosylab.vdct.plugin.debug.PluginDebugManager;
import com.cosylab.vdct.undo.UndoManager;
import com.cosylab.vdct.vdb.CommentProperty;

public class SpreadsheetTableModel extends AbstractTableModel implements PropertyTableModel, ActionListener {

    SpreadsheetInspector inspector = null;
	
	private String dataType = null;
	private Inspectable[] inspectables = null;
	private SpreadsheetRowComparator comparator = null;
    private Vector recentSplitData = null;
	private HashMap nameToRecentSplitDataIndex = null;
	
	private int modelRowCount = 0;
    private SpreadsheetTable table = null;
    private JScrollPane pane = null;
    private boolean showAllRows = true;
    private Color defaultBackground = null;
    private Color background = null;
    
	private int mode = -1;
	private int propertiesRowCount = 0;
	private int propertiesColumnCount = 0;
	
	private InspectableProperty[][] properties = null;
	private HashMap nameToPropertiesColumnIndex = null;
	private HashMap nameToPropertiesRowIndex = null;
	private String[] propertiesColumnNames = null;
	private boolean[] propertiesRowVisibilities = null;
	private boolean[] propertiesColumnVisibilities = null;
    private SplitData[] propertiesColumnSplitData = null;
    private JCheckBoxMenuItem[] propertiesColumnMenuItem = null;
    
    private JPopupMenu popUpMenu = null;
    private JMenuItem hideItem = null; 
	private JMenu splitMenu = null; 
    private JMenuItem joinItem = null;
	private JMenuItem sortAscItem = null; 
	private JMenuItem sortDesItem = null; 
    private JComponent sortSeparator = null;
    private JMenuItem showAllRowsItem = null;
    private JMenuItem extendCountersItem = null;

    private int propertiesToModelColumnIndex[] = null;
    private int modelToPropertiesColumnIndex[] = null;
    
    private int modelColumnCount = 0;
	private InspectableProperty[][] model = null;
	private String[] modelColumnNames = null;

    // The current sorted state.
    private int sortedPropertiesColumn = -1;
    private int sortedSplitIndex = 0;
    private boolean sortedOrderAsc = true;     

    // The name of the column that contains the row names.  
    private static final String propertiesNamesColumn = "Name";
    
    // The name of the column that contains the comments.  
    private static final String propertiesCommentsColumn = "Comment";
    
    // The number of starting columns that should not be hidden or split. Currently this is the name.
    private static final int solidPropertiesColumnCount = 1;    

    // The number of items in visibility submenus.
    private static final int visibilityMenuItemCount = 16;    

    // The maximum number of recent entries for splitting columns.
    private static final int recentSplitDataMaxCount = 8;    
    
    private int popUpModelRow = -1;
    private int popUpModelColumn = -1;

	private static ArrayList defaultModes = null; 
	
	private static final String hide = "Hide column"; 
	private static final String sortAsc = "Sort ascending"; 
	private static final String sortDes = "Sort descending"; 
	private static final String showAll = "Show all columns"; 
	private static final String hideAll = "Hide all columns"; 
	private static final String visibility = "Column visibility"; 
	private static final String extendCounters = "Increase counters"; 
	private static final String showAllRowsString = "Show all rows";
	private static final String backgroundColorString = "Set background color...";
	private static final String defaultColorString = "Default background color";
	
	private static final String split = "Split column by"; 
	private static final String whitespaces = "Whitespaces";
	private static final String customPattern = "Custom...";
	private static final String join = "Join columns"; 

	private static final String hideRow = "Hide row"; 

    private static final String colorChooserTitle = "Set the table background color";
	
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
		comparator = new SpreadsheetRowComparator(this);
		inspectables = new Inspectable[inspectData.size()];
		inspectData.copyInto(inspectables); 
        recentSplitData = new Vector();
    	nameToRecentSplitDataIndex = new HashMap();
        propertiesRowCount = inspectables.length;
	}

	public void refresh() {
		saveView();
		refreshProperties();
		loadView();
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
		
	    // Prepare space for properties column data.
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
		
		createPopupMenu();

		refreshModel();
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
					int propertyRow = modelToPropertiesRowIndex[i];
					SplitPropertyGroup group = new SplitPropertyGroup(properties[propertyRow][j], split);
					for (int p = 0; p < parts; p++) {
						model[i][modelJ + p] = group.getPart(p);
					}
				}
			} else {
		    	modelColumnNames[modelJ] = name;
				for (int i = 0; i < modelRowCount; i++) {
					model[i][modelJ] = properties[modelToPropertiesRowIndex[i]][j];
				}
            }
		}
		sortModelRows();
	}
	
	private void updateSplitData() {

		// Calculate the required parts to which the a specific column must be split into.
		for (int j = 0; j < propertiesColumnCount; j++) {
			SplitData split = propertiesColumnSplitData[j];
			if (split != null) {
				int maxParts = 1;
				for (int i = 0; i < propertiesRowCount; i++) {
					String value = properties[i][j].getValue();
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

	public JScrollPane getTableScrollPane() {
	    return pane;
	}
	
	public void setTable(SpreadsheetTable table, JScrollPane pane) {
	    this.table = table;
	    this.pane = pane;
	    
	    defaultBackground = table.getBackground();
	    background = defaultBackground;
	    updateBackgroundColor(); 
	    
		table.getTableHeader().addMouseListener(new MouseAdapter() {
			
			private int draggedColumnModelIndex = -1;
			private int draggedColumnViewIndex = -1;
			
	        public void mouseClicked(MouseEvent event) {
			    if (event.getButton() == MouseEvent.BUTTON1) {
			    	
			    	JTable table = getTable();
			        int columnIndex = table.getColumnModel().getColumnIndexAtX(event.getX());
			    	int index = table.convertColumnIndexToModel(columnIndex);
			    	if (index >= 1) {
			    		updateSortedColumn(index);
			    	} else if (index == 0) {
						setShowAllRows(!showAllRows);
			    	}
	            } else if (event.getButton() == MouseEvent.BUTTON3) {
			    	Point point = event.getPoint();
			    	JTable table = getTable();
			        popUpModelRow = table.rowAtPoint(point);
			        popUpModelColumn = table.convertColumnIndexToModel(table.columnAtPoint(point));
	            	displayPopupMenu(table, event.getX(), event.getY());
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
				if (event.getButton() == MouseEvent.BUTTON1 && event.getClickCount() >= 2) {
				
					Point point = event.getPoint();
					JTable table = getTable();
					popUpModelRow = table.rowAtPoint(point);
					popUpModelColumn = table.convertColumnIndexToModel(table.columnAtPoint(point));
					if (popUpModelRow >= 0 && popUpModelColumn >= 0) {
						InspectableProperty property = model[popUpModelRow][popUpModelColumn];
						InspectableProperty baseProperty = property;
						if (baseProperty instanceof SplitPropertyPart) {
						    baseProperty = ((SplitPropertyPart)baseProperty).getOwner().getOwner();
						}
						
						if (baseProperty instanceof CommentProperty) {
							CommentDialog dialog = inspector.getCommentDialog();
							dialog.setComment(property.getValue());
							dialog.setLocationRelativeTo(inspector);
							dialog.setVisible(true);
							if (dialog.isConfirmed()) {
								setValueAt(dialog.getComment(), popUpModelRow, popUpModelColumn);
							}
							fireTableCellUpdated(popUpModelRow, popUpModelColumn);
						}
					}
			    }

				if (event.getButton() == MouseEvent.BUTTON3) {
			    	Point point = event.getPoint();
			    	
			    	JTable table = getTable();
			        popUpModelRow = table.rowAtPoint(point);
			        popUpModelColumn = table.convertColumnIndexToModel(table.columnAtPoint(point));
			        
			        if (popUpModelRow >= 0 && popUpModelColumn == 0) {
			        	
		        		/* If all rows are shown, at the right click, switch the row visibility instantly. If only
		        		 * unhidden rows are shown, instead bring up a popup menu with the hide command. 
		        		 */
			        	if (showAllRows) {
		        			int propertyIndex = modelToPropertiesRowIndex(popUpModelRow);
		        			boolean state = !propertiesRowVisibilities[propertyIndex];
		        			propertiesRowVisibilities[propertyIndex] = state;
		        			((SpreadsheetRowVisible)properties[propertyIndex][0]).setVisible(state);
	        				fireTableCellUpdated(popUpModelRow, 0);
			        	} else {
			        		JPopupMenu popupMenu = new JPopupMenu();
			        		JMenuItem menuItem = new JMenuItem(hideRow);
			        		menuItem.addActionListener(new ActionListener() {
			        			public void actionPerformed(ActionEvent event) {
			        				int propertyIndex = modelToPropertiesRowIndex(popUpModelRow);
				        			boolean state = !propertiesRowVisibilities[propertyIndex];
				        			propertiesRowVisibilities[propertyIndex] = state;
				        			((SpreadsheetRowVisible)properties[propertyIndex][0]).setVisible(state);
			        				refreshModel();
			        				fireTableDataChanged();
			        			}
			        		});
			        		popupMenu.add(menuItem);
			        		popupMenu.show(table, event.getX(), event.getY());
			        	}
			        } else {
				    	displayPopupMenu(table, event.getX(), event.getY());
			        }
			    }
	        }
		}); 

		pane.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				if (event.getButton() == MouseEvent.BUTTON3) {
			        popUpModelRow = -1;
			        popUpModelColumn = -1;
					displayPopupMenu(getTableScrollPane(), event.getX(), event.getY());
			    }
	        }
		});
		
		loadView();
	}

    private void createPopupMenu() {
    	popUpMenu = new JPopupMenu();

		hideItem = new JMenuItem(hide);
		hideItem.addActionListener(this);
    	popUpMenu.add(hideItem);

		sortAscItem = new JMenuItem(sortAsc);
		sortAscItem.addActionListener(this);
    	popUpMenu.add(sortAscItem);

    	sortDesItem = new JMenuItem(sortDes);
    	sortDesItem.addActionListener(this);
    	popUpMenu.add(sortDesItem);

    	splitMenu = new JMenu(split);
    	popUpMenu.add(splitMenu);
    	updateSplitMenu();
    	
    	joinItem = new JMenuItem(join);
    	joinItem.addActionListener(this);
    	popUpMenu.add(joinItem);

	    sortSeparator = new JSeparator(); 
    	popUpMenu.add(sortSeparator);
    	
    	JMenuItem hideAllItem = new JMenuItem(hideAll);
    	hideAllItem.addActionListener(this);
    	popUpMenu.add(hideAllItem);

    	JMenuItem showAllItem = new JMenuItem(showAll);
    	showAllItem.addActionListener(this);
    	popUpMenu.add(showAllItem);

    	JMenu visibilityMenu = new JMenu(visibility);
    	popUpMenu.add(visibilityMenu);

    	popUpMenu.addSeparator();

    	ArrayList list = getModeNames();

       	for (int i = 0; i < list.size(); i++) {
       		String action = list.get(i).toString();
       		JMenuItem menuItem = new JMenuItem(action + " columns");
       		menuItem.setActionCommand(action);
       		menuItem.addActionListener(this);
       		popUpMenu.add(menuItem);
       	}
    	
    	popUpMenu.addSeparator();

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
    	
    	JMenuItem setBackgroundItem = new JMenuItem(backgroundColorString);
    	setBackgroundItem.addActionListener(this);
    	popUpMenu.add(setBackgroundItem);
    	
    	JMenuItem defaultBackgroundItem = new JMenuItem(defaultColorString);
    	defaultBackgroundItem.addActionListener(this);
    	popUpMenu.add(defaultBackgroundItem);

    	popUpMenu.addSeparator();

    	showAllRowsItem = new JCheckBoxMenuItem(showAllRowsString);
    	showAllRowsItem.setSelected(showAllRows);
    	showAllRowsItem.addActionListener(this);
    	popUpMenu.add(showAllRowsItem);

    	popUpMenu.addSeparator();
    	
    	extendCountersItem = new JMenuItem(extendCounters);
    	extendCountersItem.addActionListener(this);
    	popUpMenu.add(extendCountersItem);
    }
    
	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		Object source = event.getSource();

		if (action.equals(hide)) {
			if (popUpModelColumn >= 0) {
	    		int propertiesColumn = modelToPropertiesColumnIndex[popUpModelColumn];
	    		propertiesColumnMenuItem[propertiesColumn].setSelected(false);
	    		propertiesColumnVisibilities[propertiesColumn] = false;
	    		updateColumnVisibility();
	    	}
		} else if (action.equals(whitespaces) || action.equals(customPattern)) {

			int index = popUpModelColumn;
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
					/* Add the data from the first row of the splitting column as starting test example. If no visible
					 * rows, add from the first among all.
					 */
					InspectableProperty testProperty = (modelRowCount >= 0) ? model[0][index] : properties[0][index];
                    dialog.setTestExample(testProperty.getValue());
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
			if (popUpModelColumn >= 0) {
				propertiesColumnSplitData[modelToPropertiesColumnIndex[popUpModelColumn]] = null;
				refreshModel();
				// refresh
				saveView();
				loadView();
	    	}
		} else if (action.equals(sortAsc) || action.equals(sortDes)) {
	    	if (popUpModelColumn >= 0) {
	    		updateSortedColumn(popUpModelColumn);
	    	}
		} else if (action.equals(hideAll) || action.equals(showAll)) {
		    boolean state = action.equals(showAll);
		    // do this on all but the first two
		    for (int i = solidPropertiesColumnCount + 1; i < propertiesColumnMenuItem.length; i++) {
		    	propertiesColumnMenuItem[i].setSelected(state);
				propertiesColumnVisibilities[i] = state;
		    }
		    updateColumnVisibility();
		    
		} else if (action.equals(showAllRowsString)) {
			setShowAllRows(((JCheckBoxMenuItem)source).isSelected());
			
		} else if (action.equals(backgroundColorString)) {
		    final JColorChooser chooser = ColorChooser.getInstance();
		    
		    chooser.setColor(background);
		    ActionListener okListener = new ActionListener() {
				public void actionPerformed(ActionEvent event) {
				    background = chooser.getColor();
				    updateBackgroundColor();
				}
		    };
		    JColorChooser.createDialog(inspector, colorChooserTitle, true, chooser, okListener, null)
		        .setVisible(true); 
		} else if (action.equals(defaultColorString)) {
		    background = defaultBackground;
		    updateBackgroundColor();
		} else if (action.equals(extendCounters)) {
			extendCounters(table.getSelectedRows(), table.getSelectedColumns());
		} else if (source instanceof JCheckBoxMenuItem) {
			propertiesColumnVisibilities[nameToPropertiesColumnIndex(action)] =
				((JCheckBoxMenuItem)source).isSelected();
		    updateColumnVisibility();
		} else {
			if (popUpModelColumn >= 0) {
				Integer recentIndexObject = (Integer)nameToRecentSplitDataIndex.get(action);
				if (recentIndexObject != null) {
					int recentIndex = recentIndexObject.intValue();
					// Add the used item to the top of the list.
					SplitData data = (SplitData)recentSplitData.remove(recentIndex);
					recentSplitData.add(0, data);
					updateSplitMenu();

					int propertiesColumnIndex = modelToPropertiesColumnIndex[popUpModelColumn]; 
					String name = propertiesColumnNames[propertiesColumnIndex];
					SplitData split = new SplitData(name, data.getDelimiterTypeString(), data.getPattern());
					propertiesColumnSplitData[propertiesColumnIndex] = split;
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
	
	private void setShowAllRows(boolean state) {
		showAllRows = state;
		showAllRowsItem.setSelected(state);
		refreshModel();
		table.getTableHeader().repaint();
		fireTableDataChanged();
	}

	private void updateBackgroundColor() {
	    table.setBackground(background);
	    table.getTableHeader().setBackground(background);
	    pane.getViewport().setBackground(background);
	} 
	
	private void updateSortedColumn(int modelColumn) {
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
	
	private void sortModelRows() {
		if (sortedPropertiesColumn >= 0) {
			comparator.setColumn(propertiesToModelColumnIndex[sortedPropertiesColumn] + sortedSplitIndex);
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

    private void displayPopupMenu(JComponent component, int posX, int posY) {
        boolean inTable = popUpModelRow >= 0 && popUpModelColumn >= 0;
        boolean first = popUpModelColumn == 0;
        boolean persistant = popUpModelColumn <= solidPropertiesColumnCount;
        boolean splitted = false;
        if (inTable) {
        	splitted = propertiesColumnSplitData[modelToPropertiesColumnIndex[popUpModelColumn]] != null;
        }
        
        boolean manipulatable = inTable && !first && !persistant; 
        boolean sortable = inTable && !first;
        
        setJComponentVisible(hideItem, manipulatable);
        setJComponentVisible(splitMenu, manipulatable && !splitted);
        setJComponentVisible(joinItem, manipulatable && splitted);
        setJComponentVisible(sortAscItem, sortable);
        setJComponentVisible(sortDesItem, sortable);
        setJComponentVisible(sortSeparator, sortable);
        
        extendCountersItem.setEnabled(table.getSelectedColumnCount() >= 1 && table.getSelectedRowCount() >= 2);
        
        popUpMenu.show(component, posX, posY);
    }
    
    private void setJComponentVisible(JComponent component, boolean visible) {
    	if (component.isVisible() != visible) {
    		component.setVisible(visible);
    	}
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
    
    private int modelToPropertiesRowIndex(int modelRow) {
    	int propertiesNamesColumnIndex = nameToPropertiesColumnIndex(propertiesNamesColumn);
    	if (propertiesNamesColumnIndex == -1) {
    		return -1;
    	}
    	String rowName = model[modelRow][propertiesToModelColumnIndex[propertiesNamesColumnIndex]].getValue();
    	return nameToPropertiesRowIndex(rowName);
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
	    String value = model[rowIndex][columnIndex].getValue();
		// TODO: The only properties with null values are CommentProperty. This could maybe be fixed.
	    return (value != null) ? value : ""; 
	}

	public void setValueAt(Object aValue, int row, int column) {
		
		InspectableProperty property = model[row][column];

		int propertiesRow = modelToPropertiesRowIndex(row);
		
		// If a name changes, remove the old name.
		int propertiesColumn = modelToPropertiesColumnIndex[column];
		int propertiesNamesColumnIndex = nameToPropertiesColumnIndex(propertiesNamesColumn);
		if (propertiesColumn == propertiesNamesColumnIndex) {
			nameToPropertiesRowIndex.remove(property.getValue());
		}
		
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

			creatorProperty.setName(propertiesColumnNames[propertiesColumn]);
			creatorProperty.setValue(aValue.toString());
			property = creatorProperty.getCreatedProperty();
			
			if (propertyGroup != null) {
				propertyGroup.setOwner(property);
			} else {
				model[row][column] = property; 
			}

			properties[propertiesRow][propertiesColumn] = property; 
			
		} else {
			model[row][column].setValue(aValue.toString());
		}
		
		// If the name was changed, add the new name.
		if (propertiesColumn == propertiesNamesColumnIndex) {
   			nameToPropertiesRowIndex.put(model[row][column].getValue(), new Integer(propertiesRow));
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
	    return (column > 0) ? PropertyTableModel.DISP_VALUE : PropertyTableModel.DISP_VISIBILITY;
	}
	
	public int getHeaderDisplayType(int column) {
	    if (column > 0) {
	    	return PropertyTableModel.HEADERDISP_TEXT;
	    }
	    return showAllRows ? PropertyTableModel.HEADERDISP_EYE : PropertyTableModel.HEADERDISP_NONE;
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
		
		UndoManager.getInstance().startMacroAction();
		for (int c = 0; c < viewColumns.length; c++) {
			int modelColumn = table.convertColumnIndexToModel(viewColumns[c]);
			
			String firstEntry = model[rows[0]][modelColumn].getValue();
			String secondEntry = model[rows[1]][modelColumn].getValue();
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
				model[rows[r]][modelColumn].setValue(baseString + value);
				value += step;
				fireTableCellUpdated(rows[r], modelColumn);
			}
		}
        UndoManager.getInstance().stopMacroAction();
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
		    background = defaultBackground;
    		updateBackgroundColor();
    	    createDefaultColumnModel();
    		table.resizeColumns();
			return;
		}
		
		if (showAllRows != record.isShowAllRows()) {
			showAllRows = record.isShowAllRows();
			table.getTableHeader().repaint();
		}
		
		background = new Color(record.getBackgroundColor());
		updateBackgroundColor();
		
    	// If no rows, assume all are visible. 
        for (int i = 0; i < propertiesRowCount; i++) {
        	propertiesRowVisibilities[i] = true;
			((SpreadsheetRowVisible)properties[i][0]).setVisible(true);
        }
		String[] rows = record.getHiddenRows();
		if (rows != null && rows.length > 0) {
            for (int i = 0; i < rows.length; i++) {
            	int propertiesIndex = nameToPropertiesRowIndex(rows[i]);
            	if (propertiesIndex >= 0) {
            		propertiesRowVisibilities[propertiesIndex] = false;
        			((SpreadsheetRowVisible)properties[propertiesIndex][0]).setVisible(false);
            	}
            }
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
    	}
		refreshModel();
    	
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
        		sortedPropertiesColumn = index;
        		sortedSplitIndex = splitIndex;
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
		column.setHeaderRenderer(table.getDefaultRenderer(String.class));
		String name = propertiesColumnNames[modelToPropertiesColumnIndex[colIndex]];
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
	
	public void saveView() {
		String typeSign = getTypeString(inspectables[0]);
		
		// Determine if the current column visibility/order and splitting is default.
		boolean defaultNoSortOrder = (sortedPropertiesColumn == -1);
		boolean defaultColumnVisibilityAndOrder = true;
		boolean defaultNoColumnSplit = true;
		boolean defaultBackgroundColour = background.equals(defaultBackground);
		boolean defaultNoRecentSplits = recentSplitData.isEmpty();
		boolean defaultRowVisibility = true;
		
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
        for (int i = 0; i < propertiesRowCount; i++) {
        	if (!propertiesRowVisibilities[i]) {
        		defaultRowVisibility = false;
        		break;
        	}
        }
        
        boolean allDefault = defaultNoSortOrder && defaultColumnVisibilityAndOrder && defaultNoColumnSplit
        	&& defaultBackgroundColour && defaultNoRecentSplits && defaultRowVisibility; 

        // If everything is default, save nothing and remove the data.
        if (allDefault && mode == 0 && showAllRows) {
        	SpreadsheetTableViewData.getInstance().remove(typeSign + dataType);
        	return;
        }

        // If other modes, save an entry with all data that is not default. 
        String modeName = getModeNames().get(mode).toString();
        SpreadsheetTableViewRecord viewRecord =
        	new SpreadsheetTableViewRecord(typeSign, dataType, modeName, showAllRows, background.getRGB());
        
        if (!defaultNoSortOrder) {
        	String name = propertiesColumnNames[sortedPropertiesColumn];
        	viewRecord.setRowOrder(new SpreadsheetRowOrder(name, sortedSplitIndex, sortedOrderAsc));
        }
        
        if (!defaultColumnVisibilityAndOrder) {
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
            String[] columns = new String[columnStrings.size()]; 
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
        
        if (!defaultRowVisibility) {
    		Vector rowsStrings = new Vector();
        	int namesIndex = nameToPropertiesColumnIndex(propertiesNamesColumn);
        	if (namesIndex >= 0) {
        		for (int i = 0; i < propertiesRowCount; i++) {
        			if (!propertiesRowVisibilities[i]) {
        				rowsStrings.add(properties[i][namesIndex].getValue());
        			}
        		}
            }
        	String[] rows = new String[rowsStrings.size()]; 
            rowsStrings.copyInto(rows);
    		viewRecord.setHiddenRows(rows);
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
