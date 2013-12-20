/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.parsing.sdoc;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.io.IOException;

import junit.framework.TestCase;
import beaver.Symbol;

public class SDocFlexScannerTest
{
	private SDocFlexScanner _scanner;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
//	@Override
	@Before
	public void setUp() throws Exception
	{
//		super.setUp();

		_scanner = new SDocFlexScanner();
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
//	@Override
	@After
	public void tearDown() throws Exception
	{
		_scanner = null;

//		super.tearDown();
	}

	/**
	 * lexemeTypeTests
	 * 
	 * @param source
	 * @param types
	 * @throws beaver.Scanner.Exception
	 * @throws IOException
	 */
	protected void lexemeTypeTests(String source, SDocTokenType... types)
	{
		this._scanner.setSource(source);

		for (int i = 0; i < types.length; i++)
		{
			try
			{
				SDocTokenType targetType = types[i];
				Symbol token = this._scanner.nextToken();
				SDocTokenType actualType = SDocTokenType.get(token.getId());

				assertEquals("at index " + i, targetType, actualType);
			}
			catch (Throwable t)
			{
				fail(t.getMessage());
			}
		}
	}

	/**
	 * testNoTypes
	 */
	@Test
	public void testNoTypes()
	{
		String source = "{}";

		// @formatter:off
		lexemeTypeTests(
			source,
			SDocTokenType.LCURLY,
			SDocTokenType.RCURLY
		);
		// @formatter:on
	}

	/**
	 * testTypes
	 */
	@Test
	public void testTypes()
	{
		String source = "{Number}";

		// @formatter:off
		lexemeTypeTests(
			source,
			SDocTokenType.LCURLY,
			SDocTokenType.IDENTIFIER,
			SDocTokenType.RCURLY
		);
		// @formatter:on
	}

	/**
	 * testLeftBracket
	 */
	@Test
	public void testLeftBracket()
	{
		String source = "[";

		lexemeTypeTests(source, SDocTokenType.LBRACKET);
	}

	/**
	 * testRightBracket
	 */
	@Test
	public void testRightBracket()
	{
		String source = "]";

		lexemeTypeTests(source, SDocTokenType.RBRACKET);
	}

	/**
	 * testStartDocumentation
	 */
	@Test
	public void testStartDocumentation()
	{
		String source = "/**";

		lexemeTypeTests(source, SDocTokenType.START_DOCUMENTATION);
	}

	/**
	 * testEndDocumentation
	 */
	@Test
	public void testEndDocumentation()
	{
		String source = "*/";

		lexemeTypeTests(source, SDocTokenType.END_DOCUMENTATION);
	}

	/**
	 * testAdvanced
	 */
	@Test
	public void testAdvanced()
	{
		String source = "@advanced";

		lexemeTypeTests(source, SDocTokenType.ADVANCED);
	}

	/**
	 * testAlias
	 */
	@Test
	public void testAlias()
	{
		String source = "@alias";

		lexemeTypeTests(source, SDocTokenType.ALIAS);
	}

	/**
	 * testAuthor
	 */
	@Test
	public void testAuthor()
	{
		String source = "@author";

		lexemeTypeTests(source, SDocTokenType.AUTHOR);
	}

	/**
	 * testClassDescription
	 */
	@Test
	public void testClassDescription()
	{
		String source = "@classDescription";

		lexemeTypeTests(source, SDocTokenType.CLASS_DESCRIPTION);
	}

	/**
	 * testConstructor
	 */
	@Test
	public void testConstructor()
	{
		String source = "@constructor";

		lexemeTypeTests(source, SDocTokenType.CONSTRUCTOR);
	}

	/**
	 * testExample
	 */
	@Test
	public void testExample()
	{
		String source = "@example";

		lexemeTypeTests(source, SDocTokenType.EXAMPLE);
	}

	/**
	 * testException
	 */
	@Test
	public void testException()
	{
		String source = "@exception";

		lexemeTypeTests(source, SDocTokenType.EXCEPTION);
	}

	/**
	 * testExtends
	 */
	@Test
	public void testExtends()
	{
		String source = "@extends";

		lexemeTypeTests(source, SDocTokenType.EXTENDS);
	}

	/**
	 * testInternal
	 */
	@Test
	public void testInternal()
	{
		String source = "@internal";

		lexemeTypeTests(source, SDocTokenType.INTERNAL);
	}

	/**
	 * testMethod
	 */
	@Test
	public void testMethod()
	{
		String source = "@method";

		lexemeTypeTests(source, SDocTokenType.METHOD);
	}

	@Test
	public void testModule()
	{
		String source = "@module";

		lexemeTypeTests(source, SDocTokenType.MODULE);
	}

	/**
	 * testNamespace
	 */
	@Test
	public void testNamespace()
	{
		String source = "@namespace";

		lexemeTypeTests(source, SDocTokenType.NAMESPACE);
	}

	/**
	 * testOverview
	 */
	@Test
	public void testOverview()
	{
		String source = "@overview";

		lexemeTypeTests(source, SDocTokenType.OVERVIEW);
	}

	/**
	 * testParam
	 */
	@Test
	public void testParam()
	{
		String source = "@param";

		lexemeTypeTests(source, SDocTokenType.PARAM);
	}

	/**
	 * testPrivate
	 */
	@Test
	public void testPrivate()
	{
		String source = "@private";

		lexemeTypeTests(source, SDocTokenType.PRIVATE);
	}

	/**
	 * testProperty
	 */
	@Test
	public void testProperty()
	{
		String source = "@property";

		lexemeTypeTests(source, SDocTokenType.PROPERTY);
	}

	/**
	 * testReturn
	 */
	@Test
	public void testReturn()
	{
		String source = "@return";

		lexemeTypeTests(source, SDocTokenType.RETURN);
	}

	/**
	 * testSee
	 */
	@Test
	public void testSee()
	{
		String source = "@see";

		lexemeTypeTests(source, SDocTokenType.SEE);
	}

	/**
	 * testUserTag
	 */
	@Test
	public void testUserTag()
	{
		String source = "@myCustomTag";

		lexemeTypeTests(source, SDocTokenType.UNKNOWN);
	}

	/**
	 * testCR
	 */
	@Test
	public void testCR()
	{
		String source = "\r";

		lexemeTypeTests(source, SDocTokenType.EOF);
	}

	/**
	 * testLF
	 */
	@Test
	public void testLF()
	{
		String source = "\n";

		lexemeTypeTests(source, SDocTokenType.EOF);
	}

	/**
	 * testCRLF
	 */
	@Test
	public void testCRLF()
	{
		String source = "\r\n";

		lexemeTypeTests(source, SDocTokenType.EOF);
	}

	/**
	 * testSpace
	 */
	@Test
	public void testSpace()
	{
		String source = " ";

		lexemeTypeTests(source, SDocTokenType.EOF);
	}

	/**
	 * testTab
	 */
	@Test
	public void testTab()
	{
		String source = "\t";

		lexemeTypeTests(source, SDocTokenType.EOF);
	}
}
