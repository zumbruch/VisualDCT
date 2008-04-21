/**
 * Copyright (c) 2002, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
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
import java.util.Map;

/** This stores the view of a single spreadsheet table.
 * 
 * @author ssah
 */
public class SpreadsheetTableViewRecord {

	private String type = null;
	private String name = null;
	
	private String modeName = null;
	private Boolean showAllRows = null;
	private Integer backgroundColor = null;

	private SpreadsheetRowOrder rowOrder = null;
	private Map columns = null;
	private SplitData[] splitColumns = null;
	private String[] hiddenRows = null;
	private SplitData[] recentSplits = null;
	
	/**
	 * @param type
	 * @param name
	 */
	public SpreadsheetTableViewRecord(String type, String name) {
		super();
		this.type = type;
		this.name = name;
		columns = new HashMap();
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the modeName
	 */
	public String getModeName() {
		return modeName;
	}
	
	/**
	 * @return the rowOrder
	 */
	public SpreadsheetRowOrder getRowOrder() {
		return rowOrder;
	}

	/**
	 * @param rowOrder the rowOrder to set
	 */
	public void setRowOrder(SpreadsheetRowOrder rowOrder) {
		this.rowOrder = rowOrder;
	}

	/**
	 * @return the splitColumns
	 */
	public SplitData[] getSplitColumns() {
		return splitColumns;
	}

	public String getKey() {
		return type + name;
	}

	/**
	 * @return the recentSplits
	 */
	public SplitData[] getRecentSplits() {
		return recentSplits;
	}

	/**
	 * @param recentSplits the recentSplits to set
	 */
	public void setRecentSplits(SplitData[] recentSplits) {
		this.recentSplits = recentSplits;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param modeName the modeName to set
	 */
	public void setModeName(String modeName) {
		this.modeName = modeName;
	}

	/**
	 * @param splitColumns the splitColumns to set
	 */
	public void setSplitColumns(SplitData[] splitColumns) {
		this.splitColumns = splitColumns;
	}

	/**
	 * @return the rows
	 */
	public String[] getHiddenRows() {
		return hiddenRows;
	}

	/**
	 * @param rows the rows to set
	 */
	public void setHiddenRows(String[] rows) {
		this.hiddenRows = rows;
	}

	public Boolean getShowAllRows() {
		return showAllRows;
	}

	public void setShowAllRows(Boolean showAllRows) {
		this.showAllRows = showAllRows;
	}

	public Integer getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Integer backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	/**
	 * @return the columns
	 */
	public Map getColumns() {
		return columns;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(Map columns) {
		this.columns = columns;
	}
}
