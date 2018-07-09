/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.outline;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Before;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.aptana.core.util.StringUtil;
import com.aptana.js.core.parsing.GraalJSParser;
import com.aptana.parsing.IParser;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;

/**
 * @author Kevin Lindsey
 */
public abstract class OutlineItemTestCase
{
	private IParser fParser;

	@Before
	public void setUp() throws Exception
	{
		fParser = new GraalJSParser();
	}

	protected IParseNode getParseResults(String source)
	{
		ParseState parseState = new ParseState(source);
		try
		{
			return fParser.parse(parseState).getRootNode();
		}
		catch (Exception e)
		{
		}
		return null;
	}

	protected String getXML(String source)
	{
		IParseNode root = getParseResults(source);
		JSOutlineContentProvider provider = new JSOutlineContentProvider();

		StringBuilder text = new StringBuilder();
		text.append("<?xml version='1.0'?>");
		text.append("<outline>");
		buildXML(provider.getElements(root), text, provider);
		text.append("</outline>");

		return text.toString();
	}

	protected Node getNode(String source, String expression)
	{
		return (Node) getType(source, expression, XPathConstants.NODE);
	}

	protected double getNumber(String source, String expression)
	{
		return ((Double) getType(source, expression, XPathConstants.NUMBER)).doubleValue();
	}

	protected String getString(String source, String expression)
	{
		return (String) getType(source, expression, XPathConstants.STRING);
	}

	protected void testItem(String source, String xpath, String label)
	{
		testItem(source, xpath, label, 0);
	}

	/**
	 * Make sure there is only one element for the given xpath. Check that element's label and make sure it has the
	 * specified number of children
	 * 
	 * @param source
	 *            The JS source code to test
	 * @param xpath
	 *            The XPath selector for the element we're interested in
	 * @param label
	 *            The expected text on the element's item in the outline
	 * @param childCount
	 *            The expected number of children for the element
	 */
	protected void testItem(String source, String xpath, String label, int childCount)
	{
		String countPath = "count(" + xpath + ")";
		String labelPath = xpath + "/@label";

		// make sure there is only one object at the specified path
		double count = getNumber(source, countPath);
		assertEquals(1, count, 0);

		// grab the element
		Element element = (Element) getNode(source, xpath);

		// count how many children are elements
		NodeList children = element.getChildNodes();
		int elementCount = 0;
		Node child;
		for (int i = 0; i < children.getLength(); ++i)
		{
			child = children.item(i);

			if (child.getNodeType() == Node.ELEMENT_NODE)
			{
				elementCount++;
			}
		}
		// assert that we have the expected number of children
		assertEquals(childCount, elementCount);

		// assert that we have the expected label
		assertEquals(label, getString(source, labelPath));
	}

	private Object getType(String source, String expression, QName type)
	{
		String xml = getXML(source);
		InputSource inputSource = new InputSource(new StringReader(xml));
		XPath xpath = XPathFactory.newInstance().newXPath();
		try
		{
			return xpath.evaluate(expression, inputSource, type);
		}
		catch (XPathExpressionException e)
		{
		}
		return null;
	}

	private static void buildXML(Object[] nodes, StringBuilder writer, JSOutlineContentProvider provider)
	{
		for (Object node : nodes)
		{
			if (node instanceof JSOutlineItem)
			{
				JSOutlineItem item = (JSOutlineItem) node;
				String label = item.getLabel();
				String name;
				switch (item.getType())
				{
					case ARRAY:
						name = "array-literal";
						break;
					case BOOLEAN:
						name = "boolean";
						break;
					case FUNCTION:
						name = "function";
						break;
					case NULL:
						name = "null";
						break;
					case NUMBER:
						name = "number";
						break;
					case OBJECT_LITERAL:
						name = "object-literal";
						break;
					case REGEX:
						name = "regex";
						break;
					case STRING:
						name = "string";
						break;
					default:
						name = "property";
				}
				writer.append("<").append(name);
				writer.append(" label='").append(StringUtil.sanitizeHTML(label)).append("'");

				if (item.getChildrenCount() > 0)
				{
					writer.append(">");
					buildXML(provider.getChildren(node), writer, provider);
					writer.append("</").append(name).append(">");
				}
				else
				{
					writer.append("/>");
				}
			}
		}
	}
}
