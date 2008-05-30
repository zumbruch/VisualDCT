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
import java.util.Iterator;
import java.util.Map;

/**
 * @author ssah
 */
public class SpreadsheetTableViewData {

	private static SpreadsheetTableViewData data = null;
	
	private Map map = null;
	/**
	 * 
	 */
	private SpreadsheetTableViewData() {
		super();
		map = new HashMap();
	}
	
	public static SpreadsheetTableViewData getInstance() {
		if (data == null) {
			data = new SpreadsheetTableViewData(); 
		}
		return data ;
	}
	
	public void add(SpreadsheetTableViewRecord record) {
		map.put(record.getKey(), record);
	}

	/** Returns the record with the given key, or null if there is no such record.
	 */
	public SpreadsheetTableViewRecord get(String key) {
		return (SpreadsheetTableViewRecord)map.get(key);
	}

	public void remove(String key) {
		map.remove(key);
	}
	
	public Iterator getRecords() {
		return map.values().iterator();
	}
}
