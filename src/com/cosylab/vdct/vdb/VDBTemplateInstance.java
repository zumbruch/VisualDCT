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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.TreeMap;

/**
 * @author Matej
 */
public class VDBTemplateInstance
{
	String name = null;
	VDBTemplate template = null;
//	Hashtable properties = null;
	TreeMap properties = null;
	Hashtable inputs = null;
	Hashtable outputs = null;
	
	/**
	 * Constructor.
	 */
	public VDBTemplateInstance(String name, VDBTemplate template)
	{
		this.name = name;
		this.template = template;
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
	 * Returns the properties.
	 * @return Hashtable
	 */
//	public Hashtable getProperties()
	public TreeMap getProperties()
	{
		return properties;
	}

	/**
	 * Returns the template.
	 * @return VDBTemplate
	 */
	public VDBTemplate getTemplate()
	{
		return template;
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
	 * Sets the properties.
	 * @param properties The properties to set
	 */
	public void setProperties(Hashtable properties)
	{
//		this.properties = properties;
		this.properties = new TreeMap(properties);
	}

	/**
	 * Make macro substitutions on a string using properties table.
	 * @param s The string to be applied
	 * @param properties The properties to use
	 * @return String  
	 */
//	public static String applyProperties(String s, Hashtable properties)
	public static String applyProperties(String s, TreeMap properties)
	{
		java.util.Iterator e = properties.keySet().iterator();
//		Enumeration e = properties.keys();
//		while (s.indexOf('$')>=0 && e.hasMoreElements())
		while (s.indexOf('$')>=0 && e.hasNext())
		{
			String key = e.next().toString();
//			String key = e.nextElement().toString();
			String val = properties.get(key).toString();
			s = s.replaceAll("\\$\\("+key+"\\)", val);
			s = s.replaceAll("\\$\\{"+key+"\\}", val);
		}		
		return s;
	}

	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName()
	{
		return name;
	}

}
