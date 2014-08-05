/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.xml.core.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.build.IProblem;
import com.aptana.parsing.ParseResult;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.INameNode;
import com.aptana.parsing.ast.IParseError;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseNodeAttribute;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.xml.core.parsing.ast.XMLElementNode;
import com.aptana.xml.core.parsing.ast.XMLNodeType;

public class XMLParserTest
{

	private XMLParser fParser;

	@Before
	public void setUp() throws Exception
	{
		fParser = new XMLParser();
	}

	@After
	public void tearDown() throws Exception
	{
		fParser = null;
	}

	@Test
	public void testSelfClosing() throws Exception
	{
		String source = "<html/>\n";
		IParseNode root = parseTest(source, "<html></html>\n");
		assertEquals(1, root.getChildCount());
		IParseNode html = root.getFirstChild();
		assertElement(0, 6, "html", 1, 4, html);
	}

	@Test
	public void testAttributes() throws Exception
	{
		String source = "<html class=\"myClass\" id=\"myId\"/>\n";
		IParseNode root = parseTest(source, "<html></html>\n");
		assertEquals(1, root.getChildCount());
		XMLElementNode html = (XMLElementNode) root.getFirstChild();
		assertElement(0, 32, "html", 1, 4, html);
		IParseNodeAttribute[] attrs = html.getAttributes();
		assertEquals(2, attrs.length);
		assertEquals("myId", html.getAttributeValue("id"));
		assertEquals("myClass", html.getAttributeValue("class"));
	}

	private void assertElement(int start, int end, String name, int nameStart, int nameEnd, IParseNode elementNode)
	{
		assertEquals("startingOffset", start, elementNode.getStartingOffset());
		assertEquals("endingOffset", end, elementNode.getEndingOffset());
		INameNode nameNode = elementNode.getNameNode();
		assertEquals("name", name, nameNode.getName());
		assertEquals("nameStart", nameStart, nameNode.getNameRange().getStartingOffset());
		assertEquals("nameEnd", nameEnd, nameNode.getNameRange().getEndingOffset());
	}

	@Test
	public void testTags() throws Exception
	{
		String source = "<html><head></head><body><p>Text</p></html>\n";
		IParseNode root = parseTest(source, "<html><head></head><body><p></p></body></html>\n");
		assertEquals(1, root.getChildCount());
		IParseNode html = root.getFirstChild();
		assertElement(0, 5, "html", 1, 4, html);
		assertEquals(2, html.getChildCount());
		IParseNode head = html.getFirstChild();
		assertElement(6, 18, "head", 7, 10, head);
		assertEquals(0, head.getChildCount());
		IParseNode body = html.getChild(1);
		assertEquals(1, body.getChildCount());
		assertElement(19, 42, "body", 20, 23, body);
	}

	@Test
	public void testBlah() throws Exception
	{
		// @formatter:off
		String source = "<?xml version=\"1.0\"\n" + 
				"encoding=\"ISO-8859-1\"?>\n" +
				"<note>\n" +
				"<to>Tove</to>\n" +
				"<from>Jani</from>\n" +
				"<heading>Reminder</heading>\n" +
				"<body>Don't forget me this weekend!</body>\n" +
				"</note>";
		// @formatter:on
		IParseNode root = parseTest(source, "<note><to></to><from></from><heading></heading><body></body></note>\n");
		assertEquals(1, root.getChildCount());
		IParseNode note = root.getFirstChild();
		assertElement(44, 160, "note", 45, 48, note);
		assertEquals(4, note.getChildCount());
		IParseNode to = note.getFirstChild();
		assertElement(51, 63, "to", 52, 53, to);
		assertEquals(0, to.getChildCount());
		IParseNode from = note.getChild(1);
		assertEquals(0, from.getChildCount());
		assertElement(65, 81, "from", 66, 69, from);
	}

	@Test
	public void testComment() throws Exception
	{
		String source = "<!-- this is a comment -->";
		ParseState parseState = new ParseState(source);
		IParseRootNode rootNode = fParser.parse(parseState).getRootNode();

		assertEquals(1, rootNode.getChildCount());

		IParseNode[] comments = rootNode.getCommentNodes();
		assertEquals(1, comments.length);
		assertEquals(rootNode.getChild(0), comments[0]);
		assertEquals(XMLNodeType.COMMENT.getIndex(), comments[0].getNodeType());
		assertEquals(0, comments[0].getStartingOffset());
		assertEquals(source.length() - 1, comments[0].getEndingOffset());
		assertEquals(source, comments[0].getText());
	}

