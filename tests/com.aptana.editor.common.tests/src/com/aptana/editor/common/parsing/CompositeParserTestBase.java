/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.parsing;

import org.junit.Test;
import static org.junit.Assert.*;
import junit.framework.TestCase;

import com.aptana.core.util.StringUtil;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.parsing.ast.ParseNode;
import com.aptana.parsing.util.ParseUtil;

/**
 * CompositeParserTestBase
 */
public abstract class CompositeParserTestBase
{
	static interface IMerger
	{
		void merge(IParseRootNode ast, IParseNode[] embeddedNodes);
	}

	static class NamedParseNode extends ParseNode
	{
		private String name;
		private String language;

		public NamedParseNode(String language, String name, int startingOffset, int endingOffset)
		{
			super();
			this.language = language;
			this.name = name;

			setLocation(startingOffset, endingOffset);
		}

		public String getLanguage()
		{
			return language;
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.parsing.ast.ParseNode#getElementName()
		 */
		@Override
		public String getElementName()
		{
			if (!StringUtil.isEmpty(name))
			{
				return getLanguage() + ":" + name;
			}
			else
			{
				return getLanguage();
			}
		}
	}

	static class NamedParseRootNode extends NamedParseNode implements IParseRootNode
	{
		public NamedParseRootNode(String language, int startingOffset, int endingOffset)
		{
			super(language, null, startingOffset, endingOffset);
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.parsing.ast.IParseRootNode#getCommentNodes()
		 */
		public IParseNode[] getCommentNodes()
		{
			// TODO Auto-generated method stub
			return null;
		}
	}

	protected abstract IMerger createMerger();

	protected void merge(IParseRootNode root, IParseNode... nodes)
	{
		IMerger merger = createMerger();

		merger.merge(root, nodes);
	}

	@Test
	public void testNodeBeforeRoot()
	{
		IParseRootNode root = new NamedParseRootNode("html", 5, 4);
		IParseNode php = new NamedParseNode("php", "print", 0, 4);

		merge(root, php);

		assertEquals("Merged tree does not match expected shape", "(html (php:print))", ParseUtil.toTreeString(root));
	}

	@Test
	public void testNodeAfterRoot()
	{
		IParseRootNode root = new NamedParseRootNode("html", 0, -1);

		// create embedded nodes and merge
		IParseNode php = new NamedParseNode("php", "print", 1, 5);
		merge(root, php);

		assertEquals("Merged tree does not match expected shape", "(html (php:print))", ParseUtil.toTreeString(root));
	}

	@Test
	public void testNodeBeforeChild()
	{
		IParseRootNode root = new NamedParseRootNode("html", 0, 9);
		IParseNode child = new NamedParseNode("html", "child", 5, 9);

		// build tree
		root.addChild(child);

		// create embedded nodes and merge
		IParseNode php = new NamedParseNode("php", "print", 0, 4);
		merge(root, php);

		assertEquals("Merged tree does not match expected shape", "(html (php:print) (html:child))",
				ParseUtil.toTreeString(root));
	}

	@Test
	public void testNodeEndTouchesChildStart()
	{
		IParseRootNode root = new NamedParseRootNode("html", 0, 9);
		IParseNode child = new NamedParseNode("html", "child", 5, 9);

		// build tree
		root.addChild(child);

		// create embedded nodes and merge
		IParseNode php = new NamedParseNode("php", "print", 1, 5);
		merge(root, php);

		assertEquals("Merged tree does not match expected shape", "(html (php:print))", ParseUtil.toTreeString(root));
	}

	@Test
	public void testNodeEndInsideChild()
	{
		IParseRootNode root = new NamedParseRootNode("html", 0, 9);
		IParseNode child = new NamedParseNode("html", "child", 5, 9);

		// build tree
		root.addChild(child);

		// create embedded nodes and merge
		IParseNode php = new NamedParseNode("php", "print", 2, 6);
		merge(root, php);

		assertEquals("Merged tree does not match expected shape", "(html (php:print))", ParseUtil.toTreeString(root));
	}

	@Test
	public void testNodeEndTouchesChildEnd()
	{
		IParseRootNode root = new NamedParseRootNode("html", 0, 9);
		IParseNode child = new NamedParseNode("html", "child", 5, 9);

		// build tree
		root.addChild(child);

		// create embedded nodes and merge
		IParseNode php = new NamedParseNode("php", "printing", 2, 9);
		merge(root, php);

		assertEquals("Merged tree does not match expected shape", "(html (php:printing))", ParseUtil.toTreeString(root));
	}

	@Test
	public void testNodeEndAfterChild()
	{
		IParseRootNode root = new NamedParseRootNode("html", 0, 11);
		IParseNode child = new NamedParseNode("html", "child", 5, 9);

		// build tree
		root.addChild(child);

		// create embedded nodes and merge
		IParseNode php = new NamedParseNode("php", "printing", 4, 11);
		merge(root, php);

		assertEquals("Merged tree does not match expected shape", "(html (php:printing))", ParseUtil.toTreeString(root));
	}

	@Test
	public void testNodeStartTouchesChildStart()
	{
		IParseRootNode root = new NamedParseRootNode("html", 0, 12);
		IParseNode child = new NamedParseNode("html", "child", 5, 9);

		// build tree
		root.addChild(child);

		// create embedded nodes and merge
		IParseNode php = new NamedParseNode("php", "printing", 5, 12);
		merge(root, php);

		assertEquals("Merged tree does not match expected shape", "(html (php:printing))", ParseUtil.toTreeString(root));
	}

	@Test
	public void testNodeStartInsideChild()
	{
		IParseRootNode root = new NamedParseRootNode("html", 0, 13);
		IParseNode child = new NamedParseNode("html", "child", 5, 9);

		// build tree
		root.addChild(child);

		// create embedded nodes and merge
		IParseNode php = new NamedParseNode("php", "printing", 6, 13);
		merge(root, php);

		assertEquals("Merged tree does not match expected shape", "(html (php:printing))", ParseUtil.toTreeString(root));
	}

	@Test
	public void testNodeStartTouchesChildEnd()
	{
		IParseRootNode root = new NamedParseRootNode("html", 0, 16);
		IParseNode child = new NamedParseNode("html", "child", 5, 9);

		// build tree
		root.addChild(child);

		// create embedded nodes and merge
		IParseNode php = new NamedParseNode("php", "printing", 9, 16);
		merge(root, php);

		assertEquals("Merged tree does not match expected shape", "(html (php:printing))", ParseUtil.toTreeString(root));
	}

	@Test
	public void testNodeAfterChild()
	{
		IParseRootNode root = new NamedParseRootNode("html", 0, 17);
		IParseNode child = new NamedParseNode("html", "child", 5, 9);

		// build tree
		root.addChild(child);

		// create embedded nodes and merge
		IParseNode php = new NamedParseNode("php", "printing", 10, 17);
		merge(root, php);

		assertEquals("Merged tree does not match expected shape", "(html (html:child) (php:printing))",
				ParseUtil.toTreeString(root));
	}
}
