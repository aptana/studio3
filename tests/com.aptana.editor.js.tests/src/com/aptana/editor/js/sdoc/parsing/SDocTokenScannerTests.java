/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.sdoc.parsing;

import java.io.IOException;

import junit.framework.TestCase;
import beaver.Symbol;

import com.aptana.editor.js.sdoc.lexer.SDocTokenType;

public class SDocTokenScannerTests extends TestCase
{
	private SDocFlexScanner _scanner;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		_scanner = new SDocFlexScanner();
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		_scanner = null;

		super.tearDown();
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
	public void testLeftBracket()
	{
		String source = "[";

		lexemeTypeTests(source, SDocTokenType.LBRACKET);
	}

	/**
	 * testRightBracket
	 */
	public void testRightBracket()
	{
		String source = "]";

		lexemeTypeTests(source, SDocTokenType.RBRACKET);
	}

	/**
	 * testStartDocumentation
	 */
	public void testStartDocumentation()
	{
		String source = "/**";

		lexemeTypeTests(source, SDocTokenType.START_DOCUMENTATION);
	}

	/**
	 * testEndDocumentation
	 */
	public void testEndDocumentation()
	{
		String source = "*/";

		lexemeTypeTests(source, SDocTokenType.END_DOCUMENTATION);
	}

	/**
	 * testAdvanced
	 */
	public void testAdvanced()
	{
		String source = "@advanced";

		lexemeTypeTests(source, SDocTokenType.ADVANCED);
	}

	/**
	 * testAlias
	 */
	public void testAlias()
	{
		String source = "@alias";

		lexemeTypeTests(source, SDocTokenType.ALIAS);
	}

	/**
	 * testAuthor
	 */
	public void testAuthor()
	{
		String source = "@author";

		lexemeTypeTests(source, SDocTokenType.AUTHOR);
	}

	/**
	 * testClassDescription
	 */
	public void testClassDescription()
	{
		String source = "@classDescription";

		lexemeTypeTests(source, SDocTokenType.CLASS_DESCRIPTION);
	}

	/**
	 * testConstructor
	 */
	public void testConstructor()
	{
		String source = "@constructor";

		lexemeTypeTests(source, SDocTokenType.CONSTRUCTOR);
	}

	/**
	 * testExample
	 */
	public void testExample()
	{
		String source = "@example";

		lexemeTypeTests(source, SDocTokenType.EXAMPLE);
	}

	/**
	 * testException
	 */
	public void testException()
	{
		String source = "@exception";

		lexemeTypeTests(source, SDocTokenType.EXCEPTION);
	}

	/**
	 * testExtends
	 */
	public void testExtends()
	{
		String source = "@extends";

		lexemeTypeTests(source, SDocTokenType.EXTENDS);
	}

	/**
	 * testInternal
	 */
	public void testInternal()
	{
		String source = "@internal";

		lexemeTypeTests(source, SDocTokenType.INTERNAL);
	}

	/**
	 * testMethod
	 */
	public void testMethod()
	{
		String source = "@method";

		lexemeTypeTests(source, SDocTokenType.METHOD);
	}

	/**
	 * testNamespace
	 */
	public void testNamespace()
	{
		String source = "@namespace";

		lexemeTypeTests(source, SDocTokenType.NAMESPACE);
	}

	/**
	 * testOverview
	 */
	public void testOverview()
	{
		String source = "@overview";

		lexemeTypeTests(source, SDocTokenType.OVERVIEW);
	}

	/**
	 * testParam
	 */
	public void testParam()
	{
		String source = "@param";

		lexemeTypeTests(source, SDocTokenType.PARAM);
	}

	/**
	 * testPrivate
	 */
	public void testPrivate()
	{
		String source = "@private";

		lexemeTypeTests(source, SDocTokenType.PRIVATE);
	}

	/**
	 * testProperty
	 */
	public void testProperty()
	{
		String source = "@property";

		lexemeTypeTests(source, SDocTokenType.PROPERTY);
	}

	/**
	 * testReturn
	 */
	public void testReturn()
	{
		String source = "@return";

		lexemeTypeTests(source, SDocTokenType.RETURN);
	}

	/**
	 * testSee
	 */
	public void testSee()
	{
		String source = "@see";

		lexemeTypeTests(source, SDocTokenType.SEE);
	}

	/**
	 * testUserTag
	 */
	public void testUserTag()
	{
		String source = "@myCustomTag";

		lexemeTypeTests(source, SDocTokenType.UNKNOWN);
	}

	/**
	 * testCR
	 */
	public void testCR()
	{
		String source = "\r";

		lexemeTypeTests(source, SDocTokenType.EOF);
	}

	/**
	 * testLF
	 */
	public void testLF()
	{
		String source = "\n";

		lexemeTypeTests(source, SDocTokenType.EOF);
	}

	/**
	 * testCRLF
	 */
	public void testCRLF()
	{
		String source = "\r\n";

		lexemeTypeTests(source, SDocTokenType.EOF);
	}

	/**
	 * testSpace
	 */
	public void testSpace()
	{
		String source = " ";

		lexemeTypeTests(source, SDocTokenType.EOF);
	}

	/**
	 * testTab
	 */
	public void testTab()
	{
		String source = "\t";

		lexemeTypeTests(source, SDocTokenType.EOF);
	}
}
