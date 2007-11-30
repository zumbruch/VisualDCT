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

/**
 * @author ssah
 *
 */
public class SplitData {
    boolean delimiterType = false;
    private String pattern = null;
    int parts = 0;

	/**
	 * @param pattern
	 * @param repetitions
	 * @param parts
	 */
    public SplitData(boolean delimiterType, String pattern) {
    	this(delimiterType, pattern, 0);
	}
    
	/**
	 * @param pattern
	 * @param repetitions
	 * @param parts
	 */
    public SplitData(boolean delimiterType, String pattern, int parts) {
		super();
		this.pattern = pattern;
		this.delimiterType = delimiterType;
		this.parts = parts;
	}
    
    static SplitData getWhitespaceSplitData() {
    	return new SplitData(true, "\\s+");
    }

	/**
	 * @return the delimiterType
	 */
	public boolean isDelimiterType() {
		return delimiterType;
	}

	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * @return the parts
	 */
	public int getParts() {
		return parts;
	}

	/**
	 * @param parts the parts to set
	 */
	public void setParts(int parts) {
		this.parts = parts;
	}
}