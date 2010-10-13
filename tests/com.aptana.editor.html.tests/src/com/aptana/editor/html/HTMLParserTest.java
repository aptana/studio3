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
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
import com.aptana.parsing.ast.INameNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.Range;

public class HTMLParserTest extends TestCase
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

	public void testSelfClosing() throws Exception
	{
		String source = "<html/>\n";
		parseTest(source, "<html></html>\n");
	}

	public void testTags() throws Exception
	{
		String source = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n"
				+ "<html><head></head><body><p>Text</html>\n";
		parseTest(source, "<html><head></head><body><p></p></body></html>\n");
	}

	public void testEmptyTagInXHTML() throws Exception
	{
		String source = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n"
				+ "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
				+ "<body><br /><table></table></body>\n";
		parseTest(source, "<body><br></br><table></table></body>\n");
	}

	public void testQuotedPair() throws Exception
	{
		String source = "<html><head>shouldn't</head><body>can't</body></html>\n";
		parseTest(source, "<html><head></head><body></body></html>\n");
	}

	public void testAmpersand() throws Exception
	{
		String source = "<body><p>Gifts&nbsp; & Wish Lists</p><h3></h3></body>\n";
		parseTest(source, "<body><p></p><h3></h3></body>\n");
	}

	public void testOutlineAttributes() throws Exception
	{
		String source = "<html id=\"aptana\" class=\"cool\" height=\"100\">";
		fParseState.setEditState(source, source, 0, 0);

		IParseNode result = fParser.parse(fParseState);
		IParseNode[] children = result.getChildren();
		assertEquals(1, children.length);
		assertEquals("html#aptana.cool", children[0].getText());
	}

	public void testNameNode() throws Exception
	{
		String source = "<html><head></head></html>\n";
		fParseState.setEditState(source, source, 0, 0);

		IParseNode result = fParser.parse(fParseState);
		IParseNode[] children = result.getChildren();
		INameNode nameNode = children[0].getNameNode();
		assertEquals("html", nameNode.getName());
		assertEquals(new Range(0, 5), nameNode.getNameRange());
	}

	public void testStyle() throws Exception
	{
		String source = "<html><head><style>html {color: red;}</style></head></html>\n";
		parseTest(source);
	}

	public void testScript() throws Exception
	{
		String source = "<html><head><script>var one = 1;</script></head></html>\n";
		parseTest(source);
	}

	public void testHTML5() throws Exception
	{
		String source = "<HTML><HEAD><STYLE>html {color: red;}</STYLE><SCRIPT>var one = 1;</SCRIPT></HEAD></HTML>\n";
		parseTest(source);
	}

	public void testComment() throws Exception
	{
		String source = "<html><head><!-- this is a comment --></head></html>\n";
		parseTest(source);
	}

	public void testNestedUnclosedTag() throws Exception
	{
		String source = "<p><b></b><p>";
		parseTest(source, "<p><b></b></p>\n<p></p>\n");
	}

	public void testUnclosedTags() throws Exception
	{
		String source = "<body><p><li></body>";
		parseTest(source, "<body><p><li></li></p></body>\n");
	}

	public void testCloseTagPosition() throws Exception
	{
		String source = "<body><p>text</body>";
		fParseState.setEditState(source, source, 0, 0);
		IParseNode result = fParser.parse(fParseState);
		IParseNode[] children = result.getChildren();
		assertEquals(1, children.length);
		assertEquals(19, children[0].getEndingOffset());
		INameNode endTag = ((HTMLElementNode) children[0]).getEndNode();
		assertEquals(new Range(13, 19), endTag.getNameRange());

		children = children[0].getChildren();
		assertEquals(1, children.length);
		assertEquals(12, children[0].getEndingOffset());
		endTag = ((HTMLElementNode) children[0]).getEndNode();
		assertNull(endTag);
	}

	public void testUnclosedRootTag() throws Exception
	{
		String source = "<body><p>text";
		fParseState.setEditState(source, source, 0, 0);
		IParseNode result = fParser.parse(fParseState);
		IParseNode[] children = result.getChildren();
		assertEquals(1, children.length);
		assertEquals(12, children[0].getEndingOffset());
		INameNode endTag = ((HTMLElementNode) children[0]).getEndNode();
		assertNull(endTag);

		children = children[0].getChildren();
		assertEquals(1, children.length);
		assertEquals(12, children[0].getEndingOffset());
		endTag = ((HTMLElementNode) children[0]).getEndNode();
		assertNull(endTag);
	}

	public void testSpecialNodeEnd() throws Exception
	{
		String source = "<script>var one = 1;</script>";
		fParseState.setEditState(source, source, 0, 0);
		IParseNode result = fParser.parse(fParseState);
		IParseNode[] children = result.getChildren();
		assertEquals(1, children.length);
		INameNode endTag = ((HTMLElementNode) children[0]).getEndNode();
		assertNotNull(endTag);
		assertEquals(new Range(20, 28), endTag.getNameRange());
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
