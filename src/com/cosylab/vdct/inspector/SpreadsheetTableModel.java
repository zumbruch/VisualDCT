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
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.cosylab.vdct.plugin.debug.PluginDebugManager;
import com.cosylab.vdct.vdb.NameValueInfoProperty;

public class SpreadsheetTableModel extends AbstractTableModel implements PropertyTableModel {
	
	private HashMap colIndicesMap = null;
	private String[] columnNames = null;
	private InspectableProperty[][] fields = null;
	private NameValueInfoProperty emptyProperty = null;
	private Inspectable[] inspectables = null;
	private TableColumn hiddenColumns[] = null; 

	SpreadsheetRowComparator comparator = null;

    private SpreadsheetTable table = null;
    private JPopupMenu popUpMenu = null;
    private JCheckBoxMenuItem[] columnHandlers = null;
	
	private int columnCount = 0;
	private int rowCount = 0;
	private int mode = 0;
	
	private static final String showAll = "Show all"; 
	private static final String hideAll = "Hide all"; 
	
	public SpreadsheetTableModel(Vector inspectData) throws IllegalArgumentException {

  		if (inspectData == null || inspectData.size() == 0) {
  			throw new IllegalArgumentException("inspectables must not be empty or null");
  		}
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
			    if ((event.getClickCount() == 1) && (event.getButton() == MouseEvent.BUTTON1)) {
			    	JTableHeader header = (JTableHeader)event.getSource();
			    	TableColumnModel columnModel = header.getColumnModel();
			    	int viewColumn = columnModel.getColumnIndexAtX(event.getX());
			    	int column = columnModel.getColumn(viewColumn).getModelIndex();
			    	if (column != -1) {
			    		comparator.setColumn(column);
			    		Arrays.sort(fields, comparator);
			    		fireTableDataChanged(); 
			    	}
	            }
	        }
	    });
	    
		table.addMouseListener(new MouseAdapter() {
	        public void mouseClicked(MouseEvent event) {
			    if ((event.getClickCount() == 1) && (event.getButton() == MouseEvent.BUTTON3)) {
					popUpMenu.show(getTable(), event.getX(), event.getY());
			    }
	        }
	    });

        rowCount = inspectables.length;
		refreshProperties();
	}
	
    private void createPopupMenu() {
    	popUpMenu = new JPopupMenu();

		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String action = event.getActionCommand();
				Object source = event.getSource();
				
				if (source instanceof JCheckBoxMenuItem) {
				    updateColumnState((JCheckBoxMenuItem)source);
				} else if (action.equals(hideAll) || action.equals(showAll)) {
				    boolean state = action.equals(showAll);
				    for (int i = 0; i < columnHandlers.length; i++) {
				    	columnHandlers[i].setSelected(state);
				    	updateColumnState(columnHandlers[i]);
				    }
				} else {
					ArrayList list = inspectables[0].getModeNames();
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
    		
    		private void updateColumnState(JCheckBoxMenuItem checkBox) {
    			TableColumnModel columnModel = table.getTableHeader().getColumnModel();
    			String action = checkBox.getActionCommand();

    			int index = ((Integer)colIndicesMap.get(action)).intValue();

    			if (checkBox.isSelected() && hiddenColumns[index] != null) {
    				columnModel.addColumn(hiddenColumns[index]);
    				hiddenColumns[index] = null;
    				index++;
    				while ((index < columnCount) && (hiddenColumns[index] != null)) {
    					index++; 
    				}
    				if (index < columnCount) {
    					index = columnModel.getColumnIndex(columnNames[index]);
    					columnModel.moveColumn(columnModel.getColumnCount() - 1, index);
    				}
    			} else if (!checkBox.isSelected() && hiddenColumns[index] == null) {
    				hiddenColumns[index] = columnModel.getColumn(columnModel.getColumnIndex(action));
    				columnModel.removeColumn(hiddenColumns[index]);
    			}
    		} 
		};
    	
    	JMenuItem menuItem = new JMenuItem(hideAll);
    	menuItem.addActionListener(listener);
    	popUpMenu.add(menuItem);

    	menuItem = new JMenuItem(showAll);
    	menuItem.addActionListener(listener);
    	popUpMenu.add(menuItem);

    	popUpMenu.add(new JSeparator());

        columnHandlers = new JCheckBoxMenuItem[Math.max(columnCount - 2, 0)];
    	
    	// add check boxes for all but the first two: empty column and name
    	for (int j = 0; j < columnCount - 2; j++) {
   			JCheckBoxMenuItem checkBoxItem = new JCheckBoxMenuItem(columnNames[j + 2]);
   			columnHandlers[j] = checkBoxItem;
   			checkBoxItem.setSelected(true);
   			checkBoxItem.addActionListener(listener);
   			popUpMenu.add(checkBoxItem);
    	}
    	ArrayList list = inspectables[0].getModeNames();
    	if (list != null && list.size() > 0) {
        	popUpMenu.add(new JSeparator());

    		for (int i = 0; i < list.size(); i++) {
    			menuItem = new JMenuItem(list.get(i).toString());
    			menuItem.addActionListener(listener);
    			popUpMenu.add(menuItem);
    		}
    	}
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
    	for (int j = 0; j < columnCount; j++) {
    	    hiddenColumns[j] = null;
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
}
