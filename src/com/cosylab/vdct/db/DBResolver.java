package com.cosylab.vdct.db;

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

import java.io.*;
import java.util.*;
import com.cosylab.vdct.Console;
import com.cosylab.vdct.DataProvider;
import com.cosylab.vdct.util.StringUtils;

/**
 * This type was created in VisualAge.
 */
public class DBResolver {
	private static final String errorString = "Invalid VisualDCT visual data..."; 

	private static final String nullString = "";

	// DB definitions
	public static final String FIELD		= "field";
	public static final String RECORD	= "record";
	public static final String GRECORD	= "grecord";
	public static final String INCLUDE	= "include";

	public static final String PATH  	 = "path";
	public static final String ADDPATH  	 = "addpath";

	// new R3.15
	public static final String TEMPLATE	= "template";
	public static final String PORT		= "port";

	public static final String EXPAND	= "expand";
	public static final String MACRO		= "macro";


	// internal
	private static final String ENDSTR = "}";
	private static final String SPACE = " ";
	private static final String NL = "\n";
	
	// skip commands
	
	// skip one line
 	// #! SKIP					
	// skip n lines
 	// #! SKIP(number of lines)
	public static final String VDCTSKIP = "SKIP";

	// visual data
	// used format #! Record(recordname, xpos, ypos, color, rotated, "description")
	// used format #! Group(groupname, xpos, ypos, color, "description")
	// used format #! Field(fieldname, color, rotated, "description")
	// used format #! Visibility(fieldname, visibility)
	// used format #! Link(fieldname, inLinkID)
	// used format #! Connector(id, outLinkID, xpos, ypos, color, "description")
	//	 eg.       #! Record(ts:fanOut0, 124, 432, 324568, 0, "fanOut record")
	public static final String VDCTRECORD = "Record";
	public static final String VDCTGROUP = "Group";
	public static final String VDCTFIELD = "Field";
	public static final String VDCTLINK = "Link";
	public static final String VDCTVISIBILITY = "Visibility";
	public static final String VDCTCONNECTOR = "Connector";

	// used format #! Line(name, xpos, ypos, xpos2, ypos2, dashed, startArrow, endArrow, color)
	// used format #! Box(name, xpos, ypos, xpos2, ypos2, dashed, color)
	// used format #! TextBox(name, xpos, ypos, xpos2, ypos2, border, fontFamilyName, fontSize, fontStyle, color, "description")
	public static final String VDCTLINE = "Line";
	public static final String VDCTBOX = "Box";
	public static final String VDCTTEXTBOX = "TextBox";

	// incoded DBDs
 	// used format:
 	// #! DBDSTART
 	// #! DBD("DBD filename")
	// ...
 	// #! DBD("DBD filename")
 	// #! DBDEND
	private static final String DBD_START_STR	= "DBDSTART";
	private static final String DBD_ENTRY_STR	= "DBD";
	private static final String DBD_END_STR		= "DBDEND";

	public static final String DBD_START	= "#! "+DBD_START_STR+"\n";
	public static final String DBD_ENTRY	= "#! "+DBD_ENTRY_STR+"(\"";
	public static final String DBD_END	= "#! "+DBD_END_STR+"\n";

	// template definition
	// used format:
 	// #! TemplateInput("alias", "field", "description")
 	// #! TemplateOutput("alias", "field", "description")
	
	// resets all template data && generates template name
	//public static final String TEMPLATE_INPUT = "TemplateInput";
	//public static final String TEMPLATE_OUTPUT = "TemplateOutput";
	
