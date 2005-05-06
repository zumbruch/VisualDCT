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

package com.cosylab.vdct.appplugin.archiver;

import com.cosylab.vdct.appplugin.AppFrame;
import com.cosylab.vdct.appplugin.AppTreeChannelNode;
import com.cosylab.vdct.appplugin.AppTreeElement;
import com.cosylab.vdct.appplugin.AppTreeNode;
import com.cosylab.vdct.appplugin.Channel;
import com.cosylab.vdct.appplugin.Engine;
import com.cosylab.vdct.appplugin.Group;
import com.cosylab.vdct.appplugin.Property;

import org.apache.crimson.tree.TextNode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JOptionPane;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


/**
 * <code>Engine</code> ...  DOCUMENT ME!
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class ArchiverEngine extends Engine
{
	/**
	 * String representation of the dtd file that should be declared in loaded
	 * xml files.
	 */
	public static final String DTD_FILE = "engineconfig.dtd";
	private static final short BREAK_TYPE = 0;
	private static final short INDENT_TYPE = 1;
	private static final short LONG_INDENT_TYPE = 2;
	static final String[] engineConfigProperties = new String[]{
			"write_period", "get_threshold", "file_size", "ignored_future",
			"buffer_reserve", "max_repeat_count", "disconnect"
		};
	static final String[] channelProperties = new String[]{
			"period", "scan", "monitor", "disable"
		};
	private static final String[] rejected = { "name", "#text" };
	private static final String[] descriptors = {
			"engineconfig", "group", "channel", "name"
		};
	static final String[] defaultPropertyValues = new String[]{
			"30", "20", "30", "1.0", "3", "120", "1.0"
		};

	//	private static final String[] propertyDescriptors = {
	//			"scan", "monitor", "disable", "disconnect"
	//		};
	private Document doc;

	//	private Archiver archiver;
	private boolean parsingSuccessful = true;
	private DocumentBuilder builder;
	private DocumentBuilderFactory factory;

	/**
	 * Creates a new Engine object.
	 *
	 * @param arch DOCUMENT ME!
	 */
	public ArchiverEngine(AppFrame arch)
	{
		super(arch);
	}

	/*
	 *  (non-Javadoc)
	 * @see com.cosylab.vdct.appplugin.Engine#initialize()
	 */
	protected void initialize()
	{
		factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		factory.setIgnoringElementContentWhitespace(true);

		try {
			builder = factory.newDocumentBuilder();
			builder.setErrorHandler(new ErrorHandler() {
					public void error(SAXParseException exception)
						throws SAXException
					{
						if (parsingSuccessful) {
							int i = JOptionPane.showConfirmDialog(frame,
								    exception + " Shall I continue parsing?",
								    "Parsing Error", JOptionPane.YES_NO_OPTION,
								    JOptionPane.ERROR_MESSAGE);

							if (i == JOptionPane.NO_OPTION) {
								parsingSuccessful = false;
							}
						}
					}

					public void fatalError(SAXParseException exception)
						throws SAXException
					{
						JOptionPane.showMessageDialog(frame,
						    exception + " Parsing aborted!", "Fatal Error",
						    JOptionPane.ERROR_MESSAGE);
						parsingSuccessful = false;
					}

					public void warning(SAXParseException exception)
						throws SAXException
					{
						int i = JOptionPane.showConfirmDialog(frame,
							    exception + " Shall I continue parsing?",
							    "Warning", JOptionPane.YES_NO_OPTION,
							    JOptionPane.WARNING_MESSAGE);

						if (i == JOptionPane.NO_OPTION) {
							parsingSuccessful = false;
						}
					}
				});
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see com.cosylab.vdct.appplugin.Engine#saveToFile(java.io.File, com.cosylab.vdct.appplugin.AppTreeNode)
	 */
	public boolean saveToFile(File file, AppTreeNode root)
	{
		try {
			if (builder == null) {
				builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			}

			Document doc = builder.newDocument();

			boolean success = parseToDocument(doc, root);

			if (!success) {
				return false;
			}

			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty("standalone", "no");
			t.setOutputProperty("doctype-system", ArchiverEngine.DTD_FILE);
			t.transform(new DOMSource(doc),
			    new StreamResult(new FileOutputStream(file)));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame, e);

			return false;
		}

		return true;
	}

	/*
	 *  (non-Javadoc)
	 * @see com.cosylab.vdct.appplugin.Engine#openFromFile(java.io.File)
	 */
	public AppTreeNode openFromFile(File file)
	{
		parsingSuccessful = true;

		try {
			if (builder == null) {
				builder = factory.newDocumentBuilder();
			}

			Document doc = builder.parse(file);

			return parsingSuccessful ? parseFromDocument(doc) : null;
		} catch (Exception e) {
		    JOptionPane.showMessageDialog(frame, e);
		}

		return null;
	}

	/**
	 * Wraps the Document to the form that can be displayed in the
	 * ArchiverTree. Method returns the root node of this document, containing
	 * all subnodes in correct order. If the DTD file  that is declared in the
	 * header of the Document does not match with Engine.DTD_FILE, parsing is
	 * aborted. If there is no dtd file declared parsing continues.
	 *
	 * @param doc the Document to be wrapped
	 *
	 * @return the root of the document
	 */
	public AppTreeNode parseFromDocument(Document doc)
	{
		this.doc = doc;

		NodeList list = doc.getChildNodes();
		AppTreeNode root = new AppTreeNode(new EngineConfigRoot());
		boolean accept = false;

		try {
			accept = doc.getDoctype().getSystemId().equals(DTD_FILE);
		} catch (Exception e) {
			accept = true;
		}

		//		addChildren(list.item(1).getChildNodes(), root);
		if (accept) {
			addChildren(doc.getDocumentElement().getChildNodes(), root);
		}

		return root;
	}

	/**
	 * Creates TreeNodes from the elements in the list, and adds them as
	 * children to the parent.
	 *
	 * @param list list of nodes to be added to the parent
	 * @param parent parent TreeNode for all nodes in the list
	 */
	private void addChildren(NodeList list, AppTreeNode parent)
	{
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);

			if (isRejected(node.getNodeName())) {
				continue;
			}

			AppTreeNode treeNode = null;
			AppTreeElement elem = getTreeElement(node);

			if (elem instanceof Channel) {
				treeNode = new AppTreeChannelNode((Channel)elem);
			} else {
				treeNode = new AppTreeNode(getTreeElement(node));
			}

			parent.add(treeNode);

			addChildren(node.getChildNodes(), treeNode);
		}
	}

	/*
	 * The node with the name "name" is rejected by this parser. TreeNodes don`t contain
	 * cihldren named "name", because the name property is displayed by the parent of the property.
	 * (eg. TreeNode that displays channel has the name which is saved under the tag "name").
	 * Some additional strings are rejected as well.
	 */
	private boolean isRejected(String nodeName)
	{
		for (int i = 0; i < rejected.length; i++) {
			if (nodeName.equals(rejected[i])) {
				return true;
			}
		}

		return false;
	}

	/*
	 * Constructs the ArchiverTreeElement according to the name of the Node.
	 */
	private AppTreeElement getTreeElement(Node domNode)
	{
		String represent = domNode.getNodeName();

		AppTreeElement treeElement = null;

		if (represent.equals(descriptors[1])) {
			treeElement = new Group("<name>");
		} else if (represent.equals(descriptors[0])) {
			treeElement = new EngineConfigRoot();
		} else if (represent.equals(descriptors[2])) {
			treeElement = new Channel("<name>");
		} else {
			//			for (int i = 0; i < propertyDescriptors.length; i++) {
			//				if (represent.equals(propertyDescriptors[i])) {
			//					return new Property(represent, false);
			//				}
			//			}
			try {
				return new Property(represent,
				    domNode.getChildNodes().item(0).getNodeValue().trim());
			} catch (NullPointerException e) {
				return new Property(represent, false);
			}

			//			treeElement = new Property(represent, content(domNode).trim());
		}

		NodeList list = domNode.getChildNodes();
		Node node;

		for (int i = 0; i < list.getLength(); i++) {
			node = list.item(i);

			if (node.getNodeName().equals(descriptors[3])) {
				treeElement.setName(node.getChildNodes().item(0).getNodeValue());

				//				treeElement.setName(content(node));
				break;
			}
		}

		return treeElement;
	}

	/*
	 * Returns the content od the node.
	 */

	//	private String content(Node dnode)
	//	{
	//		StringBuffer s = new StringBuffer("");
	//		NodeList nodeList = dnode.getChildNodes();
	//
	//		for (int i = 0; i < nodeList.getLength(); i++) {
	//			Node node = nodeList.item(i);
	//			int type = node.getNodeType();
	//
	//			if (type == Node.ELEMENT_NODE) {
	//				// Skip subelements that are displayed in the tree.   
	//				if (!isRejected(node.getNodeName())) {
	//					continue;
	//				}
	//				s.append("<" + node.getNodeName() + ">");
	//				s.append(content(node));
	//				s.append("</" + node.getNodeName() + ">");
	//			} else if (type == Node.TEXT_NODE) {
	//			    s.append(node.getNodeValue());
	//			} else if (type == Node.ENTITY_REFERENCE_NODE) {
	//				// The content is in the TEXT node under it
	//			    s.append(content(node));
	//			} else if (type == Node.CDATA_SECTION_NODE) {
	//				// The "value" has the text, same as a text node.
	//				//   while EntityRef has it in a text node underneath.
	//				//   (because EntityRef can contain multiple subelements)
	//				// Convert angle brackets and ampersands for display
	//				StringBuffer sb = new StringBuffer(node.getNodeValue());
	//
	//				for (int j = 0; j < sb.length(); j++) {
	//					if (sb.charAt(j) == '<') {
	//						sb.setCharAt(j, '&');
	//						sb.insert(j + 1, "lt;");
	//						j += 3;
	//					} else if (sb.charAt(j) == '&') {
	//						sb.setCharAt(j, '&');
	//						sb.insert(j + 1, "amp;");
	//						j += 4;
	//					}
	//				}
	//				
	//				s.append("<pre>" + sb + "\n</pre>");
	//			}
	//		}
	//
	//		return s.toString();
	//	}

	/**
	 * Wraps the tree structure to the Document. Root becomes the root element
	 * of the Document. All children of the root are added as Nodes to the
	 * Document. If the there is a failure in the structure of the root
	 * (according to the dtd file) this method returns false, otherwise it
	 * returns true.
	 *
	 * @param doc destination Document
	 * @param root TreeNode that is wraped to Document
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean parseToDocument(Document doc, AppTreeNode root)
	{
		this.doc = doc;

		Element e = doc.createElement(descriptors[0]);
		doc.appendChild(e);

		return appendToRootNode(root, e);

		//        appendChildrenToNode(e, root);
	}

	private boolean appendToRootNode(AppTreeNode root, Node rootNode)
	{
		if (root.getChildCount() == 0
		    || root.getChildCount() == root.getLeafCount()) {
			JOptionPane.showMessageDialog(frame,
			    "EngineConfig must contain at least one not empty group. \n Parsing aborted!",
			    "Structure invalid", JOptionPane.WARNING_MESSAGE);

			return false;
		}

		for (int i = 0; i < root.getChildCount(); i++) {
			//group or property
			AppTreeNode node = (AppTreeNode)root.getChildAt(i);

			rootNode.appendChild(getIndentNode(BREAK_TYPE));

			Element level1 = null;

			if (node.isLeaf()) {
				// root property
				AppTreeElement elem = node.getTreeUserElement();

				if (elem instanceof Property) {
					level1 = doc.createElement(elem.getName());
					rootNode.appendChild(getIndentNode(INDENT_TYPE));

					String value = ((Property)node.getTreeUserElement())
						.getValue();

					if (value != null) {
						level1.appendChild(doc.createTextNode(value));
					} else {
						if (((Property)elem).hasValue()) {
							JOptionPane.showMessageDialog(frame,
							    "Property " + elem.getName()
							    + " has invalid value. \n Parsing aborted!",
							    "Structure invalid", JOptionPane.WARNING_MESSAGE);

							return false;
						}
					}
				}

				//				} else {
				//					//                    JOptionPane.showMessageDialog(null, "Group " + node.getArchiverTreeUserElement().getName() + " must contain at least one channel. \n Parsing aborted!", "Structure invalid", JOptionPane.WARNING_MESSAGE);
				//					return false;
				//				}
			} else {
				//group 
				level1 = doc.createElement(descriptors[1]);
				level1.appendChild(getIndentNode(BREAK_TYPE));
				level1.appendChild(getIndentNode(INDENT_TYPE));

				// add name node to the group - the tree doesn`t display name as a separate node
				Element nameElement = doc.createElement("name");
				Text nameText = doc.createTextNode(node.getTreeUserElement()
					    .getName());
				nameElement.appendChild(nameText);
				level1.appendChild(nameElement);

				level1.appendChild(getIndentNode(BREAK_TYPE));

				for (int j = 0; j < node.getChildCount(); j++) {
					//channel
					AppTreeNode channelNode = (AppTreeNode)node.getChildAt(j);

					// append name node to the channel - similar to group name
					level1.appendChild(getIndentNode(INDENT_TYPE));

					Element level2 = doc.createElement(descriptors[2]);
					Element channelNameElement = doc.createElement("name");
					Text channelNameText = doc.createTextNode(channelNode.getTreeUserElement()
						    .getName());
					channelNameElement.appendChild(channelNameText);

					level2.appendChild(getIndentNode(BREAK_TYPE));
					level2.appendChild(getIndentNode(LONG_INDENT_TYPE));
					level2.appendChild(channelNameElement);
					level2.appendChild(getIndentNode(BREAK_TYPE));

					boolean period = false;
					boolean scanMonitor = false;

					for (int k = 0; k < channelNode.getChildCount(); k++) {
						//channel property
						AppTreeNode channelProperty = (AppTreeNode)channelNode
							.getChildAt(k);
						AppTreeElement elem = channelProperty
							.getTreeUserElement();
						String name = elem.getName();
						Element level3 = doc.createElement(name);

						if (name.equals(channelProperties[0])) {
							period = true;
						} else if (name.equals(channelProperties[1])
						    || name.equals(channelProperties[2])) {
							scanMonitor = true;
						}

						String value = ((Property)channelProperty
							.getTreeUserElement()).getValue();

						if (value != null) {
							Text level3Text = doc.createTextNode(value);
							level3.appendChild(level3Text);
						} else {
							if (((Property)elem).hasValue()) {
								JOptionPane.showMessageDialog(frame,
								    "Property " + elem.getName()
								    + " has invalid value. \n Parsing aborted!",
								    "Structure invalid",
								    JOptionPane.WARNING_MESSAGE);

								return false;
							}
						}

						level2.appendChild(getIndentNode(LONG_INDENT_TYPE));
						level2.appendChild(level3);
						level2.appendChild(getIndentNode(BREAK_TYPE));
					}

					if (!(period & scanMonitor)) {
						JOptionPane.showMessageDialog(frame,
						    "Channel "
						    + channelNode.getTreeUserElement().getName()
						    + " is missing properties. \n Parsing aborted!",
						    "Structure invalid", JOptionPane.WARNING_MESSAGE);

						return false;
					}

					level2.appendChild(getIndentNode(INDENT_TYPE));
					level1.appendChild(level2);
					level1.appendChild(getIndentNode(BREAK_TYPE));
				}
			}

			rootNode.appendChild(level1);
		}

		rootNode.appendChild(getIndentNode(BREAK_TYPE));

		return true;
	}

	private Text getIndentNode(short type)
	{
		if (type == BREAK_TYPE) {
			return doc.createTextNode("\n");
		} else if (type == INDENT_TYPE) {
			return doc.createTextNode("        ");
		} else if (type == LONG_INDENT_TYPE) {
			return doc.createTextNode("                ");
		}

		return new TextNode();
	}
}

/* __oOo__ */
