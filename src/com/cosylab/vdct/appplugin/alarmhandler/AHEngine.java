/*
 * Copyright (c) 2004 by Cosylab d.o.o.
 *
 * The full license specifying the redistribution, modification, usage and other
 * rights and obligations is included with the distribution of this project in
 * the file license.html. If the license is not included you may find a copy at
 * http://www.cosylab.com/legal/abeans_license.htm or may write to Cosylab, d.o.o.
 *
 * THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND, NOT EVEN THE
 * IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR OF THIS SOFTWARE, ASSUMES
 * _NO_ RESPONSIBILITY FOR ANY CONSEQUENCE RESULTING FROM THE USE, MODIFICATION,
 * OR REDISTRIBUTION OF THIS SOFTWARE.
 */

package com.cosylab.vdct.appplugin.alarmhandler;

import com.cosylab.vdct.appplugin.AppFrame;
import com.cosylab.vdct.appplugin.AppTreeChannelNode;
import com.cosylab.vdct.appplugin.AppTreeElement;
import com.cosylab.vdct.appplugin.AppTreeNode;
import com.cosylab.vdct.appplugin.Channel;
import com.cosylab.vdct.appplugin.Engine;
import com.cosylab.vdct.appplugin.Group;
import com.cosylab.vdct.appplugin.Property;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;


