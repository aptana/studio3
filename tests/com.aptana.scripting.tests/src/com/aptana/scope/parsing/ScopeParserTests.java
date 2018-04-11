/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scope.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.scope.ISelectorNode;
import com.aptana.scope.ScopeUtil;

/**
 * ScopeParserTests
 */
public class ScopeParserTests
{
	private ScopeParser parser;

	@Before
	public void setUp() throws Exception
	{
		parser = new ScopeParser();
	}

	@After
	public void tearDown() throws Exception
	{
		parser = null;
	}

	protected void assertParseResult(String source, String treeString)
	{
		try
		{
			Object result = parser.parse(source);
			assertNotNull("Expected a parse result", result);
			assertTrue("Expected an ISelectorNode as the parse result", result instanceof ISelectorNode);

			ISelectorNode node = (ISelectorNode) result;
			assertEquals("Source and conversion of AST to text should match", source, node.toString());
			assertEquals("Unexpected tree shape in AST", treeString, ScopeUtil.toTreeString(node));
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void testSimpleNameSelector()
	{
		assertParseResult("name", "name");
	}

	@Test
	public void testDottedNameSelector()
	{
		assertParseResult("name.another", "name.another");
	}

	@Test
	public void testDescendantSelector()
	{
		assertParseResult("javascript comment", "(> javascript comment)");
	}

	@Test
	public void testCommaSelector()
	{
		assertParseResult("html, javascript", "(, html javascript)");
	}

	@Test
	public void testPipeSelector()
	{
		assertParseResult("html | javascript", "(| html javascript)");
	}

	@Test
	public void testGroupSelector()
	{
		assertParseResult("(html)", "(GROUP html)");
	}

	@Test
	public void testNegativeLookaheadSelector()
	{
		assertParseResult("html - comment", "(- html comment)");
	}

	@Test
	public void testIntersectionSelector()
	{
		assertParseResult("html & javascript", "(& html javascript)");
	}

	@Test
	public void testPrecedence()
	{
		assertParseResult("a & b | c & d", "(| (& a b) (& c d))");
	}

	@Test
	public void testPrecedence2()
	{
		assertParseResult("a & b, c & d", "(, (& a b) (& c d))");
	}

	@Test
	public void testPrecedence3()
	{
		assertParseResult("a, b | c, d", "(| (, a b) (, c d))");
	}

	@Test
	public void testPrecedence4()
	{
		assertParseResult("a & b | c & d - e", "(| (& a b) (- (& c d) e))");
	}

	@Test
	public void testNegativeLookaheadWithOr()
	{
		assertParseResult("text.html - (source | string)", "(- text.html (GROUP (| source string)))");
	}

	@Test
	public void testNegativeLookaheadWithDescendantSelector()
	{
		assertParseResult("text.html - source string", "(- text.html (> source string)))");
	}
}
