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

package com.cosylab.vdct.archiver;

import org.apache.crimson.tree.TextNode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import javax.swing.JOptionPane;


/**
 * <code>Engine</code> ...  DOCUMENT ME!
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class Engine
{
	/** String representation of the dtd file that should be declared in loaded xml files. */
	public static final String DTD_FILE = "engineconfig.dtd";
	private static final short BREAK_TYPE = 0;
	private static final short INDENT_TYPE = 1;
	private static final short LONG_INDENT_TYPE = 2;

	private static final String[] rejected = { "name", "#text" };

	private static final String[] descriptors = {
			"engineconfig", "group", "channel", "name"
		};

	private static final String[] propertyDescriptors = {
			"scan", "monitor", "disable"
		};
	private Document doc;

	/**
	 * Creates a new Engine object.
	 */
	public Engine()
	{
	}

	/**
	 * Wraps the Document to the form that can be displayed in the ArchiverTree. Method returns
	 * the root node of this document, containing all subnodes in correct order. If the DTD file 
	 * that is declared in the header of the Document does not match with Engine.DTD_FILE, parsing
	 * is aborted. If there is no dtd file declared parsing continues.
	 *
	 * @param doc the Document to be wrapped 
	 *
	 * @return the root of the document
	 */
	public ArchiverTreeNode parseFromDocument(Document doc)
	{
		this.doc = doc;

		NodeList list = doc.getChildNodes();
		ArchiverTreeNode root = new ArchiverTreeNode(new EngineConfigRoot());
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
	 * Creates TreeNodes from the elements in the list, and adds them as children to the parent.
	 *
	 * @param list list of nodes to be added to the parent
	 * @param parent parent TreeNode for all nodes in the list
	 */
	private void addChildren(NodeList list, ArchiverTreeNode parent)
	{
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);

			if (isRejected(node.getNodeName())) {
				continue;
			}

			ArchiverTreeNode treeNode = null;
			ArchiverTreeElement elem = getTreeElement(node);

			if (elem instanceof Channel) {
				treeNode = new ArchiverTreeChannelNode((Channel)elem);
			} else {
				treeNode = new ArchiverTreeNode(getTreeElement(node));
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
	private ArchiverTreeElement getTreeElement(Node domNode)
	{
		String represent = domNode.getNodeName();

		ArchiverTreeElement treeElement = null;

		if (represent.equals(descriptors[1])) {
			treeElement = new Group("<name>");
		} else if (represent.equals(descriptors[0])) {
			treeElement = new EngineConfigRoot();
		} else if (represent.equals(descriptors[2])) {
			treeElement = new Channel("<name>");
		} else {
			for (int i = 0; i < propertyDescriptors.length; i++) {
				if (represent.equals(propertyDescriptors[i])) {
					return new Property(represent, false);
				}
			}
			
			treeElement = new Property(represent, content(domNode).trim());
		}

		NodeList list = domNode.getChildNodes();
		Node node;

		for (int i = 0; i < list.getLength(); i++) {
			node = list.item(i);

			if (node.getNodeName().equals(descriptors[3])) {
				treeElement.setName(content(node));

				break;
			}
		}

		return treeElement;
	}

	/*
	 * Returns the content od the node. 
	 */
	private String content(Node dnode)
	{
		String s = "";
		NodeList nodeList = dnode.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			int type = node.getNodeType();

			if (type == Node.ELEMENT_NODE) {
				// Skip subelements that are displayed in the tree.   
				if (!isRejected(node.getNodeName())) {
					continue;
				}

				s += "<" + node.getNodeName() + ">";
				s += content(node);
				s += "</" + node.getNodeName() + ">";
			} else if (type == Node.TEXT_NODE) {
				s += node.getNodeValue();
			} else if (type == Node.ENTITY_REFERENCE_NODE) {
				// The content is in the TEXT node under it
				s += content(node);
			} else if (type == Node.CDATA_SECTION_NODE) {
				// The "value" has the text, same as a text node.
				//   while EntityRef has it in a text node underneath.
				//   (because EntityRef can contain multiple subelements)
				// Convert angle brackets and ampersands for display
				StringBuffer sb = new StringBuffer(node.getNodeValue());

				for (int j = 0; j < sb.length(); j++) {
					if (sb.charAt(j) == '<') {
						sb.setCharAt(j, '&');
						sb.insert(j + 1, "lt;");
						j += 3;
					} else if (sb.charAt(j) == '&') {
						sb.setCharAt(j, '&');
						sb.insert(j + 1, "amp;");
						j += 4;
					}
				}

				s += "<pre>" + sb + "\n</pre>";
			}
		}

		return s;
	}

	/**
	 * Wraps the tree structure to the Document. Root becomes the root element of the Document.
	 * All children of the root are added as Nodes to the Document. If the there is a failure in
	 * the structure of the root (according to the dtd file) this method returns false, otherwise
	 * it returns true.
	 *
	 * @param doc destination Document 
	 * @param root TreeNode that is wraped to Document
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean parseToDocument(Document doc, ArchiverTreeNode root)
	{
		this.doc = doc;

		Element e = doc.createElement(descriptors[0]);
		doc.appendChild(e);

		return appendToRootNode(root, e);

		//        appendChildrenToNode(e, root);
	}

	
	private boolean appendToRootNode(ArchiverTreeNode root, Node rootNode)
	{
		if (!rootNode.hasChildNodes()) {
			//            JOptionPane.showMessageDialog(null, "EngineConfig must contain at least one group. \n Parsing aborted!", "Structure invalid", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		for (int i = 0; i < root.getChildCount(); i++) {
			//group or property
			ArchiverTreeNode node = (ArchiverTreeNode)root.getChildAt(i);

			rootNode.appendChild(getIndentNode(BREAK_TYPE));

			Element level1;

			if (node.isLeaf()) {
				// root property
				if (node.getArchiverTreeUserElement() instanceof Property) {
					level1 = doc.createElement(node.getArchiverTreeUserElement()
						    .getName());
					rootNode.appendChild(getIndentNode(INDENT_TYPE));
					level1.appendChild(doc.createTextNode(
					        ((Property)node.getArchiverTreeUserElement())
					        .getValue()));
				} else {
					//                    JOptionPane.showMessageDialog(null, "Group " + node.getArchiverTreeUserElement().getName() + " must contain at least one channel. \n Parsing aborted!", "Structure invalid", JOptionPane.WARNING_MESSAGE);
					return false;
				}
			} else {
				//group 
				level1 = doc.createElement(descriptors[1]);
				level1.appendChild(getIndentNode(BREAK_TYPE));
				level1.appendChild(getIndentNode(INDENT_TYPE));

				// add name node to the group - the tree doesn`t display name as a separate node
				Element nameElement = doc.createElement("name");
				Text nameText = doc.createTextNode(node.getArchiverTreeUserElement()
					    .getName());
				nameElement.appendChild(nameText);
				level1.appendChild(nameElement);

				level1.appendChild(getIndentNode(BREAK_TYPE));

				for (int j = 0; j < node.getChildCount(); j++) {
					//channel
					ArchiverTreeNode channelNode = (ArchiverTreeNode)node
						.getChildAt(j);

					// append name node to the channel - similar to group name
					level1.appendChild(getIndentNode(INDENT_TYPE));

					Element level2 = doc.createElement(descriptors[2]);
					Element channelNameElement = doc.createElement("name");
					Text channelNameText = doc.createTextNode(channelNode.getArchiverTreeUserElement()
						    .getName());
					channelNameElement.appendChild(channelNameText);

					level2.appendChild(getIndentNode(BREAK_TYPE));
					level2.appendChild(getIndentNode(LONG_INDENT_TYPE));
					level2.appendChild(channelNameElement);
					level2.appendChild(getIndentNode(BREAK_TYPE));

					for (int k = 0; k < channelNode.getChildCount(); k++) {
						//channel property
						ArchiverTreeNode channelProperty = (ArchiverTreeNode)channelNode
							.getChildAt(k);
						Element level3 = doc.createElement(channelProperty.getArchiverTreeUserElement()
							    .getName());
						String value = ((Property)channelProperty
							.getArchiverTreeUserElement()).getValue();

						if (value != null) {
							Text level3Text = doc.createTextNode(value);
							level3.appendChild(level3Text);
						}

						level2.appendChild(getIndentNode(LONG_INDENT_TYPE));
						level2.appendChild(level3);
						level2.appendChild(getIndentNode(BREAK_TYPE));
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

	/**
	 */

	//    private void appendChildrenToNode(Node parent, ArchiverTreeNode treeNode) {
	//        
	//        for (int i = 0; i < treeNode.getChildCount(); i++) {
	//            ArchiverTreeNode child = (ArchiverTreeNode) treeNode.getChildAt(i);
	//            ArchiverTreeElement element = child.getArchiverTreeUserElement();
	//            String[] names = getStringRepresentations(element);
	//            parent.appendChild(getIndentNode(BREAK_TYPE));
	//
	//            Element childNode = doc.createElement(names[0]);
	//            
	//            if (!(element instanceof Group)) { 
	//                parent.appendChild(getIndentNode(INDENT_TYPE));
	//            }
	//                        
	//            if (names[1] != null) {
	//                if (element instanceof Property) {
	//                    Text name = doc.createTextNode(names[1]);
	//                    childNode.appendChild(name);
	//                }
	//                else {
	//                    childNode.appendChild(getIndentNode(BREAK_TYPE));
	//                    if (element instanceof Channel) {
	//                        childNode.appendChild(getIndentNode(LONG_INDENT_TYPE));
	//                    } else {
	//                        childNode.appendChild(getIndentNode(INDENT_TYPE));
	//                    }
	//                    
	//                    Element nameNode = doc.createElement("name");
	//                    Text name = doc.createTextNode(names[1]);
	//                    nameNode.appendChild(name);
	//                    
	//                    childNode.appendChild(nameNode);
	//                    
	//                }
	//            }
	//            if (treeNode.getArchiverTreeUserElement() instanceof Channel && element instanceof Property) {
	//                parent.appendChild(getIndentNode(INDENT_TYPE));
	//            }
	//            
	//            parent.appendChild(childNode);
	//            if (!(treeNode.getArchiverTreeUserElement() instanceof EngineConfigRoot)) {
	//                parent.appendChild(getIndentNode(BREAK_TYPE));
	//            }
	//            if (element instanceof Property) {
	//                parent.appendChild(getIndentNode(INDENT_TYPE));
	//            } //else if (treeNode.getArchiverTreeUserElement() instanceof EngineConfigRoot) {
	////                parent.appendChild(getIndentNode(BREAK_TYPE));
	////            }
	//            appendChildrenToNode(childNode, child);
	//            
	//        }
	//        
	//    }
	//    private String[] getStringRepresentations(ArchiverTreeElement element) {
	//        
	//        String[] values = new String[2];
	//        
	//        if (element instanceof EngineConfigRoot) {
	//            values[0] = descriptors[1];
	//            values[1] = null;
	//        } else if (element instanceof Group) {
	//            values[0] = descriptors[0];
	//            values[1] = element.getName();
	//        } else if (element instanceof Channel) {
	//            values[0] = descriptors[2];
	//            values[1] = element.getName();
	//        } else if (element instanceof Property){
	//            values[0] = element.getName();
	//            values[1] = ((Property)element).getValue();
	//        }
	//        
	//        return values;
	//        
	//    }
	//    private String getStringRepresentation(ArchiverTreeElement element) {
	//        if (element instanceof EngineConfigRoot) {
	//            return descriptors[1];
	//        } else if (element instanceof Group) {
	//            return descriptors[0];
	//        } else if (element instanceof Channel) {
	//            return descriptors[2];
	//        } else {
	//            return element.getName();
	//        }
	//    }
}

/* __oOo__ */
