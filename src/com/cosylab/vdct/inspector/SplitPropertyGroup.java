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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ssah
 *
 */
public class SplitPropertyGroup {

	private InspectableProperty owner = null;
	private SplitPropertyPart[] parts = null;
	private String trail = null;
	
	private static final String defaultDelimiter = " ";

	/**
	 * @param owner
	 * @param splitPattern
	 */
	public SplitPropertyGroup(InspectableProperty owner, SplitData splitData) {
		super();
		this.owner = owner;
		
		boolean delimiterType = splitData.isDelimiterType();
		String pattern = splitData.getPattern();
		int partsCount = splitData.getParts();
		
		String value = owner.getValue();
		Vector partsVector = new Vector();
		int foundParts = 0;

		String delimiter = ""; 
		if (delimiterType) {
			Matcher matcher = Pattern.compile(pattern).matcher(value);
			int partStart = 0;
			int partEnd = 0;
			String part = "";
			while (matcher.find()) {
				partEnd = matcher.start();
				part = value.substring(partStart, partEnd);
				partStart = matcher.end();
				partsVector.add(new SplitPropertyPart(foundParts, this, part, "", delimiter, true));
				delimiter = matcher.group();
				foundParts++;
			}
			
			partsVector.add(new SplitPropertyPart(foundParts, this, value.substring(partStart), "", delimiter, true));
			foundParts++;
			trail = "";
		} else {
			Matcher matcher = Pattern.compile(pattern).matcher(value);
			boolean match = matcher.matches();

			
			boolean nestedGroups = false;
			int leadStart = 0;
			int leadEnd = 0;
			if (match) {
				// First group is the whole expression, skip it.
				int groupCount = matcher.groupCount() + 1;
				int group = 1;
				while (group < groupCount && foundParts < partsCount) {
					leadEnd = matcher.start(group);
					if (leadStart > leadEnd) {
						nestedGroups = true;
						break;
					}
					String lead = value.substring(leadStart, leadEnd);
					leadStart = matcher.end(group);
					partsVector.add(new SplitPropertyPart(foundParts, this, matcher.group(group), "", lead, true));
					foundParts++;
					group++;
				}
			}
			if (match && !nestedGroups) {
				trail = value.substring(leadStart);
			} else {
				partsVector.clear();
				partsVector.add(new SplitPropertyPart(0, this, value, "", "", true));
				foundParts = 1;
				trail = "";
			}	
		}
		
		/* If there are columns remaining, fill them with empty fields with the same delimiter. If delimiter is not
		 * known, use default.
		 */
		if (delimiter.equals("")) {
			delimiter = defaultDelimiter;
		}
		while (foundParts < partsCount) {
			partsVector.add(new SplitPropertyPart(foundParts, this, "", delimiter, "", true));
			foundParts++;
		}
		parts = new SplitPropertyPart[partsVector.size()];
		partsVector.copyInto(parts);
	}

	static int getPartsCount(String value, SplitData splitData) {
		
		boolean delimiterType = splitData.isDelimiterType();
		String pattern = splitData.getPattern();
		
		if (delimiterType) {
			return value.split(pattern).length;
		} else {
			Matcher matcher = Pattern.compile(pattern).matcher(value);
			boolean match = matcher.matches();
			return match ? matcher.groupCount() : 0;
		}
	}
	
	public void setValuePart(SplitPropertyPart part) {
		String value = "";
		for (int p = 0; p < parts.length; p++) {
			value += parts[p].getLead() + parts[p].getValue();
		}
		owner.setValue(value + trail);
	}

	/**
	 * @return the owner
	 */
	public InspectableProperty getOwner() {
		return owner;
	}
	
	public SplitPropertyPart getPart(int index) {
		return parts[index];
	}
}