	// template 'instatiation'
	// used format:
 	// #! TemplateInstance("template instance id", x, y, color, "desc")
	public static final String TEMPLATE_INSTANCE = "TemplateInstance";

/**
 * This method was created in VisualAge.
 * @return java.io.StreamTokenizer
 * @param fileName java.lang.String
 */
public static StreamTokenizer getStreamTokenizer(String fileName) {

	FileInputStream fi = null;
	StreamTokenizer tokenizer = null;
	
	try	{
		fi = new FileInputStream(fileName);
		tokenizer = new StreamTokenizer(new BufferedReader(new InputStreamReader(fi)));
		initializeTokenizer(tokenizer);
	} catch (IOException e) {
		Console.getInstance().println("\no) Error occured while opening file '"+fileName+"'");
		Console.getInstance().println(e);
	}

	return tokenizer;
}
/**
 * This method was created in VisualAge.
 * @param st java.io.StreamTokenizer
 */
public static void initializeTokenizer(StreamTokenizer tokenizer) {
	tokenizer.resetSyntax();
	tokenizer.whitespaceChars(0, 32);
	tokenizer.wordChars(33, 255);			// reset
	tokenizer.eolIsSignificant(true);
	tokenizer.parseNumbers();
	tokenizer.quoteChar(DBConstants.quoteChar);
	tokenizer.whitespaceChars(',', ',');
	tokenizer.whitespaceChars('{', '{');
	tokenizer.whitespaceChars('(', '(');
	tokenizer.whitespaceChars(')',')');
}



private static String loadTemplate(DBData data, String templateFile, String referencedFromFile, DBPathSpecification paths) throws Exception
{

	File file = paths.search4File(templateFile);
	String templateToResolve = file.getAbsolutePath();

	// check if file already loaded
	if(DataProvider.getInstance().getLoadedDBs().contains(templateToResolve))
	{
		Console.getInstance().println("Template \""+templateFile+"\" already loaded...");
		
		// extract id (not a prefect solution)
		// id is name
		return file.getName();
	}

	// cyclic reference check !!!
	{
	}
	
	Console.getInstance().println("Loading template \""+templateFile+"\"...");

	DBData templateData = resolveDB(templateToResolve);
	templateData.getTemplateData().setData(templateData);
	data.addTemplate(templateData.getTemplateData());
	
	// add to loaded list
	DataProvider.getInstance().getLoadedDBs().addElement(templateData.getTemplateData().getFileName());

	Console.getInstance().println("Template \""+templateFile+"\" loaded.");

	return templateData.getTemplateData().getId();
}



/**
 * VisualDCT layout data is also processed here
 * @param rootData com.cosylab.vdct.db.DBData
 * @param tokenizer java.io.StreamTokenizer
 */
public static String processComment(DBData data, StreamTokenizer tokenizer, String fileName) throws Exception {
  
 if ((data==null) || !tokenizer.sval.equals(DBConstants.layoutDataString)) {	// comment
	 String comment = tokenizer.sval;
	 
	// initialize tokenizer for comments
	tokenizer.resetSyntax();
	tokenizer.whitespaceChars(0, 31);
	tokenizer.wordChars(32, 255);		
	tokenizer.wordChars('\t', '\t');
	tokenizer.eolIsSignificant(true);

	while ((tokenizer.nextToken() != tokenizer.TT_EOL) &&						// read till EOL
		    (tokenizer.ttype != tokenizer.TT_EOF))
	 	if (tokenizer.ttype == tokenizer.TT_NUMBER) {
		 	//if (!comment.equals(nullString)) comment+=SPACE;
	 		comment=comment+tokenizer.nval;
	 	}
		else {
		 	//if (!comment.equals(nullString)) comment+=SPACE;
			comment=comment+tokenizer.sval;
		}

	 // reinitialzie it back
	 initializeTokenizer(tokenizer);

	 return comment+NL;

 }
 else {																		// graphics layout data
	
	 DBRecordData rd;
	 DBFieldData fd;
 	 String str, str2, desc; int t, tx, tx2, ty, ty2, t2, t3;
 	 boolean r1, r2;

 	 while ((tokenizer.nextToken() != tokenizer.TT_EOL) &&
		    (tokenizer.ttype != tokenizer.TT_EOF)) 
		if (tokenizer.ttype == tokenizer.TT_WORD) 

				if (tokenizer.sval.equalsIgnoreCase(VDCTRECORD)) {

					// read record_name
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD)||
						(tokenizer.ttype == DBConstants.quoteChar)) str=tokenizer.sval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					rd = (DBRecordData)(data.getRecords().get(str));
					if (rd!=null) {
						// read x pos
						tokenizer.nextToken();
						if (tokenizer.ttype == tokenizer.TT_NUMBER) rd.setX((int)tokenizer.nval);
						else throw (new DBGParseException(errorString, tokenizer, fileName));
					
						// read y pos
						tokenizer.nextToken();
						if (tokenizer.ttype == tokenizer.TT_NUMBER) rd.setY((int)tokenizer.nval);
						else throw (new DBGParseException(errorString, tokenizer, fileName));

						// read color
						tokenizer.nextToken();
						if (tokenizer.ttype == tokenizer.TT_NUMBER) rd.setColor(StringUtils.int2color((int)tokenizer.nval));
						else throw (new DBGParseException(errorString, tokenizer, fileName));

						// read rotation
						tokenizer.nextToken();
						if (tokenizer.ttype == tokenizer.TT_NUMBER) rd.setRotated(((int)tokenizer.nval)!=0);
						else throw (new DBGParseException(errorString, tokenizer, fileName));
						
						// read description
						tokenizer.nextToken();
						if ((tokenizer.ttype == tokenizer.TT_WORD)||
							(tokenizer.ttype == DBConstants.quoteChar)) rd.setDescription(tokenizer.sval);
						else throw (new DBGParseException(errorString, tokenizer, fileName));

					}
				}

