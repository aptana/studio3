/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.parsing;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;

/**
 * CompositeParserTests
 */
public class CompositeParserTests extends CompositeParserTestBase
{
	private static class MergingParser extends CompositeParser implements IMerger
	{
		public MergingParser()
		{
			super(null, null);
		}

		public void merge(IParseRootNode ast, IParseNode[] embeddedNodes)
		{
			mergeEmbeddedNodes(ast, embeddedNodes);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.parsing.CompositeParserTestBase#createMerger()
	 */
	@Override
	protected IMerger createMerger()
	{
		return new MergingParser();
	}

	@Test
	public void testNullRoot()
	{
		try
		{
			merge(null, new NamedParseNode("php", "print", 0, 4));
			// no exception means success
		}
		catch (Throwable t)
		{
			fail(t.getMessage());
		}
	}

	@Test
	public void testNullNodes()
	{
		try
		{
			merge(new NamedParseRootNode("php", 0, 4), (IParseNode[]) null);
			// no exception means success
		}
		catch (Throwable t)
		{
			fail(t.getMessage());
		}
	}
}
