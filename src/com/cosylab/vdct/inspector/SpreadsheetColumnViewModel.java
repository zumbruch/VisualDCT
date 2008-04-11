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

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.ListSelectionModel;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * @author ssah
 *
 */
public class SpreadsheetColumnViewModel extends SpreadsheetSplitViewModel implements TableColumnModel {
	
	TableColumnModel columnModel = null;

	/**
	 * @param dataType
	 * @param displayData
	 * @param loadedData
	 * @throws IllegalArgumentException
	 */
	public SpreadsheetColumnViewModel(String dataType, Vector displayData,
			Vector loadedData) throws IllegalArgumentException {
		super(dataType, displayData, loadedData);
		columnModel = new DefaultTableColumnModel();
	}

	public void addColumn(TableColumn column) {
		columnModel.addColumn(column);
	}

	public void addColumnModelListener(TableColumnModelListener x) {
		columnModel.addColumnModelListener(x);
	}

	public TableColumn getColumn(int columnIndex) {
		return columnModel.getColumn(columnIndex);
	}

	public int getColumnCount() {
		return columnModel.getColumnCount();
	}

	public int getColumnIndex(Object columnIdentifier) {
		return columnModel.getColumnIndex(columnIdentifier);
	}

	public int getColumnIndexAtX(int position) {
		return columnModel.getColumnIndexAtX(position);
	}

	public int getColumnMargin() {
		return columnModel.getColumnMargin();
	}

	public Enumeration getColumns() {
		return columnModel.getColumns();
	}

	public boolean getColumnSelectionAllowed() {
		return columnModel.getColumnSelectionAllowed();
	}

	public int getSelectedColumnCount() {
		return columnModel.getSelectedColumnCount();
	}

	public int[] getSelectedColumns() {
		return columnModel.getSelectedColumns();
	}

	public ListSelectionModel getSelectionModel() {
		return columnModel.getSelectionModel();
	}

	public int getTotalColumnWidth() {
		return columnModel.getTotalColumnWidth();
	}

	public void moveColumn(int columnIndex, int newIndex) {
		columnModel.moveColumn(columnIndex, newIndex);
	}

	public void removeColumn(TableColumn column) {
		columnModel.removeColumn(column);
	}

	public void removeColumnModelListener(TableColumnModelListener x) {
		columnModel.removeColumnModelListener(x);
	}

	public void setColumnMargin(int newMargin) {
		columnModel.setColumnMargin(newMargin);
	}

	public void setColumnSelectionAllowed(boolean flag) {
		columnModel.setColumnSelectionAllowed(flag);
	}

	public void setSelectionModel(ListSelectionModel newModel) {
		columnModel.setSelectionModel(newModel);
	}
}
