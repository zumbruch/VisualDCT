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

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import com.cosylab.vdct.plugin.debug.PluginDebugManager;

public class SpreadsheetTableModel extends AbstractTableModel
        implements PropertyTableModel {
	
	private Vector inspectables = null;
	private int propertiesCount = 0;
	private Inspectable firstRow = null;
	
	public SpreadsheetTableModel(Vector inspectables)
	        throws IllegalArgumentException {

  		if (inspectables == null || inspectables.size() == 0) {
  			throw new IllegalArgumentException(
  					"inspectables must not be empty or null");
  		}
		this.inspectables = inspectables;
  		firstRow = (Inspectable)inspectables.elementAt(0);
    	propertiesCount = firstRow.getProperties(0).length;
	}

	public Class getColumnClass(int column) {
		return String.class;
	}

	public String getColumnName(int column) {
		return firstRow.getProperties(0)[column].getName();
	}

	public int getColumnCount() {
		return propertiesCount;
	}

	public int getRowCount() {
		return inspectables.size();
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		Inspectable inspectable = (Inspectable)inspectables.elementAt(rowIndex);
		return inspectable.getProperties(0)[columnIndex].getValue();
	}

	public void setValueAt(Object aValue, int row, int column) {
		Inspectable inspectable = (Inspectable)inspectables.elementAt(row);
		inspectable.getProperties(0)[column].setValue(aValue.toString());
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// no editing in debug
		return !PluginDebugManager.isDebugState();
	}

	public InspectableProperty getPropertyAt(int row, int column) {
		Inspectable inspectable = (Inspectable)inspectables.elementAt(row);
		return inspectable.getProperties(0)[column];
	}
}