				else if (tokenizer.sval.equalsIgnoreCase(VDCTFIELD)) {

					// read name
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD)||
						(tokenizer.ttype == DBConstants.quoteChar)) str=tokenizer.sval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					int pos = str.lastIndexOf(com.cosylab.vdct.Constants.FIELD_SEPARATOR);
					str2 = str.substring(pos+1);
					str = str.substring(0, pos);

					rd = (DBRecordData)data.getRecords().get(str);
					if (rd!=null)
					{
						fd=(DBFieldData)rd.getFields().get(str2);
						if (fd==null)
						{
							fd = new DBFieldData(str2, nullString);
							rd.addField(fd);
						}
							
						// read color
						tokenizer.nextToken();
						if (tokenizer.ttype == tokenizer.TT_NUMBER) fd.setColor(StringUtils.int2color((int)tokenizer.nval));
						else throw (new DBGParseException(errorString, tokenizer, fileName));

						// read rotation
						tokenizer.nextToken();
						if (tokenizer.ttype == tokenizer.TT_NUMBER) fd.setRotated(((int)tokenizer.nval)!=0);
						else throw (new DBGParseException(errorString, tokenizer, fileName));
						
						// read description
						tokenizer.nextToken();
						if ((tokenizer.ttype == tokenizer.TT_WORD)||
							(tokenizer.ttype == DBConstants.quoteChar)) fd.setDescription(tokenizer.sval);
						else throw (new DBGParseException(errorString, tokenizer, fileName));
							
					}
				}
				
				else if (tokenizer.sval.equalsIgnoreCase(VDCTVISIBILITY)) {

					// read name
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD)||
						(tokenizer.ttype == DBConstants.quoteChar)) str=tokenizer.sval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					int pos = str.lastIndexOf(com.cosylab.vdct.Constants.FIELD_SEPARATOR);
					str2 = str.substring(pos+1);
					str = str.substring(0, pos);

					rd = (DBRecordData)data.getRecords().get(str);
					if (rd!=null)
					{
						fd=(DBFieldData)rd.getFields().get(str2);
						if (fd==null)
						{
							fd = new DBFieldData(str2, nullString);
							rd.addField(fd);
						}
							
						tokenizer.nextToken();
						if (tokenizer.ttype == tokenizer.TT_NUMBER) fd.setVisibility((int)tokenizer.nval);
						else throw (new DBGParseException(errorString, tokenizer, fileName));
					}
				}

				else if (tokenizer.sval.equalsIgnoreCase(VDCTCONNECTOR)) {

					// read connector id
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD)||
						(tokenizer.ttype == DBConstants.quoteChar)) str=tokenizer.sval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read target id
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD)||
						(tokenizer.ttype == DBConstants.quoteChar)) str2=tokenizer.sval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read x pos
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) tx=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));
					
					// read y pos
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) ty=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read color
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) t=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read description
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD)||
						(tokenizer.ttype == DBConstants.quoteChar)) desc=tokenizer.sval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					data.addConnector(new DBConnectorData(str, str2, tx, ty, StringUtils.int2color(t), desc));

				}


				else if (tokenizer.sval.equalsIgnoreCase(VDCTLINK)) {
					
					// read name
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD)||
						(tokenizer.ttype == DBConstants.quoteChar)) str=tokenizer.sval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));
				
					// read target
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD)||
						(tokenizer.ttype == DBConstants.quoteChar)) str2=tokenizer.sval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					data.addLink(new DBLinkData(str, str2));
						
				}
				
				else if (tokenizer.sval.equalsIgnoreCase(VDCTGROUP)) {

					// read group_name
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD)||
						(tokenizer.ttype == DBConstants.quoteChar)) str=tokenizer.sval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read x pos
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) tx=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));
					
					// read y pos
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) ty=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read color
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) t=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read description
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD)||
						(tokenizer.ttype == DBConstants.quoteChar)) desc=tokenizer.sval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					data.addGroup(new DBGroupData(str, tx, ty, StringUtils.int2color(t), desc));

				}
				else if (tokenizer.sval.equalsIgnoreCase(TEMPLATE_INSTANCE)) {

					// read template instance id
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD)||
						(tokenizer.ttype == DBConstants.quoteChar)) str=tokenizer.sval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));
					
					// read x pos
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) tx=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));
					
					// read y pos
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) ty=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read color
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) t=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read description
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD)||
						(tokenizer.ttype == DBConstants.quoteChar)) desc=tokenizer.sval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					DBTemplateInstance ti = (DBTemplateInstance)data.getTemplateInstances().get(str);
					if (ti!=null)
					{
						ti.setX(tx); ti.setY(ty); ti.setColor(StringUtils.int2color(t));
						ti.setDescription(desc);
					}
				}
