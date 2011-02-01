/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html;

import junit.framework.TestCase;

import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.editor.html.parsing.HTMLParser;
import com.aptana.parsing.ast.IParseNode;

public class HTMLParserTypeAttributeTest extends TestCase
{

	private HTMLParser fParser;
	private HTMLParseState fParseState;

	protected void setUp() throws Exception
	{
		fParser = new HTMLParser();
		fParseState = new HTMLParseState();
	}

	protected void tearDown() throws Exception
	{
		fParser = null;
	}

	public void testStyleWithTypeEmpty() throws Exception
	{
		String source = "<html><head><style>html {color: red;}</style></head></html>\n";
		parseTest(source);
	}

	public void testStyleWithTypeCSS() throws Exception
	{
		String source = "<html><head><style type=\"text/css\">html {color: red;}</style></head></html>\n";
		parseTest(source);
	}

	public void testStyleWithTypeJS() throws Exception
	{
		String source = "<html><head><style type=\"text/javascript\">var one = 1;</style></head></html>\n";
		parseTest(source);
	}

	public void testStyleWithInvalidType() throws Exception
	{
		String source = "<html><head><style type=\"css\">html {color: red;}</style></head></html>\n";
		parseTest(source, "<html><head><style type=\"css\"></style></head></html>\n");
	}

	public void testScriptWithTypeEmpty() throws Exception
	{
		String source = "<html><head><script>var one = 1;</script></head></html>\n";
		parseTest(source);
	}

	public void testScriptWithTypeApplicationJS() throws Exception
	{
		String source = "<html><head><script type=\"application/javascript\">var one = 1;</script></head></html>\n";
		parseTest(source);
	}

	public void testScriptWithTypeApplicationEcma() throws Exception
	{
		String source = "<html><head><script type=\"application/ecmascript\">var one = 1;</script></head></html>\n";
		parseTest(source);
	}

	public void testScriptWithTypeApplicationXJS() throws Exception
	{
		String source = "<html><head><script type=\"application/x-javascript\">var one = 1;</script></head></html>\n";
		parseTest(source);
	}

	public void testScriptWithTypeApplicationXEcma() throws Exception
	{
		String source = "<html><head><script type=\"application/x-ecmascript\">var one = 1;</script></head></html>\n";
		parseTest(source);
	}

	public void testScriptWithTypeTextJS() throws Exception
	{
		String source = "<html><head><script type=\"text/javascript\">var one = 1;</script></head></html>\n";
		parseTest(source);
	}

	public void testScriptWithTypeTextEcma() throws Exception
	{
		String source = "<html><head><script type=\"text/ecmascript\">var one = 1;</script></head></html>\n";
		parseTest(source);
	}

	public void testScriptWithTypeTextJScript() throws Exception
	{
		String source = "<html><head><script type=\"text/jscript\">var one = 1;</script></head></html>\n";
		parseTest(source);
	}

	public void testScriptWithTypeVersion() throws Exception
	{
		String source = "<html><head><script type=\"text/javascript;version=1.5\">var one = 1;</script></head></html>\n";
		parseTest(source);
	}

	public void testScriptWithLanguageJS() throws Exception
	{
		String source = "<html><head><script language=\"JavaScript\">var one = 1;</script></head></html>\n";
		parseTest(source);
	}

	public void testScriptWithLanguageVersion() throws Exception
	{
		String source = "<html><head><script language=\"JavaScript1.5\">var one = 1;</script></head></html>\n";
		parseTest(source);
	}

	public void testScriptWithInvalidType() throws Exception
	{
		String source = "<html><head><script type=\"javascript\">var one = 1;</script></head></html>\n";
		parseTest(source, "<html><head><script type=\"javascript\"></script></head></html>\n");
	}

	protected void parseTest(String source) throws Exception
	{
		parseTest(source, source);
	}

	protected void parseTest(String source, String expected) throws Exception
	{
		fParseState.setEditState(source, source, 0, 0);
		IParseNode result = fParser.parse(fParseState);

		StringBuilder text = new StringBuilder();
		IParseNode[] children = result.getChildren();
		for (IParseNode child : children)
		{
			text.append(child).append("\n");
		}
		assertEquals(expected, text.toString());
	}
}
