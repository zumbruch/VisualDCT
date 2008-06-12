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

package com.cosylab.vdct.graphics;

import java.util.ArrayList;
import java.util.Vector;

/**
 * @author ssah
 *
 */
public class CopyContext {

	// to remember on cut from which group object has been cut 
	private ArrayList pasteNames = null;
	// to remember copied objects for multiple pasting
	private Vector copiedObjects = null;
	private int pasteCount = 0;
	
	private double pasteX = 0;
	private double pasteY = 0;
	private boolean doOffsetAtPaste = false;
	
	
	public CopyContext() {
		super();
		pasteNames = new ArrayList();
		copiedObjects = new Vector();
	}
	
	public int getPasteCount() {
		return pasteCount;
	}
	public void setPasteCount(int pasteCount) {
		this.pasteCount = pasteCount;
	}
	public double getPasteX() {
		return pasteX;
	}
	public void setPasteX(double pasteX) {
		this.pasteX = pasteX;
	}
	public double getPasteY() {
		return pasteY;
	}
	public void setPasteY(double pasteY) {
		this.pasteY = pasteY;
	}
	public boolean isDoOffsetAtPaste() {
		return doOffsetAtPaste;
	}
	public void setDoOffsetAtPaste(boolean doOffsetAtPaste) {
		this.doOffsetAtPaste = doOffsetAtPaste;
	}
	public ArrayList getPasteNames() {
		return pasteNames;
	}
	public Vector getCopiedObjects() {
		return copiedObjects;
	}
}
