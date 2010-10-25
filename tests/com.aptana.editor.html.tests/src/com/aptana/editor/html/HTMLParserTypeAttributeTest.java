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