	@Test
	public void testCDATA() throws Exception
	{
		String source = "<![CDATA[<author>Appcelerator</author>]]>";
		ParseState parseState = new ParseState(source);
		IParseRootNode rootNode = fParser.parse(parseState).getRootNode();

		assertEquals(1, rootNode.getChildCount());

		IParseNode cdataNode = rootNode.getChild(0);
		assertEquals(XMLNodeType.CDATA.getIndex(), cdataNode.getNodeType());
		assertEquals(0, cdataNode.getStartingOffset());
		assertEquals(source.length() - 1, cdataNode.getEndingOffset());
		assertEquals(source, cdataNode.getText());
	}

	@Test
	public void testUnquotedAttributeValueBeginningWithDigit() throws Exception
	{
		String source = "<note attr=123></note><note attr1=321 attr2=\"something\"></note>";
		ParseState parseState = new ParseState(source);
		ParseResult result = fParser.parse(parseState);

		List<IParseError> errors = new ArrayList<IParseError>(result.getErrors());
		assertEquals(2, errors.size());
		Collections.sort(errors, new Comparator<IParseError>()
		{
			public int compare(IParseError o1, IParseError o2)
			{
				return o1.getOffset() - o2.getOffset();
			}
		});
		assertEquals("Unquoted attribute value", errors.get(0).getMessage());
		assertEquals(IProblem.Severity.ERROR, errors.get(0).getSeverity());
		assertEquals(11, errors.get(0).getOffset());
		assertEquals(3, errors.get(0).getLength());

		assertEquals("Unquoted attribute value", errors.get(1).getMessage());
		assertEquals(IProblem.Severity.ERROR, errors.get(1).getSeverity());
		assertEquals(34, errors.get(1).getOffset());
		assertEquals(3, errors.get(1).getLength());

		IParseRootNode root = result.getRootNode();
		assertNotNull(root);
		XMLElementNode ee = (XMLElementNode) root.getChild(0);
		IParseNodeAttribute[] attrs = ee.getAttributes();
		assertEquals(1, attrs.length);
		assertEquals("123", ee.getAttributeValue("attr"));

		ee = (XMLElementNode) root.getChild(1);
		attrs = ee.getAttributes();
		assertEquals(2, attrs.length);
		assertEquals("321", ee.getAttributeValue("attr1"));
		assertEquals("something", ee.getAttributeValue("attr2"));
	}

	@Test
	public void testAttributeWithNoValue() throws Exception
	{
		String source = "<note attr></note><note attr1 attr2=\"true\"></note>";
		ParseState parseState = new ParseState(source);
		ParseResult result = fParser.parse(parseState);

		List<IParseError> errors = new ArrayList<IParseError>(result.getErrors());
		assertEquals(2, errors.size());
		Collections.sort(errors, new Comparator<IParseError>()
		{
			public int compare(IParseError o1, IParseError o2)
			{
				return o1.getOffset() - o2.getOffset();
			}
		});
		assertEquals("Attribute declared with no value", errors.get(0).getMessage());
		assertEquals(IProblem.Severity.ERROR, errors.get(0).getSeverity());
		assertEquals(6, errors.get(0).getOffset());
		assertEquals(4, errors.get(0).getLength());

		assertEquals("Attribute declared with no value", errors.get(1).getMessage());
		assertEquals(IProblem.Severity.ERROR, errors.get(1).getSeverity());
		assertEquals(24, errors.get(1).getOffset());
		assertEquals(5, errors.get(1).getLength());

		IParseRootNode root = result.getRootNode();
		assertNotNull(root);
		XMLElementNode ee = (XMLElementNode) root.getChild(0);
		IParseNodeAttribute[] attrs = ee.getAttributes();
		assertEquals(1, attrs.length);
		assertEquals("attr", ee.getAttributeValue("attr")); // unquoted is treated like having value of it's own name

		ee = (XMLElementNode) root.getChild(1);
		attrs = ee.getAttributes();
		assertEquals(2, attrs.length);
		assertEquals("attr1", ee.getAttributeValue("attr1")); // unquoted is treated like having value of it's own name
		assertEquals("true", ee.getAttributeValue("attr2"));
	}

	protected IParseNode parseTest(String source) throws Exception
	{
		return parseTest(source, source);
	}

	protected IParseNode parseTest(String source, String expected) throws Exception
	{
		ParseState parseState = new ParseState(source);
		IParseNode result = fParser.parse(parseState).getRootNode();

		StringBuilder text = new StringBuilder();
		IParseNode[] children = result.getChildren();
		for (IParseNode child : children)
		{
			text.append(child).append("\n");
		}
		assertEquals(expected, text.toString());

		return result;
	}
}
