/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

@SuppressWarnings("nls")
public class ParseNodeTests
{
	static class TextNode extends ParseNode
	{
		private String _text;

		public TextNode(String text)
		{
			super();

			this._text = text;
		}

		public String getLanguage()
		{
			return LANG;
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
	@Test
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
	@Test
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
	@Test
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
	@Test
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
	@Test
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
	@Test
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
	@Test
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
