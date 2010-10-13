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
