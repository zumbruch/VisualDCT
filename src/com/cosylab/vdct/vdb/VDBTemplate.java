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

import java.awt.Component;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.cosylab.vdct.Console;
import com.cosylab.vdct.events.CommandManager;
import com.cosylab.vdct.events.commands.GetGUIInterface;
import com.cosylab.vdct.graphics.objects.Group;
import com.cosylab.vdct.inspector.Inspectable;
import com.cosylab.vdct.inspector.InspectableProperty;
import com.cosylab.vdct.inspector.InspectorManager;

/**
 * Data object representing EPICS DB template.
 * All data is obtained from DB (.template) file.
 * <code>Group</code> contains template structure.
 * The name of the group is "<name>.template". 
 * @author Matej
 */

public class VDBTemplate implements Inspectable, Commentable
{
	
	protected String id = null;
	protected String fileName = null;
	protected String description = null;
	protected Hashtable inputs = null;
	protected Hashtable outputs = null;
	protected Hashtable inputComments = null;
	protected Hashtable outputComments = null;
	protected Group group = null;
	
	private String comment = null;
	private CommentProperty commentProperty = null;

	private static ImageIcon icon = null;

	private static GUISeparator templateSeparator = null;
	private static GUISeparator inputsSeparator = null;
	private static GUISeparator outputsSeparator = null;

	private String tempDescription = null;

	class DescriptionProperty implements InspectableProperty
	{
		private static final String defaultDescription = "";
		private static final String name = "Description";
		private static final String helpString = "Template description";
		
		/**
		 * @see com.cosylab.vdct.inspector.InspectableProperty#allowsOtherValues()
		 */
		public boolean allowsOtherValues()
		{
			return false;
		}

		/**
		 * @see com.cosylab.vdct.inspector.InspectableProperty#getEditPattern()
		 */
		public Pattern getEditPattern()
		{
			return null;
		}

		/**
		 * @see com.cosylab.vdct.inspector.InspectableProperty#getHelp()
		 */
		public String getHelp()
		{
			return helpString;
		}

		/**
		 * @see com.cosylab.vdct.inspector.InspectableProperty#getInitValue()
		 */
		public String getInitValue()
		{
			return null;
		}

		/**
		 * @see com.cosylab.vdct.inspector.InspectableProperty#getName()
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * @see com.cosylab.vdct.inspector.InspectableProperty#getSelectableValues()
		 */
		public String[] getSelectableValues()
		{
			return null;
		}

		/**
		 * @see com.cosylab.vdct.inspector.InspectableProperty#getToolTipText()
		 */
		public String getToolTipText()
		{
			return null;
		}

		/**
		 * @see com.cosylab.vdct.inspector.InspectableProperty#getValue()
		 */
		public String getValue()
		{
			String val = getRealDescription();
			if (val==null)
				return defaultDescription;
			else
				return val;
		}

		/**
		 * @see com.cosylab.vdct.inspector.InspectableProperty#getVisibility()
		 */
		public int getVisibility()
		{
			return InspectableProperty.UNDEFINED_VISIBILITY;
		}

		/**
		 * @see com.cosylab.vdct.inspector.InspectableProperty#isEditable()
		 */
		public boolean isEditable()
		{
			return true;
		}

		/**
		 * @see com.cosylab.vdct.inspector.InspectableProperty#isSepatator()
		 */
		public boolean isSepatator()
		{
			return false;
		}

		/**
		 * @see com.cosylab.vdct.inspector.InspectableProperty#isValid()
		 */
		public boolean isValid()
		{
			return true;
		}

		/**
		 * @see com.cosylab.vdct.inspector.InspectableProperty#popupEvent(Component, int, int)
		 */
		public void popupEvent(Component component, int x, int y)
		{
		}

		/**
		 * @see com.cosylab.vdct.inspector.InspectableProperty#setValue(String)
		 */
		public void setValue(String value)
		{
			setDescription(value);
		}

		/**
		 * @see com.cosylab.vdct.inspector.InspectableProperty#toString(String)
		 */
		public String toString()
		{
			return name;
		}
	}

	/**
	 * Constructor for VDBTemplate.
	 */
	public VDBTemplate(String id, String fileName)
	{
		this.id = id;
		this.fileName = fileName;
		updateDescription();			
		
		Console.getInstance().println("Template '"+id+"' loaded, file '"+fileName+"'.");
		
	}

	/**
	 * Returns the description.
	 * @return String
	 */
	public String getDescription()
	{
		if (tempDescription!=null)
		{
			return tempDescription;
		}
		else
			return description;
	}

