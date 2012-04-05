/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scope.parsing;

import junit.framework.TestCase;

import com.aptana.scope.ISelectorNode;
import com.aptana.scope.ScopeUtil;

/**
 * ScopeParserTests
 */
public class ScopeParserTests extends TestCase
{
	private ScopeParser parser;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();

		parser = new ScopeParser();
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		parser = null;

		super.tearDown();
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

	public void testSimpleNameSelector()
	{
		assertParseResult("name", "name");
	}

	public void testDottedNameSelector()
	{
		assertParseResult("name.another", "name.another");
	}

	public void testDescendantSelector()
	{
		assertParseResult("javascript comment", "(> javascript comment)");
	}

	public void testCommaSelector()
	{
		assertParseResult("html, javascript", "(, html javascript)");
	}

	public void testPipeSelector()
	{
		assertParseResult("html | javascript", "(| html javascript)");
	}

	public void testGroupSelector()
	{
		assertParseResult("(html)", "(GROUP html)");
	}

	public void testNegativeLookaheadSelector()
	{
		assertParseResult("html - comment", "(- html comment)");
	}

	public void testIntersectionSelector()
	{
		assertParseResult("html & javascript", "(& html javascript)");
	}

	public void testPrecedence()
	{
		assertParseResult("a & b | c & d", "(| (& a b) (& c d))");
	}

	public void testPrecedence2()
	{
		assertParseResult("a & b, c & d", "(, (& a b) (& c d))");
	}

	public void testPrecedence3()
	{
		assertParseResult("a, b | c, d", "(| (, a b) (, c d))");
	}

	public void testPrecedence4()
	{
		assertParseResult("a & b | c & d - e", "(| (& a b) (- (& c d) e))");
	}

	public void testNegativeLookaheadWithOr()
	{
		assertParseResult("text.html - (source | string)", "(- text.html (GROUP (| source string)))");
	}

	public void testNegativeLookaheadWithDescendantSelector()
	{
		assertParseResult("text.html - source string", "(- text.html (> source string)))");
	}
}
