/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.outline;

import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import junit.framework.TestCase;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.aptana.editor.js.parsing.JSParser;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;

/**
 * @author Kevin Lindsey
 */
public abstract class BaseOutlineItemTest extends TestCase
{
	private JSParser fParser;
	private ParseState fParseState;

	@Override
	protected void setUp() throws Exception
	{
		fParser = new JSParser();
		fParseState = new ParseState();
	}

	protected IParseNode getParseResults(String source)
	{
		fParseState.setEditState(source, source, 0, 0);
		try
		{
			fParser.parse(fParseState);
			return fParseState.getParseResult();
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
				writer.append(" label='").append(label).append("'");

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
