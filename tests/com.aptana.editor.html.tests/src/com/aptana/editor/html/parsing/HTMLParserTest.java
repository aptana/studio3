/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.MessageFormat;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.build.IProblem;
import com.aptana.core.util.FileUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.css.core.ICSSConstants;
import com.aptana.css.core.parsing.ast.CSSParseRootNode;
import com.aptana.editor.html.core.IHTMLConstants;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
import com.aptana.js.core.IJSConstants;
import com.aptana.js.core.parsing.ast.JSParseRootNode;
import com.aptana.parsing.ParseResult;
import com.aptana.parsing.ast.INameNode;
import com.aptana.parsing.ast.IParseError;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseNodeAttribute;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.parsing.ast.ParseNode;
import com.aptana.parsing.lexer.Range;

public class HTMLParserTest
{
	private static final String EOL = FileUtil.NEW_LINE;

	private static final String[] JS_VALID_TYPE_ATTR = new String[] { "application/javascript",
			"application/ecmascript", "application/x-javascript", "application/x-ecmascript", "text/javascript",
			"text/ecmascript", "text/jscript" };

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
	public void testSelfClosing() throws Exception
	{
		String source = "<html/>";
		parseTest(source, "<html></html>\n");
	}

	@Test
	public void testTags() throws Exception
	{
		String source = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">"
				+ "<html><head></head><body><p>Text</html>";
		parseTest(source, "<html><head></head><body><p>Text</p></body></html>\n");
	}

	@Test
	public void testEmptyTagInXHTML() throws Exception
	{
		String source = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n"
				+ "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" + "<body><br /><table></table></body>";
		parseTest(source, "<body><br></br><table></table></body>\n");
	}

	@Test
	public void testQuotedPair() throws Exception
	{
		String source = "<html><head>shouldn't</head><body>can't</body></html>";
		parseTest(source, source + EOL);
	}

	@Test
	public void testAmpersand() throws Exception
	{
		String source = "<body><p>Gifts&nbsp; & Wish Lists</p><h3></h3></body>";
		parseTest(source, source + EOL);
	}

	@Test
	public void testOutlineAttributes() throws Exception
	{
		String source = "<html id=\"aptana\" class=\"cool\" height=\"100\">";
		fParseState = new HTMLParseState(source);

		IParseNode result = parse();
		IParseNode[] children = result.getChildren();
		assertEquals(1, children.length);
		assertEquals("html#aptana.cool", children[0].getText());
	}

	private IParseRootNode parse() throws Exception
	{
		return fParser.parse(fParseState).getRootNode();
	}

	@Test
	public void testNameNode() throws Exception
	{
		String source = "<html><head></head></html>\n";
		fParseState = new HTMLParseState(source);

		IParseNode result = parse();
		IParseNode[] children = result.getChildren();
		INameNode nameNode = children[0].getNameNode();
		assertEquals("html", nameNode.getName());
		assertEquals(new Range(0, 5), nameNode.getNameRange());
	}

	@Test
	public void testStyle() throws Exception
	{
		String source = "<html><head><style>html {color: red;}</style></head></html>";
		parseTest(source, source + EOL);
	}

	@Test
	public void testScript() throws Exception
	{
		String source = "<html><head><script>var one = 1;</script></head></html>";
		parseTest(source, source + EOL);
	}

	@Test
	public void testHTML5() throws Exception
	{
		String source = "<HTML><HEAD><STYLE>html {color: red;}</STYLE><SCRIPT>var one = 1;</SCRIPT></HEAD></HTML>";
		parseTest(source, source + EOL);
	}

	@Test
	public void testComment() throws Exception
	{
		String source = "<html><head><!-- this is a comment --></head></html>";
		parseTest(source, source + EOL);
	}

	@Test
	public void testNestedUnclosedTag() throws Exception
	{
		String source = "<p><b></b><p>";
		parseTest(source, "<p><b></b></p>\n<p></p>" + EOL);
	}

	@Test
	public void testUnclosedTags() throws Exception
	{
		String source = "<body><p><li></body>";
		parseTest(source, "<body><p><li></li></p></body>" + EOL);
	}