/**
 * <code>AHEngine</code> ...  DOCUMENT ME!
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class AHEngine extends Engine
{
	private static String comments = "#";
	static String[] inputFormat = new String[]{
			"CHANNEL", "GROUP", "INCLUDE", "$SEVRCOMMAND", "$STATCOMMAND",
			"$ALARMCOUNTFILTER", "$HEARTBEATPV", "$ACKPV", "$FORCEPV",
			"$FORCEPV CALC", "$FORCEPV_CALC", "$FORCEPV_CALC_A",
			"$FORCEPV_CALC_B", "$FORCEPV_CALC_C", "$FORCEPV_CALC_D",
			"$FORCEPV_CALC_E", "$FORCEPV_CALC_F", "$SEVRPV", "$ALIAS",
			"$COMMAND", "$BEEPSEVERITY", "$GUIDANCE", "$END"
		};
	static String[] propertyItems = new String[]{
			"guidanceText", "value", "seconds", "mask", "ackValue", "forceMask",
			"forceValue", "resetValue", "severityChangeValue", "severityCommand",
			"alarmStatusStringValue", "statusCommand", "inputCount",
			"inputSeconds"
		};
	private static String[] severityCommands = new String[]{
			"UP_INVALID", "UP_MAJOR", "UP_MINOR", "UP_ANY", "DOWN_MAJOR",
			"DOWN_MINOR", "DOWN_NO_ALARM", "DOWN_ANY", "UP_ALARM"
		};
	private static String[] statCommands = new String[]{
			"NO_ALARM", "READ", "WRITE", "HIHI", "HIGH", "READ_ACCESS", "LOLO",
			"LOW", "STATE", "COS", "COMM", "WRITE_ACCESS", "TIMEOUT", "HWLIMIT",
			"CALC", "SCAN", "LINK", "SOFT", "BAD_SUB", "UDF", "DISABLE", "SIMM"
		};
	private static String[] beepSeverities = new String[]{
			"MINOR", "MAJOR", "INVALID", "ERROR"
		};
	private static String includeExt = ".alhinclude";
	private AppTreeNode rootNode;
	private ArrayList savedData = new ArrayList();
	private AppTreeNode lastGroupOrChannelNode;

	/**
	 * Creates a new AHEngine object.
	 *
	 * @param frame DOCUMENT ME!
	 */
	public AHEngine(AppFrame frame)
	{
		super(frame);
	}

	/*
	 *  (non-Javadoc)
	 * @see com.cosylab.vdct.appplugin.Engine#initialize()
	 */
	protected void initialize()
	{
	}

	/**
	 * Reads the file and creates a tree structure created from the file's
	 * data. The returned <code>AppTreeNode</code> is the root for the tree.
	 *
	 * @param f source file
	 *
	 * @return tree structure read from the file
	 */
	public AppTreeNode openFromFile(File f)
	{
		BufferedReader reader = null;
		rootNode = null;
		lastGroupOrChannelNode = null;

		boolean include = f.getAbsolutePath().toLowerCase().endsWith(includeExt);

		try {
			reader = new BufferedReader(new FileReader(f));

			String currentLine = "";

			while (reader.ready()) {
				String l = reader.readLine();

				if (!l.startsWith(comments)) {
					String[] line = breakLine(l);

					if (line.length > 0) {
						if (line[0].equals("GROUP")) {
							// check for group
							if (line.length != 3) {
								JOptionPane.showMessageDialog(frame,
								    "Declaration of group " + line[1]
								    + " is invalid. \n Cannot open file " + f
								    + ".", "Invalid structure.",
								    JOptionPane.ERROR_MESSAGE);

								return null;
							}

							if (line[1].equals("NULL")) {
								rootNode = new AppTreeNode(new Group(line[2]));
								lastGroupOrChannelNode = rootNode;
							} else {
								if (include) {
									if (rootNode == null) {
										rootNode = new AppTreeNode(new Group(
											        line[1]));
										lastGroupOrChannelNode = rootNode;
									}
								}

								AppTreeNode node = new AppTreeNode(new Group(
									        line[2]));
								lastGroupOrChannelNode = node;

								if (!include) {
									if (!insertNode(rootNode, node, line[1])) {
										JOptionPane.showMessageDialog(frame,
										    "Declaration of GROUP " + line[2]
										    + " is invalid. \n Parent "
										    + line[1]
										    + " should be declared first. \n Cannot open file "
										    + f + ".", "Invalid structure.",
										    JOptionPane.ERROR_MESSAGE);

										return null;
									}
								} else {
									if (!insertNode(rootNode, node, line[1])) {
										if (!insertNode(rootNode, node, null)) {
											JOptionPane.showMessageDialog(frame,
											    "Declaration of GROUP "
											    + line[2]
											    + " is invalid. \n Cannot open file "
											    + f + ".",
											    "Invalid structure.",
											    JOptionPane.ERROR_MESSAGE);

											return null;
										}
									}
								}
							}
						} else if (line[0].equals("CHANNEL")) {
							// check for channel
							if (line.length > 4 || line.length < 3) {
								JOptionPane.showMessageDialog(frame,
								    "Declaration of CHANNEL " + line[2]
								    + " is invalid. \n Cannot open file " + f
								    + ".", "Invalid structure.",
								    JOptionPane.ERROR_MESSAGE);

								return null;
							}

							AppTreeChannelNode channelNode = new AppTreeChannelNode(new Channel(
								        line[2]));
							lastGroupOrChannelNode = channelNode;

							if (line.length == 4) {
								channelNode.add(new AppTreeNode(
								        new Property("mask", line[3])));
							}

							if (!insertNode(rootNode, channelNode, line[1])) {
								JOptionPane.showMessageDialog(frame,
								    "Declaration of CHANNEL " + line[2]
								    + " is invalid. \n Parent " + line[1]
								    + " should be declared first. \n Cannot open file "
								    + f + ".", "Invalid structure.",
								    JOptionPane.ERROR_MESSAGE);

								return null;
							}
						} else if (line[0].equals("INCLUDE")) {
							//check for include
							if (line.length != 3) {
								JOptionPane.showMessageDialog(frame,
								    "Declaration of INCLUDE " + line[2]
								    + " is invalid. \n Cannot open file " + f
								    + ".", "Invalid structure.",
								    JOptionPane.ERROR_MESSAGE);

								return null;
							}

							AppTreeNode includeNode = new AppTreeNode(new Include(
								        line[2]));

							if (!insertNode(rootNode, includeNode, line[1])) {
								JOptionPane.showMessageDialog(frame,
								    "Declaration of INCLUDE " + line[2]
								    + " is invalid. \n Parent " + line[1]
								    + " should be declared first. \n Cannot open file "
								    + f + ".", "Invalid structure.",
								    JOptionPane.ERROR_MESSAGE);

								return null;
							}
						} else if (line[0].equals("$HEARTBEATPV")) {
							//heartbeatpv
							if (line.length < 2 || line.length > 4) {
								JOptionPane.showMessageDialog(frame,
								    "Declaration of HEARTBEATPV " + line[1]
								    + " is invalid. \n Cannot open file " + f
								    + ".", "Invalid structure.",
								    JOptionPane.ERROR_MESSAGE);

								return null;
							}

							AppTreeNode heartNode = new AppTreeNode(new Property(
								        line[0], line[1]));

							int i = line.length;

							switch (i) {
							case 3: {
								AppTreeNode valueNode = new AppTreeNode(new Property(
									        "value", line[2]));
								heartNode.add(valueNode);

								break;
							}

							case 4: {
								AppTreeNode valueNode = new AppTreeNode(new Property(
									        "value", line[2]));
								AppTreeNode secondsNode = new AppTreeNode(new Property(
									        "seconds", line[3]));
								heartNode.add(valueNode);
								heartNode.add(secondsNode);

								break;
							}

							default:break;
							}

							if (!insertNode(lastGroupOrChannelNode, heartNode,
							        null)) {
								JOptionPane.showMessageDialog(frame,
								    "Declaration of HEARTBEATPV " + line[1]
								    + " is invalid. \n Cannot open file " + f
								    + ".", "Invalid structure.",
								    JOptionPane.ERROR_MESSAGE);

								return null;
							}
						} else if (line[0].equals("$FORCEPV")) {
							if (line.length < 3 || line.length > 5) {
								JOptionPane.showMessageDialog(frame,
								    "Declaration of FORCEPV " + line[1]
								    + " is invalid. \n Cannot open file " + f
								    + ".", "Invalid structure.",
								    JOptionPane.ERROR_MESSAGE);

								return null;
							}

							AppTreeNode forceNode = new AppTreeNode(new Property(
								        line[0], line[1]));
							AppTreeNode forceMask = new AppTreeNode(new Property(
								        "forceMask", line[2]));
							forceNode.add(forceMask);

							int i = line.length;

							switch (i) {
							case 4: {
								AppTreeNode valueNode = new AppTreeNode(new Property(
									        "forceValue", line[3]));
								forceNode.add(valueNode);

								break;
							}

							case 5: {
								AppTreeNode valueNode = new AppTreeNode(new Property(
									        "forceValue", line[3]));
								AppTreeNode resetNode = new AppTreeNode(new Property(
									        "resetValue", line[4]));
								forceNode.add(valueNode);
								forceNode.add(resetNode);

								break;
							}

							default:break;
							}

							if (!insertNode(lastGroupOrChannelNode, forceNode,
							        null)) {
								JOptionPane.showMessageDialog(frame,
								    "Declaration of FORCEPV " + line[1]
								    + " is invalid. \n Cannot open file " + f
								    + ".", "Invalid structure.",
								    JOptionPane.ERROR_MESSAGE);

								return null;
							}
						} else if (line[0].equals("$FORCEPV CALC")) {
							if (line.length < 2 || line.length > 4) {
								JOptionPane.showMessageDialog(frame,
								    "Declaration of FORCEPV CALC " + line[2]
								    + " is invalid. \n Cannot open file " + f
								    + ".", "Invalid structure.",
								    JOptionPane.ERROR_MESSAGE);

								return null;
							}

							AppTreeNode forceNode = new AppTreeNode(new Property(
								        line[0], false));
							AppTreeNode forceMask = new AppTreeNode(new Property(
								        "forceMask", line[1]));
							forceNode.add(forceMask);

							int i = line.length;

							switch (i) {
							case 3: {
								AppTreeNode valueNode = new AppTreeNode(new Property(
									        "forceValue", line[2]));
								forceNode.add(valueNode);

								break;
							}

							case 4: {
								AppTreeNode valueNode = new AppTreeNode(new Property(
									        "forceValue", line[2]));
								AppTreeNode resetNode = new AppTreeNode(new Property(
									        "resetValue", line[3]));
								forceNode.add(valueNode);
								forceNode.add(resetNode);

								break;
							}

							default:break;
							}

							if (!insertNode(lastGroupOrChannelNode, forceNode,
							        null)) {
								JOptionPane.showMessageDialog(frame,
								    "Declaration of FORCEPV CALC " + line[1]
								    + " is invalid. \n Cannot open file " + f
								    + ".", "Invalid structure.",
								    JOptionPane.ERROR_MESSAGE);
								System.out.println("testdsds");

								return null;
							}
						} else if (line[0].equals("$ACKPV")) {
							if (line.length != 3) {
								JOptionPane.showMessageDialog(frame,
								    "Declaration of ACKPV " + line[2]
								    + " is invalid. \n Cannot open file " + f
								    + ".", "Invalid structure.",
								    JOptionPane.ERROR_MESSAGE);

								return null;
							}

							AppTreeNode ackNode = new AppTreeNode(new Property(
								        line[0], line[1]));
							AppTreeNode valueNode = new AppTreeNode(new Property(
								        "ackValue", line[2]));
							ackNode.add(valueNode);

							if (!insertNode(lastGroupOrChannelNode, ackNode,
							        null)) {
								JOptionPane.showMessageDialog(frame,
								    "Declaration of ACKPV " + line[1]
								    + " is invalid. \n Cannot open file " + f
								    + ".", "Invalid structure.",
								    JOptionPane.ERROR_MESSAGE);

								return null;
							}
						} else if (line[0].equals("$FORCEPV_CALC")
						    || line[0].equals("$FORCEPV_CALC_A")
						    || line[0].equals("$FORCEPV_CALC_B")
						    || line[0].equals("$FORCEPV_CALC_C")
						    || line[0].equals("$FORCEPV_CALC_D")
						    || line[0].equals("$FORCEPV_CALC_E")
						    || line[0].equals("$FORCEPV_CALC_F")) {
							AppTreeNode forceNode = null;
							int i = line.length;

							switch (i) {
							case 1: {
								forceNode = new AppTreeNode(new Property(
									        line[0], true));

								break;
							}

							case 2: {
								forceNode = new AppTreeNode(new Property(
									        line[0], line[1]));

								break;
							}

							default: {
								JOptionPane.showMessageDialog(frame,
								    "Declaration of " + line[0] + " " + line[1]
								    + " is invalid. \n Cannot open file " + f
								    + ".", "Invalid structure.",
								    JOptionPane.ERROR_MESSAGE);

								return null;
							}
							}

							if (!insertNode(lastGroupOrChannelNode, forceNode,
							        null)) {
								JOptionPane.showMessageDialog(frame,
								    "Declaration of " + line[0] + " " + line[1]
								    + " is invalid. \n Cannot open file " + f
								    + ".", "Invalid structure.",
								    JOptionPane.ERROR_MESSAGE);

								return null;
							}
						} else if (line[0].equals("$ALIAS")
						    || line[0].equals("$COMMAND")) {
							String li = null;

							for (int i = 1; i < line.length; i++) {
								li += (line[i] + " ");
							}

							AppTreeNode node = new AppTreeNode(new Property(
								        line[0], line[1]));

							if (!insertNode(lastGroupOrChannelNode, node, null)) {
								JOptionPane.showMessageDialog(frame,
								    "Declaration of " + line[0] + " " + line[1]
								    + " is invalid. \n Cannot open file " + f
								    + ".", "Invalid structure.",
								    JOptionPane.ERROR_MESSAGE);

								return null;
							}
						} else if (line[0].equals("$SEVRPV")) {
							if (line.length != 2) {
								JOptionPane.showMessageDialog(frame,
								    "Declaration of SEVRPV " + line[1]
								    + " is invalid. \n Cannot open file " + f
								    + ".", "Invalid structure.",
								    JOptionPane.ERROR_MESSAGE);

								return null;
							}

							AppTreeNode node = new AppTreeNode(new Property(
								        line[0], line[1]));

							if (!insertNode(lastGroupOrChannelNode, node, null)) {
								JOptionPane.showMessageDialog(frame,
								    "Declaration of SEVRPV " + line[1]
								    + " is invalid. \n Cannot open file " + f
								    + ".", "Invalid structure.",
								    JOptionPane.ERROR_MESSAGE);

								return null;
							}
						} else if (line[0].equals("$SEVRCOMMAND")) {
							if (line.length != 3) {
								JOptionPane.showMessageDialog(frame,
								    "Declaration of SEVRCOMMAND is invalid. \n Cannot open file "
								    + f + ".", "Invalid structure.",
								    JOptionPane.ERROR_MESSAGE);

								return null;
							}

							if (isCommandValid(severityCommands, line[2])) {
								AppTreeNode severity = new AppTreeNode(new Property(
									        line[0], false));
								AppTreeNode changeNode = new AppTreeNode(new Property(
									        "severityChangeValue", line[1]));
								AppTreeNode sevrComm = new AppTreeNode(new Property(
									        "severityCommand", line[2]));
								severity.add(changeNode);
								severity.add(sevrComm);

								if (!insertNode(lastGroupOrChannelNode,
								        severity, null)) {
									JOptionPane.showMessageDialog(frame,
									    "Declaration of SEVRCOMMAND is invalid. \n Cannot open file "
									    + f + ".", "Invalid structure.",
									    JOptionPane.ERROR_MESSAGE);

									return null;
								}
							} else {
								JOptionPane.showMessageDialog(frame,
								    "Declaration of SEVRCOMMAND is invalid. \n"
								    + line[2]
								    + "is not a valid severity command. \n Cannot open file "
								    + f + ".", "Invalid structure.",
								    JOptionPane.ERROR_MESSAGE);

								return null;
							}
						} else if (line[0].equals("$STATCOMMAND")) {
							if (line.length != 3) {
								JOptionPane.showMessageDialog(frame,
								    "Declaration of STATCOMMAND is invalid. \n Cannot open file "
								    + f + ".", "Invalid structure.",
								    JOptionPane.ERROR_MESSAGE);

								return null;
							}

							if (isCommandValid(statCommands, line[1])) {
								AppTreeNode status = new AppTreeNode(new Property(
									        line[0], false));
								AppTreeNode statusString = new AppTreeNode(new Property(
									        "alarmStatusStringValue", line[1]));
								AppTreeNode statComm = new AppTreeNode(new Property(
									        "statusCommand", line[2]));
								status.add(statusString);
								status.add(statComm);

								if (!insertNode(lastGroupOrChannelNode, status,
								        null)) {
									JOptionPane.showMessageDialog(frame,
									    "Declaration of STATCOMMAND is invalid. \n Cannot open file "
									    + f + ".", "Invalid structure.",
									    JOptionPane.ERROR_MESSAGE);

									return null;
								}
							} else {
								JOptionPane.showMessageDialog(frame,
								    "Declaration of STATCOMMAND is invalid. \n"
								    + line[2]
								    + "is not a valid status command. \n Cannot open file "
								    + f + ".", "Invalid structure.",
								    JOptionPane.ERROR_MESSAGE);

								return null;
							}
						} else if (line[0].equals("$BEEPSEVERITY")) {
							if (line.length != 2) {
								JOptionPane.showMessageDialog(frame,
								    "Declaration of BEEPSEVERITY is invalid. \n Cannot open file "
								    + f + ".", "Invalid structure.",
								    JOptionPane.ERROR_MESSAGE);

								return null;
							}

							if (isCommandValid(beepSeverities, line[1])) {
								AppTreeNode beepSev = new AppTreeNode(new Property(
									        line[0], line[1]));

								if (!insertNode(lastGroupOrChannelNode,
								        beepSev, null)) {
									JOptionPane.showMessageDialog(frame,
									    "Declaration of BEEPSEVERITY is invalid. \n Cannot open file "
									    + f + ".", "Invalid structure.",
									    JOptionPane.ERROR_MESSAGE);

									return null;
								}
							} else {
								JOptionPane.showMessageDialog(frame,
								    "Declaration of BEEPSEVERITY is invalid. \n"
								    + line[2]
								    + "is not a valid beep severity. \n Cannot open file "
								    + f + ".", "Invalid structure.",
								    JOptionPane.ERROR_MESSAGE);

								return null;
							}
						} else if (line[0].equals("$ALARMCOUNTFILTER")) {
							if (line.length != 3) {
								JOptionPane.showMessageDialog(frame,
								    "Declaration of ALARMCOUNTFILTER is invalid. \n Cannot open file "
								    + f + ".", "Invalid structure.",
								    JOptionPane.ERROR_MESSAGE);

								return null;
							}

							int i = Integer.parseInt(line[1]);

							AppTreeNode alarmNode = new AppTreeNode(new Property(
								        line[0], false));
							AppTreeNode count = new AppTreeNode(new Property(
								        "inputCount", line[1]));
							AppTreeNode seconds = new AppTreeNode(new Property(
								        "inputSeconds", line[2]));
							alarmNode.add(count);
							alarmNode.add(seconds);

							if (!insertNode(lastGroupOrChannelNode, alarmNode,
							        null)) {
								JOptionPane.showMessageDialog(frame,
								    "Declaration of ALARMCOUNTFILTER is invalid. \n Cannot open file "
								    + f + ".", "Invalid structure.",
								    JOptionPane.ERROR_MESSAGE);

								return null;
							}
						} else if (line[0].equals("$GUIDANCE")) {
							AppTreeNode guidance = null;

							if (line.length > 2) {
								JOptionPane.showMessageDialog(frame,
								    "Declaration of GUIDANCE is invalid. \n Cannot open file "
								    + f + ".", "Invalid structure.",
								    JOptionPane.ERROR_MESSAGE);

								return null;
							} else if (line.length == 2) {
								guidance = new AppTreeNode(new Property(
									        line[0], line[1]));
							} else {
								guidance = new AppTreeNode(new Property(
									        line[0], false));

								String read = reader.readLine();

								while (!read.equals("$END")) {
									AppTreeNode text = new AppTreeNode(new Text(
										        read));
									guidance.add(text);
									read = reader.readLine();
								}
							}

							if (!insertNode(lastGroupOrChannelNode, guidance,
							        null)) {
								JOptionPane.showMessageDialog(frame,
								    "Declaration of GUIDANCE is invalid. \n Cannot open file "
								    + f + ".", "Invalid structure.",
								    JOptionPane.ERROR_MESSAGE);

								return null;
							}
						} else {
							JOptionPane.showMessageDialog(frame,
							    "Line cannont start with " + line[0]
							    + ". \n Cannot open file " + f + ".",
							    "Invalid structure.", JOptionPane.ERROR_MESSAGE);

							return null;
						}
					}
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame,
			    "File structure invalid. \n Cannot open file " + f + ".",
			    "Invalid structure.", JOptionPane.ERROR_MESSAGE);

			return null;
		}

		return rootNode;
	}

	private boolean isCommandValid(String[] valid, String command)
	{
		for (int i = 0; i < valid.length; i++) {
			if (valid[i].equals(command)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Inserts node nodeToAdd to the node with userElement with a name parent.
	 * Searching for parentNode is done inside the accepter node.
	 *
	 * @param accepter
	 * @param nodeToAdd
	 * @param parent
	 *
	 * @return
	 */
	private boolean insertNode(AppTreeNode accepter, AppTreeNode nodeToAdd,
	    String parent)
	{
		if (rootNode == null) {
			return false;
		}

		if (parent == null) {
			accepter.add(nodeToAdd);

			return true;
		}

		if (accepter.getTreeUserElement().getName().toUpperCase().equals(parent
		        .toUpperCase())) {
			accepter.add(nodeToAdd);

			return true;
		}

		for (int i = 0; i < accepter.getChildCount(); i++) {
			AppTreeNode node = (AppTreeNode)accepter.getChildAt(i);

			if (insertNode(node, nodeToAdd, parent)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Transforms tree structure into text and saves it to a file.
	 *
	 * @param file destination file
	 * @param source the root of the tree to be saved
	 *
	 * @return
	 */
	public boolean saveToFile(File file, AppTreeNode source)
	{
		savedData.clear();

		String ext = file.getName().toLowerCase();
		String rootName = source.getTreeUserElement().getName();

		if (ext.endsWith(".alhconfig")) {
			savedData.add(new String("GROUP NULL " + rootName));
		}

		for (int i = 0; i < source.getChildCount(); i++) {
			if (!save((AppTreeNode)source.getChildAt(i))) {
				return false;
			}
		}

		try {
			PrintWriter writer = new PrintWriter(new FileWriter(file));

			for (int i = 0; i < savedData.size(); i++) {
				writer.println(savedData.get(i));
			}

			writer.close();
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}

		return true;
	}

	/**
	 * Prepares data for saving. All data is collected and stored to an
	 * ArrayList (each line is a different object in the list).
	 *
	 * @param source
	 *
	 * @return
	 */
	private boolean save(AppTreeNode source)
	{
		AppTreeElement element = source.getTreeUserElement();
		AppTreeElement parentElem = ((AppTreeNode)source.getParent())
			.getTreeUserElement();

		if (element instanceof Group) {
			savedData.add(new String(""));
			savedData.add(new String("GROUP " + parentElem.getName() + " "
			        + element.getName()));
			savedData.add(new String(""));
		} else if (element instanceof Channel) {
			savedData.add(new String("CHANNEL " + parentElem.getName() + " "
			        + element.getName()));
		} else if (element instanceof Include) {
			savedData.add(new String(""));
			savedData.add(new String("INCLUDE " + parentElem.getName() + " "
			        + element.getName()));
			savedData.add(new String(""));
		} else if (element instanceof Text) {
			savedData.add(new String(element.getName()));

			if (((AppTreeNode)source.getParent()).getChildAfter(source) == null) {
				savedData.add(new String("$END"));
			}
		} else if (element instanceof Property) {
			String name = element.getName();
			String value = ((Property)element).getValue();
			String data = getPropertyString(source);
			StringBuffer buffer = new StringBuffer();

			if (name.equals("$COMMAND") || name.equals("$BEEPSEVERITY")
			    || name.equals("$ALIAS") || name.equals("$SEVRPV")) {
				if (value == "") {
					//TODO
					return false;
				}

				buffer.append(name + " " + value);
			} else if (name.equals("$HEARTBEATPV")) {
				if (value == "") {
					//TODO
					return false;
				}

				buffer.append(name + " " + value + " " + data);
			} else if (name.equals("$ACKPV")) {
				if (data == null) {
					//TODO
					return false;
				}

				buffer.append(name + " " + value + " " + data);
			} else if (name.equals("$FORCEPV_CALC")
			    || name.equals("$FORCEPV_CALC_A")
			    || name.equals("$FORCEPV_CALC_B")
			    || name.equals("$FORCEPV_CALC_C")
			    || name.equals("$FORCEPV_CALC_D")
			    || name.equals("$FORCEPV_CALC_E")
			    || name.equals("$FORCEPV_CALC_F")) {
				buffer.append(name + " " + data);
			} else if (name.equals("$FORCEPV CALC")
			    || name.equals("$SEVRCOMMAND") || name.equals("$STATCOMMAND")
			    || name.equals("$ALARMCOUNTFILTER") || name.equals("$FORCEPV")) {
				if (data == null) {
					//TODO
					return false;
				}

				buffer.append(name + " " + data);
			} else if (name.equals("$GUIDANCE")) {
				if (value == null) {
					buffer.append(name);
				} else {
					buffer.append(name + " " + value);
				}
			}

			if (buffer.length() > 0) {
				savedData.add(new String(buffer));
			}
		}

		for (int i = 0; i < source.getChildCount(); i++) {
			if (!save((AppTreeNode)source.getChildAt(i))) {
				//TODO
				return false;
			}
		}

		return true;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getPropertyString(AppTreeNode node)
	{
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < node.getChildCount(); i++) {
			AppTreeElement element = ((AppTreeNode)node.getChildAt(i))
				.getTreeUserElement();

			if (element instanceof Property) {
				if (((Property)element).hasValue()) {
					buffer.append(((Property)element).getValue() + " ");
				}
			}
		}

		return new String(buffer);
	}

	/**
	 * Breaks line. All words are returned in a String[].
	 *
	 * @param line
	 *
	 * @return
	 */
	private String[] breakLine(String line)
	{
		StringTokenizer st = new StringTokenizer(line, " ");

		ArrayList strings = new ArrayList();

		while (st.hasMoreTokens()) {
			strings.add(st.nextToken().trim());
		}

		if (strings.size() > 1) {
			ArrayList temp = new ArrayList();
			String com = (String)strings.get(0);

			if (com.equals("$FORCEPV") && strings.get(1).equals("CALC")) {
				temp.add(com + " " + strings.get(1));

				for (int i = 2; i < strings.size(); i++) {
					temp.add(strings.get(i));
				}

				strings = temp;
			} else if (com.equals("$COMMAND") || com.equals("$ALIAS")) {
				temp.add(com);

				StringBuffer buffer = new StringBuffer();

				for (int i = 1; i < strings.size(); i++) {
					buffer.append(strings.get(i) + " ");
				}

				temp.add(new String(buffer));
				strings = temp;
			}
		}

		String[] s = new String[strings.size()];

		return (String[])strings.toArray(s);
	}
}

/* __oOo__ */