/*				else if (tokenizer.sval.equalsIgnoreCase(TEMPLATE_INPUT)) {

					// read alias
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD)||
						(tokenizer.ttype == DBConstants.quoteChar)) str=tokenizer.sval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));
					
					// read field name
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD)||
						(tokenizer.ttype == DBConstants.quoteChar)) str2=tokenizer.sval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read description
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD)||
						(tokenizer.ttype == DBConstants.quoteChar)) desc=tokenizer.sval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// set data
					DBTemplate templateData = data.getTemplateData();
					if (!templateData.getInputs().containsKey(str))
					{
						templateData.getInputs().put(str, str2);
						templateData.getInputComments().put(str, desc);
					}
				}
				else if (tokenizer.sval.equalsIgnoreCase(TEMPLATE_OUTPUT)) {

					// read alias
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD)||
						(tokenizer.ttype == DBConstants.quoteChar)) str=tokenizer.sval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));
					
					// read field name
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD)||
						(tokenizer.ttype == DBConstants.quoteChar)) str2=tokenizer.sval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read description
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD)||
						(tokenizer.ttype == DBConstants.quoteChar)) desc=tokenizer.sval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// set data
					DBTemplate templateData = data.getTemplateData();
					if (!templateData.getOutputs().containsKey(str))
					{
						templateData.getOutputs().put(str, str2);
						templateData.getOutputComments().put(str, desc);
					}
				}
*/
				else if (tokenizer.sval.equalsIgnoreCase(VDCTLINE)) {
					// read template name
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD)||
						(tokenizer.ttype == DBConstants.quoteChar)) str=tokenizer.sval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));
					
					// read x pos
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) tx=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));
					
					// read y pos
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) ty=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read x2 pos
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) tx2=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));
					
					// read y2 pos
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) ty2=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read dashed
					boolean dashed = false;
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) dashed=((int)tokenizer.nval)!=0;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read startArrow
					boolean startArrow = false;
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) startArrow=((int)tokenizer.nval)!=0;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read endArrow
					boolean endArrow = false;
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) endArrow=((int)tokenizer.nval)!=0;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read color
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) t=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					data.addLine(new DBLine(str, tx, ty, tx2, ty2, dashed, startArrow, endArrow, StringUtils.int2color(t)));
				}
				else if (tokenizer.sval.equalsIgnoreCase(VDCTBOX)) {
					// read template name
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD)||
						(tokenizer.ttype == DBConstants.quoteChar)) str=tokenizer.sval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));
					
					// read x pos
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) tx=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));
					
					// read y pos
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) ty=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read x2 pos
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) tx2=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));
					
					// read y2 pos
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) ty2=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read dashed
					boolean dashed = false;
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) dashed=((int)tokenizer.nval)!=0;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read color
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) t=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					data.addBox(new DBBox(str, tx, ty, tx2, ty2, dashed, StringUtils.int2color(t)));
				}
				else if (tokenizer.sval.equalsIgnoreCase(VDCTTEXTBOX)) {
					// read template name
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD)||
						(tokenizer.ttype == DBConstants.quoteChar)) str=tokenizer.sval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));
					
					// read x pos
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) tx=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));
					
					// read y pos
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) ty=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read x2 pos
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) tx2=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));
					
					// read y2 pos
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) ty2=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read border
					boolean border = false;
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) border=((int)tokenizer.nval)!=0;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read fontName
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD)||
						(tokenizer.ttype == DBConstants.quoteChar)) str2=tokenizer.sval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read fontSize
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) t2=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read fontStyle
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) t3=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read color
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) t=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read description
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD)||
						(tokenizer.ttype == DBConstants.quoteChar)) desc=tokenizer.sval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					data.addTextBox(new DBTextBox(str, tx, ty, tx2, ty2, border, str2, t2, t3, StringUtils.int2color(t), desc));
				}
				else if (tokenizer.sval.equalsIgnoreCase(VDCTSKIP)) {

					// read optional n of lines
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) tx=(int)tokenizer.nval;
					else
					{
						tx = 1; 
						tokenizer.pushBack();
					}	
					
					// skip tx lines (including the rest of this one)
					skipLines(tx+1, tokenizer, fileName);
				}

				/***************************************************/
				/************* Version v1.0 support ****************/
				/***************************************************/
				
				/****************** layout data ********************/

				else if (tokenizer.sval.equalsIgnoreCase("VDCTRecordPos")) {

					// read record_name
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD)||
						(tokenizer.ttype == DBConstants.quoteChar)) str=tokenizer.sval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					rd = (DBRecordData)(data.records.get(str));
					if (rd!=null) {
						// read x pos
						tokenizer.nextToken();
						if (tokenizer.ttype == tokenizer.TT_NUMBER) rd.setX((int)tokenizer.nval);
						else throw (new DBGParseException(errorString, tokenizer, fileName));
					
						// read y pos
						tokenizer.nextToken();
						if (tokenizer.ttype == tokenizer.TT_NUMBER) rd.setY((int)tokenizer.nval);
						else throw (new DBGParseException(errorString, tokenizer, fileName));

						// read rotation
						tokenizer.nextToken();
						if (tokenizer.ttype == tokenizer.TT_NUMBER) rd.setRotated(((int)tokenizer.nval)!=0);
						else throw (new DBGParseException(errorString, tokenizer, fileName));

						rd.setColor(java.awt.Color.black);
					}
				}

				else if (tokenizer.sval.equalsIgnoreCase("VDCTGroupPos")) {

					// read group_name
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD)||
						(tokenizer.ttype == DBConstants.quoteChar)) str=tokenizer.sval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read x pos
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) tx=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));
					
					// read y pos
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) ty=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					data.addGroup(new DBGroupData(str, tx, ty, java.awt.Color.black, nullString));

				}
				else if (tokenizer.sval.equalsIgnoreCase("VDCTLinkData")) {

					// read linkID
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD)||
						(tokenizer.ttype == DBConstants.quoteChar)) str=tokenizer.sval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read desc
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD) ||
						(tokenizer.ttype == DBConstants.quoteChar)) desc=tokenizer.sval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read x pos
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) tx=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));
					
					// read x2 pos
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) tx2=(int)tokenizer.nval;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read obj1rotated
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) r1=((int)tokenizer.nval)!=0;
					else throw (new DBGParseException(errorString, tokenizer, fileName));

					// read obj2rotated
					tokenizer.nextToken();
					if (tokenizer.ttype == tokenizer.TT_NUMBER) r2=((int)tokenizer.nval)!=0;
					else throw (new DBGParseException(errorString, tokenizer, fileName));