	@Test
	public void testCloseTagPosition() throws Exception
	{
		String source = "<body><p>text</body>";
		fParseState = new HTMLParseState(source);
		IParseNode result = parse();
		IParseNode[] children = result.getChildren();
		assertEquals(1, children.length);
		assertEquals(19, children[0].getEndingOffset());
		INameNode endTag = ((HTMLElementNode) children[0]).getEndNode();
		assertEquals(new Range(13, 19), endTag.getNameRange());

		children = children[0].getChildren();
		assertEquals(1, children.length);
		assertEquals(12, children[0].getEndingOffset());
		endTag = ((HTMLElementNode) children[0]).getEndNode();
		assertEquals(new Range(12, 12), endTag.getNameRange());
	}

	@Test
	public void testUnclosedRootTag() throws Exception
	{
		String source = "<body><p>text";
		fParseState = new HTMLParseState(source);
		IParseNode result = parse();
		IParseNode[] children = result.getChildren();
		assertEquals(1, children.length);
		assertEquals(12, children[0].getEndingOffset());
		INameNode endTag = ((HTMLElementNode) children[0]).getEndNode();
		assertEquals(new Range(12, 12), endTag.getNameRange());

		children = children[0].getChildren();
		assertEquals(1, children.length);
		assertEquals(12, children[0].getEndingOffset());
		endTag = ((HTMLElementNode) children[0]).getEndNode();
		assertEquals(new Range(12, 12), endTag.getNameRange());
	}

	@Test
	public void testSpecialNodeEnd() throws Exception
	{
		String source = "<script>var one = 1;</script>";
		fParseState = new HTMLParseState(source);
		IParseNode result = parse();
		IParseNode[] children = result.getChildren();
		assertEquals(1, children.length);
		INameNode endTag = ((HTMLElementNode) children[0]).getEndNode();
		assertNotNull(endTag);
		assertEquals(new Range(20, 28), endTag.getNameRange());
	}

	@Test
	public void testAPSTUD3191() throws Exception
	{
		String source = "<html>\n" + //
				"  <head>\n" + //
				"    <script>\n" + //
				"/* JS comment */\n" + //
				"    </script>\n" + //
				"    <style>\n" + //
				"/* CSS comment */\n" + //
				"    </style>\n" + //
				"  </head>\n" + //
				"</html>"; //
		fParseState = new HTMLParseState(source);
		IParseNode result = parse();
		IParseNode[] children = result.getChildren();

		HTMLElementNode html = (HTMLElementNode) children[0];
		HTMLElementNode head = (HTMLElementNode) html.getChild(1);
		HTMLElementNode script = (HTMLElementNode) head.getChild(1);
		HTMLElementNode style = (HTMLElementNode) head.getChild(3);

		// Check JS Comment node offsets
		JSParseRootNode jsRootNode = (JSParseRootNode) script.getChild(0);
		assertEquals(29, jsRootNode.getCommentNodes()[0].getStartingOffset());
		assertEquals(44, jsRootNode.getCommentNodes()[0].getEndingOffset());

		// Check CSS comment node offsets
		CSSParseRootNode cssRootNode = (CSSParseRootNode) style.getChild(0);
		assertEquals(72, cssRootNode.getCommentNodes()[0].getStartingOffset());
		assertEquals(88, cssRootNode.getCommentNodes()[0].getEndingOffset());
	}

	@Test
	public void testMissingEndTagError() throws Exception
	{
		String source = "<title><body><div><p></body>";
		fParseState = new HTMLParseState(source);
		ParseResult parseResult = fParser.parse(fParseState);

		List<IParseError> errors = parseResult.getErrors();
		// should contain only two errors, for unclosed <title> and <div>, but not <p> since </p> is optional
		assertEquals(2, errors.size());

		IParseError divError = errors.get(0);
		assertEquals(IProblem.Severity.WARNING, divError.getSeverity());
		assertEquals(13, divError.getOffset());
		assertEquals(MessageFormat.format(Messages.HTMLParser_missing_end_tag_error, "div"), divError.getMessage());

		IParseError titleError = errors.get(1);
		assertEquals(IProblem.Severity.WARNING, titleError.getSeverity());
		assertEquals(0, titleError.getOffset());
		assertEquals(MessageFormat.format(Messages.HTMLParser_missing_end_tag_error, "title"), titleError.getMessage());
	}

	@Test
	public void testTypeAttributeForStyle() throws Exception
	{
		String source = "<style type=\"text/css\">html {color: red;}</style>";
		fParseState = new HTMLParseState(source);
		IParseNode result = parse();
		IParseNode styleTag = result.getChild(0);
		IParseNode cssNode = styleTag.getChild(0);

		// should be a CSS node
		assertEquals(ICSSConstants.CONTENT_TYPE_CSS, cssNode.getLanguage());
	}

