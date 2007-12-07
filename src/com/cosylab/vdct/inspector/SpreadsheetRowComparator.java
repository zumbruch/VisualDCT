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

import java.util.Comparator;

public class SpreadsheetRowComparator implements Comparator {
	
    private int column = 0;
    private int sign = 1;
    SpreadsheetTableModel tableModel = null;
    
    public SpreadsheetRowComparator(SpreadsheetTableModel tableModel) {
    	super();
    	this.tableModel = tableModel;
    }
	
	public void setColumn(int column) {
		this.column = column;
	}

	public void setAscending(boolean ascending) {
		sign = ascending ? 1 : -1;
	}

	public int compare(Object arg0, Object arg1) {
		
		InspectableProperty[] first = (InspectableProperty[])arg0;  
		InspectableProperty[] second = (InspectableProperty[])arg1;

	    String firstString = first[column].getValue();
	    String secondString = second[column].getValue();

	    String firstName = SplitData.removeValueAtEnd(firstString);
	    String secondName = SplitData.removeValueAtEnd(secondString);
	    
	    int firstNumber = SplitData.extractValueAtEnd(firstString);
	    int secondNumber = SplitData.extractValueAtEnd(secondString);

		int nameComp = firstName.compareTo(secondName); 
        if (nameComp != 0) {
        	return sign * nameComp;

        }
        // names with no number are displayed before any with numbers
		return sign * (firstNumber - secondNumber); 
	}
}