	/**
	 * Returns the description.
	 * @return String
	 */
	public String getRealDescription()
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
		boolean update = false;
		if (this.description==null || description==null ||		// one null
		    !this.description.equals(description))					// not equals equals	
		{
			// both null
			if (this.description!=description)
			{
				update=true;
				this.description = description;
			}
		}

		updateDescription();			

		if (update)
		{
			InspectorManager.getInstance().updateObject(this);
			/// !!!
			com.cosylab.vdct.VisualDCT.getInstance().updateLoadLabel();			
		    GetGUIInterface cmd = (GetGUIInterface)CommandManager.getInstance().getCommand("GetGUIMenuInterface");
		    cmd.getGUIMenuInterface().updateGroupLabel();
		}
	}


	/**
	 * Sets the description.
	 * @param description The description to set
	 */
	private void updateDescription()
	{
		if (this.description==null || this.description.length()==0)
		{
			// remove extension
			int pos = id.lastIndexOf('.');
			if (pos>0)
				tempDescription = id.substring(0, pos);
			else
				tempDescription = id;
		}
		else
			tempDescription = null;
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

	/**
	 * @see com.cosylab.vdct.inspector.Inspectable#getCommentProperty()
	 */
	public InspectableProperty getCommentProperty()
	{
		if (commentProperty==null)
			commentProperty = new CommentProperty(this);
		return commentProperty;
	}

	/**
	 * @see com.cosylab.vdct.inspector.Inspectable#getIcon()
	 */
	public Icon getIcon()
	{
		if (icon==null)
			icon = new javax.swing.ImageIcon(getClass().getResource("/images/template.gif"));
		return icon;
	}

	/**
	 * @see com.cosylab.vdct.inspector.Inspectable#getModeNames()
	 */
	public ArrayList getModeNames()
	{
		return null;
	}

	/**
	 * @see com.cosylab.vdct.inspector.Inspectable#getName()
	 */
	public String getName()
	{
		return id;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (3.2.2001 13:07:04)
	 * @return com.cosylab.vdct.vdb.GUISeparator
	 */
	public static com.cosylab.vdct.vdb.GUISeparator getTemplateSeparator() {
		if (templateSeparator==null) templateSeparator = new GUISeparator("Template");
		return templateSeparator;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (3.2.2001 13:07:04)
	 * @return com.cosylab.vdct.vdb.GUISeparator
	 */
	public static com.cosylab.vdct.vdb.GUISeparator getInputsSeparator() {
		if (inputsSeparator==null) inputsSeparator = new GUISeparator("Inputs");
		return inputsSeparator;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (3.2.2001 13:07:04)
	 * @return com.cosylab.vdct.vdb.GUISeparator
	 */
	public static com.cosylab.vdct.vdb.GUISeparator getOutputsSeparator() {
		if (outputsSeparator==null) outputsSeparator = new GUISeparator("Outputs");
		return outputsSeparator;
	}


	/**
	 * @see com.cosylab.vdct.inspector.Inspectable#getProperties(int)
	 */
	public InspectableProperty[] getProperties(int mode)
	{
		Vector items = new Vector();

		items.addElement(getTemplateSeparator());
		items.addElement(new NameValueInfoProperty("Class", id));
		items.addElement(new NameValueInfoProperty("FileName", fileName));
		items.addElement(new DescriptionProperty());

		items.addElement(getInputsSeparator());
		Enumeration e = getInputs().keys();
		while (e.hasMoreElements())
		{
			String key = e.nextElement().toString();
			VDBFieldData data = (VDBFieldData)getInputs().get(key);
			items.addElement(new NameValueInfoProperty(key, data.getFullName()));
		}
		
		items.addElement(getOutputsSeparator());
		e = getOutputs().keys();
		while (e.hasMoreElements())
		{
			String key = e.nextElement().toString();
			VDBFieldData data = (VDBFieldData)getOutputs().get(key);
			items.addElement(new NameValueInfoProperty(key, data.getFullName()));
		}
		
		InspectableProperty[] properties = new InspectableProperty[items.size()];
		items.copyInto(properties);
		return properties;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10.1.2001 14:49:50)
	 */
	public String toString() {
		return "Template: " + description + " [" + id + "]";
	}

	/**
	 * Returns the comment.
	 * @return String
	 */
	public String getComment()
	{
		return comment;
	}

	/**
	 * Sets the comment.
	 * @param comment The comment to set
	 */
	public void setComment(String comment)
	{
		this.comment = comment;
	}

}