	@Test
	public void testIncorrectTypeAttributeForStyle() throws Exception
	{
		String source = "<style type=\"text/incorrect\">html {color: red;}</style>";
		fParseState = new HTMLParseState(source);
		IParseNode result = parse();
		IParseNode styleTag = result.getChild(0);
		IParseNode textNode = styleTag.getChild(0);

		// should remain a HTML node
		assertEquals(IHTMLConstants.CONTENT_TYPE_HTML, textNode.getLanguage());
	}

	@Test
	public void testTypeAttributeForScript() throws Exception
	{
		for (String type : JS_VALID_TYPE_ATTR)
		{
			String source = "<script type=\"" + type + "\">var one = 1;</script>";
			fParseState = new HTMLParseState(source);
			IParseNode result = parse();
			IParseNode scriptTag = result.getChild(0);
			IParseNode jsNode = scriptTag.getChild(0);

			// should be a JS node
			assertEquals(IJSConstants.CONTENT_TYPE_JS, jsNode.getLanguage());
		}
	}

	@Test
	public void testIncorrectTypeAttributeForScript() throws Exception
	{
		String source = "<script type=\"text/incorrect\">var one = 1;</script>";
		fParseState = new HTMLParseState(source);
		IParseNode result = parse();
		IParseNode scriptTag = result.getChild(0);
		IParseNode textNode = scriptTag.getChild(0);

		// should remain a HTML node
		assertEquals(IHTMLConstants.CONTENT_TYPE_HTML, textNode.getLanguage());
	}

	@Test
	public void testAttributeWithNoValue() throws Exception
	{
		String source = "<li div=>item 1</li>";
		fParseState = new HTMLParseState(source);
		IParseNode result = parse();
		IParseNode[] children = result.getChildren();
		assertEquals(1, children.length);

		IParseNodeAttribute[] attrs = children[0].getAttributes();
		assertEquals("attribute count", 1, attrs.length);
		assertEquals("attribute name", "div", attrs[0].getName());
		assertEquals("attribute value", StringUtil.EMPTY, attrs[0].getValue());
	}

	@Test
	public void testAttributeWithNoValueOrEquals() throws Exception
	{
		String source = "<li div>item 1</li>";
		fParseState = new HTMLParseState(source);
		IParseNode result = parse();
		IParseNode[] children = result.getChildren();
		assertEquals(1, children.length);

		IParseNodeAttribute[] attrs = children[0].getAttributes();
		assertEquals("attribute count", 1, attrs.length);
		assertEquals("attribute name", "div", attrs[0].getName());
		assertEquals("attribute value", StringUtil.EMPTY, attrs[0].getValue());
	}

	@Test
	public void testNestedOptionalEndTag() throws Exception
	{
		String source = "<li>item 1<li>item 2";
		fParseState = new HTMLParseState(source);
		IParseNode result = parse();
		IParseNode[] children = result.getChildren();
		assertEquals(2, children.length);

		assertEquals(9, children[0].getEndingOffset());
		INameNode endTag = ((HTMLElementNode) children[0]).getEndNode();
		assertNotNull(endTag);
		assertEquals(new Range(9, 9), endTag.getNameRange());

		assertEquals(19, children[1].getEndingOffset());
		endTag = ((HTMLElementNode) children[1]).getEndNode();
		assertNotNull(endTag);
		assertEquals(new Range(19, 19), endTag.getNameRange());
	}

	/**
	 * This method is not being used for formal testing, but it's useful to determine how effective
	 * {@link ParseNode#trimToSize()} is.
	 * 
	 * @throws Exception
	 */
	// public void trimToSize() throws Exception
	// {
	// fParseState = new HTMLParseState(getSource("performance/amazon.html"));
	//
	// ASTUtil.showBeforeAndAfterTrim(parse());
	// }

	protected void parseTest(String source) throws Exception
	{
		parseTest(source, source);
	}

	protected void parseTest(String source, String expected) throws Exception
	{
		fParseState = new HTMLParseState(source);
		IParseNode result = parse();

		StringBuilder text = new StringBuilder();
		IParseNode[] children = result.getChildren();
		for (IParseNode child : children)
		{
			text.append(child).append("\n");
		}
		assertEquals(expected, text.toString());
	}
}
