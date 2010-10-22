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
package com.aptana.parsing.ast;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class ParseNodeTests extends TestCase
{
	static class TextNode extends ParseNode
	{
		private String _text;
		
		public TextNode(String text)
		{
			super(LANG);
			
			this._text = text;
		}
		
		public String getText()
		{
			return this._text;
		}
	}
	
	private static final String LANG = "text/simple";
	
	/**
	 * testFirstChild
	 */
	public void testFirstChild()
	{
		IParseNode a = new TextNode("A");
		IParseNode b = new TextNode("B");
		IParseNode c = new TextNode("C");
		IParseNode d = new TextNode("D");
		
		a.addChild(b);
		a.addChild(c);
		a.addChild(d);
		
		assertEquals(b, a.getFirstChild());
		assertNull(b.getFirstChild());
		assertNull(c.getFirstChild());
		assertNull(d.getFirstChild());
	}
	
	/**
	 * testLastChild
	 */
	public void testLastChild()
	{
		IParseNode a = new TextNode("A");
		IParseNode b = new TextNode("B");
		IParseNode c = new TextNode("C");
		IParseNode d = new TextNode("D");
		
		a.addChild(b);
		a.addChild(c);
		a.addChild(d);
		
		assertEquals(d, a.getLastChild());
		assertNull(b.getLastChild());
		assertNull(c.getLastChild());
		assertNull(d.getLastChild());
	}
	
	/**
	 * testParent
	 */
	public void testParent()
	{
		IParseNode a = new TextNode("A");
		IParseNode b = new TextNode("B");
		IParseNode c = new TextNode("C");
		IParseNode d = new TextNode("D");
		
		a.addChild(b);
		a.addChild(c);
		a.addChild(d);
		
		assertNull(a.getParent());
		assertEquals(a, b.getParent());
		assertEquals(a, c.getParent());
		assertEquals(a, d.getParent());
	}
	
	/**
	 * testFollowingSibling
	 */
	public void testFollowingSibling()
	{
		IParseNode a = new TextNode("A");
		IParseNode b = new TextNode("B");
		IParseNode c = new TextNode("C");
		IParseNode d = new TextNode("D");
		
		a.addChild(b);
		a.addChild(c);
		a.addChild(d);
		
		assertNull(a.getNextSibling());
		assertEquals(c, b.getNextSibling());
		assertEquals(d, c.getNextSibling());
		assertNull(d.getNextSibling());
	}
	
	/**
	 * testPrecedingSibling
	 */
	public void testPrecedingSibling()
	{
		IParseNode a = new TextNode("A");
		IParseNode b = new TextNode("B");
		IParseNode c = new TextNode("C");
		IParseNode d = new TextNode("D");
		
		a.addChild(b);
		a.addChild(c);
		a.addChild(d);
		
		assertNull(a.getPreviousSibling());
		assertEquals(c, d.getPreviousSibling());
		assertEquals(b, c.getPreviousSibling());
		assertNull(b.getPreviousSibling());
	}
	
	/**
	 * testFollowingNode
	 */
	public void testFollowingNode()
	{
		// create members
		IParseNode a = new TextNode("A");
		IParseNode b = new TextNode("B");
		IParseNode c = new TextNode("C");
		IParseNode d = new TextNode("D");
		IParseNode e = new TextNode("E");
		IParseNode f = new TextNode("F");
		IParseNode g = new TextNode("G");
		IParseNode h = new TextNode("H");
		IParseNode i = new TextNode("I");
		IParseNode j = new TextNode("J");
		IParseNode k = new TextNode("K");
		IParseNode l = new TextNode("L");
		IParseNode m = new TextNode("M");
		
		// build tree
		a.addChild(b);
		a.addChild(f);
		a.addChild(j);
		
		b.addChild(c);
		b.addChild(d);
		b.addChild(e);
		
		f.addChild(g);
		f.addChild(h);
		f.addChild(i);
		
		j.addChild(k);
		j.addChild(l);
		j.addChild(m);
		
		// traverse following
		StringBuilder buffer = new StringBuilder();
		IParseNode current = a;
		
		while (current != null)
		{
			buffer.append(current.getText());
			current = current.getNextNode();
		}
		
		assertEquals("ABCDEFGHIJKLM", buffer.toString());
	}
	
	/**
	 * testPrecedingNode
	 */
	public void testPrecedingNode()
	{
		// create members
		IParseNode a = new TextNode("A");
		IParseNode b = new TextNode("B");
		IParseNode c = new TextNode("C");
		IParseNode d = new TextNode("D");
		IParseNode e = new TextNode("E");
		IParseNode f = new TextNode("F");
		IParseNode g = new TextNode("G");
		IParseNode h = new TextNode("H");
		IParseNode i = new TextNode("I");
		IParseNode j = new TextNode("J");
		IParseNode k = new TextNode("K");
		IParseNode l = new TextNode("L");
		IParseNode m = new TextNode("M");
		
		// build tree
		a.addChild(b);
		a.addChild(f);
		a.addChild(j);
		
		b.addChild(c);
		b.addChild(d);
		b.addChild(e);
		
		f.addChild(g);
		f.addChild(h);
		f.addChild(i);
		
		j.addChild(k);
		j.addChild(l);
		j.addChild(m);
		
		// traverse following
		StringBuilder buffer = new StringBuilder();
		IParseNode current = m;
		
		while (current != null)
		{
			buffer.append(current.getText());
			current = current.getPreviousNode();
		}
		
		assertEquals("MLKJIHGFEDCBA", buffer.toString());
	}
}