/*
					// transformation to v2 visual data
					
					int pos = str.lastIndexOf('/');
					if (pos<0) continue;	// invalid, skip
					StringBuffer fieldName = new StringBuffer(str);
					fieldName.setCharAt(pos, '.');

					pos = fieldName.toString().lastIndexOf(com.cosylab.vdct.Constants.FIELD_SEPARATOR);
					String field = fieldName.substring(pos+1);
					String record = fieldName.substring(0, pos);

					rd = (DBRecordData)data.getRecords().get(record);
					if (rd==null) continue;

					fd = (DBFieldData)rd.getFields().get(field);
					if (fd==null) continue;

					String target = com.cosylab.vdct.vdb.LinkProperties.getTargetFromString(fd.getValue());
					if (target==null) continue;


					if (!com.cosylab.vdct.graphics.objects.Group.substractParentName(fieldName.toString()).equals(com.cosylab.vdct.graphics.objects.Group.substractParentName(target)))
					{
						// intergroup link, no connector needed
						data.addLink(new DBLinkData(fieldName.toString(), target));
					}
					else
					{
						String connectorName = com.cosylab.vdct.graphics.objects.Group.substractObjectName(str);
						data.addLink(new DBLinkData(fieldName.toString(), connectorName));

						int y = rd.getX()+com.cosylab.vdct.Constants.RECORD_HEIGHT;
						pos = target.lastIndexOf(com.cosylab.vdct.Constants.FIELD_SEPARATOR);
						record = target.substring(0, pos);
						rd = (DBRecordData)data.getRecords().get(record);
						if (rd!=null)
							y = (y+rd.getX()+com.cosylab.vdct.Constants.RECORD_HEIGHT)/2;
						
						data.addConnector(new DBConnectorData(connectorName, target, tx, y, java.awt.Color.black, nullString));
					}
					fd.setRotated(r1);
*/
				}

		return nullString;
  }
}

/**
 * VisualDCT layout data is also processed here
 * @param tokenizer java.io.StreamTokenizer
 */
public static void skipLines(int linesToSkip, StreamTokenizer tokenizer, String fileName) throws Exception {
  
	int lines = 0;
	while (lines < linesToSkip)
	{
		tokenizer.nextToken();
		
		// end of file
		if (tokenizer.ttype == tokenizer.TT_EOF)		
			return;
			
		else if (tokenizer.ttype == tokenizer.TT_EOL)
			lines++;
	}
}

/**
 * This method was created in VisualAge.
 * @param rootData com.cosylab.vdct.db.DBData
 * @param tokenizer java.io.StreamTokenizer
 */
