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

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;

/**
 * @author ssah
 *
 */
public class SpreadsheetTable extends JTable {
	
    private static Graphics2D graphics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).createGraphics();
    
    SpreadsheetTableModel spreadsheetModel = null;
	
	public SpreadsheetTable(Vector data) {
		super();
	    setName("ScrollPaneTable");
	
	    setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	    setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
	    setColumnSelectionAllowed(true);
	    setBackground(new Color(204, 204, 204));
	    setShowVerticalLines(true);
	    setGridColor(Color.black);
	    setBounds(0, 0, 200, 200);
	    setRowHeight(17);
	}

	/** When selecting the fields in the first column, selects the whole row.
	 */
	public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
		
		super.changeSelection(rowIndex, columnIndex, toggle, extend);
		
		setColumnSelectionAllowed(true);
		if (getSelectedColumn() == 0) {
			setColumnSelectionAllowed(false);
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.JTable#setModel(javax.swing.table.TableModel)
	 */
	public void setModel(SpreadsheetTableModel spreadsheetModel) {
		this.spreadsheetModel = spreadsheetModel;
		super.setModel(spreadsheetModel);
	}

	public void refresh() {
		spreadsheetModel.refresh();
		repaint();
	}
	
	public void resizeColumns() {
    	
    	FontMetrics metrics = graphics.getFontMetrics(getFont());

    	int colCount = getColumnCount();
    	int rowCount = getRowCount();
    	TableColumnModel colModel = getColumnModel(); 

    	for (int i = 0; i < colCount; i++) {
    		int colWidth = (i == 0) ? 0 : 48;
    		for (int j = 0; j < rowCount; j++) {
    			String value = getValueAt(j, i).toString();
    			colWidth = Math.max(colWidth, metrics.stringWidth(value));
    		}
    		colModel.getColumn(i).setPreferredWidth(colWidth + 16);
    	}
    }
}
