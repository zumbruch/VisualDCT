package com.cosylab.vdct.xml;

import java.io.*;
import java.net.*;
import java.util.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

import org.apache.crimson.tree.XmlDocument;
//import com.sun.xml.tree.*;

/**
* Insert the class' description here.
* Creation date: (6.12.2001 21:54:33)
* @author: 
*/
public final class XMLManager
{
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 21:54:51)
 * @param
 * @return
 */
public static void addDataNodeTo(Document doc, Element parentNode, String newNodeName, String newNodeData)
{
	Element node = (Element)doc.createElement(newNodeName);

	node.appendChild(doc.createTextNode(newNodeData));
	parentNode.appendChild(node);
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 21:54:51)
 * @param
 * @return
 */
public static Node findNode(Node node, String name)
{

	if(node.getNodeName().equals(name))
		return node;

	if (node.hasChildNodes())
	{
		NodeList list = node.getChildNodes();
		int size = list.getLength();

		for (int i = 0; i < size; i++)
		{
			Node found = findNode(list.item(i), name);
			if (found!=null)
				return found;
		}
	}

	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 21:56:40)
 * @param
 * @return
 */
private static DocumentBuilder getDocumentBuilder(final String dtdSymbol, final URL dtdUrl)
{

	DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	docBuilderFactory.setValidating(true);

	DocumentBuilder docBuilder = null;

	try
	{
		docBuilder = docBuilderFactory.newDocumentBuilder();
	}
	catch (Exception e)
	{
		com.cosylab.vdct.Console.getInstance().println("An error occured while trying to create new XML document builder!");
		com.cosylab.vdct.Console.getInstance().println(e.toString());
		return null;
	}

	docBuilder.setEntityResolver(
		
		new EntityResolver() {
			
			public InputSource resolveEntity(String publicId, String systemId)
			{
				if (dtdSymbol!=null && systemId.endsWith(dtdSymbol))
				{
					// Replacing systemId with dtdUrl
					try
					{
						Reader reader = new InputStreamReader(dtdUrl.openStream());
						return new InputSource(reader);
					}
					catch( Exception e )
					{
						com.cosylab.vdct.Console.getInstance().println("An error occured while trying to resolve the main DTD!");
						com.cosylab.vdct.Console.getInstance().println(e);
						return null;
					}
				}
				else
					return null;
			}
		}
	);

	return docBuilder;
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:00:32)
 * @param
 * @return
 */
public static String getNodeAttribute(Node node, String name)
{
	if (node instanceof Element)
		return ((Element)node).getAttribute(name);
		
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:00:48)
 * @param
 * @return
 */
public static String getNodeValue(Node node)
{
	if (node.getFirstChild()==null)
		return "";
	else
		return node.getFirstChild().getNodeValue();
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:01:39)
 * @param
 * @return
 */
public static Document newDocument()
{
	try
	{
		return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
	}
	catch( Exception e )
	{
		com.cosylab.vdct.Console.getInstance().println("An error occured while trying to create new XML document!");
		com.cosylab.vdct.Console.getInstance().println(e);
		return null;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:02:24)
 * @param
 * @return
 */
public static Document readFileDocument(String filename) throws IOException, SAXException, ParserConfigurationException
{
	return readFileDocument(filename, null, null);
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:02:24)
 * @param
 * @return
 */
public static Document readFileDocument(String fileName, String dtdSymbol, URL dtdUrl) throws IOException, SAXException, ParserConfigurationException
{
	return getDocumentBuilder(dtdSymbol, dtdUrl).parse(new File(fileName));
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:03:02)
 * @param
 * @return
 */
public static Document readResourceDocument(String resource) throws IOException, SAXException, ParserConfigurationException
{
	return readResourceDocument(resource, null, null);
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:03:15)
 * @param
 * @return
 */
public static Document readResourceDocument(String resource, String dtdSymbol, URL dtdUrl) throws IOException, SAXException, ParserConfigurationException
{
	return getDocumentBuilder(dtdSymbol, dtdUrl).parse(resource);
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:03:47)
 * @param
 * @return
 */
public static void writeDocument(String fileName, Document doc, String publicId, String systemId, String dtd) throws IOException
{
	OutputStream out = new BufferedOutputStream(new FileOutputStream(fileName));
	
	((XmlDocument)doc).setDoctype(publicId, systemId, dtd);
	((XmlDocument)doc).write(out);

	out.flush();
	out.close();
}
}
