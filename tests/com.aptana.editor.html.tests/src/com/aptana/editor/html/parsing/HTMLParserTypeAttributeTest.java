/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.parsing;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.parsing.ast.IParseNode;

public class HTMLParserTypeAttributeTest
{

	private static final String EOL = "\n";

	private HTMLParser fParser;
	private HTMLParseState fParseState;

	@Before
	public void setUp() throws Exception
	{
		fParser = new HTMLParser();
	}

	@After
	public void tearDown() throws Exception
	{
		fParser = null;
	}

	@Test
	public void testStyleWithTypeEmpty() throws Exception
	{
		String source = "<html><head><style>html {color: red;}</style></head></html>";
		parseTest(source, source + EOL);
	}

	@Test
	public void testStyleWithTypeCSS() throws Exception
	{
		String source = "<html><head><style type=\"text/css\">html {color: red;}</style></head></html>";
		parseTest(source, source + EOL);
	}

	@Test
	public void testStyleWithTypeJS() throws Exception
	{
		String source = "<html><head><style type=\"text/javascript\">var one = 1;</style></head></html>";
		parseTest(source, source + EOL);
	}

	@Test
	public void testStyleWithInvalidType() throws Exception
	{
		String source = "<html><head><style type=\"css\">html {color: red;}</style></head></html>";
		parseTest(source, source + EOL);
	}

	@Test
	public void testScriptWithTypeEmpty() throws Exception
	{
		String source = "<html><head><script>var one = 1;</script></head></html>";
		parseTest(source, source + EOL);
	}

	@Test
	public void testScriptWithTypeApplicationJS() throws Exception
	{
		String source = "<html><head><script type=\"application/javascript\">var one = 1;</script></head></html>";
		parseTest(source, source + EOL);
	}

	@Test
	public void testScriptWithTypeApplicationEcma() throws Exception
	{
		String source = "<html><head><script type=\"application/ecmascript\">var one = 1;</script></head></html>";
		parseTest(source, source + EOL);
	}

	@Test
	public void testScriptWithTypeApplicationXJS() throws Exception
	{
		String source = "<html><head><script type=\"application/x-javascript\">var one = 1;</script></head></html>";
		parseTest(source, source + EOL);
	}

	@Test
	public void testScriptWithTypeApplicationXEcma() throws Exception
	{
		String source = "<html><head><script type=\"application/x-ecmascript\">var one = 1;</script></head></html>";
		parseTest(source, source + EOL);
	}

	@Test
	public void testScriptWithTypeTextJS() throws Exception
	{
		String source = "<html><head><script type=\"text/javascript\">var one = 1;</script></head></html>";
		parseTest(source, source + EOL);
	}

	@Test
	public void testScriptWithTypeTextEcma() throws Exception
	{
		String source = "<html><head><script type=\"text/ecmascript\">var one = 1;</script></head></html>";
		parseTest(source, source + EOL);
	}

	@Test
	public void testScriptWithTypeTextJScript() throws Exception
	{
		String source = "<html><head><script type=\"text/jscript\">var one = 1;</script></head></html>";
		parseTest(source, source + EOL);
	}

	@Test
	public void testScriptWithTypeVersion() throws Exception
	{
		String source = "<html><head><script type=\"text/javascript;version=1.5\">var one = 1;</script></head></html>";
		parseTest(source, source + EOL);
	}

	@Test
	public void testScriptWithLanguageJS() throws Exception
	{
		String source = "<html><head><script language=\"JavaScript\">var one = 1;</script></head></html>";
		parseTest(source, source + EOL);
	}

	@Test
	public void testScriptWithLanguageVersion() throws Exception
	{
		String source = "<html><head><script language=\"JavaScript1.5\">var one = 1;</script></head></html>";
		parseTest(source, source + EOL);
	}

	@Test
	public void testScriptWithInvalidType() throws Exception
	{
		String source = "<html><head><script type=\"javascript\">var one = 1;</script></head></html>";
		parseTest(source, source + EOL);
	}

	protected void parseTest(String source) throws Exception
	{
		parseTest(source, source);
	}

	protected void parseTest(String source, String expected) throws Exception
	{
		fParseState = new HTMLParseState(source);
		IParseNode result = fParser.parse(fParseState).getRootNode();

		StringBuilder text = new StringBuilder();
		IParseNode[] children = result.getChildren();
		for (IParseNode child : children)
		{
			text.append(child).append("\n");
		}
		assertEquals(expected, text.toString());
	}
}
