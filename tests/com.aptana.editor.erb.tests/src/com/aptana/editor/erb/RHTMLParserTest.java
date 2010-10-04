package com.aptana.editor.erb;

import junit.framework.TestCase;

import com.aptana.editor.erb.html.parsing.RHTMLParser;
import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.editor.ruby.parsing.IRubyParserConstants;
import com.aptana.parsing.ast.IParseNode;

public class RHTMLParserTest extends TestCase
{

	private RHTMLParser fParser;

	private HTMLParseState fParseState;

	@Override
	protected void setUp() throws Exception
	{
		fParser = new RHTMLParser();
		fParseState = new HTMLParseState();
	}

	@Override
	protected void tearDown() throws Exception
	{
		fParser = null;
	}

	@SuppressWarnings("nls")
	public void testTopLevelERB() throws Exception
	{
		String source = "<% content_for :stylesheets do %>\n" + "<%= stylesheet_link_tag 'rails' %>\n"
				+ "<style></style>\n" + "<%= javascript_include_tag 'slidedeck/slidedeck.jquery.js' %>\n"
				+ "<script></script>\n" + "<% end %>\n";
		fParseState.setEditState(source, source, 0, 0);

		IParseNode result = fParser.parse(fParseState);
		IParseNode[] children = result.getChildren();
		assertEquals(6, children.length);
		assertEquals(IRubyParserConstants.LANGUAGE, children[0].getLanguage());
		assertEquals(IRubyParserConstants.LANGUAGE, children[1].getLanguage());
		assertEquals(3, children[2].getNodeType()); // HTMLSpecialNode
		assertEquals(IRubyParserConstants.LANGUAGE, children[3].getLanguage());
		assertEquals(3, children[4].getNodeType()); // HTMLSpecialNode
		assertEquals(IRubyParserConstants.LANGUAGE, children[5].getLanguage());
	}

	@SuppressWarnings("nls")
	public void testNestedERB() throws Exception
	{
		String source = "<p>Welcome to <em><%= ENV['SERVER_NAME'] %></em>. If you see a server name, <%= 'e' + 'Ruby' %> is probably working.</p>\n";
		fParseState.setEditState(source, source, 0, 0);

		IParseNode result = fParser.parse(fParseState);
		IParseNode[] children = result.getChildren(); // <p></p>
		assertEquals(1, children.length);
		assertEquals(2, children[0].getNodeType()); // HTMLElementNode
		children = children[0].getChildren(); // <em></em><%= %>
		assertEquals(2, children.length);
		assertEquals(2, children[0].getNodeType()); // HTMLElementNode
		assertEquals(IRubyParserConstants.LANGUAGE, children[1].getLanguage());
		children = children[0].getChildren(); // <%= %>
		assertEquals(1, children.length);
		assertEquals(IRubyParserConstants.LANGUAGE, children[0].getLanguage());
	}

	@SuppressWarnings("nls")
	public void testDoubleERBBeforeTagClose() throws Exception
	{
		String source = "<table><tr></tr><% content_for :table %><% end %></table>";
		fParseState.setEditState(source, source, 0, 0);

		IParseNode result = fParser.parse(fParseState);
		IParseNode[] children = result.getChildren(); // <table></table>
		assertEquals(1, children.length);
		children = children[0].getChildren(); // <tr></tr><% %><% %>
		assertEquals(3, children.length);
		assertEquals(2, children[0].getNodeType()); // HTMLElementNode
		assertEquals(IRubyParserConstants.LANGUAGE, children[1].getLanguage());
		assertEquals(IRubyParserConstants.LANGUAGE, children[2].getLanguage());
	}
}
