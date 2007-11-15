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

import java.util.HashMap;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import com.cosylab.vdct.plugin.debug.PluginDebugManager;

public class SpreadsheetTableModel extends AbstractTableModel
        implements PropertyTableModel {
	
	private String[] columnNames = null;
	private InspectableProperty[][] fields = null;
	private EmptyProperty empty = null;
	
	public SpreadsheetTableModel(Vector inspectData)
	        throws IllegalArgumentException {

  		if (inspectData == null || inspectData.size() == 0) {
  			throw new IllegalArgumentException(
  					"inspectables must not be empty or null");
  		}
  		
  		HashMap map = new HashMap();
        int size = inspectData.size();
        InspectableProperty[][] properties = new InspectableProperty[size][];
        int columnCount = 0;
        Vector colStrings = new Vector();

		for (int i = 0; i < size; i++) {
			properties[i] = ((Inspectable)inspectData.get(i)).getProperties(-1);
			
			for (int j = 0; j < properties[i].length; j++) {
				String name = properties[i][j].getName();
				if (map.get(name) == null) {
					map.put(name, Integer.valueOf(columnCount));
					colStrings.add(name);
					columnCount++;
				}
			}
		}
		columnNames = new String[columnCount];
		colStrings.copyInto(columnNames);
  		
		empty = new EmptyProperty();
    	fields = new InspectableProperty[size][];
		for (int i = 0; i < size; i++) {
			fields[i] =  new InspectableProperty[columnCount];
			for (int j = 0; j < columnCount; j++) {
				fields[i][j] = empty;
			}
		}

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < properties[i].length; j++) {
				String name = properties[i][j].getName();
				// map must contain the key 
				fields[i][((Integer)map.get(name)).intValue()] = properties[i][j];
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
		return fields[0].length;
	}

	public int getRowCount() {
		return fields.length;
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		return fields[rowIndex][columnIndex].getValue();
	}

	public void setValueAt(Object aValue, int row, int column) {
		fields[row][column].setValue(aValue.toString());
		// update the whole row as fields validity can change
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
