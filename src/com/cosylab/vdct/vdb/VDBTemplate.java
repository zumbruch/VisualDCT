package com.cosylab.vdct.vdb;

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

import java.util.Hashtable;
import com.cosylab.vdct.graphics.objects.Group;

/**
 * Data object representing EPICS DB template.
 * All data is obtained from DB (.template) file.
 * <code>Group</code> contains template structure.
 * The name of the group is "<name>.template". 
 * @author Matej
 */

public class VDBTemplate
{
	
	String id = null;
	String fileName = null;
	String description = null;
	Hashtable inputs = null;
	Hashtable outputs = null;
	Hashtable inputComments = null;
	Hashtable outputComments = null;
	Group group = null;
	
	/**
	 * Constructor for VDBTemplate.
	 */
	public VDBTemplate(String id, String fileName)
	{
		this.id = id;
		this.fileName = fileName;
	}

	/**
	 * Returns the description.
	 * @return String
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Returns the group.
	 * @return Group
	 */
	public Group getGroup()
	{
		return group;
	}

	/**
	 * Returns the inputs.
	 * @return Hashtable
	 */
	public Hashtable getInputs()
	{
		return inputs;
	}

	/**
	 * Returns the outputs.
	 * @return Hashtable
	 */
	public Hashtable getOutputs()
	{
		return outputs;
	}

	/**
	 * Sets the description.
	 * @param description The description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Sets the group.
	 * @param group The group to set
	 */
	public void setGroup(Group group)
	{
		this.group = group;
	}

	/**
	 * Sets the inputs.
	 * @param inputs The inputs to set
	 */
	public void setInputs(Hashtable inputs)
	{
		this.inputs = inputs;
	}

	/**
	 * Sets the outputs.
	 * @param outputs The outputs to set
	 */
	public void setOutputs(Hashtable outputs)
	{
		this.outputs = outputs;
	}

	/**
	 * Returns the fileName.
	 * @return String
	 */
	public String getFileName()
	{
		return fileName;
	}

	/**
	 * Returns the id.
	 * @return String
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Returns the inputComments.
	 * @return Hashtable
	 */
	public Hashtable getInputComments()
	{
		return inputComments;
	}

	/**
	 * Returns the outputComments.
	 * @return Hashtable
	 */
	public Hashtable getOutputComments()
	{
		return outputComments;
	}

	/**
	 * Sets the inputComments.
	 * @param inputComments The inputComments to set
	 */
	public void setInputComments(Hashtable inputComments)
	{
		this.inputComments = inputComments;
	}

	/**
	 * Sets the outputComments.
	 * @param outputComments The outputComments to set
	 */
	public void setOutputComments(Hashtable outputComments)
	{
		this.outputComments = outputComments;
	}

}