public static void processDB(DBData data, StreamTokenizer tokenizer, String fileName, DBPathSpecification paths) throws Exception
{
	
	String comment = nullString;
	String str, str2;
	String include_filename;
	StreamTokenizer inctokenizer = null;

	if (data!=null)
	
	try {
		
		while (tokenizer.nextToken() != tokenizer.TT_EOF)
  		  if (tokenizer.ttype == tokenizer.TT_WORD)
			if (tokenizer.sval.startsWith(DBConstants.commentString))
				comment+=processComment(data, tokenizer, fileName);
			else

				/****************** records ********************/

				if (tokenizer.sval.equalsIgnoreCase(RECORD) ||
					tokenizer.sval.equalsIgnoreCase(GRECORD)) {
			
					DBRecordData rd = new DBRecordData();

					// read record_type
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD) ||
						(tokenizer.ttype == DBConstants.quoteChar)) rd.setRecord_type(tokenizer.sval);
					else throw (new DBParseException("Invalid record type...", tokenizer, fileName));

					// read record_name
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD) ||
						(tokenizer.ttype == DBConstants.quoteChar)) rd.setName(tokenizer.sval);
					else throw (new DBParseException("Invalid record name...", tokenizer, fileName));

					rd.setComment(comment);	comment = nullString;
					
					processFields(rd, tokenizer, fileName);
					data.addRecord(rd);
					
				}

				/****************** templates ********************/

				else if (tokenizer.sval.equalsIgnoreCase(TEMPLATE)) {

					// read optional description
					str = null;
					tokenizer.nextToken();
					if (tokenizer.ttype == DBConstants.quoteChar) str = tokenizer.sval;
					else
						tokenizer.pushBack();

					/*
					if (str==null)
						System.out.println("template()\n{");
					else
						System.out.println("template(\""+str+"\")\n{");
					*/

					// multiple tempaltes
					// only new ports are added
					DBTemplate templateData = data.getTemplateData();
					if (!templateData.isInitialized())
					{
						templateData.setInitialized(true);
						templateData.setComment(comment); comment = nullString;
						templateData.setDescription(str);

						DBTemplateEntry entry = new DBTemplateEntry();
						data.addEntry(entry);

					}
					else
					{
						// !!! TBD multiple templates support
						comment = nullString;
					}
					
					processPorts(templateData, tokenizer, fileName);
					
					//System.out.println("}");

				}

				/****************** expands ********************/

				else if (tokenizer.sval.equalsIgnoreCase(EXPAND)) {

					// read template file
					tokenizer.nextToken();
					if (tokenizer.ttype == DBConstants.quoteChar) str = tokenizer.sval;
					else throw (new DBParseException("Invalid expand file...", tokenizer, fileName));

					// read tempalte instance id
					tokenizer.nextToken();
					if ((tokenizer.ttype == tokenizer.TT_WORD) ||
						(tokenizer.ttype == DBConstants.quoteChar)) str2 = tokenizer.sval;
					else throw (new DBParseException("Invalid expand template instance name...", tokenizer, fileName));

					String loadedTemplateId = loadTemplate(data, str, fileName, paths);

					//System.out.println("expand(\""+str+"\", "+str2+")\n{");

					DBTemplateInstance ti = new DBTemplateInstance(str2, loadedTemplateId);
					ti.setComment(comment);	comment = nullString;

					processMacros(ti, tokenizer, fileName);
					
					data.addTemplateInstance(ti);

					//System.out.println("}");
				}

				/****************** includes ********************/
				
				else if (tokenizer.sval.equalsIgnoreCase(INCLUDE)) {

					// read incude_filename
					tokenizer.nextToken();
					if (tokenizer.ttype == DBConstants.quoteChar) include_filename=tokenizer.sval;
					else throw (new DBParseException("Invalid include filename...", tokenizer, fileName));

					DBDataEntry entry = new DBDataEntry(INCLUDE+" \""+include_filename+"\"");
					entry.setComment(comment);	comment = nullString;
					data.addEntry(entry);

					File file = paths.search4File(include_filename);
					//if (file!=null)
					//{
						inctokenizer = getStreamTokenizer(file.getAbsolutePath());
						if (inctokenizer!=null) processDB(data, inctokenizer, include_filename, new DBPathSpecification(file.getParentFile().getAbsolutePath()));
					//}
				}
			
				/****************** path ********************/

				 else if (tokenizer.sval.equalsIgnoreCase(PATH))
  				{
					// read paths
					tokenizer.nextToken();
					if (tokenizer.ttype == DBConstants.quoteChar) str=tokenizer.sval;
					else throw (new DBParseException("Invalid path...", tokenizer, fileName));

					DBDataEntry entry = new DBDataEntry(PATH+" \""+str+"\"");
					entry.setComment(comment);	comment = nullString;
					data.addEntry(entry);

					paths.setPath(str);
					//Console.getInstance().println("Warning: 'path' command is not yet supported...");
  				}
				
				/****************** addpath ********************/

				 else if (tokenizer.sval.equalsIgnoreCase(ADDPATH))
  				{
					// read add paths
					tokenizer.nextToken();
					if (tokenizer.ttype == DBConstants.quoteChar) str=tokenizer.sval;
					else throw (new DBParseException("Invalid addpath...", tokenizer, fileName));

					DBDataEntry entry = new DBDataEntry(ADDPATH+" \""+str+"\"");
					entry.setComment(comment);	comment = nullString;
					data.addEntry(entry);

					paths.addAddPath(str);
					//Console.getInstance().println("Warning: 'addpath' command is not yet supported...");
  				}

	} catch (Exception e) {
		Console.getInstance().println("\n"+e);
		throw e;
	}	
	
}

/**
 * This method was created in VisualAge.
 * @param rd com.cosylab.vdct.db.DBRecordData
 * @param tokenizer java.io.StreamTokenizer
 * @exception java.lang.Exception The exception description.
 */
public static void processMacros(DBTemplateInstance templateInstance, StreamTokenizer tokenizer, String fileName) throws Exception {

	String name;
	String value;
	String include_filename;
	StreamTokenizer inctokenizer = null;
	
	if (templateInstance!=null)
	
	/********************** macros area *************************/
		
	while (tokenizer.nextToken() != tokenizer.TT_EOF) 
		if (tokenizer.ttype == tokenizer.TT_WORD) 
			if (tokenizer.sval.equals(ENDSTR)) break;
			else if (tokenizer.sval.startsWith(DBConstants.commentString)) 
				processComment(null, tokenizer, fileName);				// !!! no comments are preserved in macro part
			else if (tokenizer.sval.equalsIgnoreCase(MACRO)) {

				// read name
				tokenizer.nextToken();
				if ((tokenizer.ttype == tokenizer.TT_WORD) ||
					(tokenizer.ttype == DBConstants.quoteChar)) name=tokenizer.sval;
				else throw (new DBParseException("Invalid macro name...", tokenizer, fileName));
					
				// read value
				tokenizer.nextToken();
				if (tokenizer.ttype == DBConstants.quoteChar) value=tokenizer.sval;
				else throw (new DBParseException("Invalid macro value...", tokenizer, fileName));

				//System.out.println("\tmacro("+name+", \""+value+"\")");

				templateInstance.addProperty(name, value);
			}

			else if (tokenizer.sval.equalsIgnoreCase(INCLUDE)) {

				// read incude_filename
				tokenizer.nextToken();
				if (tokenizer.ttype == DBConstants.quoteChar) include_filename=tokenizer.sval;
				else throw (new DBParseException("Invalid include filename...", tokenizer, fileName));

				// if not absulute fileName, do not use relative path
				if (!(include_filename.charAt(0)=='/' || include_filename.charAt(0)=='\\' || (include_filename.length()>1 && include_filename.charAt(1)==':')))
					include_filename = com.cosylab.vdct.util.StringUtils.replaceFileName(fileName, include_filename);

				inctokenizer = getStreamTokenizer(include_filename);
				if (inctokenizer!=null) processMacros(templateInstance, inctokenizer, include_filename);
			}	
						
	/***********************************************************/

}

