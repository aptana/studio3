/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml;

import junit.framework.TestCase;

import com.aptana.editor.xml.parsing.XMLParser;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;

public class XMLParserTest extends TestCase
{

	private XMLParser fParser;

	protected void setUp() throws Exception
	{
		fParser = new XMLParser();
	}

	protected void tearDown() throws Exception
	{
		fParser = null;
	}

	public void testSelfClosing() throws Exception
	{
		String source = "<html/>\n";
		parseTest(source, "<html></html>\n");
	}

	public void testTags() throws Exception
	{
		String source = "<html><head></head><body><p>Text</p></html>\n";
		parseTest(source, "<html><head></head><body><p></p></body></html>\n");
	}

	protected void parseTest(String source) throws Exception
	{
		parseTest(source, source);
	}

	protected void parseTest(String source, String expected) throws Exception
	{
		ParseState parseState = new ParseState();
		parseState.setEditState(source, source, 0, 0);
		IParseNode result = fParser.parse(parseState);

		StringBuilder text = new StringBuilder();
		IParseNode[] children = result.getChildren();
		for (IParseNode child : children)
		{
			text.append(child).append("\n");
		}
		assertEquals(expected, text.toString());
	}
}