/**
 * This method was created in VisualAge.
 * @param rd com.cosylab.vdct.db.DBRecordData
 * @param tokenizer java.io.StreamTokenizer
 * @exception java.lang.Exception The exception description.
 */
public static void processPorts(DBTemplate template, StreamTokenizer tokenizer, String fileName) throws Exception {

	String name;
	String value;
	String description;
	String include_filename;
	String comment = nullString;
	StreamTokenizer inctokenizer = null;
	
	if (template!=null)
	
	/********************** ports area *************************/
		
	while (tokenizer.nextToken() != tokenizer.TT_EOF) 
		if (tokenizer.ttype == tokenizer.TT_WORD) 
			if (tokenizer.sval.equals(ENDSTR)) break;
			else if (tokenizer.sval.startsWith(DBConstants.commentString)) 
				comment+=processComment(null, tokenizer, fileName);
			else if (tokenizer.sval.equalsIgnoreCase(PORT)) {

				// read name
				tokenizer.nextToken();
				if ((tokenizer.ttype == tokenizer.TT_WORD) ||
					(tokenizer.ttype == DBConstants.quoteChar)) name=tokenizer.sval;
				else throw (new DBParseException("Invalid port name...", tokenizer, fileName));
					
				// read field
				tokenizer.nextToken();
				if ((tokenizer.ttype == tokenizer.TT_WORD) ||
					(tokenizer.ttype == DBConstants.quoteChar)) value=tokenizer.sval;
				else throw (new DBParseException("Invalid port value...", tokenizer, fileName));

				// read optional description
				description = null;
				tokenizer.nextToken();
				if (tokenizer.ttype == DBConstants.quoteChar) description=tokenizer.sval;
				else
					tokenizer.pushBack();

				//System.out.println("\tport("+name+", \""+value+"\", \""+description+"\")");

				DBPort port = new DBPort(name, value);
				port.setComment(comment); comment = nullString;
				port.setDescription(description);
				
				template.addPort(port);
			}

			else if (tokenizer.sval.equalsIgnoreCase(INCLUDE)) {

				// read incude_filename
				tokenizer.nextToken();
				if (tokenizer.ttype == DBConstants.quoteChar) include_filename=tokenizer.sval;
				else throw (new DBParseException("Invalid include filename...", tokenizer, fileName));

				// if not absulute fileName, do not use relative path
				if (!(include_filename.charAt(0)=='/' || include_filename.charAt(0)=='\\' || (include_filename.length()>1 && include_filename.charAt(1)==':')))
					include_filename = com.cosylab.vdct.util.StringUtils.replaceFileName(fileName, include_filename);

				inctokenizer = getStreamTokenizer(include_filename);
				if (inctokenizer!=null) processPorts(template, inctokenizer, include_filename);
			}	
						
	/***********************************************************/

}



/**
 * This method was created in VisualAge.
 * @param rd com.cosylab.vdct.db.DBRecordData
 * @param tokenizer java.io.StreamTokenizer
 * @exception java.lang.Exception The exception description.
 */
public static void processFields(DBRecordData rd, StreamTokenizer tokenizer, String fileName) throws Exception {

	String name;
	String value;
	String comment = nullString;
	String include_filename;
	StreamTokenizer inctokenizer = null;
	
	if (rd!=null)
	
	/********************** fields area *************************/
		
	while (tokenizer.nextToken() != tokenizer.TT_EOF) 
		if (tokenizer.ttype == tokenizer.TT_WORD) 
			if (tokenizer.sval.equals(ENDSTR)) break;
			else if (tokenizer.sval.startsWith(DBConstants.commentString)) 
				comment+=processComment(null, tokenizer, fileName);
			else if (tokenizer.sval.equalsIgnoreCase(FIELD)) {

				// read field_name
				tokenizer.nextToken();
				if ((tokenizer.ttype == tokenizer.TT_WORD) ||
					(tokenizer.ttype == DBConstants.quoteChar)) name=tokenizer.sval;
				else throw (new DBParseException("Invalid field name...", tokenizer, fileName));
					
				// read field_value
				tokenizer.nextToken();
				if (tokenizer.ttype == DBConstants.quoteChar) value=tokenizer.sval;
				else throw (new DBParseException("Invalid field value...", tokenizer, fileName));

				DBFieldData fd = new DBFieldData(name, value);
				fd.setComment(comment);	comment = nullString;
				rd.addField(fd);
			}

			else if (tokenizer.sval.equalsIgnoreCase(INCLUDE)) {

				// read incude_filename
				tokenizer.nextToken();
				if (tokenizer.ttype == DBConstants.quoteChar) include_filename=tokenizer.sval;
				else throw (new DBParseException("Invalid include filename...", tokenizer, fileName));

				// if not absulute fileName, do not use relative path
				if (!(include_filename.charAt(0)=='/' || include_filename.charAt(0)=='\\' || (include_filename.length()>1 && include_filename.charAt(1)==':')))
					include_filename = com.cosylab.vdct.util.StringUtils.replaceFileName(fileName, include_filename);

				inctokenizer = getStreamTokenizer(include_filename);
				if (inctokenizer!=null) processFields(rd, inctokenizer, include_filename);
			}	
						
	/***********************************************************/

}



/**
 * This method was created in VisualAge.
 * @return Vector
 * @param fileName java.lang.String
 */
public static String[] resolveIncodedDBDs(String fileName) throws IOException {
	
	StreamTokenizer tokenizer = getStreamTokenizer(fileName);
	if (tokenizer==null) return null;

	String[] dbds = null;
	Vector vec = null;
	
	while (tokenizer.nextToken() != tokenizer.TT_EOF)
		if (tokenizer.ttype == tokenizer.TT_WORD)
			if (tokenizer.sval.startsWith(DBConstants.layoutDataString))
			{
				
	 	 		while ((tokenizer.nextToken() != tokenizer.TT_EOL) &&
			    		(tokenizer.ttype != tokenizer.TT_EOF)) 
					if (tokenizer.ttype == tokenizer.TT_WORD) 
						if (tokenizer.sval.equalsIgnoreCase(DBD_START_STR))
							vec = new Vector();
						else if (tokenizer.sval.equalsIgnoreCase(DBD_ENTRY_STR))
						{
							// check for DBD_START_STR 
							if (vec==null)
							{
								vec = new Vector();
								Console.getInstance().println("Warning: error found in file '"+fileName+"', line "+tokenizer.lineno()+" near token '"+tokenizer.sval+"':\n\t'"+DBD_ENTRY_STR+"' before '"+DBD_END_STR+"'...");
							}

							// read DBD filename
							tokenizer.nextToken();
							if ((tokenizer.ttype == tokenizer.TT_WORD)||
								(tokenizer.ttype == DBConstants.quoteChar)) vec.addElement(tokenizer.sval);
							else
								Console.getInstance().println("Warning: error found in file '"+fileName+"', line "+tokenizer.lineno()+" near token '"+tokenizer.sval+"':\n\tinvalid '"+DBD_ENTRY_STR+"' entry. Quoted DBD filename expected...");
						}
						else if (tokenizer.sval.equalsIgnoreCase(DBD_END_STR))
							break;
			}
	
	if (vec!=null)
	{
		dbds = new String[vec.size()];
		vec.toArray(dbds);
	}	 
	return dbds;
}

/**
 * This method was created in VisualAge.
 * @return Vector
 * @param fileName java.lang.String
 */
public static DBData resolveDB(String fileName) {
	
	DBData data = null; 
	
	StreamTokenizer tokenizer = getStreamTokenizer(fileName);
	if (tokenizer!=null) 
	{
		try
		{
			// generate template id from fileName
			File file = new File(fileName);
			
			DBPathSpecification paths = new DBPathSpecification(file.getParentFile().getAbsolutePath());
			data = new DBData(file.getName(), file.getAbsolutePath());

			processDB(data, tokenizer, fileName, paths);
		}
		catch (Exception e)
		{
			data = null;
		}
		finally
		{
			System.gc();
		}
	}
	
	return data;
}

/**
 * This method was created in VisualAge.
 * @return Vector
 * @param fileName java.lang.String
 */
public static DBData resolveDBasURL(java.net.URL url) {
	
	DBData data = null;

	InputStream fi = null;
	StreamTokenizer tokenizer = null;	

	try {
		fi = url.openStream();
		tokenizer = new StreamTokenizer(new BufferedReader(new InputStreamReader(fi)));
		initializeTokenizer(tokenizer);
	} catch (Exception e) {
		Console.getInstance().println("\nError occured while opening URL '"+url.toString()+"'");
		Console.getInstance().println(e);
		return null;
	}

	if (tokenizer!=null) 
	{
		try
		{
			// generate template id from fileName
			//File file = new File(fileName);
			//data = new DBData(file.getName(), file.getAbsolutePath());

			// !!! TDB not supported yet 
			
			//processDB(data, tokenizer, url.toString(), null);
		}
		finally
		{
			System.gc();
		}
	}
	
	return data;
}

}
